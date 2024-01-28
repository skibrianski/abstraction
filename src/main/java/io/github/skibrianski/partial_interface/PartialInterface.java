package io.github.skibrianski.partial_interface;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class PartialInterface {

    // TODD: should we tweak enableAllInfo() to be more precise & only include what we need?

    static {
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().scan()) {
            check(scanResult, false);
        }
    }

    public static void check(Class... classes) {
        String[] classNames = Arrays.stream(classes).map(Class::getName).toArray(String[]::new);
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().acceptClasses(classNames).scan()) {
            check(scanResult, true);
        }
    }

    public static void check(Package... packages) {
        String[] packageNames = Arrays.stream(packages).map(Package::getName).toArray(String[]::new);
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(packageNames).scan()) {
            check(scanResult, true);
        }
    }

    private static void check(ScanResult scanResult, boolean isManualRun) {
        // TODO: also look for classes with RequiresTypeParameter that have no RequiresChildMethod, throw appropriately
        for (ClassInfo partialInterfaceClassInfo : scanResult.getClassesWithAnnotation(RequiresChildMethod.class)) {
            if (shouldBeAutoValidated(partialInterfaceClassInfo) || isManualRun) {
                if (!partialInterfaceClassInfo.isAbstract()) {
                    throw new PartialInterfaceException.UsageException(
                            "attempt to use @PartialInterface on non-abstract class: " + partialInterfaceClassInfo.getName()
                    );
                }
            }
            // note: for interfaces, getClassesImplementing() includes interfaces, abstract classes, and concrete
            // classes, but getSubClasses() is required for abstract classes.
            ClassInfoList implementations = partialInterfaceClassInfo.isInterface()
                    ? partialInterfaceClassInfo.getClassesImplementing()
                    : partialInterfaceClassInfo.getSubclasses();
            List<RequiresChildMethod> requiresChildMethodAnnotations =
                    loadAndReturnRepeatableAnnotationClasses(partialInterfaceClassInfo, RequiresChildMethod.class);
            List<RequiresTypeParameter> requiresTypeParameterAnnotations =
                    loadAndReturnRepeatableAnnotationClasses(partialInterfaceClassInfo, RequiresTypeParameter.class);
            for (ClassInfo implementationClassInfo : implementations) {
                if (shouldBeAutoValidated(implementationClassInfo) || isManualRun) {
                    validateImplementation(
                            implementationClassInfo.loadClass(),
                            requiresTypeParameterAnnotations,
                            requiresChildMethodAnnotations
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
            ClassInfo partialInterfaceClassInfo,
            Class<A> annotationClass
    ) {
        return partialInterfaceClassInfo
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
            Class<?> implementation,
            java.lang.reflect.Type[] implementedTypeParameters,
            List<RequiresTypeParameter> requiredTypeParameters,
            TypeNameResolver typeNameResolver
    ) {
        for (int pos = 0; pos < implementedTypeParameters.length; pos++) {
            java.lang.reflect.Type implementedTypeParameter = implementedTypeParameters[pos];
            RequiresTypeParameter requiredTypeParameter = requiredTypeParameters.get(pos);

            for (int upperBoundNum = 0; upperBoundNum < requiredTypeParameter.upperBound().length; upperBoundNum++) {
                Type upperBound = typeNameResolver.resolve(requiredTypeParameter.upperBound()[upperBoundNum]);
                if (!TypeValidator.isAssignableType(upperBound, implementedTypeParameter)) {
                    throw new PartialInterfaceException.TypeParameterViolatesBounds(
                            "implementation does not fulfill upper bound: " + upperBound
                                    + " with implemented type; " + implementedTypeParameter
                    );
                }
            }

            for (int lowerBoundNum = 0; lowerBoundNum < requiredTypeParameter.lowerBound().length; lowerBoundNum++) {
                Type lowerBound = typeNameResolver.resolve(requiredTypeParameter.lowerBound()[lowerBoundNum]);
                if (!TypeValidator.isAssignableType(implementedTypeParameter, lowerBound)) {
                    throw new PartialInterfaceException.TypeParameterViolatesBounds(
                            "implementation does not fulfill lower bound: " + lowerBound
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

        // TODO: requiresTypeParameter can have bounds

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
            throw new PartialInterfaceException.UsageException(
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
            throw new PartialInterfaceException.MissingTypeParameterException(
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
            throw new PartialInterfaceException.ExtraneousTypeParameterException(
                    "implementation: " + implementation
                            + " has extraneous type parameter(s): "
                            + extraneousTypeParameters
            );
        }
    }
    private static void validateImplementation(
            Class<?> implementation,
            List<RequiresTypeParameter> requiresTypeParameterAnnotations,
            List<RequiresChildMethod> requiresChildMethodAnnotations
    ) {
        HasTypeParameter[] hasTypeParameters = implementation.getAnnotationsByType(HasTypeParameter.class);
        validateHasAllTypeParameters(implementation, hasTypeParameters, requiresTypeParameterAnnotations);

        TypeNameResolver typeNameResolver = new TypeNameResolver();
        java.lang.reflect.Type[] implementedTypes = Arrays.stream(hasTypeParameters)
                .map(typeNameResolver::lookup)
                .toArray(java.lang.reflect.Type[]::new);
        for (int pos = 0; pos < implementedTypes.length; pos++) {
            typeNameResolver.addTypeParameter(hasTypeParameters[pos].name(), implementedTypes[pos]);
        }
        TypeValidator typeValidator = new TypeValidator(typeNameResolver);
        validateTypeParameterBounds(implementation, implementedTypes, requiresTypeParameterAnnotations, typeNameResolver);

        Method[] methods = implementation.getMethods();
        for (RequiresChildMethod requiresChildMethod : requiresChildMethodAnnotations) {
            // for both abstract and concrete, if arguments & name match, return type must as well
            // or else "attempting to use incompatible return type"
            List<Method> methodsMatchingNameAndArguments = methodsMatchingNameAndArguments(
                    methods,
                    requiresChildMethod,
                    typeValidator
            );

            if (methodsMatchingNameAndArguments.size() > 1) {
                // TODO: think: is this actually possible?
                throw new RuntimeException(
                        "internal error: i didn't think this was possible. " + methodsMatchingNameAndArguments
                );
            }
            if (isConcrete(implementation) && methodsMatchingNameAndArguments.isEmpty()) {
                throw new PartialInterfaceException.NotCompletedException(
                        "implementation " + implementation.getName()
                                + " does not implement partial interface method: "
                                + RequiresChildMethod.Util.stringify(requiresChildMethod)
                                + " with type parameters: " + typeNameResolver
                );
            }

            if (methodsMatchingNameAndArguments.size() == 1) {
                if (
                        !typeValidator.isAssignableType(
                                methodsMatchingNameAndArguments.get(0).getGenericReturnType(),
                                requiresChildMethod.returnType()
                        )
                ) {
                    throw new PartialInterfaceException.ClashingReturnTypeException(
                            "implementation " + implementation.getName()
                                    + " has clashing return type for method: "
                                    + RequiresChildMethod.Util.stringify(requiresChildMethod)
                                    + " with type parameters: " + typeNameResolver
                    );
                }
            }
        }
    }

    private static boolean isConcrete(Class<?> implementation) {
        int modifiers = implementation.getModifiers();
        return !(Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers));
    }

    private static List<Method> methodsMatchingNameAndArguments(
            Method[] methods,
            RequiresChildMethod requiresChildMethod,
            TypeValidator typeValidator
    ) {
        return Arrays.stream(methods)
                .filter(m -> m.getName().equals(requiresChildMethod.methodName()))
                .filter(m -> typeValidator.hasAssignableArgumentTypes(m, requiresChildMethod.argumentTypes()))
                .collect(Collectors.toList());
    }
}

