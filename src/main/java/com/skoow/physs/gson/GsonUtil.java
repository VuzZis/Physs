package com.skoow.physs.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class GsonUtil {
    public static final Gson GSON = new GsonBuilder()
            .create();
    public static final Gson GSON_PRETTY = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    public static InputStreamReader createInput(File f) throws FileNotFoundException
    { return new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8); }

    public static OutputStreamWriter createOutput(File f) throws FileNotFoundException
    { return new OutputStreamWriter(new FileOutputStream(f),StandardCharsets.UTF_8); }
    public static <T> T jsonToJava(File f, Class<T> classOf) throws InstantiationException, IllegalAccessException {
        try {
            f.createNewFile();
            InputStreamReader reader = createInput(f);
            T data = GSON_PRETTY.fromJson(reader,classOf);
            if(data == null) data = classOf.newInstance();
            javaToJson(f,data);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return classOf.newInstance();
        }
    }

    public static void javaToJson(File f, Object data) {
        try {
            f.createNewFile();
            OutputStreamWriter writer = createOutput(f);
            GSON_PRETTY.toJson(data,writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
