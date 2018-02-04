package com.yibao.music.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/9/4 03:04
 */
public class MusicDialogInfo implements Parcelable {
    private ArrayList<MusicBean> mList;
    private MusicBean mInfo;

    public MusicDialogInfo(ArrayList<MusicBean> list, MusicBean info) {
        mList = list;
        mInfo = info;
    }

    public MusicDialogInfo(Parcel in) {
        mList = in.createTypedArrayList(MusicBean.CREATOR);
        mInfo = in.readParcelable(MusicBean.class.getClassLoader());
    }

    public static final Creator<MusicDialogInfo> CREATOR = new Creator<MusicDialogInfo>() {
        @Override
        public MusicDialogInfo createFromParcel(Parcel in) {
            return new MusicDialogInfo(in);
        }

        @Override
        public MusicDialogInfo[] newArray(int size) {
            return new MusicDialogInfo[size];
        }
    };

    public ArrayList<MusicBean> getList() {
        return mList;
    }

    public void setList(ArrayList<MusicBean> list) {
        mList = list;
    }

    public MusicBean getInfo() {
        return mInfo;
    }

    public void setInfo(MusicBean info) {
        mInfo = info;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(mList);
        parcel.writeParcelable(mInfo, i);
    }
}
