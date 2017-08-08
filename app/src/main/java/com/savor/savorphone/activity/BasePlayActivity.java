package com.savor.savorphone.activity;

import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.savor.savorphone.bean.platformvo.LocationStaticsRequestVo;


public class BasePlayActivity extends BaseActivity {
    private PowerManager powerManager = null;
    private WakeLock wakeLock = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        powerManager = (PowerManager) this.getSystemService(this.POWER_SERVICE);
        wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        new StopAsyncTask(this, null).execute(1);
        super.onSaveInstanceState(outState);
    }

    protected LocationStaticsRequestVo initLocationStaticsRequestVo(int interactType, int interactTime) {
        LocationStaticsRequestVo locationStaticsRequestVo = new LocationStaticsRequestVo();
        locationStaticsRequestVo.setDeviceId(mSession.getImei());
        locationStaticsRequestVo.setDeviceModel(mSession.getModel());
        locationStaticsRequestVo.setHotelId(mSession.getHotelid());
        locationStaticsRequestVo.setRoomId(mSession.getRoomid());
        locationStaticsRequestVo.setInteractType(interactType);
        locationStaticsRequestVo.setInteractTime(interactTime);
        return locationStaticsRequestVo;
    }

    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();
    }

    @Override
    public void getViews() {

    }

    @Override
    public void setViews() {

    }

    @Override
    public void setListeners() {

    }
}
