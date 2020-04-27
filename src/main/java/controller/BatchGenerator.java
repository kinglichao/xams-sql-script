package controller;

import org.apache.commons.io.FileUtils;
import utils.*;
import vo.DirDict;
import vo.FileMap;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author wanglc
 * @date 2020-04-08
 */
public class BatchGenerator {
    private File srcDir;
    private FileMap fileMap;
    private DirDict dirDict;
    private int startDate;
    private int endDate;
    private String encode;

    public BatchGenerator(File srcDir, int startDate, int endDate, String encode) {
        this.srcDir = srcDir;
        this.startDate = startDate;
        this.endDate = endDate;
        this.encode = encode;
        fileMap = new FileMap();
        fileMap.setSrcDir(srcDir);
        dirDict = new DirDict();
    }

    public void start() throws Exception {
        /**
         * 创建临时输出目录temp和temp/common，保存到fileMap中
         */
        createTempDir(srcDir);

        /**
         * 根据srcDir下的内容，在temp下创建规范的目录，并复制文件
         * fileMap: srcDir, tempDir, commonDir, projectDirs
         */
        beforeSrc2Temp(srcDir, fileMap.getTempDir());


        /**
         * 文件转码
         */
        ConvertEncodeUtil.convertTo(fileMap.getTempDir(), encode);

        /**
         * 修复tempDir中的所有文件
         */
        repaireFileName(fileMap.getTempDir());
        repaireBlank(fileMap.getTempDir());
        RepairUtil.repaireAllFile(fileMap.getTempDir(), encode);

        /**
         * 复制common下的文件夹到各个项目点中,并删除common
         */
        common2Projects(fileMap.getCommonDir());

        /**
         * 在各个项目点下生产批处理文件
         */
        generateBatcch(fileMap.getTempDir(), startDate, endDate, encode);

        ConvertEncodeUtil.convertTo(fileMap.getTempDir(), encode);

        /**
         * 复制tempDir到srcDir中，并删除原emptyDir
         */
        copyTemp2Src(fileMap.getTempDir(), fileMap.getSrcDir());


    }

    private void copyTemp2Src(File tempDir, File srcDir) throws IOException {

        String tempDirName = tempDir.getName();
        String newName = tempDirName;

        while (tempDirExist(newName, srcDir)) {
            newName = newName + "_" + getNowTime();
        }

        if (!newName.equals(tempDirName)) {
            tempDir.renameTo(new File(tempDir.getParentFile(), newName));
        }

        FileUtils.copyDirectory(tempDir.getParentFile(), srcDir);
        FileUtils.deleteDirectory(tempDir.getParentFile());

        System.out.println("批量脚本文件目录为: " + srcDir.getAbsolutePath() + "\\" + newName);
    }

