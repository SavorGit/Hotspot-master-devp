package com.savor.savorphone.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 专题组详情实体类
 * Created by hezd on 2017/8/30.
 */

public class SpecialDetail implements Serializable {

    /**
     * id : 1
     * name : 123333
     * title : 123个
     * img_url : http://devp.oss.littlehotspot.com/media/resource/cKrmsiApNT.jpg
     * desc : 而为
     * "contentUrl": "http://devp.admin.littlehotspot.com/admin/SpecialgroupShow/showsp?id=37"
     * list : [{"sgtype":1,"stext":"WERWR"},{"artid":"1892","sort_num":"1892","type":4,"title":"君不可一日无茶","duration":"90","imageURL":"http://devp.oss.littlehotspot.com/media/resource/ePmr6x2kwa.jpg","contentURL":"http://devp.admin.littlehotspot.com/content/1892.html","videoURL":"http://200048203.vod.myqcloud.com/200048203_d78ea76abcfb11e6bc811bda67685817","updateTime":"2017-02-17","createTime":"2017-02-17 10:25:33","sourceName":"光明网","logo":"http://devp.oss.littlehotspot.com/media/resource/TeMN4NKQba.png","sgtype":2},{"sgtype":"3","img_url":"http://devp.oss.littlehotspot.com/media/resource/aTbkGMAkHm.jpg"},{"sgtype":"4","stitle":"按时大声大声道"}]
     */

    private String id;
    private String name;
    private String title;
    private String img_url;
    private String desc;
    private String contentUrl;
    private List<SpecialDetailTypeBean> list;

