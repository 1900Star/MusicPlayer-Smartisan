package com.yibao.music.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.model
 * @文件名: AlbumInfo
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/8 18:13
 * @描述： {TODO}
 */
@Entity
public class AlbumInfo implements Parcelable, Comparable<AlbumInfo> {
    @Id(autoincrement = true)
    @Unique
    private Long id;
    private String albumName;
    private String artist;
    private Long albumId;
    private int songCount;
    private String firstChar;
    private String songName;
    private int year;
    private boolean mSelected;


    protected AlbumInfo(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
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
        mSelected = in.readByte() != 0;
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

    @Generated(hash = 643847150)
    public AlbumInfo(Long id, String albumName, String artist, Long albumId,
            int songCount, String firstChar, String songName, int year,
            boolean mSelected) {
        this.id = id;
        this.albumName = albumName;
        this.artist = artist;
        this.albumId = albumId;
        this.songCount = songCount;
        this.firstChar = firstChar;
        this.songName = songName;
        this.year = year;
        this.mSelected = mSelected;
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

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(albumName);
        dest.writeString(artist);
        if (albumId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(albumId);
        }
        dest.writeInt(songCount);
        dest.writeString(firstChar);
        dest.writeString(songName);
        dest.writeInt(year);
        dest.writeByte((byte) (mSelected ? 1 : 0));
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getMSelected() {
        return this.mSelected;
    }

    public void setMSelected(boolean mSelected) {
        this.mSelected = mSelected;
    }
}
