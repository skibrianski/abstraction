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
    Class<?> ofClass() default None.class;
    String ofString() default "";
    class None { }

    class Util {
        // TODO: move
        public static java.lang.reflect.Type lookup(HasTypeParameter hasTypeParameter, TypeNameResolver typeNameResolver) {
            boolean hasClass = !hasTypeParameter.ofClass().equals(HasTypeParameter.None.class);
            boolean hasString = !hasTypeParameter.ofString().isEmpty();
            if (hasClass && hasString) {
                throw new PartialInterfaceException.UsageException(
                        "cannot specify both ofClass and ofString for: " + hasTypeParameter
                );
            }
            if (hasClass) {
                return hasTypeParameter.ofClass();
            } else {
                // TODO: shouldn't  have to churn a tpp object every time. we also do this in
                // TypeValidator::convertFromAnnotation
                TypeParameterParser typeParameterParser = new TypeParameterParser(typeNameResolver);
                return typeParameterParser.parse(hasTypeParameter.ofString());
            }
        }
    }
}

