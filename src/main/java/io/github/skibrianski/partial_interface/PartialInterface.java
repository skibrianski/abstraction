package io.github.skibrianski.partial_interface;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import io.github.skibrianski.partial_interface.exception.PartialInterfaceException;
import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import io.github.skibrianski.partial_interface.exception.PartialInterfaceUsageException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
            if (!annotationClassInfo.isInterface()) {
                throw new PartialInterfaceUsageException(
                        "attempt to use @PartialInterface on non-interface" + annotationClassInfo.getName()
                );
            }
            // note: includes extending interfaces, abstract classes, and concrete classes
            ClassInfoList implementations = annotationClassInfo.getClassesImplementing();
            List<RequiresChildMethod> requiresChildMethodAnnotations = annotationClassInfo
                    .getAnnotationInfoRepeatable(RequiresChildMethod.class)
                    .stream()
                    .map(AnnotationInfo::loadClassAndInstantiate)
                    .map(RequiresChildMethod.class::cast)
                    .collect(Collectors.toList());
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
                        requiresChildMethodAnnotations
                );
            }
        }
    }

    private static void validateImplementation(
            Class<?> implementation,
            List<RequiresChildMethod> requiresChildMethodAnnotations
    ) {
        HasTypeParameter[] hasTypeParameters = implementation.getAnnotationsByType(HasTypeParameter.class);
        Map<String, Class<?>> scalarTypeParameterMap = Arrays.stream(hasTypeParameters)
                .collect(Collectors.toMap(HasTypeParameter::name, HasTypeParameter::value));
        TypeNameResolver typeNameResolver = new TypeNameResolver(scalarTypeParameterMap);
        Method[] methods = implementation.getMethods();
        for (RequiresChildMethod requiresChildMethod : requiresChildMethodAnnotations) {
            TypeValidator typeValidator = new TypeValidator(typeNameResolver);
            List<Method> matchingMethods = Arrays.stream(methods)
                    .filter(m -> m.getName().equals(requiresChildMethod.methodName()))
                    .filter(m -> typeValidator.isAssignableType(m.getReturnType(), requiresChildMethod.returnType()))
                    .filter(m -> typeValidator.hasAssignableArgumentTypes(m, requiresChildMethod.argumentTypes()))
                    .collect(Collectors.toList());
            if (matchingMethods.isEmpty()) {
                String message = "implementation " + implementation.getName()
                        + " does not implement partial interface method: "
                        + RequiresChildMethod.Util.stringify(requiresChildMethod);
                if (!typeNameResolver.isEmpty()) {
                    message += " with type parameters: " + typeNameResolver; // TODO: is this readable?
                }
                throw new PartialInterfaceNotCompletedException(message);
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

