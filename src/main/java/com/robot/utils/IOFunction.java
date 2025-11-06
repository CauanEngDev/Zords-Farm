package com.robot.utils;

public class IOFunction {
    public static void print(String prompt){
        System.out.print(prompt);
    }

    public static void println(String prompt){
        System.out.println(prompt);
    }

    public static void printf(String prompt, Object ... args){
        System.out.printf(prompt, args);
    }
}
