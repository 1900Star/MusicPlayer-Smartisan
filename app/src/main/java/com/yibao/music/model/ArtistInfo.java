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

    private String artist;
    private String albumName;
    private int albumCount;
    private int songCount;
    private String firstChar;
    private int year;
    private long albumId;

    protected ArtistInfo(Parcel in) {
        artist = in.readString();
        albumName = in.readString();
        albumCount = in.readInt();
        songCount = in.readInt();
        firstChar = in.readString();
        year = in.readInt();
        albumId = in.readLong();
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

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public ArtistInfo() {
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String songName) {
        this.albumName = songName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(artist);
        parcel.writeString(albumName);
        parcel.writeInt(albumCount);
        parcel.writeInt(songCount);
        parcel.writeString(firstChar);
        parcel.writeInt(year);
        parcel.writeLong(albumId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArtistInfo that = (ArtistInfo) o;

        if (albumCount != that.albumCount) return false;
        if (songCount != that.songCount) return false;
        if (year != that.year) return false;
        if (albumId != that.albumId) return false;
        if (artist != null ? !artist.equals(that.artist) : that.artist != null) return false;
        if (albumName != null ? !albumName.equals(that.albumName) : that.albumName != null)
            return false;
        return firstChar != null ? firstChar.equals(that.firstChar) : that.firstChar == null;
    }

    @Override
    public int hashCode() {
        int result = artist != null ? artist.hashCode() : 0;
        result = 31 * result + (albumName != null ? albumName.hashCode() : 0);
        result = 31 * result + albumCount;
        result = 31 * result + songCount;
        result = 31 * result + (firstChar != null ? firstChar.hashCode() : 0);
        result = 31 * result + year;
        result = 31 * result + (int) (albumId ^ (albumId >>> 32));
        return result;
    }
}
