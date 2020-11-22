package com.zyf.framework.util;

import java.io.*;
import java.net.URLDecoder;

/**
 * 文件工具
 *
 * @author yuanfeng.z
 * @date 2020/8/7 1:58
 */
public class FileUtil {

    /**
     * 读取json文件
     *
     * @param fileName 文件名
     * @return
     */
    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            InputStream stream = FileUtil.class.getClassLoader().getResourceAsStream(fileName);
            Reader reader = new InputStreamReader(stream, "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            String message = String.format("load file %s failed.", fileName);
            throw new RuntimeException(message);
        }
    }

    /**
     * 写json文件
     *
     * @param fileName 文件名
     * @param content  json格式字符串
     */
    public static void writeJsonFile(String fileName, String content) {
        try {
            boolean isWin = System.getProperty("os.name").toLowerCase().contains("win");
            FileWriter fileWriter = null;
            if (isWin) {
                java.net.URL uri = FileUtil.class.getClass().getResource("/");
                fileWriter = new FileWriter(uri.getPath() + "/" + fileName);
            } else {
                fileWriter = new FileWriter("./" + fileName);
            }
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            String message = String.format("load file %s failed.", fileName);
            throw new RuntimeException(message);
        }
    }
}
