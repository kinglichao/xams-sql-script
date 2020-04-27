package controller;

import java.io.File;

/**
 * @author wlc
 * @date 2020-04-22
 */
public class AppMain {

    public static void main(String[] args) throws Exception {
        System.out.println("批量脚本生产器开始运行...\n");

        String srcDirPath = "D:\\Library\\Documents\\脚本问题\\顺德SD1919\\V4.0.0.4_SD1919_427";
        int startDate = 0;
        int endDate = 999999999;
//        String encode = "utf-8";
        String encode = "gbk";

        long start = System.currentTimeMillis();
        System.out.println("\n开始生成脚本文件...");

        File srcDir = new File(srcDirPath);
        BatchGenerator batchGenerator = new BatchGenerator(srcDir, startDate, endDate, encode);
        batchGenerator.start();

        long end = System.currentTimeMillis();
        System.out.println("结束运行");
        System.out.println("程序运行了： " + (end - start)/1000 + "秒");
    }

}
