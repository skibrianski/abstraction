package io.github.skibrianski.abstraction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class TypeReferenceToAnotherTypeReferenceAsWildcardExtendsMultipleTypeParameterTest {

    interface Foo<T extends Number, U extends Collection<? extends T>, V extends Map<? extends T, ? extends U>> {
        void method(Map<? extends T, ? extends U> map);
    }

    public static class FooImpl implements Foo<Integer, List<Integer>, Map<Integer, List<Integer>>> {

        @Override
        public void method(Map<? extends Integer, ? extends List<Integer>> map) {

        }
    }

    @RequiresTypeParameter(value = "T", extending = "Number")
    @RequiresTypeParameter(value = "U", extending = "Collection<? extends T>")
    @RequiresTypeParameter(value = "V", extending = "Map<? extends T, ? extends U>")
    @RequiresChildMethod(
            returnType = @Type(ofClass = void.class),
            argumentTypes = {@Type("V")},
            methodName = "method"
    )
    interface WithMethod { }


// TypeParameterViolatesBoundsException:
// TypeReferenceToAnotherTypeReferenceAsWildcardExtendsMultipleTypeParameterTest$ValidSubclass
// does not extend: ParameterizedTypeImpl{
//   rawType=interface java.util.Map,
//   actualTypeArguments=[
//     WildcardTypeImpl{
//       lowerBounds=[],
//       upperBounds=[
//         class java.lang.Integer,
//         WildcardTypeImpl{
//           lowerBounds=[],
//           upperBounds=[
//             ParameterizedTypeImpl{
//               rawType=interface java.util.List,
//               actualTypeArguments=[class java.lang.Integer]
// }]}]}]} with implemented type; ParameterizedTypeImpl{
//   rawType=interface java.util.Map,
//   actualTypeArguments=[
//     class java.lang.Integer,
//     ParameterizedTypeImpl{
//       rawType=interface java.util.List,
//       actualTypeArguments=[class java.lang.Integer]
// }]}

    @ManualValidation
    @HasTypeParameter(name = "T", ofClass = Integer.class)
    @HasTypeParameter(name = "U", ofString = "List<Integer>")
    @HasTypeParameter(name = "V", ofString = "Map<Integer, List<Integer>>")
    static class ValidSubclass implements WithMethod {
        public void method(Map<Integer, List<Integer>> map) { }
    }
    @Test
    void test_valid_happyPath() {
        Assertions.assertDoesNotThrow(() -> Abstraction.check(ValidSubclass.class));
    }

//    @ManualValidation
//    @HasTypeParameter(name = "T", ofClass = Number.class)
//    @HasTypeParameter(name = "U", ofString = "List<String>")
//    static class InvalidWrongTypeParameter implements WithMethod {
//        public void method(Number t, List<String> u) { }
//    }
//    @Test
//    void test_invalid_wrongScalarType() {
//        Assertions.assertThrows(
//                AbstractionException.TypeParameterViolatesBoundsException.class,
//                () -> Abstraction.check(InvalidWrongTypeParameter.class)
//        );
//    }
//
//    @ManualValidation
//    @HasTypeParameter(name = "T", ofClass = Number.class)
//    @HasTypeParameter(name = "U", ofString = "AtomicReference<Integer>")
//    static class InvalidWrongRawType implements WithMethod {
//        public void method(Number t, AtomicReference<Integer> u) { }
//    }
//    @Test
//    void test_invalid_wrongRawType() {
//        Assertions.assertThrows(
//                AbstractionException.TypeParameterViolatesBoundsException.class,
//                () -> Abstraction.check(InvalidWrongRawType.class)
//        );
//    }

}