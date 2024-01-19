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
import java.util.stream.Collectors;

public final class PartialInterface {

    // TODD: do we need enableAllInfo()?

    public static void check(Class... classes) {
        String[] classNames = Arrays.stream(classes).map(Class::getName).toArray(String[]::new);
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().acceptClasses(classNames).scan()) {
            check(scanResult);
        }
    }

    public static void check(Package... packages) {
        String[] packageNames = Arrays.stream(packages).map(Package::getName).toArray(String[]::new);
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(packageNames).scan()) {
            check(scanResult);
        }
    }

    private static void check(ScanResult scanResult) {
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
                Class<?> implementation = implementationClassInfo.loadClass();
                Method[] methods = implementation.getMethods();
                HasTypeParameters hasTypeParameters = implementation.getAnnotation(HasTypeParameters.class);
                Class<?>[] typeParameters = hasTypeParameters == null
                        ? new Class<?>[0]
                        : hasTypeParameters.value();
                for (RequiresChildMethod requiresChildMethod : requiresChildMethodAnnotations) {
                    List<Method> matchingMethods = Arrays.stream(methods)
                            .filter(m -> m.getName().equals(requiresChildMethod.methodName()))
                            .filter(m -> {
                                switch (requiresChildMethod.returnType().type()) {
                                    case REGULAR:
                                        return m.getReturnType().equals(requiresChildMethod.returnType().value());
                                    case PARAMETERIZED:
                                        if (requiresChildMethod.returnType().value().equals(RequiresChildMethod.FirstParameter.class)) {
                                            if (typeParameters.length == 0) {
                                                throw new PartialInterfaceUsageException(
                                                        "no type parameter. missing @HasTypeParameters?" // TODO: more detail
                                                );
                                            }
                                            return m.getReturnType().equals(typeParameters[0]);
                                        }
                                }
                                throw new RuntimeException("unimplemented");
                            })
                            .filter(m -> Arrays.equals(m.getParameterTypes(), requiresChildMethod.argumentTypes()))
                            .collect(Collectors.toList());
                    if (matchingMethods.isEmpty()) {
                        throw new PartialInterfaceNotCompletedException(
                                "implementation " + implementation.getName()
                                        + " does not implement partial interface method: "
                                        + RequiresChildMethod.Util.stringify(requiresChildMethod)
                        );
                    }
                    if (matchingMethods.size() > 1) {
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
    }
}

