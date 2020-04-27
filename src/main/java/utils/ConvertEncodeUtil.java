package utils;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;

/**
 * @author wlc
 * @date 2020-04-21
 */
public class ConvertEncodeUtil {
    public static void main(String[] args) throws Exception {

        String filepath = "D:\\test\\pptp";
        File file = new File(filepath);


//        printEncode(file);
//        convertUTF8(file); // 转换文件或文件夹下所有文件为utf8
        convertGBK(file);  // 转换文件或文件夹下所有文件为gbk
    }

    public static void convertTo(File file, String encode) {
        if ("gbk".equalsIgnoreCase(encode)) {
            convertGBK(file);
        } else {
            convertUTF8(file);
        }
    }

    public static void convertGBK(File file) {

        if (file.isFile()) {
            convertFileToGBK(file);
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                convertGBK(f);
            }
        }

    }

    private static void convertFileToGBK(File file) {

        String oldEncode = EncodeUtil.getEncode(file);
        String filepath = file.getAbsolutePath();

        if ("gbk".equalsIgnoreCase(oldEncode)) {
            return;
        } else if ("utf-8".equalsIgnoreCase(oldEncode)) {
            EncodeUtil.convert(filepath, oldEncode, filepath, "gbk");
        } else {
            convertFileToUTF8(file);
            EncodeUtil.convert(filepath, "utf-8", filepath, "gbk");
        }

        // 转码失败(文件内容全英文会转码失败)
        if (!EncodeUtil.getEncode(file).equalsIgnoreCase("gbk")) {
            try {
                FileInputStream fis = new FileInputStream(file);
                String s = IOUtils.toString(fis, "utf-8");
                s = s + "\n\n--不用管这一行，转码gbk用的";
                FileWriter fw = new FileWriter(file);
                fw.write(s);
                fis.close();
                fw.close();

                EncodeUtil.convert(filepath, "utf-8", filepath, "gbk");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public static void convertUTF8(File file) {
        if (file.isFile()) {
            convertFileToUTF8(file);
        } else {
            convertDirToUTF8(file);
        }
    }

    private static void convertFileToUTF8(File file) {
        String oldEncode = EncodeUtil.getEncode(file);
        if ("utf-8_bom".equalsIgnoreCase(oldEncode)) {
            dropBom(file);
        } else if ("utf-8".equalsIgnoreCase(oldEncode)) {
            return;
        } else {
            String filepath = file.getAbsolutePath();
            EncodeUtil.convert(filepath, oldEncode, filepath, "utf-8");
        }
    }

    public static void convertDirToUTF8(File dir) {
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.isFile()) {
                convertFileToUTF8(f);
            } else {
                convertDirToUTF8(f);
            }
        }

    }

    public static void printEncode(File file) {

        if (file.isFile()) {
            System.out.println(EncodeUtil.getEncode(file.getAbsolutePath(), false) + ": "
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

    /**
     * 将utf-8_bom文件转码成utf-8文件
     */
    public static void dropBom(File file) {

        try {
            String filepath = file.getAbsolutePath();
            FileInputStream fis = new FileInputStream(file);
            String s = IOUtils.toString(fis, "utf-8");
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file));
            byte[] bytes = s.getBytes("utf-8");
            byte[] subarray = ArrayUtils.subarray(bytes, 3, bytes.length);
            String news = new String(subarray, "utf-8");
            IOUtils.write(subarray, osw, "utf-8");
            fis.close();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
