package com.nan.funxtion.types.functional;

@FunctionalInterface
public interface CheckedRunnable {

    void run() throws Throwable;
}
