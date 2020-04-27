package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * 批量sql脚本生产工具类
 * @author wanglc
 * @date 2020-04-10
 */
public class SqlUtil {

    public static void generatorBatchSqlFile(File dir, int startDate, int endDate, String encode) throws IOException {
        String dirName = dir.getName();

        StringBuilder trd = new StringBuilder();
        StringBuilder md = new StringBuilder();

//        trd.append("spool trd_cams_script.log;\r\n").append("set echo on;\r\n").append("set sqlbl on;\r\n\r\n")
//                .append("-- trd库批量脚本\r\n\r\n");
//        md.append("spool xir_md_j_script.log;\r\n").append("set echo on;\r\n").append("set sqlbl on;\r\n\r\n")
//                .append("-- md库批量脚本\r\n\r\n");

        trd.append("spool trd_cams_script.log;\r\n").append("set sqlbl on;\r\n\r\n")
                .append("-- trd库批量脚本\r\n\r\n");
        md.append("spool xir_md_j_script.log;\r\n").append("set sqlbl on;\r\n\r\n")
                .append("-- md库批量脚本\r\n\r\n");

        File[] files = dir.listFiles();
        for (File f : files) {
            generatorBatchString(f, trd, md, startDate, endDate);
        }

//        md.append("set echo off;\r\n").append("set sqlbl off;\r\n").append("spool off;");
//        trd.append("set echo off;\r\n").append("set sqlbl off;\n").append("spool off;");

        md.append("set sqlbl off;\r\n").append("spool off;");
        trd.append("set sqlbl off;\n").append("spool off;");

        createSqlFile(dir, trd.toString(), md.toString(), encode);
    }

    private static void createSqlFile(File dir, String trd, String md, String encode) throws IOException {

        File trdSql = new File(dir, "trd_cams_script.sql");
        File mdSql = new File(dir, "xir_md_j_script.sql");

        trdSql.createNewFile();
        mdSql.createNewFile();

        OutputStreamWriter trdWriter = new OutputStreamWriter(new FileOutputStream(trdSql), encode);
        trdWriter.write(trd);
        trdWriter.flush();
        if (trdWriter != null) {trdWriter.close();}

        OutputStreamWriter mdWriter = new OutputStreamWriter(new FileOutputStream(mdSql), encode);
        mdWriter.write(md);
        mdWriter.flush();
        if (mdWriter != null) {mdWriter.close();}

    }

    private static String[] generatorBatchString(File dir, StringBuilder trd, StringBuilder md, int startDate, int endDate) {
        String dirName = dir.getName();
        trd.append("--" + dirName + "\r\n");
        md.append("--" + dirName + "\r\n");


        File[] files = dir.listFiles();
        for (File f : files) {

            if (FileDetectUtil.isRepairable(f.getName()) == 1) {
                int fileDate = Integer.parseInt(f.getName().substring(0, 8));
                if (fileDate > startDate && fileDate < endDate) {
                    if (FileDetectUtil.isMD(f.getName())) {
                        md.append("prompt ./").append(dirName).append("/").append(f.getName()).append("\r\n");
                        md.append("@@./").append(dirName).append("/").append(f.getName()).append("\r\n");
                    } else {
                        trd.append("prompt ./").append(dirName).append("/").append(f.getName()).append("\r\n");
                        trd.append("@@./").append(dirName).append("/").append(f.getName()).append("\r\n");
                    }
                }
            } else {
                if (FileDetectUtil.isMD(f.getName())) {
                    md.append("prompt ./").append(dirName).append("/").append(f.getName()).append("\r\n");
                    md.append("@@./").append(dirName).append("/").append(f.getName()).append("\r\n");
                } else {
                    trd.append("prompt ./").append(dirName).append("/").append(f.getName()).append("\r\n");
                    trd.append("@@./").append(dirName).append("/").append(f.getName()).append("\r\n");
                }
            }
        }
        md.append("\r\n");
        trd.append("\r\n");

        return new String[]{md.toString(), trd.toString()};
    }
}
