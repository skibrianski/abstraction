package io.github.skibrianski.partial_interface;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class PartialInterface {

    // TODD: do we need enableAllInfo()?

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
            boolean interfaceShouldBeAutoValidated =
                    partialInterfaceClassInfo.getAnnotationInfo(ManualValidation.class) == null;
            if (interfaceShouldBeAutoValidated || isManualRun) {
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
                // if we're doing an automatic pass, skip implementations tagged for manual validation
                boolean classShouldBeAutoValidated =
                        implementationClassInfo.getAnnotationInfo(ManualValidation.class) == null;
                if (classShouldBeAutoValidated || isManualRun) {
                    validateImplementation(
                            implementationClassInfo.loadClass(),
                            requiresTypeParameterAnnotations,
                            requiresChildMethodAnnotations
                    );

                }
            }
        }
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

    private static void validateHasAllTypeParameters(
            Class<?> implementation,
            String[] implementedTypeParameters,
            String[] requiredTypeParameters
    ) {
        if (Arrays.equals(implementedTypeParameters, requiredTypeParameters)) {
            return;
        }

        Set<String> implementedTypeParameterSet = Arrays.stream(implementedTypeParameters)
                .collect(Collectors.toSet());
        if (implementedTypeParameterSet.size() != implementedTypeParameters.length) {
            Map<String, Long> counts = Arrays.stream(implementedTypeParameters)
                    .collect(Collectors.groupingBy(x -> x, Collectors.counting()));
            Set<String> duplicates = counts.keySet().stream()
                    .filter(x -> counts.get(x) > 1)
                    .collect(Collectors.toSet());
            throw new PartialInterfaceException.UsageException(
                    "implementation: " + implementation
                            + " has duplicate type parameters: " + duplicates
            );
        }
        Set<String> requiredTypeParameterSet = Arrays.stream(requiredTypeParameters)
                .collect(Collectors.toSet());
        List<String> missingTypeParameters = Arrays.stream(requiredTypeParameters)
                .filter(requiredTypeParameter -> !implementedTypeParameterSet.contains(requiredTypeParameter))
                .collect(Collectors.toList());
        if (!missingTypeParameters.isEmpty()) {
            throw new PartialInterfaceException.MissingTypeParameterException(
                    "implementation: " + implementation
                            + " is missing type parameter(s): "
                            + missingTypeParameters
            );
        }
        List<String> extraneousTypeParameters = Arrays.stream(implementedTypeParameters)
                .filter(implementedTypeParameter -> !requiredTypeParameterSet.contains(implementedTypeParameter))
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
        String[] requiredTypeParameters = requiresTypeParameterAnnotations.stream()
                .map(RequiresTypeParameter::value)
                .toArray(String[]::new);
        HasTypeParameter[] hasTypeParameters = implementation.getAnnotationsByType(HasTypeParameter.class);
        String[] implementedTypeParameters = Arrays.stream(hasTypeParameters)
                .map(HasTypeParameter::name)
                .toArray(String[]::new);
        validateHasAllTypeParameters(implementation, implementedTypeParameters, requiredTypeParameters);

        TypeNameResolver typeNameResolver = new TypeNameResolver();
        for (HasTypeParameter hasTypeParameter : hasTypeParameters) {
            typeNameResolver.addTypeParameter(
                    hasTypeParameter.name(),
                    HasTypeParameter.Util.lookup(hasTypeParameter, typeNameResolver)
            );
        }
        TypeValidator typeValidator = new TypeValidator(typeNameResolver);

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
            int modifiers = implementation.getModifiers();
            boolean isConcrete = !(Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers));
            if (isConcrete) {
                if (methodsMatchingNameAndArguments.isEmpty()) {
                    throw new PartialInterfaceException.NotCompletedException(
                            "implementation " + implementation.getName()
                                    + " does not implement partial interface method: "
                                    + RequiresChildMethod.Util.stringify(requiresChildMethod)
                                    + " with type parameters: " + typeNameResolver
                    );
                }
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

