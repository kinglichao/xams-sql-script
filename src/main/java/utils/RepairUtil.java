package utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wlc
 * @date 2020-04-23
 */
public class RepairUtil {

    private static final Pattern patternOfProcedure = Pattern.compile("(?i)(?s)create or replace procedure(.*?)end;");
    private static final Pattern patternOfFunction = Pattern.compile("(?i)(?s)create or replace function(.*?)end;");
    private static final Pattern patternOfTrigger = Pattern.compile("(?i)(?s)create or replace trigger(.*?)end;");

    public static void repaireAllFile(File file, String encode) throws IOException {
        if (file.isFile()) {
            repairFileContent(file, encode);
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                repaireAllFile(f, encode);
            }
        }
    }

    /**
     * 修复文件：注释，终结符
     */
    public static void repairFileContent(File file, String encode) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        String content = IOUtils.toString(fis, encode);
        if (fis != null) { fis.close(); }

        content = deleteAnno(content);
        content = addTerminalOfProcedure(content);
        content = addTerminalOfFunction(content);
        content = addTerminalOfTrigger(content);
        content = addTerminalOfEnd(content);


        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), encode);

        osw.write(content);
        if (osw != null) { osw.close(); }
    }

    /**
     * 添加存储过程的终结符/
     */
    public static String addTerminalOfProcedure(String before) {

        before = before + "   ";

        Matcher matcher1 = patternOfProcedure.matcher(before);

        if (!matcher1.find()) {
            return before;
        }

        Matcher matcher2 = patternOfProcedure.matcher(before);
        ArrayList<String> strs = new ArrayList<>();
        while (matcher2.find()) {
            char c = before.charAt(matcher2.end() + 2);
            if (c != '/') {
                strs.add(before.substring(0, matcher2.end()));
                strs.add(before.substring(matcher2.end()));
            }
        }
        if (strs.size() == 2) {
            StringBuilder sb = new StringBuilder();
            sb.append(strs.get(0)).append("\n/").append(strs.get(1));
            return sb.toString();
        } else {
            return before;
        }

    }

    /**
     * 添加存储函数的终结符/
     */
    public static String addTerminalOfFunction(String before) {

        before = before + "   ";

        Matcher matcher1 = patternOfFunction.matcher(before);

        if (!matcher1.find()) {
            return before;
        }

        Matcher matcher2 = patternOfFunction.matcher(before);
        ArrayList<String> strs = new ArrayList<>();
        while (matcher2.find()) {
            char c = before.charAt(matcher2.end() + 2);
            if (c != '/') {
                strs.add(before.substring(0, matcher2.end()));
                strs.add(before.substring(matcher2.end()));
            }
        }
        if (strs.size() == 2) {
            StringBuilder sb = new StringBuilder();
            sb.append(strs.get(0)).append("\n/").append(strs.get(1));
            return sb.toString();
        } else {
            return before;
        }

    }

    /**
     * 添加触发器的终结符/
     */
    public static String addTerminalOfTrigger(String before) {

        before = before + "   ";

        Matcher matcher1 = patternOfTrigger.matcher(before);

        if (!matcher1.find()) {
            return before;
        }

        Matcher matcher2 = patternOfTrigger.matcher(before);
        ArrayList<String> strs = new ArrayList<>();
        while (matcher2.find()) {
            char c = before.charAt(matcher2.end() + 2);
            if (c != '/') {
                strs.add(before.substring(0, matcher2.end()));
                strs.add(before.substring(matcher2.end()));
            }
        }
        if (strs.size() == 2) {
            StringBuilder sb = new StringBuilder();
            sb.append(strs.get(0)).append("\n/").append(strs.get(1));
            return sb.toString();
        } else {
            return before;
        }

    }

    /**
     * 添加正常的终结符;
     */
    public static String addTerminalOfEnd(String before) {
        String trim = before.trim();
        if (trim.endsWith("/") || trim.endsWith(";")) {
            return before;
        } else {
            return before.trim() + ";";
        }
    }

    private static String deleteAnno(String content) {
        if (content.contains("/*")) {
            if (content.contains("/*+parallel")) {
                content = StringUtils.replace(content, "/*+parallel(8)*/", "+parallel(8)");
                content = replaceAnno(content);
                content = StringUtils.replace(content, "+parallel(8)", "/*+parallel(8)*/");
            } else {
                content = replaceAnno(content);
            }
        }
        return content;
    }

    /**
     * 替换/*型注释
     */
    private static String replaceAnno(String before) {
        String regex = "(?s)\\/\\*(.*?)\\*\\/";
        String after = before.replaceAll(regex, "    ");
        return after;
    }

}
