package com.tao.common.retry.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tao.common.core.common.base.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

@Slf4j
public class RetryUtils {

    private RetryUtils() {

    }

    public static byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
        return bytes;
    }

    public static String toDigest(Object object) {
        return DigestUtils.md5DigestAsHex(toByteArray(object));
    }

    /**
     * Calculates the time interval to a com.tao.retry attempt. <br>
     * The interval increases exponentially with each attempt, at a rate of nextInterval *= 1.5
     * (where 1.5 is the backoff factor), to the maximum interval.
     *
     * @return time in nanoseconds from now until the next attempt.
     */
    public static long nextMaxInterval(long period, int attempt) {
        return (long) (period * Math.pow(2d, attempt - 1d));
    }

    public static long nextMaxInterval(int attempt) {
        return nextMaxInterval(2000, attempt);
    }

    public static Page convert2Page(IPage iPage) {
        Page page = new Page();
        page.setList(iPage.getRecords());
        page.setPageNum((int) iPage.getCurrent());
        page.setPageSize((int) iPage.getSize());
        page.setTotal(iPage.getTotal());
        page.setPages((int) iPage.getPages());
        return page;
    }
}
