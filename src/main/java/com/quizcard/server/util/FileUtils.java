package com.quizcard.server.util;

public class FileUtils {

    public static String getExtension(String fileName) {
        int pos = fileName.lastIndexOf('.');
        return pos == -1 ? "" : fileName.substring(pos + 1).toLowerCase();
    }
}
