package ru.tki.helpers;

import com.google.gson.Gson;

import java.io.*;

public class FileHelper {

    public static void writeToFile(File file, String content) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> T readFromFile(File file, Class<T> classOfT) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            return new Gson().fromJson(br, classOfT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
