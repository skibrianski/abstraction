## description

allows for constraints on child classes, abstract classes, and interfaces; much like an interface itself.
but additionally allows for some tricks that interfaces cannot provide:
  - primitive-friendly parameterization
    - when T = int.class, implementing `T sum(List<T> list)` is fulfilled via `int sum(List<Integer> list)`
  - TODO: interfaces that require static methods
  - TODO: non-public methods in interface (bad idea?)
  - TODO: constructor constraints?

## caveats

- the parent class gives java no indication that these child classes have the methods specified. this means the
  common practice of storing variables to an abstract type will cause problems - in the above example, you would
  not be able to call sum() without interrogating the type and casting it. generally, the best practice here is to
  store generated types with the most concrete type possible to avoid this issue. often, this is not ideal.
- refactoring cannot be done automatically in an IDE
- if classes are not found, use full class paths to load. there are limitations to what short class names
  can be loaded automatically.

## todo

- rename. maybe @Trait (what rust calls it) or just @Abstraction ?
- do we need a solution for loading all child classes of the classes *used* by the abstraction?
- do wildcard types and boxing interact negatively? are there other edge cases with boxing?
- test for / support type constraints that reference other types, eg Foo<T extends Number, U extends T>
- think: how precise do we want to be about boxing? when T = int.class, should you be able to implement
  `T sum(List<T> list)` with `Integer sum(List<T> list)` ? or only with return type `int`? what is the
  current behavior?

## questions

- alternative to boxing support of eg Stream<T>
  - could be supported with some alternative, eg Stream<T> for int would be an IntStream

## design notes

- use class annotations, not method annotations, b/c of potential return type confusion
- use classgraph instead of ctor checks b/c an interface cannot run code at init or clinit time.
