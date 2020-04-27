package utils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import vo.DirDict;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wlc
 * @date 2020-04-23
 */
public class GetFileUtil {

    // key: "dirError", "annoError", "encodeWarn", "bomWarn", "noSqlWarn", "annoWarn", "parallelWarn"
    private static HashMap<String, ArrayList<String>> errorMap = new HashMap<>();
    private static List<String> tablelist = new ArrayList<>();

    private File srcFile;
    private String encode;

    public GetFileUtil(File srcFile, String encode) {
        this.srcFile = srcFile;
        this.encode = encode;
    }



    public static void main(String[] args) throws IOException{
        File file = new File("D:\\yhzg\\xAMS\\trunk\\script\\开发脚本\\V4.1.2.0");
        // 对根目录的一条龙检测
        System.out.println("\n==================== 检测不规范的目录 ====================\n");
        getDirName(file);
//        System.out.println("\n==================== 检测所有文件编码 ====================\n");
//        printEncode(file);
//        System.out.println("\n==================== 检测utf-8_bom文件 ====================\n");
//        getBomFile(file);
//        System.out.println("\n==================== 检测非sql文件 ====================\n");
//        getNoSqlFile(file);
//        System.out.println("\n==================== 检测含有/**/注释的sql文件 ====================\n");
//        getAnno(file);
        System.out.println("\n==================== 检测含有/*+parallel的sql文件 ====================\n");
        getParallel(file);
        System.out.println("\n==================== 检测左右注释数量不匹配的sql文件 ====================\n");
        getDifferentCountAnno(file);

//        getDropTableSql(file);
//        System.out.println(tablelist);
//        System.out.println(getDropSql(tablelist));
        getDropTableSqlLog(file);
    }

