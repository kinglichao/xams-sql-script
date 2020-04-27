package vo;

import java.io.File;
import java.util.HashMap;

/**
 * @author wanglc
 * @date 2020-04-10
 */
public class FileMap {

    private File srcDir;

    private File emptyDir;

    private File tempDir;

    private File commonDir;

    private HashMap<String, File> projectDirs = new HashMap<>();

    public File getEmptyDir() {
        return emptyDir;
    }

    public void setEmptyDir(File emptyDir) {
        this.emptyDir = emptyDir;
    }

    public File getSrcDir() {
        return srcDir;
    }

    public void setSrcDir(File srcDir) {
        this.srcDir = srcDir;
    }

    public File getTempDir() {
        return tempDir;
    }

    public void setTempDir(File tempDir) {
        this.tempDir = tempDir;
    }

    public File getCommonDir() {
        return commonDir;
    }

    public void setCommonDir(File commonDir) {
        this.commonDir = commonDir;
    }

    public HashMap<String, File> getProjectDirs() {
        return projectDirs;
    }

    public void setProjectDirs(HashMap<String, File> projectDirs) {
        this.projectDirs = projectDirs;
    }
}
