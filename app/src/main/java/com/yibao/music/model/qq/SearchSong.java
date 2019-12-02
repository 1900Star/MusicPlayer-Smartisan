package com.yibao.music.model.qq;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author luoshipeng
 * createDate：2019/12/2 0002 11:01
 * className   SearchSong
 * Des：TODO
 */
public class SearchSong {

    /**
     * code : 0
     * data : {"keyword":"泡沫","priority":0,"qc":[],"semantic":{"curnum":0,"curpage":1,"list":[],"totalnum":0},"song":{"curnum":2,"curpage":1,"list":[{"albumid":123298,"albummid":"002YFufr4bXZyI","albumname":"Xposed","albumname_hilight":"Xposed","alertid":2,"belongCD":0,"cdIdx":4,"chinesesinger":0,"docid":"15989832620412542665","grp":[],"interval":258,"isonly":0,"lyric":"","lyric_hilight":"","media_mid":"000HjG8v1DTWRO","msgid":16,"newStatus":2,"nt":2779002879,"pay":{"payalbum":0,"payalbumprice":0,"paydownload":1,"payinfo":1,"payplay":0,"paytrackmouth":1,"paytrackprice":200},"preview":{"trybegin":0,"tryend":0,"trysize":0},"pubtime":1341417600,"pure":0,"singer":[{"id":13948,"mid":"001fNHEf1SFEFN","name":"G.E.M. 邓紫棋","name_hilight":"G.E.M. 邓紫棋"}],"size128":4143023,"size320":10357240,"sizeape":0,"sizeflac":26269879,"sizeogg":5321629,"songid":1530858,"songmid":"001X0PDf0W4lBq","songname":"泡沫","songname_hilight":"<em>泡沫<\/em>","strMediaMid":"000HjG8v1DTWRO","stream":1,"switch":17413891,"t":1,"tag":11,"type":0,"ver":0,"vid":"b0011lorm6w"},{"albumid":515216,"albummid":"0011mIvH2ib3NG","albumname":"泡沫","albumname_hilight":"<em>泡沫<\/em>","alertid":2,"belongCD":0,"cdIdx":1,"chinesesinger":0,"docid":"4784575797295356506","grp":[],"interval":256,"isonly":0,"lyric":"","lyric_hilight":"","media_mid":"002L2rem1qysQy","msgid":16,"newStatus":2,"nt":3611483873,"pay":{"payalbum":0,"payalbumprice":0,"paydownload":1,"payinfo":1,"payplay":0,"paytrackmouth":1,"paytrackprice":200},"preview":{"trybegin":85227,"tryend":138339,"trysize":0},"pubtime":1399392000,"pure":0,"singer":[{"id":31035,"mid":"003U6coz1AhN3H","name":"简弘亦","name_hilight":"简弘亦"}],"size128":4109146,"size320":10272582,"sizeape":28806159,"sizeflac":28944881,"sizeogg":6054777,"songid":5517053,"songmid":"002eMvkq4g5H2D","songname":"泡沫","songname_hilight":"<em>泡沫<\/em>","strMediaMid":"002L2rem1qysQy","stream":1,"switch":636675,"t":1,"tag":12,"type":0,"ver":0,"vid":""}],"totalnum":566},"tab":0,"taglist":[],"totaltime":0,"zhida":{"chinesesinger":0,"type":0}}
     * message :
     * notice :
     * subcode : 0
     * time : 1575255567
     * tips :
     */

    private int code;
    private DataBean data;
    private String message;
    private String notice;
    private int subcode;
    private int time;
    private String tips;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public int getSubcode() {
        return subcode;
    }

