package com.savor.savorphone.bean.platformvo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * PV统计参数数据
 * Created by wmm on 2016/11/30.
 */
public class PvParamStatisVo implements Serializable {
    //手机序列号
    public String deviceId;
    //所有视频展示次数json数据
    public List<PvStatisVo> countList = new ArrayList<>();

}
