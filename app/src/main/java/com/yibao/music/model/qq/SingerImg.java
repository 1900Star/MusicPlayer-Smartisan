package com.yibao.music.model.qq;

import java.util.List;

/**
 * @author luoshipeng
 * createDate：2019/12/2 0002 11:11
 * className   SingerImg
 * Des：TODO
 */
public class SingerImg {
    /**
     * result : {"artistCount":1,"artists":[{"id":840134,"name":"刘瑞琦","picUrl":"http://p1.music.126.net/qTDkcmWPMK3U54RNC0IgMw==/109951163288035254.jpg","alias":[],"albumSize":20,"picId":109951163288035254,"img1v1Url":"http://p1.music.126.net/a13xmSNqxMY5M_R1OFvPvA==/109951163288038157.jpg","accountId":3788031,"img1v1":109951163288038157,"mvSize":16,"followed":false,"trans":null}]}
     * code : 200
     */

    private ResultBean result;
    private int code;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static class ResultBean {
        /**
         * artistCount : 1
         * artists : [{"id":840134,"name":"刘瑞琦","picUrl":"http://p1.music.126.net/qTDkcmWPMK3U54RNC0IgMw==/109951163288035254.jpg","alias":[],"albumSize":20,"picId":109951163288035254,"img1v1Url":"http://p1.music.126.net/a13xmSNqxMY5M_R1OFvPvA==/109951163288038157.jpg","accountId":3788031,"img1v1":109951163288038157,"mvSize":16,"followed":false,"trans":null}]
         */

        private List<ArtistsBean> artists;

        public List<ArtistsBean> getArtists() {
            return artists;
        }

        public void setArtists(List<ArtistsBean> artists) {
            this.artists = artists;
        }

        public static class ArtistsBean {
            /**
             * id : 840134
             * name : 刘瑞琦
             * picUrl : http://p1.music.126.net/qTDkcmWPMK3U54RNC0IgMw==/109951163288035254.jpg
             * alias : []
             * albumSize : 20
             * picId : 109951163288035254
             * img1v1Url : http://p1.music.126.net/a13xmSNqxMY5M_R1OFvPvA==/109951163288038157.jpg
             * accountId : 3788031
             * img1v1 : 109951163288038157
             * mvSize : 16
             * followed : false
             * trans : null
             */

            private int id;
            private String name;
            private String picUrl;
            private String img1v1Url;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPicUrl() {
                return picUrl;
            }

            public void setPicUrl(String picUrl) {
                this.picUrl = picUrl;
            }

            public String getImg1v1Url() {
                return img1v1Url;
            }

            public void setImg1v1Url(String img1v1Url) {
                this.img1v1Url = img1v1Url;
            }
        }
    }

    @Override
    public String toString() {
        return "SingerImg{" +
                "result=" + result +
                ", code=" + code +
                '}';
    }
}
