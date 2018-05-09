/*
 * Copyright (C) 2012 GZ-ISCAS Inc., All Rights Reserved.
 */
package com.hiido.stork.agent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import com.hiido.stork.agent.utils.LogUtil;
import com.hiido.stork.agent.utils.StringUtil;

/**
 * 
 * @Description: 全局配置参数  
 * @Author janpychou@qq.com
 * @CreateDate:   [May 26, 2015 8:04:08 PM]   
 *
 */
public class SystemConfig {

    private static Properties configs = new Properties();

    private static final String UTF_8 = "UTF-8";

    public static final String PREFIX_ARG = "-";

    private static final String SYSTEM_CONFIG_FILE = "SystemConfig.properties";
    private static final String LOG4J_CONFIG_FILE = "log4j.properties";

    public static final String PROJECT_NAME = "stork.projectname";

    public static final String API_SERVER_LISTEN_PORT = "stork.apiserver.listen.port";
    public static final String API_SERVER_LISTEN_IP = "stork.apiserver.listen.ip";

    public static final String TAIL_LINE_NUM = "stork.agent.tail.line.num";

    public static final String TAIL_INIT_CHAR_NUM = "stork.agent.tail.ini.char.num";

    public static final String LOCK_FILE = "stork.agent.lockfile";

    public static final String FILE_BASE_PATH = "file.base.path";

    public static String lockFile = "/var/run/stork.agent.lock";

    static {
        Properties logProperties = readConfigs(LOG4J_CONFIG_FILE);
        PropertyConfigurator.configure(logProperties);

        LogUtil.info("<=== 开始解析Stork系统配置文件  ===>");
        configs.putAll(readConfigs(SYSTEM_CONFIG_FILE));
        checkConfigs();
        LogUtil.info("系统配置为：" + configs);
        LogUtil.info("<=== 完成解析Stork系统配置文件 ===>");
        if (configs.containsKey(LOCK_FILE)) {
            lockFile = getString(LOCK_FILE);
        }
        LogUtil.info("lockfile：" + lockFile);
    }

    private static Properties readConfigs(String fileName) {

        Properties pro = new Properties();

        String proFileName = getAppPath() + fileName;
        System.out.println("proFilePath:" + proFileName);
        try {
            InputStream in = new FileInputStream(proFileName);
            if (in != null) {
                pro.load(new InputStreamReader(in, UTF_8));
            }
            System.out.println("Configs:" + pro);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pro;
    }

    public static String getAppPath() {
        URL url = SystemConfig.class.getProtectionDomain().getCodeSource().getLocation();
        String filePath = null;
        try {
            filePath = URLDecoder.decode(url.getPath(), "utf-8");// 转化为utf-8编码  
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (filePath.endsWith(".jar")) {// 可执行jar包运行的结果里包含".jar"  
            // 截取路径中的jar包名  
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        }

        File file = new File(filePath);

        // /If this abstract pathname is already absolute, then the pathname  
        // string is simply returned as if by the getPath method. If this  
        // abstract pathname is the empty abstract pathname then the pathname  
        // string of the current user directory, which is named by the system  
        // property user.dir, is returned.  
        filePath = file.getAbsolutePath();//得到windows下的正确路径  
        return filePath + "/";
    }

    private static void checkConfigs() {
        //检查配置参数是否正确
    }

    public static String getString(String key) {
        return configs.getProperty(key);
    }

    public static String getString(String key, String defaultValue) {
        String value = configs.getProperty(key);
        if (StringUtil.isNullOrEmpty(value)) {
            value = defaultValue;
        }
        return value;
    }

    public static String[] getStrings(String key) {
        String value = getString(key);
        return value.split(",");
    }

    public static boolean getBoolean(String key) {

        String stringValue = getString(key);
        if (stringValue == null) {
            return true;
        }

        stringValue = stringValue.trim();
        if (stringValue.equals("")) {
            return false;
        }

        return Boolean.parseBoolean(stringValue);

    }

    public static long getLong(String key) {
        return Long.parseLong(getString(key));
    }

    public static int getInt(String key) {
        return Integer.parseInt(getString(key));
    }

    /**
     * @since 5.0
     */
    public static Integer getInteger(String key) {
        String value = getString(key);
        return value == null || value.equals("") ? null : Integer.parseInt(value);
    }

    /**
     * @since 5.0
     */
    public static int[] getIntArray(String key) {
        String[] values = getStrings(key);
        return StringUtil.toInts(values);
    }

    public static void tryLock(String filePath) throws Exception {
        File file = new File(filePath);
        file.deleteOnExit();
        @SuppressWarnings("resource")
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        FileChannel channel = raf.getChannel();
        FileLock lock = channel.tryLock();
        if (lock != null && lock.isValid()) {
        } else {
            throw new RuntimeException("program is already running:" + filePath + " is already locked");
        }
    }

    public static String getLockFile() {
        return lockFile;
    }

    public static void parseArgs(String[] args) {
        Map<String, String> argmaps = StringUtil.parseArgs(PREFIX_ARG, args);
        if (argmaps.containsKey("bindip")) {
            configs.setProperty(API_SERVER_LISTEN_IP, argmaps.get("bindip"));
        }
        if (argmaps.containsKey("port")) {
            configs.setProperty(API_SERVER_LISTEN_PORT, argmaps.get("port"));
        }
        if (argmaps.containsKey("line")) {
            configs.setProperty(TAIL_LINE_NUM, argmaps.get("line"));
        }
        LogUtil.info("--------- stork agent configs -----------------------");
        LogUtil.info("系统配置参数为：" + configs);
        LogUtil.info("--------- stork agent configs -----------------------");
    }

}
