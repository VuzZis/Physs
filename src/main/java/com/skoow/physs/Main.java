package com.skoow.physs;

import com.skoow.physs.engine.Context;
import com.skoow.physs.engine.component.Translatable;
import com.skoow.physs.error.PhyssReporter;
import com.skoow.physs.util.TextUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public class Main {

    private static String[] args;

    public static void main(String[] args) {
        Translatable.loadLangs();
        String lang = Locale.getDefault().getLanguage().toLowerCase()+"_"+Locale.getDefault().getCountry().toLowerCase();
        Translatable.setLang(lang);
        PhyssReporter.reportDebug("Automatically set language to: "+(Translatable.LANGS.containsKey(lang) ? lang : "en_us"));
        if(args.length == 0) {helpCommand(args);return;};
        Main.args = args;
        executeCommand("help",Main::helpCommand);
        executeCommand("run",Main::runScriptCommand);
        executeCommand("gui",Main::runGuiCommand);
    }

    private static void executeCommand(String com, Consumer<String[]> helpCommand) {
        String command = args[0];
        if(Objects.equals(command, com)) helpCommand.accept(args);
    }


    private static void runGuiCommand(String[] args) {
        PhyssReporter.reportError("GUI Not supported yet!");
    }

    private static void runScriptCommand(String[] args) {
        if(args.length < 2) {
            PhyssReporter.reportError("Please specify file path/name.");
            return;
        }
        String fileName = args[1];
        if(!fileName.endsWith(".phy")) {
            PhyssReporter.reportError("File should be type of .phy!");
            return;
        }
        File file = new File(fileName);
        if(!file.exists()) {
            PhyssReporter.reportError("File hasn't been created.");
            return;
        }
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            String script = String.join("\n",reader.lines().toList());
            PhyssReporter.reportDebug("Evaluating "+file.getName());

            Context context = Context.beginWithNative();
            context.evaluateString(script,file.getName());
        } catch (FileNotFoundException ignored) {

        } catch (Exception e) {
            PhyssReporter.reportError(e.getMessage());
        }
    }

    private static void helpCommand(String[] args) {
        System.out.print(TextUtils.purple(String.format(Physs.WELCOME_ASCII,Physs.VERSION)));
        System.out.println(TextUtils.purple(TextUtils.dash(36)));
        outputCommandRow("help","","Outputs this command list");
        outputCommandRow("run","file","Runs Physs script");
        System.out.println(TextUtils.purple(TextUtils.dash(36)));
        outputLink("Source code","https://github.com/VuzZis/Physs");
    }

    private static void outputLink(String name, String link) {
        String str = "* "+TextUtils.red(name) + ": "+link;
        System.out.println(str);
    }

    private static void outputCommandRow(String name, String args, String desc) {
        String str = "* "+TextUtils.yellow(name) + "["+args+"] - " + TextUtils.grey(desc);
        System.out.println(str);
    }


}