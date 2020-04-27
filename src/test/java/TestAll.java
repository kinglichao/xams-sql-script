import controller.BatchGenerator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import utils.EncodeUtil;
import utils.GetFileUtil;
import utils.SqlUtil;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author wlc
 * @date 2020-04-22
 */
public class TestAll {

    @Test
    public void test21() {
        StringBuilder sb = new StringBuilder();
        sb.append("dfj").append("\t\t").append("dsf");
        System.out.println(sb.toString());
    }

    @Test
    public void test20() {
//        StringBuilder sb = new StringBuilder("select * from user_table where table_name in ('TTRD_XCC_TRACE_MESSAGE', 'TTRD_XCC_TRACE_EXCEPTION_LOG', ");
        StringBuilder sb = new StringBuilder("I am, eee");
        int len = sb.length();
        System.out.println(len);
        System.out.println(sb.delete(len - 3, len - 1));
//        System.out.println(sb.charAt(102));
    }

    @Test
    public void test19() throws IOException {
        Desktop.getDesktop().open(new File("D:/test.txt"));
    }
    
    @Test
    public void test18() {
        String s = "call trydroptable('tablename');";
        System.out.println(s);
        System.out.println(s.matches("(?i).*trydroptable"));
    }
    
    @Test
    public void test17() {
        File file = new File("D:\\test\\V4.1.2.0\\20200425_V4.1.2.0");
        String[] list = file.list();
        System.out.println(Arrays.toString(list));

        for (int i = 0; i < list.length; i++) {
            if ("HTBK".equalsIgnoreCase(list[i])) {

            }
        }

    }

    @Test
    public void test16() throws IOException {
        File pp = new File("D:\\test\\pptp");
        SqlUtil.generatorBatchSqlFile(pp, 0, 999999999, "gbk");
    }

    private static ArrayList<String> list = new ArrayList<>();
    private static File old = new File("D:\\Library\\Desktop\\昨天\\V4.1.0.0");
    private static File today = new File("D:\\Library\\Desktop\\今天\\20200423_V4.1.0.0_TL8008_gbk\\HTBK");

    @Test
    public void test15() {
        addFileName(old);
        getDifferent(today);
    }

    public static void getDifferent(File file) {
        if (file.isFile()) {
            if (!list.contains(file.getName())) {
                System.out.println(file.getAbsolutePath());
            }
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                getDifferent(f);
            }
        }
    }

    public static void addFileName(File file) {
        if (file.isFile()) {
            list.add(file.getName());
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                addFileName(f);
            }
        }
    }

    @Test
    public void test14() {

    }

    @Test
    public void test13() throws IOException {
        File file = new File("D:\\test");
        GetFileUtil.getDifferentCountAnno(file);
    }

    @Test
    public void test12() {
        File file = new File("D:\\test");
        BatchGenerator.repaireBlank(file);
    }

    @Test
    public void test11() {
        File file = new File("D:\\test\\V0.0.169.0_04  _MD_DML.sql");
//        System.out.println(file.getName());
        String name = file.getName();
        System.out.println(name);
        String replace = StringUtils.replace(name, " ", "");
        System.out.println(replace);
        file.renameTo(new File(file.getParentFile(), replace));
    }
    
    @Test
    public void test10() throws IOException {
        File file = new File("D:\\test\\V4.1.2.0\\20200423_V4.1.2.0\\DL1010\\10_projects");
    }
    
    @Test
    public void test9() {
        String s = "fdjoa/*+parallel(8)*/adf/*+parallel(8)*/sdf/*+parallel(8)*/";
        System.out.println(s);
        String replace = StringUtils.replace(s, "/*+parallel(8)*/", "+parallel(8)");
        System.out.println(replace);
    }

    @Test
    public void test8() throws IOException {
        File file = new File("D:\\test\\V4.1.2.0\\20200423_V4.1.2.0\\CZ1717\\07_tpara");
    }
    
    @Test
    public void test7() {
        File file = new File("D:\\test\\V4.1.2.0\\20200423_V4.1.2.0\\CZ1717\\trd_cams_script.sql");
        String filepath = file.getAbsolutePath();
        EncodeUtil.convert(filepath, "utf-8", filepath, "utf-8");
    }

    @Test
    public void test6() throws IOException {
        File file = new File("D:\\test\\v4.0.0.3_all");

    }

    @Test
    public void test5() throws IOException {
        File file = new File("D:\\test\\v4.0.0.3_all");
        for (File f : file.listFiles()) {
            SqlUtil.generatorBatchSqlFile(f, 0, 99999999, "gbk");
        }
    }

    @Test
    public void test4() {
        String s = "hellodsllo44";
        System.out.println(s.contains("/*+parall(8)*/"));
        String replace = s.replace("llo", " ");
        System.out.println(replace);

    }

    /**
     * 去掉bom
     */
    @Test
    public void test3() throws IOException {
        File file = new File("D:\\test\\bbb\\bom.sql");
        File file2 = new File("D:\\test\\bbb\\utf.sql");
        FileInputStream fis = new FileInputStream(file);
        String s = IOUtils.toString(fis, "utf-8");

        String s2 = IOUtils.toString(new FileInputStream(file2), "utf-8");

        byte[] bytes = s.getBytes("utf-8");
        byte[] subarray = ArrayUtils.subarray(bytes, 3, bytes.length);

        String news = new String(subarray, "utf-8");
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file));
        IOUtils.write(subarray, osw, "utf-8");
        osw.close();

    }

    @Test
    public void test2() throws IOException {
        File file = new File("D:\\test\\bbb\\bom.sql");
        File file2 = new File("D:\\test\\bbb\\utf.sql");
        FileInputStream fis = new FileInputStream(file);
        String s = IOUtils.toString(fis, "utf-8");

        String s2 = IOUtils.toString(new FileInputStream(file2), "utf-8");
//        System.out.println(s);
        byte[] bytes = s.getBytes("utf-8");
        byte[] bytes2 = s2.getBytes("utf-8");

        System.out.println(Arrays.toString(bytes));
        System.out.println(Arrays.toString(bytes2));

//        EncodeUtil.convert(filepath, "utf-8", filepath, "utf-8");


    }

    @Test
    public void test() throws IOException {
        File file = new File("D:\\test\\三种编码格式的文件\\utf8.sql");

        File bbb = new File("D:\\test\\bbb");
        File ccc = new File("D:\\test\\ccc");

        System.out.println(StringUtils.contains("wanglichao", "lichao"));

    }
}
