package com.savor.savorphone.core;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.common.api.utils.AppUtils;
import com.savor.savorphone.bean.BaseProReqeust;
import com.savor.savorphone.bean.MediaInfo;
import com.savor.savorphone.bean.RotateRequest;
import com.savor.savorphone.bean.SeekRequest;
import com.savor.savorphone.bean.SmallPlatInfoBySSDP;
import com.savor.savorphone.bean.SmallPlatformByGetIp;
import com.savor.savorphone.bean.TvBoxSSDPInfo;
import com.savor.savorphone.utils.STIDUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AppApi {
    public static final String APK_DOWNLOAD_FILENAME = "NewApp.apk";

    /**云平台php接口*/
//    public static final String CLOUND_PLATFORM_PHP_URL = "http://devp.mobile.littlehotspot.com/";
  public static final String CLOUND_PLATFORM_PHP_URL = "http://mobile.littlehotspot.com/";

    /**
     * 常用的一些key值 ,签名、时间戳、token、params
     */
    public static final String SIGN = "sign";
    public static final String TIME = "time";
    public static final String TOKEN = "token";
    public static final String PARAMS = "params";


    /**这是一个临时值，以请求时传入的值为准*/
    public static String tvBoxUrl;
    public static int hotelid;
    public static int roomid;

    /**
     * Action-自定义行为 注意：自定义后缀必须为以下结束 _FORM:该请求是Form表单请求方式 _JSON:该请求是Json字符串
     * _XML:该请求是XML请求描述文件 _GOODS_DESCRIPTION:图文详情 __NOSIGN:参数不需要进行加密
     */
    public static enum Action {
        TEST_POST_JSON,
        TEST_GET_JSON,
        TEST_DOWNLOAD_JSON,
        /**下载启动图或视频*/
        DOWNLOAD_START_UP_JSON,
        /**热点上拉（底部加載）*/
        POST_GETVODLIST_JSON,
        /**热点下拉（頂部刷新）*/
        POST_GETVODLIST_LAST_JSON,
        GET_CATEGORY_JSON,
        POST_FEEDBACK_JSON,
        /**分类上拉*/
        POST_TOPICLIST_JSON,
        /**分类下拉*/
        POST_LAST_TOPICLIST_JSON,
        /**点对点呼码*/
        GET_CALL_CODE_BY_BOXIP_JSON,
        /**点对点校验三位数字*/
        GET_VERIFY_CODE_BY_BOXIP_JSON,
        /**检查ap是否可连接*/
        POST_CHECKAP_CONNECTION_JSON,
        /**行为统计*/
        POST_TEMP_STATISTICS_JSON,
        /**请求机顶盒播放进度*/
        GET_QUERY_SEEK_JSON,
        /**通知机顶盒更新进度*/
        POST_NOTIFY_TVBOX_SEEK_JSON,
        /**通知机顶盒暂停*/
        GET_NOTIFY_PAUSE_JSON,
        /**通知机顶盒继续播放*/
        POST_NOTIFY_REPLAY_JSON,
        /**房间统计*/
        POST_ROOM_STATISTICS_JSON,
        /**退出行为统计*/
        POST_EXIT_STATISTICS_JSON,
        /**停止播放*/
        POST_NOTIFY_TVBOX_STOP_JSON,
        /**通知机顶盒旋转*/
        POST_PHOTO_ROTATE_JSON,
        /**获取小平台地址*/
        GET_SAMLL_PLATFORMURL_JSON,
        /**小平台呼码*/
        GET_CALL_QRCODE_JSON,
        /**改变进度*/
        POST_SEEK_CHANGE_JSON,
        /**记录用户首次使用*/
        POST_STATICS_FIRSTUSE_JSON,
        /**点播下拉刷新*/
        POST_LAST_HOTEL_VOD_JSON,
        /**上拉刷新*/
        POST_BOTTOM_HOTEL_VOD_JSON,
        /**升级*/
        POST_UPGRADE_JSON,

        POST_NOTIFY_VOL_UP_JSON,
        POST_NOTIFY_VOL_DOWN_JSON,
        POST_NOTIFY_VOL_ON_JSON,
        POST_NOTIFY_VOL_OFF_JSON,
        /**图片投屏*/
        POST_IMAGE_PROJECTION_JSON,
        /**点播头品*/
        GET_VOD_PRO_JSON,
        /**本地视频投屏*/
        POST_LOCAL_VIDEO_PRO_JSON,
        /**投蛋*/
        GET_EGG_JSON,
        /**砸蛋*/
        GET_HIT_EGG_JSON,
        /**获取机顶盒信息通过数字*/
        POST_BOX_INFO_JSON,
        /**启动*/
        GET_CLIENTSTART_JSON,
        /**中奖上报*/
        GET_AWARD_JSON,
        /**服务员推广统计接口**/
        POST_WAITER_EXTENSION_JSON,

        /**客户端得到所有的投屏酒楼距离**/
        GET_ALL_DISTANCE_JSON,
        /**获取最近酒楼（目前是三家)*/
        GET_NEARLY_HOTEL_JSON,
        /**收藏列表下拉20条*/
        POST_LAST_COLLECTION_JSON,

        /**收藏列表上拉20条*/
        POST_UP_COLLECTION_JSON,

        /**添加到收藏列表**/
        GET_ADD_MY_COLLECTION_JSON,
        /**检查文章是否被收藏**/
        GET_IS_COLLECTION_JSON,
        /**获取砸蛋活动配置**/
        POST_SMASH_EGG_JSON,
        /**创富生活接口**/
        POST_WEALTH_LIFE_LIST_JSON,
        /**判断文章是否在线*/
        POST_CONTENT_IS_ONLINE_JSON,
        /**图集接口**/
        POST_PICTURE_SET_JSON,
        /**获取专题列表**/
        POST_RECOMMEND_LIST_JSON,
        /**获取投屏点播列表*/
        POST_DEMAND_LIST_JSON,
        /**获取砸蛋记录url*/
        POST_AWARD_RECORD_JSON,
        /**获取专题名称*/
        POST_SPECIAL_NAME_JSON,
        /**电视推荐*/
        POST_TV_RECOMMEND_JSON,
        /**专题组详情*/
        POST_SPECIAL_DETAIL_JSON,
        /**专题组列表*/
        POST_SPECIAL_LIST_JSON,
    }

    /**
     * API_URLS:(URL集合)
     */
    public static final HashMap<Action, String> API_URLS = new HashMap<Action, String>() {
        private static final long serialVersionUID = -8469661978245513712L;

        {
//            put(Action.TEST_POST_JSON, CMS_PLATFORM_BASE_URL + "12");
            put(Action.TEST_GET_JSON, "https://www.baidu.com/");

            //热点
            put(Action.POST_GETVODLIST_JSON, formatPhpUrl("content/Home/getVodList"));
            put(Action.POST_GETVODLIST_LAST_JSON, formatPhpUrl("content/Home/getLastVodList"));

            //分类
            put(Action.GET_CATEGORY_JSON, formatPhpUrl("basedata/category/getCategoryList"));
            //意见反馈
          //  put(Action.POST_FEEDBACK_JSON, formatPhpUrl("mobile/api/feedback"));
         //   http://mobile.rerdian.com/
            put(Action.POST_FEEDBACK_JSON, formatPhpUrl("feed/Feedback/feedInsert"));

            //分类视频-上拉加载更多
            put(Action.POST_TOPICLIST_JSON, formatPhpUrl("catvideo/catvideo/getTopList"));
            //分类视频-下拉刷新20条
            put(Action.POST_LAST_TOPICLIST_JSON, formatPhpUrl("catvideo/catvideo/getLastTopList"));
            // 点播下啦
            put(Action.POST_LAST_HOTEL_VOD_JSON, formatPhpUrl("content/home/getLastHotelList"));
            // 点播上啦
            put(Action.POST_BOTTOM_HOTEL_VOD_JSON, formatPhpUrl("content/home/getHotelList"));
            //升级
            put(Action.POST_UPGRADE_JSON, formatPhpUrl("version/Upgrade/index"));
            put(Action.POST_NOTIFY_VOL_UP_JSON, tvBoxUrl);
            put(Action.POST_NOTIFY_VOL_DOWN_JSON, tvBoxUrl);
            put(Action.POST_NOTIFY_VOL_ON_JSON, tvBoxUrl);
            put(Action.POST_NOTIFY_VOL_OFF_JSON, tvBoxUrl);
            put(Action.POST_CHECKAP_CONNECTION_JSON, tvBoxUrl);
            put(Action.GET_QUERY_SEEK_JSON, tvBoxUrl);
            put(Action.POST_NOTIFY_TVBOX_SEEK_JSON, tvBoxUrl);
            put(Action.GET_NOTIFY_PAUSE_JSON, tvBoxUrl);
            put(Action.POST_NOTIFY_REPLAY_JSON, tvBoxUrl);
            put(Action.POST_EXIT_STATISTICS_JSON, tvBoxUrl);
            put(Action.POST_NOTIFY_TVBOX_STOP_JSON, tvBoxUrl);
            put(Action.POST_PHOTO_ROTATE_JSON, tvBoxUrl);
            put(Action.GET_SAMLL_PLATFORMURL_JSON, formatPhpUrl("basedata/Ip/getIp"));
            put(Action.GET_CALL_QRCODE_JSON,tvBoxUrl);
            put(Action.POST_SEEK_CHANGE_JSON,tvBoxUrl);
            put(Action.POST_STATICS_FIRSTUSE_JSON, formatPhpUrl("basedata/Firstuse/pushData"));
            put(Action.POST_IMAGE_PROJECTION_JSON,tvBoxUrl);
            put(Action.GET_VOD_PRO_JSON,tvBoxUrl);
            put(Action.POST_BOX_INFO_JSON,tvBoxUrl);
            put(Action.GET_CLIENTSTART_JSON,formatPhpUrl("clientstart/clientstart/getInfo"));
            put(Action.POST_WAITER_EXTENSION_JSON,formatPhpUrl("download/DownloadCount/recordCount"));
            put(Action.GET_AWARD_JSON, formatPhpUrl("Award/Award/recordAwardLog"));
            put(Action.GET_ALL_DISTANCE_JSON, formatPhpUrl("Screendistance/distance/getAllDistance"));
            put(Action.GET_NEARLY_HOTEL_JSON,formatPhpUrl("Screendistance/distance/getHotelDistance"));
            put(Action.POST_LAST_COLLECTION_JSON,formatPhpUrl("APP3/UserCollection/getLastCollectoinList"));
            put(Action.POST_UP_COLLECTION_JSON,formatPhpUrl("APP3/UserCollection/getUpCollectoinList"));
            put(Action.GET_ADD_MY_COLLECTION_JSON,formatPhpUrl("APP3/UserCollection/addMyCollection"));
            put(Action.GET_IS_COLLECTION_JSON,formatPhpUrl("APP3/UserCollection/getCollectoinState"));
            put(Action.POST_SMASH_EGG_JSON,formatPhpUrl("APP3/Activity/smashEgg"));
            put(Action.POST_WEALTH_LIFE_LIST_JSON,formatPhpUrl("APP3/Content/getLastCategoryList"));
            put(Action.POST_CONTENT_IS_ONLINE_JSON,formatPhpUrl("APP3/Content/isOnlie"));
            put(Action.POST_PICTURE_SET_JSON,formatPhpUrl("APP3/Content/picDetail"));
            put(Action.POST_RECOMMEND_LIST_JSON,formatPhpUrl("APP3/Recommend/getRecommendInfo"));
            put(Action.POST_DEMAND_LIST_JSON,formatPhpUrl("APP3/Content/demandList"));
            put(Action.POST_AWARD_RECORD_JSON,formatPhpUrl("APP3/Activity/geteggAwardRecord"));
            put(Action.POST_SPECIAL_NAME_JSON,formatPhpUrl("APP3/Special/getSpecialName"));
            put(Action.POST_TV_RECOMMEND_JSON,formatPhpUrl("APP3/Recommend/getTvPlayRecommend"));
            put(Action.POST_SPECIAL_DETAIL_JSON,formatPhpUrl("APP3/Special/specialGroupDetail"));
            put(Action.POST_SPECIAL_LIST_JSON,formatPhpUrl("APP3/Special/specialGroupList"));
        }
    };


    /**
     * php后台接口
     * @param url
     * @return
     */
    private static String formatPhpUrl(String url) {
        return CLOUND_PLATFORM_PHP_URL +url;
    }

    public static void testPost(Context context, String orderNo, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("loginfield", "15901559579");
        params.put("password", "123456");
        params.put("dr_rg_cd", "86");
        params.put("version_code", 19 + "");
        new AppServiceOk(context, Action.TEST_POST_JSON, handler, params).post(false, false, true, true);

    }

    public static void testGet(Context context, ApiRequestListener handler) {
        SmallPlatInfoBySSDP smallPlatInfoBySSDP = Session.get(context).getSmallPlatInfoBySSDP();
        API_URLS.put(Action.TEST_GET_JSON,"http://"+ smallPlatInfoBySSDP.getServerIp()+":"+ smallPlatInfoBySSDP.getCommandPort()+"/small-platform-1.0.0.0.1-SNAPSHOT/com/execute/call-tdc");
        final HashMap<String, Object> params = new HashMap<String, Object>();
        new AppServiceOk(context, Action.TEST_GET_JSON, handler, params).get();

    }

    public static void testDownload(Context context, String url, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        String target = AppUtils.getPath(context, AppUtils.StorageFile.file);

        String targetApk = target + "123.apk";
        File tarFile = new File(targetApk);
        if (tarFile.exists()) {
            tarFile.delete();
        }
        new AppServiceOk(context, Action.TEST_DOWNLOAD_JSON, handler, params).downLoad(url, targetApk);
    }

    public static void downApp(Context context, String url, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        String target = AppUtils.getPath(context, AppUtils.StorageFile.file);

        String targetApk = target + APK_DOWNLOAD_FILENAME;
        File tarFile = new File(targetApk);
        if (tarFile.exists()) {
            tarFile.delete();
        }
        new AppServiceOk(context, Action.TEST_DOWNLOAD_JSON, handler, params).downLoad(url, targetApk);

    }

    public static void donloadStartUpFile(Context context,String targetFile,String url,ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        File file = new File(targetFile);
        if(file.exists()) {
            file.delete();
        }
        new AppServiceOk(context,Action.DOWNLOAD_START_UP_JSON,handler,params).downLoad(url,targetFile);
    }

    /**热点底部加載*/
    public static void getVodList(Context context,ApiRequestListener listener,Long createTime) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("createTime", createTime);
        new AppServiceOk(context,Action.POST_GETVODLIST_JSON,listener,params).post();
    }

    /**热点头部加載*/
    public static void getLastVodList(Context context,ApiRequestListener listener,String hotelId,String flag) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("hotelId", hotelId);
        params.put("flag", flag);

        new AppServiceOk(context,Action.POST_GETVODLIST_LAST_JSON,listener,params).post();
    }
    /**获取分类列表数据*/
    public static void getCategoryList(Context context,ApiRequestListener listener) {
        new AppServiceOk(context,Action.GET_CATEGORY_JSON,listener,null).post();
    }

    public static void submitFeedback(Context context, String deviceId, String suggestion,String contactWay,ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("deviceId", STIDUtil.getDeviceId(context));
        params.put("suggestion", suggestion);
        params.put("contactWay", contactWay);
        new AppServiceOk(context, Action.POST_FEEDBACK_JSON, handler, params).post();

    }

    /***
     * 分类上拉加载
     */
    public static void getCategoryBottomList(Context context, int categoryId, long createTime, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("categoryId", categoryId);
        params.put("createTime", createTime);
        new AppServiceOk(context, Action.POST_TOPICLIST_JSON, handler, params).post();

    }

    //下拉刷新
    public static void getCategoryTopicList(Context context, int categoryId, String flag, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("categoryId", categoryId);
        params.put("flag", flag);
        new AppServiceOk(context, Action.POST_LAST_TOPICLIST_JSON, handler, params).post();

    }

    /**点播投屏*/
    public static void vodProection(Context context, String url, BaseProReqeust baseProReqeust,int force,ApiRequestListener handler) {
        final HashMap<String, String> params = new HashMap<>();
        params.put("name", baseProReqeust.getAssetname());
        params.put("type", baseProReqeust.getVodType()+"");
        params.put("deviceName", Build.MODEL);
        params.put("force",force+"");
        HashMap<String, String> bodyParams = new HashMap<>();
        if(!TextUtils.isEmpty(url)) {
            new AppServiceOk(context, formatProUrl(context,url+"/vod?",params),Action.GET_VOD_PRO_JSON, handler, bodyParams).get();
        }
    }

    /**视频投屏*/
    public static void localVideoPro(Context context, String url, MediaInfo VideoInfo, int force, ApiRequestListener handler) {
        final HashMap<String, String> params = new HashMap<>();
        params.put("deviceName", Build.MODEL);
        params.put("force",force+"");
        final HashMap<String, String> bodyParams = new HashMap<>();
        bodyParams.put("mediaPath", VideoInfo.getAsseturl());
        new AppServiceOk(context, formatProUrl(context,url+"/video?",params),Action.POST_LOCAL_VIDEO_PRO_JSON, handler, bodyParams).post();
    }

    /**投蛋*/
    public static void getEgg(Context context, String url,String hunger, int force,ApiRequestListener handler) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date(System.currentTimeMillis());
        String format = simpleDateFormat.format(date);
        final HashMap<String, String> params = new HashMap<>();
        params.put("deviceName", Build.MODEL);
        params.put("date",format);
        params.put("hunger",hunger);
        params.put("force",String.valueOf(force));

        final HashMap<String, String> bodyParams = new HashMap<>();

        new AppServiceOk(context, formatProUrl(context,url+"/egg?",params),Action.GET_EGG_JSON, handler, bodyParams).get();
    }

    /**砸蛋*/
    public static void hitEgg(Context context, String url,String projectId, ApiRequestListener handler) {
        final HashMap<String, String> params = new HashMap<>();
        params.put("deviceName", Build.MODEL);
        params.put("projectId", projectId);

        final HashMap<String, String> bodyParams = new HashMap<>();

        new AppServiceOk(context, formatProUrl(context,url+"/hitEgg?",params),Action.GET_HIT_EGG_JSON, handler, bodyParams).get();
    }

    /**上报中奖数据*/
    public static void recordAwardLog(Context context, String prizeid,String time,String mac,ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("prizeid", prizeid);
        params.put("award_time", time);
        params.put("mac", mac);
        params.put("deviceid",  STIDUtil.getDeviceId(context));

        new AppServiceOk(context,Action.GET_AWARD_JSON,handler,params).post();
    }
    /**
     * 请求机顶盒投屏通过上传图片的方式
     * @param context
     * @param url
     * @param baseProReqeust
     * @param filePath
     * @param small 是否缩略图，0：不是，1：是
     * @param handler
     */
    public static void updateScreenProjectionFile(Context context, String url, BaseProReqeust baseProReqeust, String filePath, int small, int force,ApiRequestListener handler) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("isThumbnail", small+"");
        params.put("imageId", baseProReqeust.getImageId());
        params.put("deviceName", Build.MODEL+"");
        params.put("rotation", baseProReqeust.getRotatevalue()+"");
        params.put("imageType", baseProReqeust.getImageType());
        params.put("seriesId",baseProReqeust.getSeriesId());
        params.put("force",force+"");
        new AppServiceOk(context, formatImageProUrl(context,url+"/pic?",params),Action.POST_IMAGE_PROJECTION_JSON, handler, params).uploadFile(filePath,true,true);

    }

    /**通过sessionid获取当前机顶盒播放进度*/
    public static void getSeekBySessionId(Context context, String url,String projectId,ApiRequestListener handler) {
        if(TextUtils.isEmpty(url))
            return;
        final HashMap<String, String> params = new HashMap<>();
        params.put("projectId", projectId);
        final HashMap<String, String> bodyParams = new HashMap<>();
        if(!TextUtils.isEmpty(url)) {
            new AppServiceOk(context,formatProUrl(context,url+"/query?",params),Action.GET_QUERY_SEEK_JSON,handler,bodyParams).get();
        }
    }

    /**通知机顶盒更新播放进度*/
    public static void notifyTvBoxSeekChange(Context context, String url, SeekRequest seekRequest,String projectId, ApiRequestListener handler) {
        final HashMap<String, String> params = new HashMap<>();
        int absolutepos = seekRequest.getAbsolutepos();
        params.put("position", absolutepos+"");
        params.put("projectId",projectId);
        final HashMap<String, String> bodyParams = new HashMap<>();
        if(!TextUtils.isEmpty(url)) {
            new AppServiceOk(context,formatProUrl(context,url+"/seek?",params),Action.POST_SEEK_CHANGE_JSON,handler,bodyParams).get();
        }
    }

    /**通知机顶盒暂停*/
    public static void notifyTvBoxPause(Context context, String url, String projectoinId, ApiRequestListener handler) {
        final HashMap<String, String> params = new HashMap<>();
        params.put("projectId", projectoinId);
        final HashMap<String, String> boyParam = new HashMap<>();
        if(!TextUtils.isEmpty(url)) {
            new AppServiceOk(context,formatProUrl(context,url+"/pause?",params),Action.GET_NOTIFY_PAUSE_JSON,handler,boyParam).get();
        }
    }

    /**通知机顶盒重新播放*/
    public static void notifyTvBoxReplay(Context context, String url, String projectId, ApiRequestListener handler) {
        final HashMap<String, String> params = new HashMap<>();
        params.put("projectId", projectId);
        final HashMap<String, String> bodyParams = new HashMap<>();
        if(!TextUtils.isEmpty(url)){
            new AppServiceOk(context,formatProUrl(context,url+"/resume?",params),Action.POST_NOTIFY_REPLAY_JSON,handler,bodyParams).get();
        }
    }

    /**通知机顶盒音量加*/
    public static void notifyTvBoxVolUp(Context context, String url, String projectId,ApiRequestListener handler) {
        final HashMap<String, String> params = new HashMap<>();
        params.put("action", 4+"");
        params.put("projectId", projectId);
        final HashMap<String, String> bodyParams = new HashMap<>();
        if(!TextUtils.isEmpty(url)) {
            new AppServiceOk(context,formatProUrl(context,url+"/volume?",params),Action.POST_NOTIFY_VOL_UP_JSON,handler,bodyParams).get();
        }
    }

    /**通知机顶盒音量减*/
    public static void notifyTvBoxVolDown(Context context, String url,String projectId, ApiRequestListener handler) {
        final HashMap<String, String> params = new HashMap<>();
        params.put("action", 3+"");
        params.put("projectId", projectId);
        final HashMap<String, String> bodyParams = new HashMap<>();
        if(!TextUtils.isEmpty(url)) {
            new AppServiceOk(context,formatProUrl(context,url+"/volume?",params),Action.POST_NOTIFY_VOL_DOWN_JSON,handler,bodyParams).get();
        }
    }
    /**通知机顶盒声音开*/
    public static void notifyTvBoxVolOn(Context context, String url, String projectId,ApiRequestListener handler) {
        final HashMap<String, String> params = new HashMap<>();
        params.put("action", 2+"");
        params.put("projectId", projectId);
        final HashMap<String, String> bodyParams = new HashMap<>();
        if(!TextUtils.isEmpty(url)) {
            new AppServiceOk(context,formatProUrl(context,url+"/volume?",params),Action.POST_NOTIFY_VOL_ON_JSON,handler,bodyParams).get();
        }
    }

    /**通知机顶盒声音关*/
    public static void notifyTvBoxVolOff(Context context, String url,String projectId, ApiRequestListener handler) {
        final HashMap<String, String> params = new HashMap<>();
        params.put("action", 1+"");
        params.put("projectId", projectId);
        final HashMap<String, String> bodyParams = new HashMap<>();
        if(!TextUtils.isEmpty(url)) {
            new AppServiceOk(context,formatProUrl(context,url+"/volume?",params),Action.POST_NOTIFY_VOL_OFF_JSON,handler,bodyParams).get();
        }
    }

    /**通知机顶盒停止播放*/
    public static void notifyTvBoxStop(Context context, String url,String projectid ,ApiRequestListener handler) {
        final HashMap<String, String> params = new HashMap<>();
        params.put("projectId", projectid);
        final HashMap<String, String> realParams = new HashMap<>();
        if(!TextUtils.isEmpty(url)) {
            new AppServiceOk(context,formatProUrl(context,url+"/stop?",params),Action.POST_NOTIFY_TVBOX_STOP_JSON,handler,realParams).get();
        }
    }

    /**通知机顶盒旋转*/
    public static void notifyTvBoxRotate(Context context, String url, RotateRequest rotateRequest, ApiRequestListener handler) {
        final HashMap<String, String> params = new HashMap<>();
        params.put("projectId", rotateRequest.getProjectId());
        final HashMap<String, String> realParams = new HashMap<>();
        if(!TextUtils.isEmpty(url)) {
            new AppServiceOk(context,formatProUrl(context,url+"/rotate?",params),Action.POST_PHOTO_ROTATE_JSON,handler,realParams).get();
        }
    }

    /**获取小平台地址*/
    public static void getSmallPlatformIp(Context context, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        new AppServiceOk(context,Action.GET_SAMLL_PLATFORMURL_JSON,handler,params).get();
    }

    /**
     * 通过云平台获取的小平台地址进行呼码
     * @param context
     * @param handler
     */
    public static void callQrcodeByClound(Context context, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        Session session = Session.get(context);
        SmallPlatformByGetIp smallPlatformByGetIp = session.getmSmallPlatInfoByIp();
        if(smallPlatformByGetIp!=null) {
            String localIp = smallPlatformByGetIp.getLocalIp();
            String type = smallPlatformByGetIp.getType();
            String command_port = smallPlatformByGetIp.getCommand_port();
            if(!TextUtils.isEmpty(localIp)
                    &&!TextUtils.isEmpty(type)
                    &&!TextUtils.isEmpty(command_port)) {
                String url = "http://"+localIp+":"+command_port+"/"+type.toLowerCase()+"/command/execute/call-tdc";
                new AppServiceOk(context,url,Action.GET_CALL_QRCODE_JSON,handler,params).get();
            }
        }
    }

    /**
     * 通过小平台ssdp提供的小平台地址进行呼玛
     * @param context
     * @param handler
     */
    public static void callQrcodeBySPSSDP(Context context, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        Session session = Session.get(context);
        SmallPlatInfoBySSDP smallPlatInfoBySSDP = session.getSmallPlatInfoBySSDP();
        if(smallPlatInfoBySSDP !=null) {
            String url = getSPlatUrl(smallPlatInfoBySSDP)+"call-tdc";
            new AppServiceOk(context,url,Action.GET_CALL_QRCODE_JSON,handler,params).get();
        }
    }

    /**
     * 通过盒子ssdp获取的小平台地址进行呼码
     * @param context
     * @param info
     * @param handler
     */
    public static void callQrcodeFromBoxInfo(Context context, TvBoxSSDPInfo info,ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        String serverIp = info.getServerIp();
        String commandPort = info.getCommandPort();
        String type = "small";
        String url = "http://"+serverIp+":"+commandPort+"/"+type.toLowerCase()+"/command/execute/call-tdc";
        new AppServiceOk(context,url,Action.GET_CALL_QRCODE_JSON,handler,params).get();
    }

    public static void callCodeByBoxIp(Context context, String boxUrl,ApiRequestListener handler) {
        final HashMap<String, String> params = new HashMap<>();
        String url = formatProUrl(context,boxUrl + "/showCode?",params);
        final HashMap<String, String> bodyPramas = new HashMap<>();
        new AppServiceOk(context,url,Action.GET_CALL_CODE_BY_BOXIP_JSON,handler,bodyPramas).get();
    }

    public static void verifyNumByBoxIp(Context context, String boxUrl, String number, ApiRequestListener handler) {
        final HashMap<String, String> params = new HashMap<>();
        params.put("code",number);
        String url = formatProUrl(context,boxUrl + "/verify?",params);
        final HashMap<String, String> bodyPramas = new HashMap<>();
        if(!TextUtils.isEmpty(url)) {
            new AppServiceOk(context,url,Action.GET_VERIFY_CODE_BY_BOXIP_JSON,handler,bodyPramas).get();
        }
    }


    public static void getAllDistance(Context context, String hotelid, String lng,String lat, String pageNum, ApiRequestListener handler) {
        final HashMap<String, String> params = new HashMap<>();
        params.put("hotelid",hotelid);
        params.put("lng",lng);
        params.put("lat",lat);
        params.put("pageNum",pageNum);
        new AppServiceOk(context,Action.GET_ALL_DISTANCE_JSON,handler,params).get();



//        final HashMap<String, Object> params = new HashMap<String, Object>();
//        params.put("prizeid", prizeid);
//        params.put("award_time", time);
//        params.put("mac", mac);
//        params.put("deviceid",  STIDUtil.getDeviceId(context));
//
//        new AppServiceOk(context,Action.GET_AWARD_JSON,handler,params).post();
    }

   // getAllDistance
    /**
     * 通过数字获取机顶盒信息
     */
    public static void verifyNumBySpSSDP(Context context, String number, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        Session session = Session.get(context);
        SmallPlatInfoBySSDP smallPlatInfoBySSDP = session.getSmallPlatInfoBySSDP();
        if(smallPlatInfoBySSDP !=null) {
            String type = smallPlatInfoBySSDP.getType();
            String serverIp = smallPlatInfoBySSDP.getServerIp();
            String commandPort = smallPlatInfoBySSDP.getCommandPort();
            String url = "http://"+serverIp+":"+commandPort+"/"+type.toLowerCase()+"/command/box-info/"+number;
//            String url = "http://192.168.2.154:"+commandPort+"/small/command/box-info/"+number;
            new AppServiceOk(context,url,Action.POST_BOX_INFO_JSON,handler,params).get();
        }
    }

    /**
    * 通过云平台返回的小平台地址进行校验
     */
    public static void verifyNumByClound(Context context, String number, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        Session session = Session.get(context);
        SmallPlatformByGetIp smallPlatformByGetIp = session.getmSmallPlatInfoByIp();
        if(smallPlatformByGetIp !=null) {
            String type = smallPlatformByGetIp.getType();
            String serverIp = smallPlatformByGetIp.getLocalIp();
            String commandPort = smallPlatformByGetIp.getCommand_port();
            String url = "http://"+serverIp+":"+commandPort+"/"+type.toLowerCase()+"/command/box-info/"+number;
            new AppServiceOk(context,url,Action.POST_BOX_INFO_JSON,handler,params).get();
        }
    }

    /**
     * 通过机顶盒ssdp返回的小平台地址进行校验
     */
    public static void verifyNumByBoxSSDP(Context context, String number, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        Session session = Session.get(context);
        TvBoxSSDPInfo tvBoxSSDPInfo = session.getTvBoxSSDPInfo();
        if(tvBoxSSDPInfo!=null) {
            String serverIp = tvBoxSSDPInfo.getServerIp();
            String commandPort = tvBoxSSDPInfo.getCommandPort();
            String type = "small";
            String url = "http://"+serverIp+":"+commandPort+"/"+type.toLowerCase()+"/command/box-info/"+number;
            new AppServiceOk(context,url,Action.POST_BOX_INFO_JSON,handler,params).get();
        }
    }

    /**统计首次在酒店使用*/
    public static void staticsFirstUseInHotel(Context context, String hotelId, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("hotelId", hotelId);
        new AppServiceOk(context,Action.POST_STATICS_FIRSTUSE_JSON,handler,params).post();
    }

    /**餐厅环境-点播底部加载*/
    public static void getBottomHotelVodList(Context context,ApiRequestListener handler,int hotelId,long createTime) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("hotelId", hotelId);
        params.put("createTime", createTime);
        new AppServiceOk(context,Action.POST_BOTTOM_HOTEL_VOD_JSON,handler,params).post();
    }

    /**餐厅环境-点播头部刷新
     * @param flag 服务端用来判断当前更新数据
     * */
    public static void getLastHotelVodList(Context context,ApiRequestListener handler,int hotelId,String flag) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("hotelId",hotelId);
        params.put("flag",flag);
        new AppServiceOk(context,Action.POST_LAST_HOTEL_VOD_JSON,handler,params).post();
    }

    /**升级*/
    public static void Upgrade(Context context,ApiRequestListener handler,int versionCode) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("versionCode", versionCode);
        params.put("deviceType", 3);
        new AppServiceOk(context,Action.POST_UPGRADE_JSON,handler,params).post();
    }
    public static String getSPlatUrl(SmallPlatInfoBySSDP info) {
        String type = info.getType();
        String serverIp = info.getServerIp();
        String commandPort = info.getCommandPort();
        return "http://"+serverIp+":"+commandPort+"/"+type.toLowerCase()+"/command/execute/";
//        return "http://"+"192.168.2.154"+":"+commandPort+"/"+type.toLowerCase()+"/command/execute/";
    }


    /**获取启动配置*/
    public static void getStartUpSettings(Context context, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        new AppServiceOk(context,Action.GET_CLIENTSTART_JSON,handler,params).get();
    }

    /**获取启动配置*/
    public static void postWaiterExtension(Context context, ApiRequestListener handler,String source,String hotelid,String waiterid) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("st",source);
        params.put("hotelid",hotelid);
        params.put("waiterid",waiterid);
        new AppServiceOk(context,Action.POST_WAITER_EXTENSION_JSON,handler,params).post();
    }

    /**获取启动配置*/
    public static void getNearlyHotel(Context context, ApiRequestListener handler,String lng,String lat,String hotelid) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("lng",lng);
        params.put("lat",lat);
        params.put("hotelid",hotelid);
        new AppServiceOk(context,Action.GET_NEARLY_HOTEL_JSON,handler,params).get();
    }

    /**
     * 格式化请求url添加deviceId
     * @param url
     * @param mParameter
     * @return
     */
    private static String formatProUrl(Context context,String url, HashMap<String, String> mParameter) {
        StringBuilder sb = new StringBuilder();
//        url+="deviceId="+STIDUtil.getDeviceId(context);
        sb.append(url);
        if(mParameter!=null&&mParameter.size()>0) {
            Set<Map.Entry<String, String>> entries = mParameter.entrySet();
            for(Map.Entry<String, String> entry :entries) {
                String key = entry.getKey();
                String value = entry.getValue();
                sb.append("&"+key+"="+value);
            }
        }
        return sb.toString();
    }

    /**
     * 格式化请求url添加deviceId
     * @param url
     * @param mParameter
     * @return
     */
    private static String formatImageProUrl(Context context,String url, HashMap<String, String> mParameter) {
        StringBuilder sb = new StringBuilder();
        url+="deviceId="+STIDUtil.getDeviceId(context);
        sb.append(url);
        if(mParameter!=null&&mParameter.size()>0) {
            Set<Map.Entry<String, String>> entries = mParameter.entrySet();
            for(Map.Entry<String, String> entry :entries) {
                String key = entry.getKey();
                String value = entry.getValue();
                sb.append("&"+key+"="+value);
            }
        }
        return sb.toString();
    }


    /**收藏列表下拉20条*/
    public static void getLastCollectoinList(Context context,String createTime,ApiRequestListener listener) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("createTime", createTime);
        new AppServiceOk(context,Action.POST_LAST_COLLECTION_JSON,listener,params).post();
    }

    /**收藏列表上拉20条*/
    public static void getUpCollectoinList(Context context,long createTime,ApiRequestListener listener) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("createTime", createTime);
        new AppServiceOk(context,Action.POST_UP_COLLECTION_JSON,listener,params).post();
    }

    /**
     * 收藏/取消收藏接口
     * @param context
     * @param articleId 文章ID
     * @param state 收藏状态,1:收藏，0:取消收藏
     * @param listener
     */
    public static void handleCollection(Context context,ApiRequestListener listener,String articleId,String state) {
        final HashMap<String, Object> params = new HashMap<>();
        params.put("articleId", articleId);
        params.put("state", state);
        new AppServiceOk(context,Action.GET_ADD_MY_COLLECTION_JSON,listener,params).get();
    }

    /**
     * 检查文章是否被收藏
     * @param context
     * @param listener
     * @param articleId
     */
    public static void isCollection(Context context,ApiRequestListener listener,String articleId) {
        final HashMap<String, Object> params = new HashMap<>();
        params.put("articleId", articleId);
        new AppServiceOk(context,Action.GET_IS_COLLECTION_JSON,listener,params).get();
    }

    /**获取砸蛋活动配置*/
    public static void smashEgg(Context context,String hotelId,ApiRequestListener listener) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("hotelId", hotelId);
        new AppServiceOk(context,Action.POST_SMASH_EGG_JSON,listener,params).post();
    }

    /**
     *
     * @param context
     * @param handler
     * @param cateId 分类ID 1:创富，2：生活
     * @param sort_num 排序序号
     */
    public static void getWealthLifeList(Context context,ApiRequestListener handler,String cateId,String sort_num){
        final HashMap<String,Object> params = new HashMap<>();
        params.put("cateid",cateId);
        params.put("sort_num",sort_num);
        new AppServiceOk(context,Action.POST_WEALTH_LIFE_LIST_JSON,handler,params).post();
    }

    /**
     * 判断文章是否在线
     * @param context
     * @param handler
     * @param artid
     */
    public static void getContentIsOnline(Context context,ApiRequestListener handler,String artid){
        final HashMap<String,Object> params = new HashMap<>();
        params.put("artid",artid);
        new AppServiceOk(context,Action.POST_CONTENT_IS_ONLINE_JSON,handler,params).post();
    }

    /**
     * 图集接口
     * @param context
     * @param handler
     * @param content_id
     */
    public static void getPictureSet(Context context,ApiRequestListener handler,String content_id){
        final HashMap<String,Object> params = new HashMap<>();
        params.put("content_id",content_id);
        new AppServiceOk(context,Action.POST_PICTURE_SET_JSON,handler,params).post();
    }

    /**
     * 获取投屏点播列表
     * @param context
     * @param handler
     * @param hotelId
     */
    public static void getDemandList(Context context,ApiRequestListener handler,String hotelId){
        final HashMap<String,Object> params = new HashMap<>();
        params.put("hotelId",hotelId);
        new AppServiceOk(context,Action.POST_DEMAND_LIST_JSON,handler,params).post();
    }


    /**获取专题列表*/
    public static void getSpecialList(Context context,String update_time,ApiRequestListener listener) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("update_time", update_time);
        new AppServiceOk(context,Action.POST_SPECIAL_LIST_JSON,listener,params).post();
    }

    /**获取专题列表*/
    public static void getRecommendInfo(Context context,String articleId,ApiRequestListener listener) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("articleId", articleId);
        new AppServiceOk(context,Action.POST_RECOMMEND_LIST_JSON,listener,params).post();
    }

    /**获取砸蛋记录url*/
    public static void geteggAwardRecord(Context context,ApiRequestListener listener) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        new AppServiceOk(context,Action.POST_AWARD_RECORD_JSON,listener,null).post();
    }

    /**
     * 获取专题名称
     * @param context
     * @param listener
     */
    public static void getSpecialName(Context context,ApiRequestListener listener) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        new AppServiceOk(context,Action.POST_SPECIAL_NAME_JSON,listener,null).post();
    }

    /**
     * 获取点播详情页推荐列表
     * @param context
     * @param listener
     * @param articleId 文章id
     * @param sort_num 排序序号
     */
    public static void getTvRecommendList(Context context,ApiRequestListener listener,int articleId,int sort_num) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("articleId", articleId);
        params.put("sort_num", sort_num);
        new AppServiceOk(context,Action.POST_TV_RECOMMEND_JSON,listener,params).post();
    }

    /**
     * 获取专题组详情
     * @param context
     * @param listener
     * @param id
     */
    public static void getSpecialDetail(Context context,ApiRequestListener listener,String id) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        new AppServiceOk(context,Action.POST_SPECIAL_DETAIL_JSON,listener,params).post();
    }

    // 超时（网络）异常
    public static final String ERROR_TIMEOUT = "3001";
    // 业务异常
    public static final String ERROR_BUSSINESS = "3002";
    // 网络断开
    public static final String ERROR_NETWORK_FAILED = "3003";

    public static final String RESPONSE_CACHE = "3004";

    /**
     * 从这里定义业务的错误码
     */
    public static final int CMS_RESPONSE_STATE_SUCCESS = 1001;
    public static final int CLOUND_RESPONSE_STATE_SUCCESS = 10000;

    /**机顶盒返回响应码*/
    public static final int TVBOX_RESPONSE_STATE_SUCCESS = 0;
    public static final int TVBOX_RESPONSE_STATE_ERROR = -1;
    public static final int TVBOX_RESPONSE_STATE_FORCE = 4;
    /**大小图不匹配失败*/
    public static final int TVBOX_RESPONSE_NOT_MATCH = 10002;
    public static final int TVBOX_RESPONSE_VIDEO_COMPLETE = 10003;

    /**
     * 数据返回错误
     */
    public static final int HTTP_RESPONSE_STATE_ERROR = 101;
    /**没有更多数据响应码*/
    public static final int HTTP_RESPONSE_CODE_NO_DATA = 10060;
}