    public void setSubcode(int subcode) {
        this.subcode = subcode;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public static class DataBean {
        /**
         * keyword : 泡沫
         * priority : 0
         * qc : []
         * semantic : {"curnum":0,"curpage":1,"list":[],"totalnum":0}
         * song : {"curnum":2,"curpage":1,"list":[{"albumid":123298,"albummid":"002YFufr4bXZyI","albumname":"Xposed","albumname_hilight":"Xposed","alertid":2,"belongCD":0,"cdIdx":4,"chinesesinger":0,"docid":"15989832620412542665","grp":[],"interval":258,"isonly":0,"lyric":"","lyric_hilight":"","media_mid":"000HjG8v1DTWRO","msgid":16,"newStatus":2,"nt":2779002879,"pay":{"payalbum":0,"payalbumprice":0,"paydownload":1,"payinfo":1,"payplay":0,"paytrackmouth":1,"paytrackprice":200},"preview":{"trybegin":0,"tryend":0,"trysize":0},"pubtime":1341417600,"pure":0,"singer":[{"id":13948,"mid":"001fNHEf1SFEFN","name":"G.E.M. 邓紫棋","name_hilight":"G.E.M. 邓紫棋"}],"size128":4143023,"size320":10357240,"sizeape":0,"sizeflac":26269879,"sizeogg":5321629,"songid":1530858,"songmid":"001X0PDf0W4lBq","songname":"泡沫","songname_hilight":"<em>泡沫<\/em>","strMediaMid":"000HjG8v1DTWRO","stream":1,"switch":17413891,"t":1,"tag":11,"type":0,"ver":0,"vid":"b0011lorm6w"},{"albumid":515216,"albummid":"0011mIvH2ib3NG","albumname":"泡沫","albumname_hilight":"<em>泡沫<\/em>","alertid":2,"belongCD":0,"cdIdx":1,"chinesesinger":0,"docid":"4784575797295356506","grp":[],"interval":256,"isonly":0,"lyric":"","lyric_hilight":"","media_mid":"002L2rem1qysQy","msgid":16,"newStatus":2,"nt":3611483873,"pay":{"payalbum":0,"payalbumprice":0,"paydownload":1,"payinfo":1,"payplay":0,"paytrackmouth":1,"paytrackprice":200},"preview":{"trybegin":85227,"tryend":138339,"trysize":0},"pubtime":1399392000,"pure":0,"singer":[{"id":31035,"mid":"003U6coz1AhN3H","name":"简弘亦","name_hilight":"简弘亦"}],"size128":4109146,"size320":10272582,"sizeape":28806159,"sizeflac":28944881,"sizeogg":6054777,"songid":5517053,"songmid":"002eMvkq4g5H2D","songname":"泡沫","songname_hilight":"<em>泡沫<\/em>","strMediaMid":"002L2rem1qysQy","stream":1,"switch":636675,"t":1,"tag":12,"type":0,"ver":0,"vid":""}],"totalnum":566}
         * tab : 0
         * taglist : []
         * totaltime : 0
         * zhida : {"chinesesinger":0,"type":0}
         */

        private String keyword;
        private int priority;
        private SemanticBean semantic;
        private SongBean song;
        private int tab;
        private int totaltime;
        private ZhidaBean zhida;
        private List<?> qc;
        private List<?> taglist;

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public SemanticBean getSemantic() {
            return semantic;
        }

        public void setSemantic(SemanticBean semantic) {
            this.semantic = semantic;
        }

        public SongBean getSong() {
            return song;
        }

        public void setSong(SongBean song) {
            this.song = song;
        }

        public int getTab() {
            return tab;
        }

        public void setTab(int tab) {
            this.tab = tab;
        }

        public int getTotaltime() {
            return totaltime;
        }

        public void setTotaltime(int totaltime) {
            this.totaltime = totaltime;
        }

        public ZhidaBean getZhida() {
            return zhida;
        }

        public void setZhida(ZhidaBean zhida) {
            this.zhida = zhida;
        }

        public List<?> getQc() {
            return qc;
        }

        public void setQc(List<?> qc) {
            this.qc = qc;
        }

        public List<?> getTaglist() {
            return taglist;
        }

        public void setTaglist(List<?> taglist) {
            this.taglist = taglist;
        }

        public static class SemanticBean {
            /**
             * curnum : 0
             * curpage : 1
             * list : []
             * totalnum : 0
             */

            private int curnum;
            private int curpage;
            private int totalnum;
            private List<?> list;

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

            public List<?> getList() {
                return list;
            }

            public void setList(List<?> list) {
                this.list = list;
            }
        }

        public static class SongBean {
            /**
             * curnum : 2
             * curpage : 1
             * list : [{"albumid":123298,"albummid":"002YFufr4bXZyI","albumname":"Xposed","albumname_hilight":"Xposed","alertid":2,"belongCD":0,"cdIdx":4,"chinesesinger":0,"docid":"15989832620412542665","grp":[],"interval":258,"isonly":0,"lyric":"","lyric_hilight":"","media_mid":"000HjG8v1DTWRO","msgid":16,"newStatus":2,"nt":2779002879,"pay":{"payalbum":0,"payalbumprice":0,"paydownload":1,"payinfo":1,"payplay":0,"paytrackmouth":1,"paytrackprice":200},"preview":{"trybegin":0,"tryend":0,"trysize":0},"pubtime":1341417600,"pure":0,"singer":[{"id":13948,"mid":"001fNHEf1SFEFN","name":"G.E.M. 邓紫棋","name_hilight":"G.E.M. 邓紫棋"}],"size128":4143023,"size320":10357240,"sizeape":0,"sizeflac":26269879,"sizeogg":5321629,"songid":1530858,"songmid":"001X0PDf0W4lBq","songname":"泡沫","songname_hilight":"<em>泡沫<\/em>","strMediaMid":"000HjG8v1DTWRO","stream":1,"switch":17413891,"t":1,"tag":11,"type":0,"ver":0,"vid":"b0011lorm6w"},{"albumid":515216,"albummid":"0011mIvH2ib3NG","albumname":"泡沫","albumname_hilight":"<em>泡沫<\/em>","alertid":2,"belongCD":0,"cdIdx":1,"chinesesinger":0,"docid":"4784575797295356506","grp":[],"interval":256,"isonly":0,"lyric":"","lyric_hilight":"","media_mid":"002L2rem1qysQy","msgid":16,"newStatus":2,"nt":3611483873,"pay":{"payalbum":0,"payalbumprice":0,"paydownload":1,"payinfo":1,"payplay":0,"paytrackmouth":1,"paytrackprice":200},"preview":{"trybegin":85227,"tryend":138339,"trysize":0},"pubtime":1399392000,"pure":0,"singer":[{"id":31035,"mid":"003U6coz1AhN3H","name":"简弘亦","name_hilight":"简弘亦"}],"size128":4109146,"size320":10272582,"sizeape":28806159,"sizeflac":28944881,"sizeogg":6054777,"songid":5517053,"songmid":"002eMvkq4g5H2D","songname":"泡沫","songname_hilight":"<em>泡沫<\/em>","strMediaMid":"002L2rem1qysQy","stream":1,"switch":636675,"t":1,"tag":12,"type":0,"ver":0,"vid":""}]
             * totalnum : 566
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
                 * albumid : 123298
                 * albummid : 002YFufr4bXZyI
                 * albumname : Xposed
                 * albumname_hilight : Xposed
                 * alertid : 2
                 * belongCD : 0
                 * cdIdx : 4
                 * chinesesinger : 0
                 * docid : 15989832620412542665
                 * grp : []
                 * interval : 258
                 * isonly : 0
                 * lyric :
                 * lyric_hilight :
                 * media_mid : 000HjG8v1DTWRO
                 * msgid : 16
                 * newStatus : 2
                 * nt : 2779002879
                 * pay : {"payalbum":0,"payalbumprice":0,"paydownload":1,"payinfo":1,"payplay":0,"paytrackmouth":1,"paytrackprice":200}
                 * preview : {"trybegin":0,"tryend":0,"trysize":0}
                 * pubtime : 1341417600
                 * pure : 0
                 * singer : [{"id":13948,"mid":"001fNHEf1SFEFN","name":"G.E.M. 邓紫棋","name_hilight":"G.E.M. 邓紫棋"}]
                 * size128 : 4143023
                 * size320 : 10357240
                 * sizeape : 0
                 * sizeflac : 26269879
                 * sizeogg : 5321629
                 * songid : 1530858
                 * songmid : 001X0PDf0W4lBq
                 * songname : 泡沫
                 * songname_hilight : <em>泡沫</em>
                 * strMediaMid : 000HjG8v1DTWRO
                 * stream : 1
                 * switch : 17413891
                 * t : 1
                 * tag : 11
                 * type : 0
                 * ver : 0
                 * vid : b0011lorm6w
                 */

                private int albumid;
                private String albummid;
                private String albumname;
                private String albumname_hilight;
                private int alertid;
                private int belongCD;
                private int cdIdx;
                private int chinesesinger;
                private String docid;
                private int interval;
                private int isonly;
                private String lyric;
                private String lyric_hilight;
                private String media_mid;
                private int msgid;
                private int newStatus;
                private long nt;
                private PayBean pay;
                private PreviewBean preview;
                private int pubtime;
                private int pure;
                private int size128;
                private int size320;
                private int sizeape;
                private int sizeflac;
                private int sizeogg;
                private int songid;
                private String songmid;
                private String songname;
                private String songname_hilight;
                private String strMediaMid;
                private int stream;
                @SerializedName("switch")
                private int switchX;
                private int t;
                private int tag;
                private int type;
                private int ver;
                private String vid;
                private List<?> grp;
                private List<SingerBean> singer;

                public int getAlbumid() {
                    return albumid;
                }

                public void setAlbumid(int albumid) {
                    this.albumid = albumid;
                }

                public String getAlbummid() {
                    return albummid;
                }

                public void setAlbummid(String albummid) {
                    this.albummid = albummid;
                }

                public String getAlbumname() {
                    return albumname;
                }

                public void setAlbumname(String albumname) {
                    this.albumname = albumname;
                }

                public String getAlbumname_hilight() {
                    return albumname_hilight;
                }

                public void setAlbumname_hilight(String albumname_hilight) {
                    this.albumname_hilight = albumname_hilight;
                }

                public int getAlertid() {
                    return alertid;
                }

                public void setAlertid(int alertid) {
                    this.alertid = alertid;
                }

                public int getBelongCD() {
                    return belongCD;
                }

                public void setBelongCD(int belongCD) {
                    this.belongCD = belongCD;
                }

                public int getCdIdx() {
                    return cdIdx;
                }

                public void setCdIdx(int cdIdx) {
                    this.cdIdx = cdIdx;
                }

                public int getChinesesinger() {
                    return chinesesinger;
                }

                public void setChinesesinger(int chinesesinger) {
                    this.chinesesinger = chinesesinger;
                }

                public String getDocid() {
                    return docid;
                }

                public void setDocid(String docid) {
                    this.docid = docid;
                }

                public int getInterval() {
                    return interval;
                }

                public void setInterval(int interval) {
                    this.interval = interval;
                }

                public int getIsonly() {
                    return isonly;
                }

                public void setIsonly(int isonly) {
                    this.isonly = isonly;
                }

                public String getLyric() {
                    return lyric;
                }

                public void setLyric(String lyric) {
                    this.lyric = lyric;
                }

                public String getLyric_hilight() {
                    return lyric_hilight;
                }

                public void setLyric_hilight(String lyric_hilight) {
                    this.lyric_hilight = lyric_hilight;
                }

                public String getMedia_mid() {
                    return media_mid;
                }

                public void setMedia_mid(String media_mid) {
                    this.media_mid = media_mid;
                }

                public int getMsgid() {
                    return msgid;
                }

                public void setMsgid(int msgid) {
                    this.msgid = msgid;
                }

                public int getNewStatus() {
                    return newStatus;
                }

                public void setNewStatus(int newStatus) {
                    this.newStatus = newStatus;
                }

                public long getNt() {
                    return nt;
                }

                public void setNt(long nt) {
                    this.nt = nt;
                }

                public PayBean getPay() {
                    return pay;
                }

                public void setPay(PayBean pay) {
                    this.pay = pay;
                }

                public PreviewBean getPreview() {
                    return preview;
                }

                public void setPreview(PreviewBean preview) {
                    this.preview = preview;
                }

                public int getPubtime() {
                    return pubtime;
                }

                public void setPubtime(int pubtime) {
                    this.pubtime = pubtime;
                }

                public int getPure() {
                    return pure;
                }

                public void setPure(int pure) {
                    this.pure = pure;
                }

                public int getSize128() {
                    return size128;
                }

                public void setSize128(int size128) {
                    this.size128 = size128;
                }

                public int getSize320() {
                    return size320;
                }

                public void setSize320(int size320) {
                    this.size320 = size320;
                }

                public int getSizeape() {
                    return sizeape;
                }

                public void setSizeape(int sizeape) {
                    this.sizeape = sizeape;
                }

                public int getSizeflac() {
                    return sizeflac;
                }

                public void setSizeflac(int sizeflac) {
                    this.sizeflac = sizeflac;
                }

                public int getSizeogg() {
                    return sizeogg;
                }

                public void setSizeogg(int sizeogg) {
                    this.sizeogg = sizeogg;
                }

                public int getSongid() {
                    return songid;
                }

                public void setSongid(int songid) {
                    this.songid = songid;
                }

                public String getSongmid() {
                    return songmid;
                }

                public void setSongmid(String songmid) {
                    this.songmid = songmid;
                }

                public String getSongname() {
                    return songname;
                }

                public void setSongname(String songname) {
                    this.songname = songname;
                }

                public String getSongname_hilight() {
                    return songname_hilight;
                }

                public void setSongname_hilight(String songname_hilight) {
                    this.songname_hilight = songname_hilight;
                }

                public String getStrMediaMid() {
                    return strMediaMid;
                }

                public void setStrMediaMid(String strMediaMid) {
                    this.strMediaMid = strMediaMid;
                }

                public int getStream() {
                    return stream;
                }

                public void setStream(int stream) {
                    this.stream = stream;
                }

                public int getSwitchX() {
                    return switchX;
                }

                public void setSwitchX(int switchX) {
                    this.switchX = switchX;
                }

                public int getT() {
                    return t;
                }

                public void setT(int t) {
                    this.t = t;
                }

                public int getTag() {
                    return tag;
                }

                public void setTag(int tag) {
                    this.tag = tag;
                }

                public int getType() {
                    return type;
                }

                public void setType(int type) {
                    this.type = type;
                }

                public int getVer() {
                    return ver;
                }

                public void setVer(int ver) {
                    this.ver = ver;
                }

                public String getVid() {
                    return vid;
                }

                public void setVid(String vid) {
                    this.vid = vid;
                }

                public List<?> getGrp() {
                    return grp;
                }

                public void setGrp(List<?> grp) {
                    this.grp = grp;
                }

                public List<SingerBean> getSinger() {
                    return singer;
                }

                public void setSinger(List<SingerBean> singer) {
                    this.singer = singer;
                }

                @Override
                public String toString() {
                    return "ListBean{" +
                            "albumid=" + albumid +
                            ", albummid='" + albummid + '\'' +
                            ", albumname='" + albumname + '\'' +
                            ", albumname_hilight='" + albumname_hilight + '\'' +
                            ", alertid=" + alertid +
                            ", belongCD=" + belongCD +
                            ", cdIdx=" + cdIdx +
                            ", chinesesinger=" + chinesesinger +
                            ", docid='" + docid + '\'' +
                            ", interval=" + interval +
                            ", isonly=" + isonly +
                            ", lyric='" + lyric + '\'' +
                            ", lyric_hilight='" + lyric_hilight + '\'' +
                            ", media_mid='" + media_mid + '\'' +
                            ", msgid=" + msgid +
                            ", newStatus=" + newStatus +
                            ", nt=" + nt +
                            ", pay=" + pay +
                            ", preview=" + preview +
                            ", pubtime=" + pubtime +
                            ", pure=" + pure +
                            ", size128=" + size128 +
                            ", size320=" + size320 +
                            ", sizeape=" + sizeape +
                            ", sizeflac=" + sizeflac +
                            ", sizeogg=" + sizeogg +
                            ", songid=" + songid +
                            ", songmid='" + songmid + '\'' +
                            ", songname='" + songname + '\'' +
                            ", songname_hilight='" + songname_hilight + '\'' +
                            ", strMediaMid='" + strMediaMid + '\'' +
                            ", stream=" + stream +
                            ", switchX=" + switchX +
                            ", t=" + t +
                            ", tag=" + tag +
                            ", type=" + type +
                            ", ver=" + ver +
                            ", vid='" + vid + '\'' +
                            ", grp=" + grp +
                            ", singer=" + singer +
                            '}';
                }

                public static class PayBean {
                    /**
                     * payalbum : 0
                     * payalbumprice : 0
                     * paydownload : 1
                     * payinfo : 1
                     * payplay : 0
                     * paytrackmouth : 1
                     * paytrackprice : 200
                     */

                    private int payalbum;
                    private int payalbumprice;
                    private int paydownload;
                    private int payinfo;
                    private int payplay;
                    private int paytrackmouth;
                    private int paytrackprice;

                    public int getPayalbum() {
                        return payalbum;
                    }

                    public void setPayalbum(int payalbum) {
                        this.payalbum = payalbum;
                    }

                    public int getPayalbumprice() {
                        return payalbumprice;
                    }

                    public void setPayalbumprice(int payalbumprice) {
                        this.payalbumprice = payalbumprice;
                    }

                    public int getPaydownload() {
                        return paydownload;
                    }

                    public void setPaydownload(int paydownload) {
                        this.paydownload = paydownload;
                    }

                    public int getPayinfo() {
                        return payinfo;
                    }

                    public void setPayinfo(int payinfo) {
                        this.payinfo = payinfo;
                    }

                    public int getPayplay() {
                        return payplay;
                    }

                    public void setPayplay(int payplay) {
                        this.payplay = payplay;
                    }

                    public int getPaytrackmouth() {
                        return paytrackmouth;
                    }

                    public void setPaytrackmouth(int paytrackmouth) {
                        this.paytrackmouth = paytrackmouth;
                    }

                    public int getPaytrackprice() {
                        return paytrackprice;
                    }

                    public void setPaytrackprice(int paytrackprice) {
                        this.paytrackprice = paytrackprice;
                    }
                }

                public static class PreviewBean {
                    /**
                     * trybegin : 0
                     * tryend : 0
                     * trysize : 0
                     */

                    private int trybegin;
                    private int tryend;
                    private int trysize;

                    public int getTrybegin() {
                        return trybegin;
                    }

                    public void setTrybegin(int trybegin) {
                        this.trybegin = trybegin;
                    }

                    public int getTryend() {
                        return tryend;
                    }

                    public void setTryend(int tryend) {
                        this.tryend = tryend;
                    }

                    public int getTrysize() {
                        return trysize;
                    }

                    public void setTrysize(int trysize) {
                        this.trysize = trysize;
                    }
                }

                public static class SingerBean {
                    /**
                     * id : 13948
                     * mid : 001fNHEf1SFEFN
                     * name : G.E.M. 邓紫棋
                     * name_hilight : G.E.M. 邓紫棋
                     */

                    private int id;
                    private String mid;
                    private String name;
                    private String name_hilight;

                    public int getId() {
                        return id;
                    }

                    public void setId(int id) {
                        this.id = id;
                    }

                    public String getMid() {
                        return mid;
                    }

                    public void setMid(String mid) {
                        this.mid = mid;
                    }

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
                }
            }
        }

        public static class ZhidaBean {
            /**
             * chinesesinger : 0
             * type : 0
             */

            private int chinesesinger;
            private int type;

            public int getChinesesinger() {
                return chinesesinger;
            }

            public void setChinesesinger(int chinesesinger) {
                this.chinesesinger = chinesesinger;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }
        }
    }

    @Override
    public String toString() {
        return "SearchSong{" +
                "code=" + code +
                ", data=" + data +
                ", message='" + message + '\'' +
                ", notice='" + notice + '\'' +
                ", subcode=" + subcode +
                ", time=" + time +
                ", tips='" + tips + '\'' +
                '}';
    }
}
