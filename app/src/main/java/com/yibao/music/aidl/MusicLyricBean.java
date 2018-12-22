package com.yibao.music.aidl;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Des：${TODO}
 * Time:2017/9/14 00:58
 * @author Stran
 */
public class MusicLyricBean
        implements Parcelable, Comparable<MusicLyricBean> {
    private int startTime;
    private String content;
    public MusicLyricBean(int startTime, String content) {
        this.startTime = startTime;
        this.content = content;
    }

    protected MusicLyricBean(Parcel in) {
        startTime = in.readInt();
        content = in.readString();
    }

    public static final Creator<MusicLyricBean> CREATOR = new Creator<MusicLyricBean>() {
        @Override
        public MusicLyricBean createFromParcel(Parcel in) {
            return new MusicLyricBean(in);
        }

        @Override
        public MusicLyricBean[] newArray(int size) {
            return new MusicLyricBean[size];
        }
    };

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int compareTo(@NonNull MusicLyricBean musicLrcBean) {
        return startTime - musicLrcBean.getStartTime();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(startTime);
        dest.writeString(content);
    }
}
