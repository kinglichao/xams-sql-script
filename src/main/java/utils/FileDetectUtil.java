package utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件内容处理工具类
 *
 * @author wanglc
 * @date 2020-04-09
 */
public class FileDetectUtil {

    /**
     * 判断文件是否是给定日期内的
     */
    public static boolean isInPeriod(File file, int start, int end) {
        String filename = file.getName();
        if (isRepairable(filename) == 1) {
            int fileDate = Integer.parseInt(filename.substring(0, 8));
            if (fileDate >= start && fileDate <= end) {
                return true;
            }
        }
        return false;
    }


    /**
     * 修复文件内容:
     * 删除/*注释
     * 给存储过程, 存储函数加/;
     */
    public static void repairFileContent(File file) throws IOException {
        String charset = EncodeUtil.getEncode(file.getAbsolutePath(), true);
        FileInputStream fis = new FileInputStream(file);
        String content = IOUtils.toString(fis, charset);
        if (fis != null) {
            fis.close();
        }
        content = replaceAnno(content);
        content = addTerminalOfProcedure(content);
        content = addTerminalOfFunction(content);
        content = addTerminalOfTrigger(content);
        content = addTerminal2(content);
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(content);
        if (bw != null) {
            bw.close();
        }
    }

    /**
     * 添加存储过程的终结符/
     */
    public static String addTerminalOfProcedure(String before) {

        before = before + "   ";

        String regex = "(?i)(?s)create or replace procedure(.*?)end;";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher1 = pattern.matcher(before);

        if (!matcher1.find()) {
            return before;
        }

        Matcher matcher2 = pattern.matcher(before);
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

        String regex = "(?i)(?s)create or replace function(.*?)end;";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher1 = pattern.matcher(before);

        if (!matcher1.find()) {
            return before;
        }

        Matcher matcher2 = pattern.matcher(before);
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

        String regex = "(?i)(?s)create or replace trigger(.*?)end;";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher1 = pattern.matcher(before);

        if (!matcher1.find()) {
            return before;
        }

        Matcher matcher2 = pattern.matcher(before);
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
    public static String  addTerminal2(String before) {
        String trim = before.trim();
        if (trim.endsWith("/") || trim.endsWith(";")) {
            return before;
        } else {
            return before.trim() + ";";
        }
    }

    /**
     * 替换/*型注释
     */
    private static String replaceAnno(String before) {
        String regex = "(?s)\\/\\*(.*?)\\*\\/";
        String after = before.replaceAll(regex, "    ");
        return after;
    }

//    public static void removeBlank()

    public static void delBlank(File oldFile) {
        String name = oldFile.getName();
        if (name.contains(" ")) {
            String newName = StringUtils.replace(name, " ", "");
            oldFile.renameTo(new File(oldFile.getParentFile(), newName));
        }
    }


    /**
     * 修复文件名中的日期格式
     */
    public static void repairFileName(File oldFile) {

        String oldName = oldFile.getName();

        String reg1 = "^\\d{8}_.+"; // 20200401_
        if (oldName.matches(reg1)) {
            return;
        }

        String reg2 = "^\\d{4}_\\d{4}_.*"; // 2020_0401_
        String reg3 = "^\\d{6}_.+";  // 202041_
        String reg4 = "^\\d{4}_\\d{2}_\\d{2}_.+"; // 2020_04_02_
        String reg5 = "^\\d{4}_\\d_\\d_.+";  // 2020_4_1_
        String reg6 = "^\\d{4}_\\d{2}_\\d_.+";  // 2020_04_1_
        String reg7 = "^\\d{4}_\\d_\\d{2}_.+";  // 2020_4_01

        String newName = "";
        String year = oldName.substring(0, 4);
        String month = "";
        String day = "";

        if (oldName.matches(reg2)) {
            newName = year + oldName.substring(5);
        } else if (oldName.matches(reg3)) {
            month = "0" + oldName.charAt(4);
            day = "0" + oldName.charAt(5);
            newName = year + month + day + oldName.substring(6);
        } else if (oldName.matches(reg4)) {
            month = oldName.substring(5, 7);
            day = oldName.substring(8, 10);
            newName = year + month + day + oldName.substring(10);
        } else if (oldName.matches(reg5)) {
            month = "0" + oldName.substring(5, 6);
            day = "0" + oldName.substring(7, 8);
            newName = year + month + day + oldName.substring(8);
        } else if (oldName.matches(reg6)) {
            month = oldName.substring(5, 7);
            day = "0" + oldName.substring(8, 9);
            newName = year + month + day + oldName.substring(9);
        } else if (oldName.matches(reg7)) {
            month = "0" + oldName.substring(5, 6);
            day = oldName.substring(7, 9);
            newName = year + month + day + oldName.substring(9);
        } else {
            return;
        }

        File newFile = new File(oldFile.getParentFile(), newName);
        oldFile.renameTo(newFile);
    }

    /**
     * 文件名的日期格式是否可修复
     * 1：规范的
     * 2：不规范但可修复的
     * 0：不规范也不能修复的
     */
    public static int isRepairable(String filename) {
        // 规范的 20200401_
        String reg1 = "^\\d{8}_.+";

        // 不规范，但可以修复的
        String reg2 = "^\\d{4}_\\d{4}_.+";        // 2020_0401_
        String reg3 = "^\\d{6}_.+";               // 202041_
        String reg4 = "^\\d{4}_\\d{2}_\\d{2}_.+"; // 2020_04_02_
        String reg5 = "^\\d{4}_\\d_\\d_.+";       // 2020_4_1_
        String reg6 = "^\\d{4}_\\d{2}_\\d_.+";    // 2020_04_1_
        String reg7 = "^\\d{4}_\\d_\\d{2}_.+";    // 2020_4_01

        if (filename.matches(reg1)) {
            return 1;
        } else if (filename.matches(reg2) || filename.matches(reg3)
                || filename.matches(reg4) || filename.matches(reg5)
                || filename.matches(reg6) || filename.matches(reg7)) {
            return 2;
        } else {
            return 0;
        }
    }

    /**
     * 文件是否来自MD库
     */
    public static boolean isMD(String filename) {
        String reg = ".+_((?i)md)_.+";
        return filename.matches(reg);
    }

    /**
     * 是不是sql文件
     */
    public static boolean isSQL(String filename) {
        return filename.endsWith(".sql") || filename.endsWith(".SQL");
    }

}
