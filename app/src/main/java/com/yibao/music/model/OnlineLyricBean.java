package com.yibao.filereaddemo.util;

import java.util.List;

/**
 * @ Author: Luoshipeng
 * @ Name:   OnlineLyricBean
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/10/3/ 16:10
 * @ Des:    TODO
 */
public class OnlineLyricBean {

    /**
     * code : 0
     * count : 14
     * result : [{"aid":2492174,"artist_id":2,"lrc":"http://s.gecimi.com/lrc/294/29497/2949755.lrc","sid":2949755,"song":"七友"},{"aid":2585090,"artist_id":2,"lrc":"http://s.gecimi.com/lrc/308/30808/3080834.lrc","sid":3080834,"song":"七友"},{"aid":2589446,"artist_id":2,"lrc":"http://s.gecimi.com/lrc/308/30865/3086542.lrc","sid":3086542,"song":"七友"},{"aid":2646479,"artist_id":2384,"lrc":"http://s.gecimi.com/lrc/316/31655/3165558.lrc","sid":3165558,"song":"七友"},{"aid":2449748,"artist_id":2419,"lrc":"http://s.gecimi.com/lrc/289/28908/2890807.lrc","sid":2890807,"song":"七友"},{"aid":2472014,"artist_id":2605,"lrc":"http://s.gecimi.com/lrc/292/29226/2922683.lrc","sid":2922683,"song":"七友"},{"aid":2505107,"artist_id":2605,"lrc":"http://s.gecimi.com/lrc/296/29680/2968009.lrc","sid":2968009,"song":"七友"},{"aid":2412335,"artist_id":13168,"lrc":"http://s.gecimi.com/lrc/283/28371/2837135.lrc","sid":2837135,"song":"七友"},{"aid":2430551,"artist_id":13168,"lrc":"http://s.gecimi.com/lrc/286/28629/2862987.lrc","sid":2862987,"song":"七友"},{"aid":2525087,"artist_id":13168,"lrc":"http://s.gecimi.com/lrc/299/29966/2996635.lrc","sid":2996635,"song":"七友"},{"aid":3018296,"artist_id":13168,"lrc":"http://s.gecimi.com/lrc/367/36711/3671107.lrc","sid":3671107,"song":"七友"},{"aid":3216836,"artist_id":13168,"lrc":"http://s.gecimi.com/lrc/393/39366/3936642.lrc","sid":3936642,"song":"七友"},{"aid":2902322,"artist_id":32686,"lrc":"http://s.gecimi.com/lrc/351/35182/3518295.lrc","sid":3518295,"song":"七友"},{"aid":2504684,"artist_id":34751,"lrc":"http://s.gecimi.com/lrc/296/29672/2967275.lrc","sid":2967275,"song":"七友"}]
     */

    private int code;
    private int count;
    private List<ResultBean> result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * aid : 2492174
         * artist_id : 2
         * lrc : http://s.gecimi.com/lrc/294/29497/2949755.lrc
         * sid : 2949755
         * song : 七友
         */

        private int aid;
        private int artist_id;
        private String lrc;
        private int sid;
        private String song;

        public int getAid() {
            return aid;
        }

        public void setAid(int aid) {
            this.aid = aid;
        }

        public int getArtist_id() {
            return artist_id;
        }

        public void setArtist_id(int artist_id) {
            this.artist_id = artist_id;
        }

        public String getLrc() {
            return lrc;
        }

        public void setLrc(String lrc) {
            this.lrc = lrc;
        }

        public int getSid() {
            return sid;
        }

        public void setSid(int sid) {
            this.sid = sid;
        }

        public String getSong() {
            return song;
        }

        public void setSong(String song) {
            this.song = song;
        }
    }
}
