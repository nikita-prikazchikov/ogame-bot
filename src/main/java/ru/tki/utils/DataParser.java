package ru.tki.utils;

public class DataParser {

    public static int parseResource(String value){
        return Integer.parseInt(value.replace(".", ""));
    }
}
