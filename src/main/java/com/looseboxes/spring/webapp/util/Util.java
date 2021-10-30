package com.looseboxes.spring.webapp.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author hp
 */
public final class Util {

    public static <T> List<T> getDeclaredStaticFields(Class clazz, Function<Field, T> converter) throws IllegalArgumentException {
        Field [] fields = clazz.getDeclaredFields();
        List<T> result = new ArrayList<>(fields.length);
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                result.add(converter.apply(field));
            }
        }
        return result.isEmpty() ? Collections.EMPTY_LIST : Collections.unmodifiableList(result);
    }

    public final static <E> List<E> asList(E [] array) {
        final List<E> output;
            switch (array.length) {
                case 0:
                    output = Collections.EMPTY_LIST;
                    break;
                case 1:
                    output = Collections.singletonList(array[0]);
                    break;
                default:
                    output = Arrays.asList(array);
                    break;
        }
        return output;
    }

    public final static <E> List<E> unmodifiableList(Collection<E> collection) {
        final List<E> output;
        if(collection.isEmpty()) {
            output = Collections.EMPTY_LIST;
        }else if(collection.size() == 1) {
            output = Collections.singletonList(collection.iterator().next());
        }else{
            final List list = collection instanceof List ? (List)collection : new ArrayList(collection);
            output = Collections.unmodifiableList(list);
        }
      return output;
    }

    public final static long availableMemory() {
        final Runtime runtime = Runtime.getRuntime();
        final long max = runtime.maxMemory(); // Max heap VM can use e.g. Xmx setting
        final long availableHeapMemory = max - _usedMemory(runtime); // available memory i.e. Maximum heap size minus the current amount used
        return availableHeapMemory;
    }

    public final static long usedMemory() {
        return _usedMemory(Runtime.getRuntime());
    }

    private static long _usedMemory(Runtime runtime) {
        final long total = runtime.totalMemory(); // current heap allocated to the VM process
        final long free = runtime.freeMemory(); // out of the current heap, how much is free
        final long used = total - free; // how much of the current heap the VM is using
        return used;
    }

    public final static long usedMemory(long bookmarkMemory) {
        return bookmarkMemory - availableMemory();
    }

    /**
     * Convert a {@link java.time.temporal.ChronoUnit ChronoUnit} to a {@link java.util.concurrent.TimeUnit TimeUnit}.
     *
     * Java 9 has method {@code TimeUnit.toChronoUnit()} as well as method {@code TimeUnit.of(ChronoUnit chronoUnit)}
     * @param chronoUnit The {@link java.time.temporal.ChronoUnit ChronoUnit} to convert to a {@link java.util.concurrent.TimeUnit TimeUnit}.
     * @return The {@link java.util.concurrent.TimeUnit TimeUnit} equivalent of the specified {@link java.time.temporal.ChronoUnit ChronoUnit}
     */
    public static TimeUnit toTimeUnit(ChronoUnit chronoUnit) {
        if (chronoUnit == null) {
            return null;
        }
        switch (chronoUnit) {
            case DAYS:
                return TimeUnit.DAYS;
            case HOURS:
                return TimeUnit.HOURS;
            case MINUTES:
                return TimeUnit.MINUTES;
            case SECONDS:
                return TimeUnit.SECONDS;
            case MICROS:
                return TimeUnit.MICROSECONDS;
            case MILLIS:
                return TimeUnit.MILLISECONDS;
            case NANOS:
                return TimeUnit.NANOSECONDS;
            default:
                //TODO support the rest
                throw new UnsupportedOperationException("Man, use a real temporal unit");
        }
    }

    /**
     * Convert a {@link java.util.concurrent.TimeUnit TimeUnit} to a {@link java.time.temporal.ChronoUnit ChronoUnit}.
     *
     * Java 9 has method {@code TimeUnit.toChronoUnit()} as well as method {@code TimeUnit.of(ChronoUnit chronoUnit)}
     * @param timeUnit The {@link java.util.concurrent.TimeUnit TimeUnit} to convert to {@link java.time.temporal.ChronoUnit ChronoUnit}
     * @return The {@link java.time.temporal.ChronoUnit ChronoUnit} equivalent of the specified {@link java.util.concurrent.TimeUnit TimeUnit}
     */
    public static ChronoUnit toChronoUnit(TimeUnit timeUnit) {
        if (timeUnit == null) {
            return null;
        }
        switch (timeUnit) {
            case DAYS:
                return ChronoUnit.DAYS;
            case HOURS:
                return ChronoUnit.HOURS;
            case MINUTES:
                return ChronoUnit.MINUTES;
            case SECONDS:
                return ChronoUnit.SECONDS;
            case MICROSECONDS:
                return ChronoUnit.MICROS;
            case MILLISECONDS:
                return ChronoUnit.MILLIS;
            case NANOSECONDS:
                return ChronoUnit.NANOS;
            default:
                assert false : "There are no other TimeUnit ordinal values";
                return null;
        }
    }}
