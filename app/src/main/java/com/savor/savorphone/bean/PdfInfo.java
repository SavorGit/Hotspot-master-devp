package com.savor.savorphone.bean;

import java.io.Serializable;

/**
 * Created by hezd on 2017/4/26.
 */

public class PdfInfo implements Serializable {
    /**pdf路径*/
    private String path;
    /**pdf文件名称*/
    private String name;

    @Override
    public String toString() {
        return "PdfInfo{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PdfInfo info = (PdfInfo) o;

        if (path != null ? !path.equals(info.path) : info.path != null) return false;
        return name != null ? name.equals(info.name) : info.name == null;

    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
