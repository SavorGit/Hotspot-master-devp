package com.savor.savorphone.bean.platformvo;

import java.io.Serializable;

/**
 * PV统计数据
 * Created by wmm on 2016/11/30.
 */
public class PvStatisVo implements Serializable {
    //内容id
    public int contentId;
    //浏览次数
    public int count;

    @Override
    public boolean equals(Object obj) {
        return this.contentId == ((PvStatisVo) obj).contentId;
    }
}
