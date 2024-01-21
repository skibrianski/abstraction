package io.github.skibrianski.partial_interface;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import io.github.skibrianski.partial_interface.exception.PartialInterfaceException;
import io.github.skibrianski.partial_interface.exception.PartialInterfaceNotCompletedException;
import io.github.skibrianski.partial_interface.exception.PartialInterfaceUsageException;
import io.github.skibrianski.partial_interface.util.StringTruncator;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
        Map<String, Class<?>> typeParameterMap = Arrays.stream(hasTypeParameters)
                .collect(Collectors.toMap(HasTypeParameter::name, HasTypeParameter::value));
        Method[] methods = implementation.getMethods();
        for (RequiresChildMethod requiresChildMethod : requiresChildMethodAnnotations) {
            List<Method> matchingMethods = Arrays.stream(methods)
                    .filter(m -> m.getName().equals(requiresChildMethod.methodName()))
                    .filter(m -> validateType(m.getReturnType(), requiresChildMethod.returnType(), typeParameterMap))
                    .filter(m ->
                        validateArgumentTypes(m, requiresChildMethod.argumentTypes(), typeParameterMap)
                    )
                    .collect(Collectors.toList());
            if (matchingMethods.isEmpty()) {
                String message = "implementation " + implementation.getName()
                        + " does not implement partial interface method: "
                        + RequiresChildMethod.Util.stringify(requiresChildMethod);
                if (!typeParameterMap.isEmpty()) {
                    message += " with type parameters: " + typeParameterMap; // TODO: is this readable?
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

    // TODO: change signature to void and throw exception on mismatch so we can provide more detailed messages
    private static boolean validateArgumentTypes(
            Method implementedMethod,
            Type[] requiredParameterTypes,
            Map<String, Class<?>> typeParameterMap
    ) {
        Class<?>[] parameterTypes = implementedMethod.getParameterTypes();
        if (requiredParameterTypes.length != parameterTypes.length) {
            return false;
        }
        for (int pos = 0; pos < requiredParameterTypes.length; pos++) {
            boolean argumentOk = validateType(
                    implementedMethod.getParameterTypes()[pos],
                    requiredParameterTypes[pos],
                    typeParameterMap
            );
            if (!argumentOk) {
                return false;
            }
        }
        return true;
    }

    // TODO: change signature to void and throw exception on mismatch so we can provide more detailed messages
    private static boolean validateType(
            Class<?> implementedType,
            Type type,
            Map<String, Class<?>> typeParameterMap
    ) {
        if (Type.TypeParameter.class.equals(type.value())) {

            StringTruncator parameterNameTruncator = new StringTruncator(type.parameterName())
                    .truncateOnce("...")
                    .truncateAll("[]");
            String baseParameterName = parameterNameTruncator.value();
            int arrayLevels = parameterNameTruncator.truncationCount();

            Class<?> baseType = typeParameterMap.get(baseParameterName);
            if (baseType == null) {
                if (baseParameterName.equals(type.parameterName())) {
                    throw new PartialInterfaceUsageException(
                            "cannot find type parameter: " + type.parameterName() // TODO: more detail
                    );
                } else {
                    throw new PartialInterfaceUsageException(
                            "cannot find base type parameter: " + baseParameterName // TODO: more detail
                                    + " for parameter: " + type.parameterName()
                    );
                }
            }

            Class<?> actualType = baseType;
            while (arrayLevels > 0) {
                actualType = Array.newInstance(actualType, 0).getClass();
                arrayLevels--;
            }
            return actualType.isAssignableFrom(implementedType);
        } else {
            return type.value().isAssignableFrom(implementedType);
        }
    }
}

