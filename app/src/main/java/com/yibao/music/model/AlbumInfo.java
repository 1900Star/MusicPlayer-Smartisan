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
    private String artist;
    private Long albumId;
    private int songCount;
    private String firstChar;
    private String songName;
    private int year;
    private boolean mSelected;

    private AlbumInfo(Parcel in) {
        albumName = in.readString();
        artist = in.readString();
        if (in.readByte() == 0) {
            albumId = null;
        } else {
            albumId = in.readLong();
        }
        songCount = in.readInt();
        firstChar = in.readString();
        songName = in.readString();
        year = in.readInt();
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

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

    public String getArtist() {
        return artist;
    }

    public void setArtist(String singerName) {
        this.artist = singerName;
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(albumName);
        parcel.writeString(artist);
        if (albumId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(albumId);
        }
        parcel.writeInt(songCount);
        parcel.writeString(firstChar);
        parcel.writeString(songName);
        parcel.writeInt(year);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AlbumInfo albumInfo = (AlbumInfo) o;

        if (songCount != albumInfo.songCount) return false;
        if (year != albumInfo.year) return false;
        if (albumName != null ? !albumName.equals(albumInfo.albumName) : albumInfo.albumName != null)
            return false;
        if (artist != null ? !artist.equals(albumInfo.artist) : albumInfo.artist != null)
            return false;
        if (albumId != null ? !albumId.equals(albumInfo.albumId) : albumInfo.albumId != null)
            return false;
        if (firstChar != null ? !firstChar.equals(albumInfo.firstChar) : albumInfo.firstChar != null)
            return false;
        return songName != null ? songName.equals(albumInfo.songName) : albumInfo.songName == null;
    }

    @Override
    public int hashCode() {
        int result = albumName != null ? albumName.hashCode() : 0;
        result = 31 * result + (artist != null ? artist.hashCode() : 0);
        result = 31 * result + (albumId != null ? albumId.hashCode() : 0);
        result = 31 * result + songCount;
        result = 31 * result + (firstChar != null ? firstChar.hashCode() : 0);
        result = 31 * result + (songName != null ? songName.hashCode() : 0);
        result = 31 * result + year;
        return result;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }
}
