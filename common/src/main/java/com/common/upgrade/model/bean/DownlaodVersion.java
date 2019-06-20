package com.common.upgrade.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 */
public class DownlaodVersion implements Parcelable {

    public static final Creator<DownlaodVersion> CREATOR = new Creator<DownlaodVersion>() {
        @Override
        public DownlaodVersion createFromParcel(Parcel in) {
            return new DownlaodVersion(in);
        }

        @Override
        public DownlaodVersion[] newArray(int size) {
            return new DownlaodVersion[size];
        }
    };
    /**
     * 版本
     */
    private int version;
    /**
     * 是否忽略
     */
    private boolean isIgnored;

    protected DownlaodVersion(Parcel in) {
        version = in.readInt();
        isIgnored = in.readByte() != 0;
    }

    public DownlaodVersion() {
    }

    public DownlaodVersion(int version, boolean isIgnored) {
        this.version = version;
        this.isIgnored = isIgnored;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isIgnored() {
        return isIgnored;
    }

    public void setIgnored(boolean ignored) {
        isIgnored = ignored;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(version);
        dest.writeByte((byte) (isIgnored ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "DownlaodVersion{" +
                "version=" + version +
                ", isIgnored=" + isIgnored +
                '}';
    }
}
