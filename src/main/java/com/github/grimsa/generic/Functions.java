package com.github.grimsa.generic;

import java.util.function.Consumer;
import java.util.function.Function;

public final class Functions {
    public static <T, E extends Exception> Consumer<T> uncheckedConsumer(ThrowingConsumer<T, E> throwingConsumer) {
        return arg -> {
            try {
                throwingConsumer.accept(arg);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static <T, R, E extends Exception> Function<T, R> uncheckedFunction(ThrowingFunction<T, R, E> throwingFunction) {
        return arg -> {
            try {
                return throwingFunction.apply(arg);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    public interface ThrowingConsumer<T, E extends Exception> {
        void accept(T t) throws E;
    }

    public interface ThrowingFunction<T, R, E extends Exception> {
        R apply(T t) throws E;
    }

    private Functions() {
    }
}
