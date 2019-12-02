package com.yibao.music.model.qq;

import java.util.List;

/**
 * @author luoshipeng
 * createDate：2019/12/2 0002 11:03
 * className   Album
 * Des：TODO
 */
public class Album {

    /**
     * code : 200
     * msg : OK
     * timestamp : 1558855295121
     * data : {"curnum":1,"curpage":1,"totalnum":113,"list":[{"albumName":"周杰伦的床边故事","singerMID":"0025NhlN2yWrP4","singerName_hilight":"<em>周杰伦<\/em>","docid":"3609733955036397641","singer_list":[{"name":"周杰伦","name_hilight":"<em>周杰伦<\/em>","mid":"0025NhlN2yWrP4","id":4558}],"albumMID":"003RMaRI1iFoYd","albumID":1458791,"albumPic":"http://y.gtimg.cn/music/photo_new/T002R180x180M000003RMaRI1iFoYd.jpg","type":0,"singerName":"周杰伦","albumName_hilight":"<em>周杰伦<\/em>的床边故事","publicTime":"2016-06-24","singerID":4558,"song_count":10,"catch_song":""}]}
     */

    private int code;
    private String msg;
    private DataBean data;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private AlbumBean album;

        public AlbumBean getAlbum() {
            return album;
        }

        public static class AlbumBean {
            /**
             * curnum : 1
             * curpage : 1
             * totalnum : 113
             * list : [{"albumName":"周杰伦的床边故事","singerMID":"0025NhlN2yWrP4","singerName_hilight":"<em>周杰伦<\/em>","docid":"3609733955036397641","singer_list":[{"name":"周杰伦","name_hilight":"<em>周杰伦<\/em>","mid":"0025NhlN2yWrP4","id":4558}],"albumMID":"003RMaRI1iFoYd","albumID":1458791,"albumPic":"http://y.gtimg.cn/music/photo_new/T002R180x180M000003RMaRI1iFoYd.jpg","type":0,"singerName":"周杰伦","albumName_hilight":"<em>周杰伦<\/em>的床边故事","publicTime":"2016-06-24","singerID":4558,"song_count":10,"catch_song":""}]
             */
            private int curnum;
            private int curpage;
            private int totalnum;
            private List<ListBean> list;

            public int getCurnum() {
                return curnum;
            }

            public void setCurnum(int curnum) {
                this.curnum = curnum;
            }

            public int getCurpage() {
                return curpage;
            }

            public void setCurpage(int curpage) {
                this.curpage = curpage;
            }

            public int getTotalnum() {
                return totalnum;
            }

            public void setTotalnum(int totalnum) {
                this.totalnum = totalnum;
            }

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }

            public static class ListBean {
                /**
                 * albumName : 周杰伦的床边故事
                 * singerMID : 0025NhlN2yWrP4
                 * singerName_hilight : <em>周杰伦</em>
                 * docid : 3609733955036397641
                 * singer_list : [{"name":"周杰伦","name_hilight":"<em>周杰伦<\/em>","mid":"0025NhlN2yWrP4","id":4558}]
                 * albumMID : 003RMaRI1iFoYd
                 * albumID : 1458791
                 * albumPic : http://y.gtimg.cn/music/photo_new/T002R180x180M000003RMaRI1iFoYd.jpg
                 * type : 0
                 * singerName : 周杰伦
                 * albumName_hilight : <em>周杰伦</em>的床边故事
                 * publicTime : 2016-06-24
                 * singerID : 4558
                 * song_count : 10
                 * catch_song :
                 */

                private String albumName;
                private String singerMID;
                private String singerName_hilight;
                private String docid;
                private String albumMID;
                private int albumID;
                private String albumPic;
                private int type;
                private String singerName;
                private String albumName_hilight;
                private String publicTime;
                private int singerID;
                private int song_count;
                private String catch_song;
                private List<SingerListBean> singer_list;

                public String getAlbumName() {
                    return albumName;
                }

                public void setAlbumName(String albumName) {
                    this.albumName = albumName;
                }

                public String getSingerMID() {
                    return singerMID;
                }

                public void setSingerMID(String singerMID) {
                    this.singerMID = singerMID;
                }

                public String getSingerName_hilight() {
                    return singerName_hilight;
                }

                public void setSingerName_hilight(String singerName_hilight) {
                    this.singerName_hilight = singerName_hilight;
                }

                public String getDocid() {
                    return docid;
                }

                public void setDocid(String docid) {
                    this.docid = docid;
                }

                public String getAlbumMID() {
                    return albumMID;
                }

                public void setAlbumMID(String albumMID) {
                    this.albumMID = albumMID;
                }

                public int getAlbumID() {
                    return albumID;
                }

                public void setAlbumID(int albumID) {
                    this.albumID = albumID;
                }

                public String getAlbumPic() {
                    return albumPic;
                }

                public void setAlbumPic(String albumPic) {
                    this.albumPic = albumPic;
                }

                public int getType() {
                    return type;
                }

                public void setType(int type) {
                    this.type = type;
                }

                public String getSingerName() {
                    return singerName;
                }

                public void setSingerName(String singerName) {
                    this.singerName = singerName;
                }

                public String getAlbumName_hilight() {
                    return albumName_hilight;
                }

                public void setAlbumName_hilight(String albumName_hilight) {
                    this.albumName_hilight = albumName_hilight;
                }

                public String getPublicTime() {
                    return publicTime;
                }

                public void setPublicTime(String publicTime) {
                    this.publicTime = publicTime;
                }

                public int getSingerID() {
                    return singerID;
                }

                public void setSingerID(int singerID) {
                    this.singerID = singerID;
                }

                public int getSong_count() {
                    return song_count;
                }

                public void setSong_count(int song_count) {
                    this.song_count = song_count;
                }

                public String getCatch_song() {
                    return catch_song;
                }

                public void setCatch_song(String catch_song) {
                    this.catch_song = catch_song;
                }

                public List<SingerListBean> getSinger_list() {
                    return singer_list;
                }

                public void setSinger_list(List<SingerListBean> singer_list) {
                    this.singer_list = singer_list;
                }

                public static class SingerListBean {
                    /**
                     * name : 周杰伦
                     * name_hilight : <em>周杰伦</em>
                     * mid : 0025NhlN2yWrP4
                     * id : 4558
                     */

                    private String name;
                    private String name_hilight;
                    private String mid;
                    private int id;

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public String getName_hilight() {
                        return name_hilight;
                    }

                    public void setName_hilight(String name_hilight) {
                        this.name_hilight = name_hilight;
                    }

                    public String getMid() {
                        return mid;
                    }

                    public void setMid(String mid) {
                        this.mid = mid;
                    }

                    public int getId() {
                        return id;
                    }

                    public void setId(int id) {
                        this.id = id;
                    }
                }
            }
        }
    }
}
