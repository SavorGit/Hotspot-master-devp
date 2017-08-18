package com.savor.savorphone.Servlet;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.common.api.utils.LogUtils;
import com.google.gson.Gson;
import com.savor.savorphone.R;
import com.savor.savorphone.SavorApplication;
import com.savor.savorphone.activity.HotspotMainActivity;
import com.savor.savorphone.activity.LocalVideoProAcitvity;
import com.savor.savorphone.activity.SingleImageProActivity;
import com.savor.savorphone.activity.PdfPreviewActivity;
import com.savor.savorphone.activity.SlidesActivity;
import com.savor.savorphone.activity.VideoPlayVODInHotelActivity;
import com.savor.savorphone.service.ProjectionService;
import com.savor.savorphone.utils.ActivitiesManager;
import com.savor.savorphone.utils.BaseResponse;
import com.savor.savorphone.projection.ProjectionManager;
import com.savor.savorphone.utils.RecordUtils;
import com.savor.savorphone.widget.CommonDialog;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by bichao on 2017/5/22.
 */

public class StopProjectionServlet extends HttpServlet{
//    http://xxx:xxxx/stopProjection?type=1&uname=bichao
//    type=1投屏被抢投，手机退出投屏；
//    type=2机顶盒主动或者意外退出投屏，通知手机退出投屏
//    uname，抢投人的用户名
    private Context context;
    private CommonDialog dialog;



    private Handler handler = new Handler();

    @Override
    protected void doGet(HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {

        String type = req.getParameter("type");
        String tipMsg = req.getParameter("tipMsg");

        if (!TextUtils.isEmpty(tipMsg)){
            tipMsg =  tipMsg+"正在操作投屏，您的投屏已经退出了";
        }else{
            tipMsg = "您的投屏已经退出了";
        }
        final String content = tipMsg;
        sendResponse(resp);
        if (type.equals("1")){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (dialog!=null&&dialog.isShowing()){
                        return;
                    }
                    Activity activity = ActivitiesManager.getInstance().getCurrentActivity();
                    RecordUtils.onEvent(activity,activity.getString(R.string.competitioned_hint));
                    dialog = new CommonDialog(activity, content, new CommonDialog.OnConfirmListener() {
                        @Override
                        public void onConfirm() {
                            dialog.cancel();
                            dialog = null;
                        }
                    });
                    dialog.show();
                }
            });
        }else if (type.equals("2")){
            handler.post(new Runnable() {
                @Override
                public void run() {

                    Activity activity = ActivitiesManager.getInstance().getCurrentActivity();
                    if (dialog!=null&&dialog.isShowing()){
                        return;
                    }
                    RecordUtils.onEvent(activity,activity.getString(R.string.competitioned_hint));
                    dialog = new CommonDialog(activity, content, new CommonDialog.OnConfirmListener() {
                        @Override
                        public void onConfirm() {
                            dialog.cancel();
                            dialog = null;
                        }
                    });
                    dialog.show();
                }
            });
        }


    }

    private void sendResponse(HttpServletResponse resp){
        Class projectionActivity  = ProjectionManager.getInstance().getProjectionActivity();
        if(projectionActivity == SingleImageProActivity.class) {
            SingleImageProActivity iGActivity = (SingleImageProActivity) ActivitiesManager.getInstance().getSpecialActivity(projectionActivity);
            if (iGActivity!=null){
                iGActivity.stopPro();
            }
        }else if(projectionActivity == VideoPlayVODInHotelActivity.class) {
//            ActivitiesManager.getInstance().popSpecialActivity(projectionActivity);
            Activity specialActivity = ActivitiesManager.getInstance().getSpecialActivity(VideoPlayVODInHotelActivity.class);
            if(specialActivity!=null&& specialActivity instanceof VideoPlayVODInHotelActivity) {
                VideoPlayVODInHotelActivity hotelActivity = (VideoPlayVODInHotelActivity) specialActivity;
                hotelActivity.handleProExit();
            }
            ProjectionService projectionService = SavorApplication.getInstance().projectionService;
            if(projectionService!=null) {
                projectionService.stopQuerySeek();
            }
        }else if(projectionActivity == LocalVideoProAcitvity.class) {
//            ActivitiesManager.getInstance().popSpecialActivity(projectionActivity);
            ProjectionService projectionService = SavorApplication.getInstance().projectionService;
            if(projectionService!=null) {
                projectionService.stopQuerySeek();
            }
            Activity currentActivity = ActivitiesManager.getInstance().getCurrentActivity();
            if(currentActivity instanceof LocalVideoProAcitvity) {
                LocalVideoProAcitvity proAcitvity = (LocalVideoProAcitvity) currentActivity;
                proAcitvity.stopProjection();
            }
        }else if(projectionActivity == SlidesActivity.class) {
            SlidesActivity slidesActivity = (SlidesActivity) ActivitiesManager.getInstance().getSpecialActivity(projectionActivity);
            if (slidesActivity!=null){
                slidesActivity.stopSlideProjection();
            }
            ProjectionService projectionService = SavorApplication.getInstance().projectionService;
            if(projectionService!=null) {
                projectionService.stopSlide();
            }
        }else if(projectionActivity == PdfPreviewActivity.class){
            Activity currentActivity = ActivitiesManager.getInstance().getCurrentActivity();
            if(currentActivity instanceof PdfPreviewActivity) {
                PdfPreviewActivity pdfPreviewActivity = (PdfPreviewActivity) ActivitiesManager.getInstance().getCurrentActivity();
                pdfPreviewActivity.stopPdfPro();
            }
        }
        HotspotMainActivity mainActivity = (HotspotMainActivity)ActivitiesManager.getInstance().getSpecialActivity(HotspotMainActivity.class);
        if (mainActivity!=null){
            mainActivity.initBackgroundProjectionHint();
        }
        ProjectionManager.getInstance().resetProjection();
        resp.setContentType("application/json;charset=utf-8");
        resp.setStatus(200);

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setInfo("停止成功啦，老六和小九恋爱啦");
        baseResponse.setResult(10000);
        String response = new Gson().toJson(baseResponse);
        LogUtils.d("返回结果:" + resp);
        try {
            resp.getWriter().println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
