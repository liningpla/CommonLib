package com.common.download.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.Serializable;

/**
 */

public final class DownerOptions implements Parcelable {
    public static final Creator<DownerOptions> CREATOR = new Creator<DownerOptions>() {
        @Override
        public DownerOptions createFromParcel(Parcel in) {
            return new DownerOptions(in);
        }

        @Override
        public DownerOptions[] newArray(int size) {
            return new DownerOptions[size];
        }
    };
    /**文件总长度*/
    private long filelength = 0L;
    /**重定向后的真实下载url*/
    private String trueUrl;
    /**
     * 通知栏图标
     */
    private final Bitmap icon;
    /**
     * 通知栏标题
     */
    private final CharSequence title;
    /**
     * 通知内容
     */
    private final CharSequence description;
    /**
     * 文件存储
     */
    private final File storage;
    /**
     * 下载链接或更新文档链接
     */
    private final String url;
    /**
     * MD5文件完整校验
     */
    private final String md5;
    /**
     * 是否支持多线程下载
     */
    private final boolean multithreadEnabled;
    /**
     * 多线程下载线程池最大数量
     */
    private final int multithreadPools;
    /**
     * 是否自动安装安装包
     */
    private final boolean automountEnabled;
    /**
     * 是否自动清除安装包
     */
    private final boolean autocleanEnabled;

    /**
     * 是否自动清除安装包
     */
    private final boolean isSupportRange;
    /**是否支持覆盖下载*/
    private final boolean isOverride;
    /**是否需要通知栏*/
    private final boolean isNeedNotify;

    private DownerOptions(Params params) {
        icon = params.icon;
        title = params.title;
        description = params.description;
        storage = params.storage;
        url = params.url;
        md5 = params.md5;
        multithreadEnabled = params.multithreadEnabled;
        multithreadPools = params.multithreadPools;
        automountEnabled = params.autocleanEnabled;
        autocleanEnabled = params.autocleanEnabled;
        isSupportRange = params.isSupportRange;
        isOverride = params.isOverride;
        isNeedNotify = params.isNeedNotify;
    }

    protected DownerOptions(Parcel in) {
        icon = in.readParcelable(Bitmap.class.getClassLoader());
        title = (CharSequence) in.readValue(CharSequence.class.getClassLoader());
        description = (CharSequence) in.readValue(CharSequence.class.getClassLoader());
        storage = (File) in.readSerializable();
        url = in.readString();
        md5 = in.readString();
        multithreadEnabled = in.readByte() != 0;
        multithreadPools = in.readInt();
        automountEnabled = in.readByte() != 0;
        autocleanEnabled = in.readByte() != 0;
        isSupportRange = in.readByte() != 0;
        isOverride = in.readByte() != 0;
        isNeedNotify = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(icon, flags);
        dest.writeValue(title);
        dest.writeValue(description);
        dest.writeSerializable(storage);
        dest.writeString(url);
        dest.writeString(md5);
        dest.writeByte((byte) (multithreadEnabled ? 1 : 0));
        dest.writeInt(multithreadPools);
        dest.writeInt((byte) (automountEnabled ? 1 : 0));
        dest.writeInt((byte) (autocleanEnabled ? 1 : 0));
        dest.writeInt((byte) (isSupportRange ? 1 : 0));
        dest.writeInt((byte) (isOverride ? 1 : 0));
        dest.writeInt((byte) (isNeedNotify ? 1 : 0));

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void setFilelength(long filelength) {this.filelength = filelength; }

    public long getFilelength() {return filelength; }

    public String getTrueUrl() { return trueUrl;}

    public void setTrueUrl(String trueUrl) {this.trueUrl = trueUrl;}

    public Bitmap getIcon() {
        return icon;
    }

    public CharSequence getTitle() {
        return title;
    }

    public CharSequence getDescription() {
        return description;
    }

    public File getStorage() {
        return storage;
    }

    public String getUrl() {
        return url;
    }

    public String getMd5() {
        return md5;
    }

    public boolean isMultithreadEnabled() {
        return multithreadEnabled;
    }

    public int getMultithreadPools() {
        return multithreadPools;
    }

    public boolean isAutomountEnabled() {
        return automountEnabled;
    }

    public boolean isAutocleanEnabled() {
        return autocleanEnabled;
    }

    public boolean isSupportRange() {
        return isSupportRange;
    }

    public boolean isOverride() {
        return isOverride;
    }

    public boolean isNeedNotify(){
        return isNeedNotify;
    }

    public static class Builder implements Parcelable {
        private Params params;

        public Builder() {
            params = new Params();
        }

        protected Builder(Parcel in) {
        }

        public static final Creator<Builder> CREATOR = new Creator<Builder>() {
            @Override
            public Builder createFromParcel(Parcel in) {
                return new Builder(in);
            }

            @Override
            public Builder[] newArray(int size) {
                return new Builder[size];
            }
        };

        public Builder setIcon(Bitmap icon) {
            params.icon = icon;
            return this;
        }

        public Builder setTitle(CharSequence title) {
            params.title = title;
            return this;
        }

        public Builder setDescription(CharSequence description) {
            params.description = description;
            return this;
        }

        public Builder setStorage(File storage) {
            params.storage = storage;
            return this;
        }

        public Builder setUrl(String url) {
            params.url = url;
            return this;
        }

        public Builder setMd5(String md5) {
            params.md5 = md5;
            return this;
        }

        public Builder setMultithreadEnabled(boolean enabled) {
            params.multithreadEnabled = enabled;
            return this;
        }

        public Builder setMultithreadPools(int pools) {
            params.multithreadPools = pools < 0 ? 0 : pools;
            return this;
        }

        public Builder setAutomountEnabled(boolean enabled) {
            params.automountEnabled = enabled;
            return this;
        }

        public Builder setAutocleanEnabled(boolean enabled) {
            params.autocleanEnabled = enabled;
            return this;
        }

        public Builder setSupportRange(boolean isSupportRange){
            params.isSupportRange = isSupportRange;
            return this;
        }

        public Builder setOverride(boolean isOverride){
            params.isOverride = isOverride;
            return this;
        }
        public Builder needNotify(boolean isNeedNotify){
            params.isNeedNotify = isNeedNotify;
            return this;
        }


        public DownerOptions build() {
            return new DownerOptions(params);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(params.icon, flags);
            dest.writeValue(params.title);
            dest.writeValue(params.description);
            dest.writeSerializable(params.storage);
            dest.writeString(params.url);
            dest.writeString(params.md5);
            dest.writeByte((byte) (params.multithreadEnabled ? 1 : 0));
            dest.writeInt(params.multithreadPools);
            dest.writeInt((byte) (params.automountEnabled ? 1 : 0));
            dest.writeInt((byte) (params.autocleanEnabled ? 1 : 0));
            dest.writeInt((byte) (params.isSupportRange ? 1 : 0));
            dest.writeInt((byte) (params.isOverride ? 1 : 0));
        }
    }

    static class Params implements Serializable {
        Bitmap icon;
        CharSequence title;
        CharSequence description;
        File storage;
        String url;
        String md5;
        boolean multithreadEnabled;
        int multithreadPools;
        boolean automountEnabled;
        boolean autocleanEnabled;
        boolean isSupportRange;
        boolean isOverride;
        boolean isNeedNotify;
    }

}
