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
}
