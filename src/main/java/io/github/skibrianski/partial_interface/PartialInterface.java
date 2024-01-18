package io.github.skibrianski.partial_interface;

import java.lang.annotation.Repeatable;

// TODO: should this be a repeatable class-level annotation or should we do method-level?
// method-level would be cleaner, but it creates a method to override (and what if we want to vary on return type?)
// for now, we'll go with class-level.
// TODO: support primitive-friendly "parameterized" types
// TODO: can we also support static methods?
@Repeatable(PartialInterfaces.class)
public @interface PartialInterface {
    Class<?> returnType();
    Class<?>[] argumentTypes();
    String methodName();
    boolean isStatic() default false;

//
//    interface Stack1<E> {
//        boolean isEmpty();
//        int size();
//
//        void add()
//
////         *   public final B add(E... elementsToAdd);
//// *   public ImmutableArrayList<E> build();
//    }

}

