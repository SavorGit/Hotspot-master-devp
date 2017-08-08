package com.common.api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 流处理工具类
 * 
 * @author jiecw
 * @date 2010-9-9
 * 
 */
public class StreamUtil {

    /**
     * 流复制
     * 
     * @param in
     * @param out
     * @return
     * @throws IOException
     */
    public static long copyStream(InputStream in, OutputStream out)
            throws IOException {
        StreamUtil util = new StreamUtil(false);
        util.copyStreamInner(in, out);
        return util.length;
    }

    /**
     * 流复制
     * 
     * @param in
     * @param out
     * @throws IOException
     */
    public void copyStreamInner(InputStream in, OutputStream out)
            throws IOException {
        byte[] buff = new byte[4096];
        int length = 0;
        while ((length = in.read(buff)) >= 0) {
            if (out != null) {
                out.write(buff, 0, length);
                out.flush();
            }
            this.length += length;
            if (withMd5) {
                md.update(buff, 0, length);
            }
            if (handler != null
                    && !handler.onProcess(this.length, buff, 0, length)) {
                break;
            }
        }
    }

    private long length = 0;
    private MessageDigest md = null;
    private byte[] md5 = null;
    private boolean withMd5 = false;
    Processhandler handler = null;

    /**
     * 构造方法
     * 
     * @param withMd5
     *            处理流的过程中同时计算MD5值
     */
    public StreamUtil(boolean withMd5) {
        length = 0;
        this.withMd5 = withMd5;
        if (withMd5) {
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 构造方法
     * 
     * @param withMd5
     *            处理流的过程中同时计算MD5值
     * @param handler
     *            流处理进度反馈处理回调函数
     */
    public StreamUtil(boolean withMd5, Processhandler handler) {
        this(withMd5);
        this.handler = handler;
    }

    /**
     * 取得已计算的MD5值
     * 
     * @return
     */
    public byte[] getMD5() {
        if (!withMd5) {
            return null;
        }
        if (md5 == null) {
            md5 = md.digest();
        }
        return md5;
    }

    /**
     * 取得处理过的字节数
     * 
     * @return
     */
    public long getLength() {
        return length;
    }

    /**
     * 进度反馈处理接口
     * 
     * @author jiecw
     * @date 2010-9-9
     * 
     */
    public static interface Processhandler {
        /**
         * 进度反馈
         * 
         * @param alreadyRead
         *            已处理的字节数
         * @param b
         *            当前处理的字节
         * @param offset
         *            字节开始索引
         * @param len
         *            字节长度
         * @return
         */
        public boolean onProcess(long alreadyRead, byte[] b, int offset, int len);
    }
}
