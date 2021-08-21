package com.tao.common.core.utils;


import com.tao.common.core.common.keygen.SnowflakeKeyGenerator;
import com.tao.common.core.common.keygen.UUIDKeyGenerator;
import com.tao.common.core.common.other.ServiceUtils;

/**
 * @author william
 */
public class KeyUtils {
    private KeyUtils() {

    }

    public static long getSnakeflakeKey() {
        return ServiceUtils.getService(SnowflakeKeyGenerator.class).generateKey();
    }

    public static String getSnakeflakeStringKey() {
        return String.valueOf(getSnakeflakeKey());
    }

    public static String getUUIDKey() {
        return UUIDKeyGenerator.generateKey();
    }

    /**
     * 获得指定数目的id
     *
     * @param number
     * @return
     */
    public static long[] getSnakeflakeKeys(int number) {
        if (number < 1) {
            return new long[0];
        }
        long[] retArray = new long[number];
        for (int i = 0; i < number; i++) {
            retArray[i] = getSnakeflakeKey();
        }
        return retArray;
    }

    /**
     * 获得指定数目的id
     *
     * @param number
     * @return
     */
    public static String[] getSnakeflakeStringKeys(int number) {
        if (number < 1) {
            return new String[0];
        }
        String[] retArray = new String[number];
        for (int i = 0; i < number; i++) {
            retArray[i] = getSnakeflakeStringKey();
        }
        return retArray;
    }

    /**
     * 获得指定数目的id
     *
     * @param number
     * @return
     */
    public static String[] getUUIDKeys(int number) {
        if (number < 1) {
            return new String[0];
        }
        String[] retArray = new String[number];
        for (int i = 0; i < number; i++) {
            retArray[i] = getUUIDKey();
        }
        return retArray;
    }
}
