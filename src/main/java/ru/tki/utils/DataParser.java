package ru.tki.utils;

public class DataParser {

    public static int parseResource(String value){
        if (value.contains("М")) {
            Double d = Double.parseDouble(value.replace("М", "")) * 1000 * 1000;
            return d.intValue();
        }
        return Integer.parseInt(value.replace(".", ""));
    }
}