    public static void getDropTableSqlLog(File file) throws IOException {
        HashMap<String, ArrayList<String>> dropMap = new HashMap<>();
        getDropTableInfo(file, dropMap);

        // 生成sql语句
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM USER_TABLES WHERE TABLE_NAME IN (");
        Set<String> tablenames = dropMap.keySet();
        for (String name : tablenames) {
            sql.append("'").append(name).append("'").append(", ");
        }
        int len = sql.length();
        sql = sql.delete(len - 2, len);
        sql.append(");").append("\r\n\r\n\r\n\r\n");

        // 生成日志
        StringBuilder filenames = new StringBuilder();
        filenames.append("-- 以下是执行过call trydroptable存储过程的sql文件\r\n\r\n");
        for (Map.Entry<String, ArrayList<String>> table : dropMap.entrySet()) {
            filenames.append(table.getKey()).append("\r\n");
            for (String path : table.getValue()) {
                filenames.append("\t\t").append(path);
            }
            filenames.append("\r\n");
        }

        // 输出到文件中
        File dropTableSql = null;
        if (file.isFile()) {
            dropTableSql = new File(file.getParentFile(), "dropTable.sql");
            if (!dropTableSql.exists()) {
                dropTableSql.createNewFile();
            }
        } else {
            dropTableSql = new File(file, "dropTable.sql");
            if (!dropTableSql.exists()) {
                dropTableSql.createNewFile();
            }
        }

        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(dropTableSql))) {
            osw.write(sql.toString() + filenames.toString());
        }
    }

    public static void getDropTableInfo(File file, HashMap<String, ArrayList<String>> dropMap) {
        if (file.isFile()) {
            if (isSql(file)) {
                String content = getSqlString(file);
                String regex = "(?s)(?i)(trydroptable\\(.*?'(\\w+)'\\))";
                Matcher matcher = Pattern.compile(regex).matcher(content);
                while (matcher.find()) {
                    String tablename = matcher.group(2);
                    if (!dropMap.containsKey(tablename)) {
                        ArrayList<String> filenames = new ArrayList<>();
                        filenames.add(file.getAbsolutePath());
                        dropMap.put(tablename, filenames);
                    } else {
                        dropMap.get(tablename).add(file.getAbsolutePath());
                    }
                }
            }
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                getDropTableInfo(f, dropMap);
            }
        }
    }


    public static String getSqlString(File file) {
        FileInputStream fis = null;
        try {
            String encode = EncodeUtil.getEncode(file);
            fis = new FileInputStream(file);
            String content = IOUtils.toString(fis, encode.equalsIgnoreCase("gbk") ? "gbk" : "utf-8");
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    /**
     * 获取开发脚本不规范的目录（待优化）
     */
    public static void getDirName(File file) {
        if (file.isFile()) {
            System.out.println("不合法的根目录");
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                if (!DirDict.getSrcMainDir().contains(f.getName())) {
                    System.out.println("根目录下存在不规范的目录：" + f.getName());
                }
                if ("02 项目个性".equals(f.getName())) {
                    File[] projects = f.listFiles();
                    for (File file1 : projects) {
                        if (file1.isFile()) {
                            System.out.println("[02 项目个性]中存在单独的文件，需要移至相应的目录中");
                        } else if (!DirDict.getProjects().containsKey(file1.getName())) {
                            System.out.println("[02 项目个性]文件夹下存在未知的文件夹:" + file1.getName() + "，需要将其添加到DirDict中");
                        }
                    }
                }
                if ("03 系统参数".equals(f.getName())) {
                    File[] projects = f.listFiles();
                    for (File file1 : projects) {
                        if (file1.isFile()) {
                            System.out.println("[03 系统参数]中存在单独的文件，需要移至相应的目录中");
                        } else if (!DirDict.getCommon().containsKey(file1.getName()) &&
                                    !DirDict.getProjects().containsKey(file1.getName())) {
                            System.out.println("[03 系统参数]文件夹下存在未知的文件夹:" + file1.getName() + "，需要将其添加到DirDict中");
                        }
                    }
                }
                if ("01 公共组件".equals(f.getName())) {
                    File[] files1 = f.listFiles();
                    for (File file1 : files1) {
                        if (file1.isFile()) {
                            System.out.println("[01 公共组件]中存在单独的文件，需要移至相应的目录中");
                        } else if (!DirDict.getCommon().containsKey(file1.getName())) {
                            System.out.println("[01 公共组件]文件夹下存在未知的文件夹:" + file1.getName() + "，需要将其添加到DirDict中");
                        }
                    }
                }
            }
        }
    }

    public static void printEncode(File file) {

        if (file.isFile()) {
            System.out.println(EncodeUtil.getEncode(file) + ": "
                    + file.getParentFile().getName() + "/"
                    + file.getName());
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                printEncode(f);
            }
        }
    }

    /**
     * 获取目录下所有非sql文件
     */
    public static void getNoSqlFile(File file) {
        if (file.isFile()) {
            if (!isSql(file)) {
                System.out.println(file.getAbsolutePath());
            }
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                getNoSqlFile(f);
            }
        }
    }

    public static boolean isSql(File file) {
        String ext = FilenameUtils.getExtension(file.getName());
        if ("sql".equalsIgnoreCase(ext)) {
            return true;
        }
        return false;
    }

    /**
     * 获取含有/*注释的文件名
     */
    public static void getAnno(File file) throws IOException {
        if (file.isFile()) {
            if (isSql(file)) {
                FileInputStream fis = new FileInputStream(file);
                String content = IOUtils.toString(fis,
                        EncodeUtil.getEncode(file).equalsIgnoreCase("gbk") ? "gbk" : "utf-8");
                if (content.contains("/*") || content.contains("*/")) {
                    System.out.println(file.getAbsolutePath());
                }
                fis.close();
            }
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                getAnno(f);
            }
        }
    }

    /**
     * 获取/*数量不匹配的sql文件
     */
    public static void getDifferentCountAnno(File file) throws IOException{
        if (file.isFile()) {
            if (isSql(file)) {
                int left = 0;
                int right = 0;
                FileInputStream fis = new FileInputStream(file);
                String content = IOUtils.toString(fis,
                        EncodeUtil.getEncode(file).equalsIgnoreCase("gbk") ? "gbk" : "utf-8");
                if (content.contains("/*")) {
                    String leftAnno = StringUtils.replace(content, "/*", "");
                    left = (content.length() - leftAnno.length()) / 2;
                    String rightAnno = StringUtils.replace(content, "*/", "");
                    right = (content.length() - rightAnno.length()) / 2;
                }
                fis.close();
                if (left != right) {
                    System.out.println(file.getAbsolutePath());
                }
            }

        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                getDifferentCountAnno(f);
            }
        }
    }

    /**
     * 获取所有并发执行的脚本/*parallel
     */
    public static void getParallel(File file) throws IOException {
        if (file.isFile()) {
            if (isSql(file)) {
                FileInputStream fis = new FileInputStream(file);
                String content = IOUtils.toString(fis,
                        EncodeUtil.getEncode(file).equalsIgnoreCase("gbk") ? "gbk" : "utf-8");
                if (content.contains("/*+parallel")) {
                    System.out.println(file.getAbsolutePath());
                }
                fis.close();
            }
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                getParallel(f);
            }
        }
    }

    /**
     * 获取所有utf8_bom编码的文件
     */
    public static void getBomFile(File file) {

        if (file.isFile()) {
            String encode = EncodeUtil.getEncode(file.getAbsolutePath(), false);
            if (encode.equalsIgnoreCase("utf-8_bom")) {
                System.out.println(encode + ": " + file.getAbsolutePath());
            }
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                getBomFile(f);
            }
        }
    }

}
