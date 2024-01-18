package io.github.skibrianski.partial_interface;

import java.lang.annotation.Repeatable;

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

