package io.github.skibrianski.partial_interface;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Currency;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.Stack;
import java.util.TimeZone;
import java.util.UUID;
import java.util.Vector;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class BuiltInTypes extends TypeNameResolver {

    private static Stream<Class<?>> builtInClasses() {
        return Stream.of(
                // primitives
                boolean.class,
                byte.class,
                char.class,
                double.class,
                float.class,
                int.class,
                long.class,
                short.class,

                // boxed primitives
                Boolean.class,
                Byte.class,
                Character.class,
                Double.class,
                Float.class,
                Integer.class,
                Long.class,
                Short.class,

                // java.lang
                Annotation.class,
                Comparable.class,
                Enum.class,
                Error.class,
                Exception.class,
                CharSequence.class,
                Iterable.class,
                Number.class,
                RuntimeException.class,
                String.class,
                StringBuilder.class,
                Void.class,
                void.class,

                // java.math
                BigDecimal.class,
                BigInteger.class,

                // java.time
                Clock.class,
                DayOfWeek.class,
                Duration.class,
                Instant.class,
                LocalDate.class,
                LocalDateTime.class,
                LocalTime.class,
                Month.class,
                MonthDay.class,
                OffsetDateTime.class,
                OffsetTime.class,
                Period.class,
                Year.class,
                YearMonth.class,
                ZoneId.class,
                ZoneOffset.class,
                ZonedDateTime.class,

                // java.util
                BitSet.class,
                Collection.class,
                Comparator.class,
                Currency.class,
                Deque.class,
                Iterator.class,
                List.class,
                Locale.class,
                Map.class,
                Map.Entry.class,
                Optional.class,
                PriorityQueue.class,
                Queue.class,
                Set.class,
                SortedMap.class,
                SortedSet.class,
                Spliterator.class,
                Stack.class,
                TimeZone.class,
                UUID.class,
                Vector.class,

                // java.util.function
                BiConsumer.class,
                BiFunction.class,
                BiPredicate.class,
                BinaryOperator.class,
                Consumer.class,
                Function.class,
                Predicate.class,
                Supplier.class,
                UnaryOperator.class,

                // java.util.regex
                Matcher.class,
                Pattern.class,

                // java.util.stream
                Collector.class,
                DoubleStream.class,
                IntStream.class,
                LongStream.class,
                Stream.class
        );
    }

    public static Map<String, java.lang.reflect.Type> builtInClassMap() {
        return builtInClasses().collect(
                Collectors.toMap(
                        Class::getSimpleName,
                        java.lang.reflect.Type.class::cast
                )
        );
    }
}

