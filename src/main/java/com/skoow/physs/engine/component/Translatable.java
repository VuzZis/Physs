package com.skoow.physs.engine.component;

import com.skoow.physs.gson.GsonUtil;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;

public class Translatable {
    public static HashMap<String,String> LANG;
    public static HashMap<String,HashMap<String,String>> LANGS = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static void loadLangs() {
        try {
            InputStream langsStream = Translatable.class.getClassLoader().getResourceAsStream("lang");
            if (langsStream == null) {
                throw new FileNotFoundException("Resource 'lang' not found");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(langsStream));
            String[] langFiles = reader.lines().toArray(String[]::new);
            for (String langFileName : langFiles) {
                if (langFileName.endsWith(".json")) {
                    String langName = langFileName.substring(0, langFileName.length() - 5);
                    InputStream langFileStream = Translatable.class.getClassLoader().getResourceAsStream("lang/" + langFileName);
                    InputStreamReader langFileReader = new InputStreamReader(langFileStream, StandardCharsets.UTF_8);
                    HashMap<String, String> lang = GsonUtil.GSON.fromJson(langFileReader, HashMap.class);
                    LANGS.put(langName, lang);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setLang("en_us");
    }

    public static void setLang(String lang) {
        LANG = LANGS.getOrDefault(lang,LANGS.get("en_us"));
    }
    public static String get(String path) {
        return LANG.getOrDefault(path,LANGS.get("en_us").getOrDefault(path,path));
    }
    public static String getf(String path, String... args) {
        return String.format(get(path), (Object[]) args);
    }
}
