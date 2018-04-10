package com.yibao.music.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Author：Sid
 * Des：${音乐实体类,收藏 删除等}
 * Time:2017/9/3 14:31
 *
 * @author Stran
 */
@Entity
public class MusicBean
        implements Parcelable, Comparable<MusicBean> {
    @Id(autoincrement = true)
    private Long id;
    private String title;
    private String artist;
    private String album;
    private long albumId;
    private long addTime;
    private long duration;
    private String time;
    private String songUrl;
    private String firstChar;
    private boolean isFavorite;
    private int playFrequency;
    private int songScore;
    private int playStatus;
    private int issueYear;
    /**
     * 这个cureetPosition是给QqPagerAdapter用的，用于切换到指定的位置
     */


    @Transient
    private int cureetPosition;


    public MusicBean() {
    }

    protected MusicBean(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        title = in.readString();
        artist = in.readString();
        album = in.readString();
        albumId = in.readLong();
        addTime = in.readLong();
        duration = in.readLong();
        time = in.readString();
        songUrl = in.readString();
        firstChar = in.readString();
        isFavorite = in.readByte() != 0;
        playFrequency = in.readInt();
        songScore = in.readInt();
        playStatus = in.readInt();
        cureetPosition = in.readInt();
    }

    @Generated(hash = 206715861)
    public MusicBean(Long id, String title, String artist, String album,
                     long albumId, long addTime, long duration, String time, String songUrl,
                     String firstChar, boolean isFavorite, int playFrequency, int songScore,
                     int playStatus, int issueYear) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.albumId = albumId;
        this.addTime = addTime;
        this.duration = duration;
        this.time = time;
        this.songUrl = songUrl;
        this.firstChar = firstChar;
        this.isFavorite = isFavorite;
        this.playFrequency = playFrequency;
        this.songScore = songScore;
        this.playStatus = playStatus;
        this.issueYear = issueYear;
    }


    public static final Creator<MusicBean> CREATOR = new Creator<MusicBean>() {
        @Override
        public MusicBean createFromParcel(Parcel in) {
            return new MusicBean(in);
        }

        @Override
        public MusicBean[] newArray(int size) {
            return new MusicBean[size];
        }
    };

    public int getIssueYear() {
        return issueYear;
    }

    public void setIssueYear(int issueYear) {
        this.issueYear = issueYear;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public String getFirstChar() {
        return firstChar;
    }

    public void setFirstChar(String firstChar) {
        this.firstChar = firstChar;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public int getPlayFrequency() {
        return playFrequency;
    }

    public void setPlayFrequency(int playFrequency) {
        this.playFrequency = playFrequency;
    }

    public int getSongScore() {
        return songScore;
    }

    public void setSongScore(int songScore) {
        this.songScore = songScore;
    }

    public int getPlayStatus() {
        return playStatus;
    }

    public void setPlayStatus(int playStatus) {
        this.playStatus = playStatus;
    }

    public int getCureetPosition() {
        return cureetPosition;
    }

    public void setCureetPosition(int cureetPosition) {
        this.cureetPosition = cureetPosition;
    }


    @Override
    public int compareTo(@NonNull MusicBean musicBean) {
        String str = "#";
        if (str.equals(musicBean.getFirstChar())) {

            return -1;
        }
        if (str.equals(firstChar)) {

            return 1;
        }
        return firstChar.compareTo(musicBean.getFirstChar());
    }


    public boolean getIsFavorite() {
        return this.isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(id);
        }
        parcel.writeString(title);
        parcel.writeString(artist);
        parcel.writeString(album);
        parcel.writeLong(albumId);
        parcel.writeLong(addTime);
        parcel.writeLong(duration);
        parcel.writeString(time);
        parcel.writeString(songUrl);
        parcel.writeString(firstChar);
        parcel.writeByte((byte) (isFavorite ? 1 : 0));
        parcel.writeInt(playFrequency);
        parcel.writeInt(songScore);
        parcel.writeInt(playStatus);
        parcel.writeInt(issueYear);
        parcel.writeInt(cureetPosition);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MusicBean musicBean = (MusicBean) o;

        if (albumId != musicBean.albumId) return false;
        if (addTime != musicBean.addTime) return false;
        if (duration != musicBean.duration) return false;
        if (isFavorite != musicBean.isFavorite) return false;
        if (playFrequency != musicBean.playFrequency) return false;
        if (songScore != musicBean.songScore) return false;
        if (playStatus != musicBean.playStatus) return false;
        if (issueYear != musicBean.issueYear) return false;
        if (cureetPosition != musicBean.cureetPosition) return false;
        if (id != null ? !id.equals(musicBean.id) : musicBean.id != null) return false;
        if (title != null ? !title.equals(musicBean.title) : musicBean.title != null) return false;
        if (artist != null ? !artist.equals(musicBean.artist) : musicBean.artist != null)
            return false;
        if (album != null ? !album.equals(musicBean.album) : musicBean.album != null) return false;
        if (time != null ? !time.equals(musicBean.time) : musicBean.time != null) return false;
        if (songUrl != null ? !songUrl.equals(musicBean.songUrl) : musicBean.songUrl != null)
            return false;
        return firstChar != null ? firstChar.equals(musicBean.firstChar) : musicBean.firstChar == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (artist != null ? artist.hashCode() : 0);
        result = 31 * result + (album != null ? album.hashCode() : 0);
        result = 31 * result + (int) (albumId ^ (albumId >>> 32));
        result = 31 * result + (int) (addTime ^ (addTime >>> 32));
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (songUrl != null ? songUrl.hashCode() : 0);
        result = 31 * result + (firstChar != null ? firstChar.hashCode() : 0);
        result = 31 * result + (isFavorite ? 1 : 0);
        result = 31 * result + playFrequency;
        result = 31 * result + songScore;
        result = 31 * result + playStatus;
        result = 31 * result + issueYear;
        result = 31 * result + cureetPosition;
        return result;
    }
}
