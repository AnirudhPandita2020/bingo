package com.anirudh.bingo.utils;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.DisplayNameGenerator;

import java.lang.reflect.Method;
import java.util.List;

public final class CamelCaseDisplayNameGenerator extends DisplayNameGenerator.Standard {

    private static String splitCamelCase(String methodName) {
        char[] charArray = methodName.toCharArray();
        charArray[0] = Character.toUpperCase(charArray[0]);
        methodName = String.valueOf(charArray);
        return methodName.replaceAll("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])", " ");
    }

    @Override
    @NonNull
    public String generateDisplayNameForMethod(@NonNull List<Class<?>> enclosingInstanceTypes, @NonNull Class<?> testClass, Method testMethod) {
        return splitCamelCase(testMethod.getName());
    }
}
