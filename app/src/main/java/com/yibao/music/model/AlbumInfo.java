package com.yibao.music.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.model
 * @文件名: AlbumInfo
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/8 18:13
 * @描述： {TODO}
 */

public class AlbumInfo implements Parcelable, Comparable<AlbumInfo> {
    private String albumName;
    private String singerName;
    private Long albumId;
    private int songCount;
    private String firstChar;
    private String songName;


    protected AlbumInfo(Parcel in) {
        albumName = in.readString();
        singerName = in.readString();
        if (in.readByte() == 0) {
            albumId = null;
        } else {
            albumId = in.readLong();
        }
        songCount = in.readInt();
        firstChar = in.readString();
        songName = in.readString();
    }

    public static final Creator<AlbumInfo> CREATOR = new Creator<AlbumInfo>() {
        @Override
        public AlbumInfo createFromParcel(Parcel in) {
            return new AlbumInfo(in);
        }

        @Override
        public AlbumInfo[] newArray(int size) {
            return new AlbumInfo[size];
        }
    };

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public AlbumInfo() {
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
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
    public int compareTo(@NonNull AlbumInfo albumInfo) {
        String str = "#";
        if (str.equals(albumInfo.getFirstChar())) {

            return -1;
        }
        if (str.equals(firstChar)) {

            return 1;
        }
        return firstChar.compareTo(albumInfo.getFirstChar());


    }


    @Override
    public int hashCode() {
        int result = albumName != null ? albumName.hashCode() : 0;
        result = 31 * result + (singerName != null ? singerName.hashCode() : 0);
        result = 31 * result + (albumId != null ? albumId.hashCode() : 0);
        result = 31 * result + songCount;
        result = 31 * result + (firstChar != null ? firstChar.hashCode() : 0);
        result = 31 * result + (songName != null ? songName.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(albumName);
        parcel.writeString(singerName);
        if (albumId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(albumId);
        }
        parcel.writeInt(songCount);
        parcel.writeString(firstChar);
        parcel.writeString(songName);
    }
}
