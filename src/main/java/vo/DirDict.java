package vo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 输入目录和输出目录的对应，可根据需求增加或修改
 *
 * @author wanglc
 * @date 2020-04-10
 */
public class DirDict {

    private static HashMap<String, String> common = new HashMap<>();
    private static HashMap<String, String> projects = new HashMap<>();
    private static ArrayList<String> srcMainDir = new ArrayList<>();

    static {
        common.put("Pp脚本", "01_PP");
        common.put("Tp脚本", "02_TP");
        common.put("06 平台部脚本", "03_0_interface");
        common.put("01 交易组件", "03_platform");
        common.put("02 交易组件_前端界面", "04_platform_com_web");
        common.put("03 web组件", "05_platform_web");
        common.put("04 平台组件", "04_platform_component");
        common.put("04 开发脚本", "06_xams");
        common.put("公共", "07_tpara");
        common.put("菜单及相关", "07_tpara");
        common.put("05 公共视图", "08_view");
        common.put("07 限额相关", "09_limit");
        common.put("08 资产界面", "09_assets");

        projects.put("N1001", "N1001");
        projects.put("X2002", "X2002");
        projects.put("C4004", "C4004");
        projects.put("L5005", "L5005");
        projects.put("B6006", "B6006");
        projects.put("T7007", "T7007");
        projects.put("TL8008", "TL8008");
        projects.put("CX9009", "CX9009");
        projects.put("DL1010", "DL1010");
        projects.put("MS1111", "MS1111");
        projects.put("WH1212", "WH1212");
        projects.put("HTBK", "HTBK");
        projects.put("DG1313", "DG1313");
        projects.put("CY1414", "CY1414");
        projects.put("QS1515", "QS1515");
        projects.put("ZS1616", "ZS1616");
        projects.put("ZS1616(初始化脚本)", "ZS1616");
        projects.put("CZ1717", "CZ1717");
        projects.put("QN1818", "QN1818");
        projects.put("SD1919", "SD1919");
        projects.put("CD2020", "CD2020");
        projects.put("SH2121", "SH2121");
        projects.put("SP2222", "SP2222");

        srcMainDir.add("01 公共组件");
        srcMainDir.add("02 项目个性");
        srcMainDir.add("03 系统参数");
        srcMainDir.add("04 开发脚本");
        srcMainDir.add("05 公共视图");
        srcMainDir.add("06 平台部脚本");
        srcMainDir.add("07 限额相关");
        srcMainDir.add("08 资产界面");
        srcMainDir.add("Pp脚本");
        srcMainDir.add("Tp脚本");
    }

    public static HashMap<String, String> getCommon() {
        return common;
    }

    public static HashMap<String, String> getProjects() {
        return projects;
    }


    public static ArrayList<String> getSrcMainDir() {
        return srcMainDir;
    }

    public boolean isSrcMainDir(String dirname) {
        return srcMainDir.contains(dirname);
    }

    /**
     * 是否是项目点的目录
     */
    public boolean isProjects(String filename) {
        return projects.containsKey(filename);
    }

    /**
     * 是否是需要创建新目录
     */
    public boolean needCreateNewDir(String key) {
        return common.containsKey(key);
    }

    /**
     * 返回新的目录名
     */
    public String getNewName(String oldName) {
        return common.get(oldName);
    }

}
