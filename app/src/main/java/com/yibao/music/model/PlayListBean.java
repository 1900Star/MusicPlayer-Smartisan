package com.yibao.music.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

/**
 * @ Author: Luoshipeng
 * @ Name:   PlayListBean
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/10/28/ 23:18
 * @ Des:    播放列表的数据库
 * @author Luoshipeng
 */
@Entity
public class PlayListBean implements Parcelable, Comparable<PlayListBean>{
    @Id(autoincrement = true)
    @Unique
    private Long id;
    private String title;
    private Long addTime;
    private int songCount;
    private boolean isSelected;
    public PlayListBean(String title, Long addTime) {
        this.title = title;
        this.addTime = addTime;
    }


    protected PlayListBean(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        title = in.readString();
        if (in.readByte() == 0) {
            addTime = null;
        } else {
            addTime = in.readLong();
        }
        songCount = in.readInt();
        isSelected = in.readByte() != 0;
    }


    @Generated(hash = 1098116397)
    public PlayListBean(Long id, String title, Long addTime, int songCount,
            boolean isSelected) {
        this.id = id;
        this.title = title;
        this.addTime = addTime;
        this.songCount = songCount;
        this.isSelected = isSelected;
    }


    @Generated(hash = 1039443864)
    public PlayListBean() {
    }

    public static final Creator<PlayListBean> CREATOR = new Creator<PlayListBean>() {
        @Override
        public PlayListBean createFromParcel(Parcel in) {
            return new PlayListBean(in);
        }

        @Override
        public PlayListBean[] newArray(int size) {
            return new PlayListBean[size];
        }
    };

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

    public Long getAddTime() {
        return addTime;
    }

    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }


    @Override
    public int compareTo(@NonNull PlayListBean o) {
        return Long.compare(o.getAddTime(), this.getAddTime());
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
        dest.writeString(title);
        if (addTime == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(addTime);
        }
        dest.writeInt(songCount);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }


    public boolean getIsSelected() {
        return this.isSelected;
    }


    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @NonNull
    @Override
    public String toString() {
        return "PlayListBean{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", addTime=" + addTime +
                ", songCount=" + songCount +
                ", isSelected=" + isSelected +
                '}';
    }
}
