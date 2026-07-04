package com.nan.funxtion.types.functional;

@FunctionalInterface
public interface CheckedSupplier<T> {

    T get() throws Throwable;
}
