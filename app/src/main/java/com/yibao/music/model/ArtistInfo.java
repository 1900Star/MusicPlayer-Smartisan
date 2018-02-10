package com.yibao.music.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.model
 * @文件名: ArtistInfo
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/7 16:51
 * @描述： {TODO}
 */

public class ArtistInfo implements Parcelable, Comparable<ArtistInfo> {

    private String name;
    private int albumCount;
    private int songCount;
    private String firstChar;

    public ArtistInfo() {
    }

    private ArtistInfo(Parcel in) {
        name = in.readString();
        albumCount = in.readInt();
        songCount = in.readInt();
        firstChar = in.readString();
    }

    public static final Creator<ArtistInfo> CREATOR = new Creator<ArtistInfo>() {
        @Override
        public ArtistInfo createFromParcel(Parcel in) {
            return new ArtistInfo(in);
        }

        @Override
        public ArtistInfo[] newArray(int size) {
            return new ArtistInfo[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAlbumCount() {
        return albumCount;
    }

    public void setAlbumCount(int albumCount) {
        this.albumCount = albumCount;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }

    public String getFirstChar() {
        return firstChar;
    }

    public void setFirstChar(String firstChar) {
        this.firstChar = firstChar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(albumCount);
        parcel.writeInt(songCount);
        parcel.writeString(firstChar);
    }

    @Override
    public int compareTo(@NonNull ArtistInfo artistInfo) {
        String str = "#";
        if (str.equals(artistInfo.getFirstChar())) {

            return -1;
        }
        if (str.equals(firstChar)) {

            return 1;
        }
        return firstChar.compareTo(artistInfo.getFirstChar());
    }
}
