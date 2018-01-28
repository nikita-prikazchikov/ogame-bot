package ru.tki.utils;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataParser {

    public static int parseResource(String value){
        if(value == null || value.isEmpty()){
            return 0;
        }
        value = value.trim();
        if (value.contains("М")) {
            Double d = Double.parseDouble(value.replace("М", "")) * 1000 * 1000;
            return d.intValue();
        }
        return Integer.parseInt(value.replace(".", ""));
    }

    public static Duration parseDuration(String text){
        Duration d = Duration.ZERO;

        Matcher m = Pattern.compile("(\\d+)с").matcher(text);
        if(m.find()){
            d = d.plusSeconds(Integer.parseInt(m.group(1)));
        }

        m = Pattern.compile("(\\d+)м").matcher(text);
        if(m.find()){
            d = d.plusMinutes(Integer.parseInt(m.group(1)));
        }

        m = Pattern.compile("(\\d+)ч").matcher(text);
        if(m.find()){
            d = d.plusHours(Integer.parseInt(m.group(1)));
        }

        m = Pattern.compile("(\\d+)д").matcher(text);
        if(m.find()){
            d = d.plusDays(Integer.parseInt(m.group(1)));
        }

        m = Pattern.compile("(\\d+)нед").matcher(text);
        if(m.find()){
            d = d.plusDays(Integer.parseInt(m.group(1)) * 7);
        }
        return d;
    }
}
