package com.savor.savorphone.bean.platformvo;

import com.savor.savorphone.bean.BaseProResponse;
import com.savor.savorphone.bean.QueryPosBySessionIdResult;

import java.util.List;

public class QueryPosBySessionIdProResponseVo extends BaseProResponse {

    private static final long serialVersionUID = 6922151567069094539L;
    private int pos;

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    private List<QueryPosBySessionIdResult> resultList;

    public void setResultList(List<QueryPosBySessionIdResult> resultList) {
        this.resultList = resultList;
    }

    public List<QueryPosBySessionIdResult> getResultList() {
        return resultList;
    }
}
