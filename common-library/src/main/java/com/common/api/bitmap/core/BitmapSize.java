package com.common.api.bitmap.core;

/**
 * 构造一个BitmapSize 图片配置参数，主要用来配置图片的高和宽
 * Date: 13-11-7
 * Time: 下午1:20
 */
public class BitmapSize {

    /**创建一个0*0的BitmapSize */
    public static final BitmapSize ZERO = new BitmapSize(0, 0);

    /**BitmapSize的宽 */
    private int width;
    /**BitmapSize的高 */
    private int height;

    public BitmapSize() {
    }

    public BitmapSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * 缩小sampleSize尺寸后， 返回新的对象.
     */
    public BitmapSize scaleDown(int sampleSize) {
        return new BitmapSize(width / sampleSize, height / sampleSize);
    }

    /**
     * 根据参数scale，高和宽分别乘以倍数后，返回对象.
     */
    public BitmapSize scale(float scale) {
        return new BitmapSize((int) (width * scale), (int) (height * scale));
    }
    
    /**
     * @return 返回BitmapSize 的宽度
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * @param width 设置BitmapSize 的宽度
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @ 返回BitmapSize 的高度
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height 设置BitmapSize 的高度
     */
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "_" + width + "_" + height;
    }
}
