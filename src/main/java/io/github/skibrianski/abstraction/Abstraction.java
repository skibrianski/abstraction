package io.github.skibrianski.abstraction;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class Abstraction {

    // TODD: should we tweak enableAllInfo() to be more precise & only include what we need?

    static {
        try (ScanResult scanResult = getConfiguredClassGraph().scan()) {
            check(scanResult, false);
        }
    }

    public static void check(Class... classes) {
        String[] classNames = Arrays.stream(classes).map(Class::getName).toArray(String[]::new);
        try (ScanResult scanResult = getConfiguredClassGraph().acceptClasses(classNames).scan()) {
            check(scanResult, true);
        }
    }

    public static void check(Package... packages) {
        String[] packageNames = Arrays.stream(packages).map(Package::getName).toArray(String[]::new);
        try (ScanResult scanResult = getConfiguredClassGraph().acceptPackages(packageNames).scan()) {
            check(scanResult, true);
        }
    }

    public static ClassGraph getConfiguredClassGraph() {
        return new ClassGraph().enableAllInfo().enableInterClassDependencies().enableExternalClasses();
    }

    private static void check(ScanResult scanResult, boolean isManualRun) {
        // TODO: also look for classes with RequiresTypeParameter that have no RequiresChildMethod, throw appropriately
        for (ClassInfo abstractionClassInfo : scanResult.getClassesWithAnnotation(RequiresChildMethod.class)) {
            if (shouldBeAutoValidated(abstractionClassInfo) || isManualRun) {
                if (!abstractionClassInfo.isAbstract()) {
                    throw new AbstractionException.UsageException(
                            "attempt to use abstraction on non-abstract class: " + abstractionClassInfo.getName()
                    );
                }
            }
            // note: for interfaces, getClassesImplementing() includes interfaces, abstract classes, and concrete
            // classes, but getSubClasses() is required for abstract classes.
            ClassInfoList implementations = abstractionClassInfo.isInterface()
                    ? abstractionClassInfo.getClassesImplementing()
                    : abstractionClassInfo.getSubclasses();
            List<RequiresChildMethod> requiresChildMethodAnnotations =
                    loadAndReturnRepeatableAnnotationClasses(abstractionClassInfo, RequiresChildMethod.class);
            List<RequiresTypeParameter> requiresTypeParameterAnnotations =
                    loadAndReturnRepeatableAnnotationClasses(abstractionClassInfo, RequiresTypeParameter.class);
            for (ClassInfo implementationClassInfo : implementations) {
                if (shouldBeAutoValidated(implementationClassInfo) || isManualRun) {
                    validateImplementation(
                            implementationClassInfo.loadClass(),
                            requiresTypeParameterAnnotations,
                            requiresChildMethodAnnotations,
                            implementationClassInfo.getClassDependencies()
                    );

                }
            }
        }
    }

    // if we're doing an automatic pass, skip implementations and abstract classes tagged for manual validation
    private static boolean shouldBeAutoValidated(ClassInfo classInfo) {
        return classInfo.getAnnotationInfo(ManualValidation.class) == null;
    }

    private static <A extends Annotation> List<A> loadAndReturnRepeatableAnnotationClasses(
            ClassInfo abstractionClassInfo,
            Class<A> annotationClass
    ) {
        return abstractionClassInfo
                .getAnnotationInfoRepeatable(annotationClass)
                .stream()
                .map(AnnotationInfo::loadClassAndInstantiate)
                .map(annotationClass::cast)
                .collect(Collectors.toList());
    }

    // note: assumption: is only run after validateHasAllTypeParameters()
    // i guess we can't use TypeValidator and such b/c we're checking the implementation class's type params,
    // not the implemented method's types.
    private static void validateTypeParameterBounds(
            String implementationName,
            java.lang.reflect.Type[] implementedTypeParameters,
            List<RequiresTypeParameter> requiredTypeParameters,
            TypeNameResolver typeNameResolver
    ) {
        for (int pos = 0; pos < implementedTypeParameters.length; pos++) {
            java.lang.reflect.Type implementedTypeParameter = implementedTypeParameters[pos];
            RequiresTypeParameter requiredTypeParameter = requiredTypeParameters.get(pos);

            for (String superTypeString : requiredTypeParameter.superOf()) {
                Type superTypeBound = new TypeParameterParser(superTypeString, typeNameResolver).parse();
                if (!TypeValidator.isAssignableType(superTypeBound, implementedTypeParameter)) {
                    throw new AbstractionException.TypeParameterViolatesBoundsException(
                            "implementation " + implementationName
                                    + " does not fulfill super constraint: " + superTypeBound
                                    + " with implemented type; " + implementedTypeParameter
                    );
                }
            }

            for (String extendingTypeString : requiredTypeParameter.extending()) {
                Type extendingTypeBound = new TypeParameterParser(extendingTypeString, typeNameResolver).parse();
                if (!TypeValidator.isAssignableType(implementedTypeParameter, extendingTypeBound)) {
                    throw new AbstractionException.TypeParameterViolatesBoundsException(
                            "implementation " + implementationName
                                    + " does not extend: " + extendingTypeBound
                                    + " with implemented type; " + implementedTypeParameter
                    );
                }
            }
        }
    }

    private static void validateHasAllTypeParameters(
            Class<?> implementation,
            HasTypeParameter[] implementedTypeParameters,
            List<RequiresTypeParameter> requiredTypeParameters
    ) {
        Set<String> implementedTypeParameterNames = Arrays.stream(implementedTypeParameters)
                .map(HasTypeParameter::name)
                .collect(Collectors.toSet());
        if (implementedTypeParameterNames.size() != implementedTypeParameters.length) {
            Map<String, Long> counts = Arrays.stream(implementedTypeParameters)
                    .map(HasTypeParameter::name)
                    .collect(Collectors.groupingBy(x -> x, Collectors.counting()));
            Set<String> duplicates = counts.keySet().stream()
                    .filter(x -> counts.get(x) > 1)
                    .collect(Collectors.toSet());
            throw new AbstractionException.UsageException(
                    "implementation: " + implementation
                            + " has duplicate type parameters: " + duplicates
            );
        }
        Set<String> requiredTypeParameterNames = requiredTypeParameters.stream()
                .map(RequiresTypeParameter::value)
                .collect(Collectors.toSet());
        List<String> missingTypeParameters = requiredTypeParameters.stream()
                .map(RequiresTypeParameter::value)
                .filter(requiredTypeParameter -> !implementedTypeParameterNames.contains(requiredTypeParameter))
                .collect(Collectors.toList());
        if (!missingTypeParameters.isEmpty()) {
            throw new AbstractionException.MissingTypeParameterException(
                    "implementation: " + implementation
                            + " is missing type parameter(s): "
                            + missingTypeParameters
            );
        }
        List<String> extraneousTypeParameters = Arrays.stream(implementedTypeParameters)
                .map(HasTypeParameter::name)
                .filter(implementedTypeParameter -> !requiredTypeParameterNames.contains(implementedTypeParameter))
                .collect(Collectors.toList());
        if (!extraneousTypeParameters.isEmpty()) {
            throw new AbstractionException.ExtraneousTypeParameterException(
                    "implementation: " + implementation
                            + " has extraneous type parameter(s): "
                            + extraneousTypeParameters
            );
        }
    }
    private static void validateImplementation(
            Class<?> implementation,
            List<RequiresTypeParameter> requiresTypeParameterAnnotations,
            List<RequiresChildMethod> requiresChildMethodAnnotations,
            ClassInfoList implementationDependencies
    ) {
        HasTypeParameter[] hasTypeParameters = implementation.getAnnotationsByType(HasTypeParameter.class);
        validateHasAllTypeParameters(implementation, hasTypeParameters, requiresTypeParameterAnnotations);

        TypeNameResolver typeNameResolver = new TypeNameResolver();
        // load all super classes and interface parents of classes used in class to typeNameResolver
        // so user doesn't need to specify full paths.
        for (Class<?> superClass : loadAllUserSubAndSuperClassesAndInterfaces(implementationDependencies)) {
            typeNameResolver.addClass(superClass);
        }
        java.lang.reflect.Type[] implementedTypes = Arrays.stream(hasTypeParameters)
                .map(typeNameResolver::lookup)
                .toArray(java.lang.reflect.Type[]::new);
        for (int pos = 0; pos < implementedTypes.length; pos++) {
            typeNameResolver.addTypeParameter(hasTypeParameters[pos].name(), implementedTypes[pos]);
        }

        TypeValidator typeValidator = new TypeValidator(typeNameResolver);
        validateTypeParameterBounds(
                implementation.getName(),
                implementedTypes,
                requiresTypeParameterAnnotations,
                typeNameResolver
        );

        for (RequiresChildMethod requiresChildMethod : requiresChildMethodAnnotations) {
            // for both abstract and concrete, if arguments & name match, return type must as well
            // or else "attempting to use incompatible return type"
            boolean isConcrete = isConcrete(implementation);

            List<Method> methodsWithMatchingName = Arrays.stream(implementation.getMethods())
                    .filter(m -> m.getName().equals(requiresChildMethod.methodName()))
                    .collect(Collectors.toList());
            if (methodsWithMatchingName.isEmpty() && isConcrete) {
                throw new AbstractionException.NoMethodWithMatchingName(
                        "no method matching name " + requiresChildMethod.methodName()
                                + " available in implementation " + implementation.getName()
                );
            }
            List<Method> methodsWithMatchingNameAndArguments = methodsWithMatchingName.stream()
                    .filter(m -> typeValidator.hasAssignableArgumentTypes(m, requiresChildMethod.argumentTypes()))
                    .collect(Collectors.toList());

            if (methodsWithMatchingNameAndArguments.isEmpty()) {
                if (isConcrete) {
                    // TODO: provide more detail
                    throw new AbstractionException.ClashingArgumentTypeException(
                            "implementation " + implementation.getName()
                                    + " does not implement abstraction method: "
                                    + RequiresChildMethod.Util.stringify(requiresChildMethod)
                                    + " with type parameters: " + typeNameResolver
                    );
                }
            } else {
                if (
                        !hasMatchingReturnType(
                                methodsWithMatchingNameAndArguments,
                                requiresChildMethod,
                                typeValidator
                        )
                ) {
                    throw new AbstractionException.ClashingReturnTypeException(
                            "implementation " + implementation.getName()
                                    + " has clashing return type for method: "
                                    + RequiresChildMethod.Util.stringify(requiresChildMethod)
                                    + " with type parameters: " + typeNameResolver
                    );
                }
            }
        }
    }

    // TODO: write test where we have 2 methods, of which one has clashing return but one is correct
    private static boolean hasMatchingReturnType(
            List<Method> methodsWithMatchingNameAndArguments,
            RequiresChildMethod requiresChildMethod,
            TypeValidator typeValidator
    ) {
        for (Method method : methodsWithMatchingNameAndArguments) {
            if (typeValidator.isAssignableType(method.getGenericReturnType(), requiresChildMethod.returnType())) {
                return true;
            }
        }
        return false;
    }

    // note: the context of this operation is limited to the scan result, so the scope of spamming is limited.
    private static Set<Class<?>> loadAllUserSubAndSuperClassesAndInterfaces(ClassInfoList classInfoList) {
        Set<Class<?>> classes = new HashSet<>();
        for (ClassInfo classInfo : classInfoList) {
            classes.add(classInfo.loadClass());
            for (ClassInfo superClassInfo : classInfo.getSuperclasses()) {
                classes.add(superClassInfo.loadClass());
            }
            for (ClassInfo interfaceClassInfo : classInfo.getInterfaces()) {
                classes.add(interfaceClassInfo.loadClass());
            }
            for (ClassInfo subClassInfo : classInfo.getSubclasses()) {
                classes.add(subClassInfo.loadClass());
            }
            for (ClassInfo implementorClassInfo : classInfo.getClassesImplementing()) {
                classes.add(implementorClassInfo.loadClass());
            }
            if (classInfo.isInnerClass()) {
                for (ClassInfo outerClassInfo : classInfo.getOuterClasses()) {
                    classes.add(outerClassInfo.loadClass());
                    // include sibling classes, e.g. A$B gets A (the outer class) and any A$C and A$D as well.
                    for (ClassInfo innerClassInfo : outerClassInfo.getInnerClasses()) {
                        classes.add(innerClassInfo.loadClass());
                    }
                }
            }
            if (classInfo.isOuterClass()) {
                for (ClassInfo innerClassInfo : classInfo.getInnerClasses()) {
                    classes.add(innerClassInfo.loadClass());
                }
            }
            // if ClassGraph ever adds a method to search for interfaces using this interface, add it here.
            // it does not presently have this feature
        }
        return classes;
    }

    private static boolean isConcrete(Class<?> implementation) {
        int modifiers = implementation.getModifiers();
        return !(Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers));
    }
}

