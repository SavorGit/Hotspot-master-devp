package com.savor.savorphone.bean;

import java.io.Serializable;

/**
 * 专题名称
 * Created by hezd on 2017/7/16.
 */

public class SpecialNameResult implements Serializable {
    private String specialName;

    @Override
    public String toString() {
        return "SpecialNameResult{" +
                "specialName='" + specialName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpecialNameResult that = (SpecialNameResult) o;

        return specialName != null ? specialName.equals(that.specialName) : that.specialName == null;

    }

    @Override
    public int hashCode() {
        return specialName != null ? specialName.hashCode() : 0;
    }

    public String getSpecialName() {
        return specialName;
    }

    public void setSpecialName(String specialName) {
        this.specialName = specialName;
    }
}
