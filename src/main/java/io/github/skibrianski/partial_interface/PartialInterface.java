package io.github.skibrianski.partial_interface;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.lang.reflect.Method;
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

    private static void check(ScanResult scanResult, boolean manual) {
        for (ClassInfo annotationClassInfo : scanResult.getClassesWithAnnotation(RequiresChildMethod.class)) {
            if (!annotationClassInfo.isAbstract()) {
                throw new PartialInterfaceException.UsageException(
                        "attempt to use @PartialInterface on non-abstract class: " + annotationClassInfo.getName()
                );
            }
            // note: for interfaces, getClassesImplementing() includes interfaces, abstract classes, and concrete
            // classes, but getSubClasses() is required for abstract classes.
            ClassInfoList implementations = annotationClassInfo.isInterface()
                    ? annotationClassInfo.getClassesImplementing()
                    : annotationClassInfo.getSubclasses();
            List<RequiresChildMethod> requiresChildMethodAnnotations = annotationClassInfo
                    .getAnnotationInfoRepeatable(RequiresChildMethod.class)
                    .stream()
                    .map(AnnotationInfo::loadClassAndInstantiate)
                    .map(RequiresChildMethod.class::cast)
                    .collect(Collectors.toList());
            AnnotationInfo requiresTypeParameterAnnotationInfo = annotationClassInfo
                    .getAnnotationInfo(RequiresTypeParameter.class);
            RequiresTypeParameter requiresTypeParameterAnnotation = requiresTypeParameterAnnotationInfo == null
                    ? null
                    : (RequiresTypeParameter) requiresTypeParameterAnnotationInfo.loadClassAndInstantiate();
            for (ClassInfo implementationClassInfo : implementations) {
                // if we're doing an automatic pass, skip implementations tagged for manual validation
                // TODO: this implementation is uggo.
                if (!manual) {
                    if (implementationClassInfo.getAnnotationInfo(PartialInterfaceWithManualValidation.class) != null) {
                        continue;
                    }
                }
                validateImplementation(
                        implementationClassInfo.loadClass(),
                        requiresTypeParameterAnnotation,
                        requiresChildMethodAnnotations
                );
            }
        }
    }

    private static void validateHasAllTypeParameters(
            Class<?> implementation,
            Set<String> implementedTypeParameters,
            Set<String> requiredTypeParameters
    ) {
        if (implementedTypeParameters.equals(requiredTypeParameters)) {
            return;
        }
        List<String> missingTypeParameters = requiredTypeParameters.stream()
                .filter(requiredTypeParameter -> !implementedTypeParameters.contains(requiredTypeParameter))
                .collect(Collectors.toList());
        if (!missingTypeParameters.isEmpty()) {
            throw new PartialInterfaceException.MissingTypeParameterException(
                    "implementation: " + implementation
                            + " is missing type parameter(s): " + String.join(", ", missingTypeParameters)
            );
        }
        List<String> extraneousTypeParameters = implementedTypeParameters.stream()
                .filter(implementedTypeParameter -> !requiredTypeParameters.contains(implementedTypeParameter))
                .collect(Collectors.toList());
        if (!extraneousTypeParameters.isEmpty()) {
            throw new PartialInterfaceException.ExtraneousTypeParameterException(
                    "implementation: " + implementation
                            + " has extraneous type parameter(s): " + String.join(", ", missingTypeParameters)
            );
        }
    }

    private static void validateImplementation(
            Class<?> implementation,
            RequiresTypeParameter requiresTypeParameterAnnotation,
            List<RequiresChildMethod> requiresChildMethodAnnotations
    ) {
        // TODO: probably should throw error on duplicate @HasTypeParameter annotation for same type
        Set<String> requiredTypeParameters = requiresTypeParameterAnnotation == null
                ? Set.of()
                : Arrays.stream(requiresTypeParameterAnnotation.value()).collect(Collectors.toSet());
        HasTypeParameter[] hasTypeParameters = implementation.getAnnotationsByType(HasTypeParameter.class);
        Map<String, Class<?>> scalarTypeParameterMap = Arrays.stream(hasTypeParameters)
                .collect(Collectors.toMap(HasTypeParameter::name, HasTypeParameter::ofClass));
        validateHasAllTypeParameters(implementation, scalarTypeParameterMap.keySet(), requiredTypeParameters);

        TypeNameResolver typeNameResolver = new TypeNameResolver(scalarTypeParameterMap);
        TypeValidator typeValidator = new TypeValidator(typeNameResolver);
        Method[] methods = implementation.getMethods();
        for (RequiresChildMethod requiresChildMethod : requiresChildMethodAnnotations) {
            List<Method> matchingMethods = Arrays.stream(methods)
                    .filter(m -> m.getName().equals(requiresChildMethod.methodName()))
                    .filter(m -> typeValidator.isAssignableType(m.getGenericReturnType(), requiresChildMethod.returnType()))
                    .filter(m -> typeValidator.hasAssignableArgumentTypes(m, requiresChildMethod.argumentTypes()))
                    .collect(Collectors.toList());
            if (matchingMethods.isEmpty()) {
                String message = "implementation " + implementation.getName()
                        + " does not implement partial interface method: "
                        + RequiresChildMethod.Util.stringify(requiresChildMethod);
                if (!typeNameResolver.isEmpty()) {
                    message += " with type parameters: " + typeNameResolver;
                }
                throw new PartialInterfaceException.NotCompletedException(message);
            }
            if (matchingMethods.size() > 1) {
                // TODO: is this possible? if not, remove it.
                throw new PartialInterfaceException(
                        "bug: internal error: implementation " + implementation.getName()
                                + " implements more than one matching interface method matching: "
                                + RequiresChildMethod.Util.stringify(requiresChildMethod)
                                + ". please report this error."
                );
            }
        }
    }

}

