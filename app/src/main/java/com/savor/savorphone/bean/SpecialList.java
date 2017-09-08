package com.savor.savorphone.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hezd on 2017/9/1.
 */

public class SpecialList implements Serializable {

    /**
     * list : [{"id":"5","name":"1231","title":"12312123123","img_url":"http://devp.oss.littlehotspot.com/media/resource/5XCAwwP8Ba.jpg","desc":"55555555555555555"},{"id":"4","name":"安邦","title":"神仙打架：安邦PK财新谁能笑到最后","img_url":"http://devp.oss.littlehotspot.com/media/resource/FjisXc6QHw.jpg","desc":"朋友圈都在猜，财新的报道是不是意味着安邦要接受调查了？这场国内最权威的财经媒体与总资产17560亿的全球化保险公司的较量，简直如神仙打架般热闹，请火速围观。"},{"id":"3","name":"测试速度a","title":"阿斯顿的阿达553","img_url":"http://devp.oss.littlehotspot.com/media/resource/87teXTQNtf.jpg","desc":"阿斯达大所多爱上大选的实打实大声道"},{"id":"2","name":"狂欢节框架","title":"孔记会客居","img_url":"http://devp.oss.littlehotspot.com/media/resource/2ZMiGsaSXP.jpg","desc":"53456孔记会客居接口"},{"id":"1","name":"123333","title":"神仙打架：安邦 PK 财新 谁能笑到最后","img_url":"http://devp.oss.littlehotspot.com/media/resource/cKrmsiApNT.jpg","desc":"朋友圈大家都在猜，财经新的报道，是不是意味着安邦要接受检查了？这场国内最权威的财经媒体与总资产1970亿的全球化公司的较量，简称这场国内最权威的财经媒体与总资产1970"}]
     * nextpage : 0
     */

    private int nextpage;
    private List<SpecialListItem> list;

    public int getNextpage() {
        return nextpage;
    }

    public void setNextpage(int nextpage) {
        this.nextpage = nextpage;
    }

    public List<SpecialListItem> getList() {
        return list;
    }

    public void setList(List<SpecialListItem> list) {
        this.list = list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpecialList that = (SpecialList) o;

        if (nextpage != that.nextpage) return false;
        return list != null ? list.equals(that.list) : that.list == null;

    }

    @Override
    public int hashCode() {
        int result = nextpage;
        result = 31 * result + (list != null ? list.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SpecialList{" +
                "nextpage=" + nextpage +
                ", list=" + list +
                '}';
    }

    /**
     * 专题组列表item
     */
    public class SpecialListItem implements Serializable{
        /**
         * id : 5
         * name : 1231
         * title : 12312123123
         * img_url : http://devp.oss.littlehotspot.com/media/resource/5XCAwwP8Ba.jpg
         * desc : 55555555555555555
         * updateTime:
         */

        private String id;
        private String name;
        private String title;
        private String img_url;
        private String desc;
        private String update_time;

        @Override
        public String toString() {
            return "SpecialListItem{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", title='" + title + '\'' +
                    ", img_url='" + img_url + '\'' +
                    ", desc='" + desc + '\'' +
                    ", update_time='" + update_time + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SpecialListItem that = (SpecialListItem) o;

            if (id != null ? !id.equals(that.id) : that.id != null) return false;
            if (name != null ? !name.equals(that.name) : that.name != null) return false;
            if (title != null ? !title.equals(that.title) : that.title != null) return false;
            if (img_url != null ? !img_url.equals(that.img_url) : that.img_url != null)
                return false;
            if (desc != null ? !desc.equals(that.desc) : that.desc != null) return false;
            return update_time != null ? update_time.equals(that.update_time) : that.update_time == null;

        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            result = 31 * result + (title != null ? title.hashCode() : 0);
            result = 31 * result + (img_url != null ? img_url.hashCode() : 0);
            result = 31 * result + (desc != null ? desc.hashCode() : 0);
            result = 31 * result + (update_time != null ? update_time.hashCode() : 0);
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

        public String getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(String update_time) {
            this.update_time = update_time;
        }
    }
}
