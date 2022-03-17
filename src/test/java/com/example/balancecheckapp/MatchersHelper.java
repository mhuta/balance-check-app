package com.example.balancecheckapp;

import org.mockito.internal.matchers.Contains;

public class MatchersHelper {

    public static Contains contains(String string) {
        return new Contains(string);
    }

}