    /**
     * 判断文件名在当前文件夹下是否存在
     */
    private boolean tempDirExist(String filename, File dir) {
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.getName().equalsIgnoreCase(filename)) {
                return true;
            }
        }
        return false;
    }

    private void generateBatcch(File dir, int startDate, int endDate, String encode) throws IOException {
        File[] files = dir.listFiles();
        for (File f : files) {
            SqlUtil.generatorBatchSqlFile(f, startDate, endDate, encode);
        }
    }

    private void common2Projects(File common) throws IOException {
        File[] projectDir = fileMap.getTempDir().listFiles();
        for (File f : projectDir) {
            if (!f.getName().equals(common.getName())) {
                FileUtils.copyDirectory(common, f);
            }
        }

        deleteCommon(common);
//        FileUtils.deleteDirectory(common);
    }

    private void deleteCommon(File common) throws IOException {
        String[] list = fileMap.getTempDir().list();
        for (int i = 0; i < list.length; i++) {
            if ("HTBK".equalsIgnoreCase(list[i])) {
                FileUtils.deleteDirectory(common);
                return;
            }
        }
        File htbk = new File(common.getParentFile(), "HTBK");
        common.renameTo(htbk);
    }


    public static void repaireFile(File dir, String charset) throws Exception {

        /**
         * 遍历dir
         *  是文件：
         *      修改文件编码
         *      修复文件内容
         *      修复文件名
         *  是目录：
         *      继续遍历
         */
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.isFile()) {
//                FileDetectUtil.transFileEncode(f, charset);
                FileDetectUtil.repairFileContent(f);
            } else {
                repaireFile(f, charset);
            }
        }

    }

    public static void repaireFileName(File dir) {
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.isFile()) {
                FileDetectUtil.repairFileName(f);
            } else {
                repaireFileName(f);
            }
        }
    }

    public static void repaireBlank(File file) {
        if (file.isFile()) {
            FileDetectUtil.delBlank(file);
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                repaireBlank(f);
            }
        }
    }

    public void beforeSrc2Temp(File srcDir, File tempDir) throws IOException {
        File[] files = srcDir.listFiles();
        for (File f : files) {
            if (dirDict.isSrcMainDir(f.getName())) {
                srcDir2Temp(f, tempDir);
            }
        }
    }

    private void srcDir2Temp(File srcDir, File tempDir) throws IOException {

        /**
         * srcDir是不是项目点目录
         *  是：
         *      在tempDir下创建项目点/10_projects目录，并将srcDir下的所有文件复制过去
         *  不是：
         *      需不需要创建输出目录
         *          需要： 在tempDir下的common下创建输出目录，并把文件复制过去
         *          不需要：继续遍历
         */
        if (dirDict.isProjects(srcDir.getName())) {
            File projectDir = new File(tempDir, DirDict.getProjects().get(srcDir.getName()));
            mkdir(projectDir);
            File projectDir2 = new File(projectDir, "10_projects");
            copyFileFromA2B(srcDir, projectDir2);
        } else {
            if (dirDict.needCreateNewDir(srcDir.getName())) {
                File newDir = new File(fileMap.getCommonDir(), dirDict.getNewName(srcDir.getName()));
                mkdir(newDir);
                fileMap.getProjectDirs().put(newDir.getName(), newDir);
                copyFileFromA2B(srcDir, newDir);
            } else {
                File[] files = srcDir.listFiles();
                for (File f : files) {
                    srcDir2Temp(f, tempDir);
                }
            }
        }

    }

    /**
     * 只复制A下的所有sql文件到B下（不复制目录）
     */
    public static void copyFileFromA2B(File srcDir, File destDir) throws IOException {
        File[] files = srcDir.listFiles();
        for (File f : files) {
            if (f.isFile()) {
                if (GetFileUtil.isSql(f)) {
                    FileUtils.copyFile(f, new File(destDir, f.getName()));
                }
            } else {
                copyFileFromA2B(f, destDir);
            }
        }
    }

    private void mkdir(File dir) {
        if (dir.isDirectory() && !dir.exists()) {
            dir.mkdir();
        }
    }

    /**
     * 在srcFile的平级创建empty/temp目录，规范目录名：当前日期+“_”+srcFile+"_"+i
     */
    private void createTempDir(File srcFile) {
        File parentFile = srcFile.getParentFile();
        String tempDirName = getTodayDate() + "_" + srcFile.getName();
        File emptyDir = new File(parentFile, tempDirName);

        int i = 1;
        while (emptyDir.exists()) {
            emptyDir = new File(parentFile, tempDirName + "_" + i);
            i++;
        }

        emptyDir.mkdir();

        File tempDir = new File(emptyDir, emptyDir.getName());
        tempDir.mkdir();

        File commonDir = new File(tempDir, "common");
        commonDir.mkdir();

        fileMap.setEmptyDir(emptyDir);
        fileMap.setTempDir(tempDir);
        fileMap.setCommonDir(commonDir);
    }

    private static String getTodayDate() {
        String date = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dd = Calendar.getInstance().getTime();
        date = sdf.format(dd).replace("-", "");
        return date;
    }

    private String getNowTime() {
        String time = "";
        SimpleDateFormat sdf = new SimpleDateFormat("HH-mm-ss");
        Date dd = Calendar.getInstance().getTime();
        time = sdf.format(dd);
        return "(" + time + ")";

    }

}
