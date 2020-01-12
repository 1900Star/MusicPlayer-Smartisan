// IMusicAidlInterface.aidl
package com.yibao.music.aidl;

import com.yibao.music.aidl.MusicBean;
import com.yibao.music.aidl.MusicLyricBean;
// Declare any non-default types here with import statements
interface IMusicAidlInterface {
    void play();
    void start();
    void pause();
    void playPre();
    void playNext();
    void autoPlayNext();
    void updateFavorite();
    void hintNotifycation();
    void seekTo(int progress);
    void setPalyMode(int playmode);
    void showNotifycation(boolean b);
    boolean isPlaying();
    int getProgress();
    int getDuration();
    int getPalyMode();
    int getPosition();
    MusicBean getMusicBean ();
    List<MusicBean> getMusicList();
    List<MusicLyricBean> getLyricList();
}
