package io.github.skibrianski.partial_interface;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Repeatable(HasTypeParameters.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface HasTypeParameter {
    String name();
    // TODO: validation - must have either ofClass != None.class, or non-blank ofString(), but not both
    Class<?> ofClass() default None.class;
    String ofString() default "";
    class None { }

    class Util {
        public static java.lang.reflect.Type asType(
                HasTypeParameter hasTypeParameter,
                TypeNameResolver typeNameResolver
        ) {
            boolean hasClass = !hasTypeParameter.ofClass().equals(None.class);
            boolean hasString = !hasTypeParameter.ofString().isEmpty();
            if (hasClass && hasString) {
                throw new PartialInterfaceException.UsageException(
                        "cannot specify both ofClass and ofString for: " + hasTypeParameter
                );
            }
            if (hasClass) {
                return hasTypeParameter.ofClass();
            } else {
                return typeNameResolver.mustResolve(hasTypeParameter.ofString());
            }
        }
    }
}

