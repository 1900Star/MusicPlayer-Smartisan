package com.yibao.music.model.qq;

import java.util.List;

/**
 * @author luoshipeng
 * createDate：2019/12/2 0002 11:11
 * className   SingerImg
 * Des：TODO
 */

public class SingerImg {


    private ResultData result;

    private int code;

    public ResultData getResult() {
        return result;
    }

    public void setResult(ResultData result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static class ResultData {

        private int artistCount;

        private List<ArtistsData> artists;

        public int getArtistCount() {
            return artistCount;
        }

        public void setArtistCount(int artistCount) {
            this.artistCount = artistCount;
        }

        public List<ArtistsData> getArtists() {
            return artists;
        }

        public void setArtists(List<ArtistsData> artists) {
            this.artists = artists;
        }

        public static class ArtistsData {

            private int id;

            private String name;

            private String picUrl;

            private List<String> alias;

            private int albumSize;

            private Long picId;


            private String img1v1Url;

            private int accountId;

            private Long img1v1;

            private int mvSize;

            private Boolean followed;

            private List<String> alia;



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

            public List<String> getAlias() {
                return alias;
            }

            public void setAlias(List<String> alias) {
                this.alias = alias;
            }

            public int getAlbumSize() {
                return albumSize;
            }

            public void setAlbumSize(int albumSize) {
                this.albumSize = albumSize;
            }

            public Long getPicId() {
                return picId;
            }

            public void setPicId(Long picId) {
                this.picId = picId;
            }



            public String getImg1v1Url() {
                return img1v1Url;
            }

            public void setImg1v1Url(String img1v1Url) {
                this.img1v1Url = img1v1Url;
            }

            public int getAccountId() {
                return accountId;
            }

            public void setAccountId(int accountId) {
                this.accountId = accountId;
            }

            public Long getImg1v1() {
                return img1v1;
            }

            public void setImg1v1(Long img1v1) {
                this.img1v1 = img1v1;
            }

            public int getMvSize() {
                return mvSize;
            }

            public void setMvSize(int mvSize) {
                this.mvSize = mvSize;
            }

            public Boolean getFollowed() {
                return followed;
            }

            public void setFollowed(Boolean followed) {
                this.followed = followed;
            }

            public List<String> getAlia() {
                return alia;
            }

            public void setAlia(List<String> alia) {
                this.alia = alia;
            }

            @Override
            public String toString() {
                return "ArtistsData{" +
                        "id=" + id +
                        ", name='" + name + '\'' +
                        ", picUrl='" + picUrl + '\'' +
                        ", alias=" + alias +
                        ", albumSize=" + albumSize +
                        ", picId=" + picId +
                        ", img1v1Url='" + img1v1Url + '\'' +
                        ", accountId=" + accountId +
                        ", img1v1=" + img1v1 +
                        ", mvSize=" + mvSize +
                        ", followed=" + followed +
                        ", alia=" + alia +
                        '}';
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
