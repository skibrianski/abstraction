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

public final class Checker {

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
        for (ClassInfo partialInterfaceClassInfo : scanResult.getClassesWithAnnotation(PartialInterface.class)) {
            if (!partialInterfaceClassInfo.isInterface()) {
                throw new PartialInterfaceUsageException(
                        "attempt to use @PartialInterface on non-interface" + partialInterfaceClassInfo.getName()
                );
            }
            // note: includes extending interfaces, abstract classes, and concrete classes
            ClassInfoList implementations = partialInterfaceClassInfo.getClassesImplementing();
            List<PartialInterface> partialInterfaces = partialInterfaceClassInfo
                    .getAnnotationInfoRepeatable(PartialInterface.class)
                    .stream()
                    .map(AnnotationInfo::loadClassAndInstantiate)
                    .map(PartialInterface.class::cast)
                    .collect(Collectors.toList());
            for (ClassInfo implementationClassInfo : implementations) {
                Class<?> implementation = implementationClassInfo.loadClass();
                Method[] methods = implementation.getMethods();
                for (PartialInterface partialInterface : partialInterfaces) {
                    List<Method> matchingMethods = Arrays.stream(methods)
                            .filter(m -> m.getName().equals(partialInterface.methodName()))
                            .filter(m -> m.getReturnType().equals(partialInterface.returnType()))
                            .filter(m -> Arrays.equals(m.getParameterTypes(), partialInterface.argumentTypes()))
                            .collect(Collectors.toList());
                    if (matchingMethods.isEmpty()) {
                        throw new PartialInterfaceNotCompletedException(
                                "implementation " + implementation.getName()
                                        + " does not implement partial interface method: "
                                        + PartialInterface.Util.stringify(partialInterface)
                        );
                    }
                    if (matchingMethods.size() > 1) {
                        throw new PartialInterfaceException(
                                "bug: internal error: implementation " + implementation.getName()
                                        + " implements more than one matching interface method matching: "
                                        + PartialInterface.Util.stringify(partialInterface)
                                        + ". please report this error."
                        );
                    }
                }
            }
        }
    }
}