    @Override
    public String toString() {
        return "SpecialDetail{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", img_url='" + img_url + '\'' +
                ", desc='" + desc + '\'' +
                ", contentUrl='" + contentUrl + '\'' +
                ", list=" + list +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpecialDetail that = (SpecialDetail) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (img_url != null ? !img_url.equals(that.img_url) : that.img_url != null) return false;
        if (desc != null ? !desc.equals(that.desc) : that.desc != null) return false;
        if (contentUrl != null ? !contentUrl.equals(that.contentUrl) : that.contentUrl != null)
            return false;
        return list != null ? list.equals(that.list) : that.list == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (img_url != null ? img_url.hashCode() : 0);
        result = 31 * result + (desc != null ? desc.hashCode() : 0);
        result = 31 * result + (contentUrl != null ? contentUrl.hashCode() : 0);
        result = 31 * result + (list != null ? list.hashCode() : 0);
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public List<SpecialDetailTypeBean> getList() {
        return list;
    }

    public void setList(List<SpecialDetailTypeBean> list) {
        this.list = list;
    }

    /**
     * 专题组详情条目类型
     */
    public static class SpecialDetailTypeBean implements Serializable {
        /**
         * sgtype : 1
         * stext : WERWR
         * artid : 1892
         * sort_num : 1892
         * type : 4
         * title : 君不可一日无茶
         * duration : 90
         * imageURL : http://devp.oss.littlehotspot.com/media/resource/ePmr6x2kwa.jpg
         * contentURL : http://devp.admin.littlehotspot.com/content/1892.html
         * videoURL : http://200048203.vod.myqcloud.com/200048203_d78ea76abcfb11e6bc811bda67685817
         * updateTime : 2017-02-17
         * createTime : 2017-02-17 10:25:33
         * sourceName : 光明网
         * logo : http://devp.oss.littlehotspot.com/media/resource/TeMN4NKQba.png
         * img_url : http://devp.oss.littlehotspot.com/media/resource/aTbkGMAkHm.jpg
         * stitle : 按时大声大声道
         */

        private int sgtype;
        private String stext;
        private String artid;
        private String sort_num;
        private int type;
        private String title;
        private String duration;
        private String imageURL;
        private String contentURL;
        private String videoURL;
        private String updateTime;
        private String createTime;
        private String sourceName;
        private String logo;
        private String img_url;
        private String stitle;

        @Override
        public String toString() {
            return "SpecialDetailTypeBean{" +
                    "sgtype=" + sgtype +
                    ", stext='" + stext + '\'' +
                    ", artid='" + artid + '\'' +
                    ", sort_num='" + sort_num + '\'' +
                    ", type=" + type +
                    ", title='" + title + '\'' +
                    ", duration='" + duration + '\'' +
                    ", imageURL='" + imageURL + '\'' +
                    ", contentURL='" + contentURL + '\'' +
                    ", videoURL='" + videoURL + '\'' +
                    ", updateTime='" + updateTime + '\'' +
                    ", createTime='" + createTime + '\'' +
                    ", sourceName='" + sourceName + '\'' +
                    ", logo='" + logo + '\'' +
                    ", img_url='" + img_url + '\'' +
                    ", stitle='" + stitle + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SpecialDetailTypeBean that = (SpecialDetailTypeBean) o;

            if (sgtype != that.sgtype) return false;
            if (type != that.type) return false;
            if (stext != null ? !stext.equals(that.stext) : that.stext != null) return false;
            if (artid != null ? !artid.equals(that.artid) : that.artid != null) return false;
            if (sort_num != null ? !sort_num.equals(that.sort_num) : that.sort_num != null)
                return false;
            if (title != null ? !title.equals(that.title) : that.title != null) return false;
            if (duration != null ? !duration.equals(that.duration) : that.duration != null)
                return false;
            if (imageURL != null ? !imageURL.equals(that.imageURL) : that.imageURL != null)
                return false;
            if (contentURL != null ? !contentURL.equals(that.contentURL) : that.contentURL != null)
                return false;
            if (videoURL != null ? !videoURL.equals(that.videoURL) : that.videoURL != null)
                return false;
            if (updateTime != null ? !updateTime.equals(that.updateTime) : that.updateTime != null)
                return false;
            if (createTime != null ? !createTime.equals(that.createTime) : that.createTime != null)
                return false;
            if (sourceName != null ? !sourceName.equals(that.sourceName) : that.sourceName != null)
                return false;
            if (logo != null ? !logo.equals(that.logo) : that.logo != null) return false;
            if (img_url != null ? !img_url.equals(that.img_url) : that.img_url != null)
                return false;
            return stitle != null ? stitle.equals(that.stitle) : that.stitle == null;

        }

        @Override
        public int hashCode() {
            int result = sgtype;
            result = 31 * result + (stext != null ? stext.hashCode() : 0);
            result = 31 * result + (artid != null ? artid.hashCode() : 0);
            result = 31 * result + (sort_num != null ? sort_num.hashCode() : 0);
            result = 31 * result + type;
            result = 31 * result + (title != null ? title.hashCode() : 0);
            result = 31 * result + (duration != null ? duration.hashCode() : 0);
            result = 31 * result + (imageURL != null ? imageURL.hashCode() : 0);
            result = 31 * result + (contentURL != null ? contentURL.hashCode() : 0);
            result = 31 * result + (videoURL != null ? videoURL.hashCode() : 0);
            result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
            result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
            result = 31 * result + (sourceName != null ? sourceName.hashCode() : 0);
            result = 31 * result + (logo != null ? logo.hashCode() : 0);
            result = 31 * result + (img_url != null ? img_url.hashCode() : 0);
            result = 31 * result + (stitle != null ? stitle.hashCode() : 0);
            return result;
        }

        public int getSgtype() {
            return sgtype;
        }

        public void setSgtype(int sgtype) {
            this.sgtype = sgtype;
        }

        public String getStext() {
            return stext;
        }

        public void setStext(String stext) {
            this.stext = stext;
        }

        public String getArtid() {
            return artid;
        }

        public void setArtid(String artid) {
            this.artid = artid;
        }

        public String getSort_num() {
            return sort_num;
        }

        public void setSort_num(String sort_num) {
            this.sort_num = sort_num;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getImageURL() {
            return imageURL;
        }

        public void setImageURL(String imageURL) {
            this.imageURL = imageURL;
        }

        public String getContentURL() {
            return contentURL;
        }

        public void setContentURL(String contentURL) {
            this.contentURL = contentURL;
        }

        public String getVideoURL() {
            return videoURL;
        }

        public void setVideoURL(String videoURL) {
            this.videoURL = videoURL;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getSourceName() {
            return sourceName;
        }

        public void setSourceName(String sourceName) {
            this.sourceName = sourceName;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getImg_url() {
            return img_url;
        }

        public void setImg_url(String img_url) {
            this.img_url = img_url;
        }

        public String getStitle() {
            return stitle;
        }

        public void setStitle(String stitle) {
            this.stitle = stitle;
        }
    }
}