package com.nobug.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author 389561407@qq.com
 * @version 1.0
 * @since 2022-11-29
 */
public class PropertiesUtil {


    private static final Properties release = getRelease();

    public static String getValue(String key) {
        return release.getProperty(key);
    }



    private static Properties getRelease() {
        InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("release.properties");
        Properties p = new Properties();
        try {
            p.load(inputStream);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return p;
    }



}
