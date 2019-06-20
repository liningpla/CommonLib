package com.common.upgrade.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 */

public class DownlaodBuffer implements Parcelable {
    /**
     * 缓存有效期
     */
    public static final int EXPIRY_DATE = 7 * 24 * 60 * 60 * 1000;
    public static final Creator<DownlaodBuffer> CREATOR = new Creator<DownlaodBuffer>() {
        @Override
        public DownlaodBuffer createFromParcel(Parcel in) {
            return new DownlaodBuffer(in);
        }

        @Override
        public DownlaodBuffer[] newArray(int size) {
            return new DownlaodBuffer[size];
        }
    };
    /**
     * 下载链接
     */
    private String downloadUrl;
    /**
     * MD5文件完整校验
     */
    private String fileMd5;
    /**
     * 文件总长度
     */
    private long fileLength;
    /**
     * 缓存长度
     */
    private long bufferLength;
    /**
     * 分流段部分
     */
    private List<BufferPart> bufferParts;
    /**
     * 最后修改时间
     */
    private long lastModified;

    public DownlaodBuffer() {
    }

    public DownlaodBuffer(String downloadUrl, String fileMd5, long fileLength, long bufferLength, List<BufferPart> bufferParts, long lastModified) {
        this.downloadUrl = downloadUrl;
        this.fileMd5 = fileMd5;
        this.fileLength = fileLength;
        this.bufferLength = bufferLength;
        this.bufferParts = bufferParts;
        this.lastModified = lastModified;
    }

    protected DownlaodBuffer(Parcel in) {
        downloadUrl = in.readString();
        fileMd5 = in.readString();
        fileLength = in.readLong();
        bufferLength = in.readLong();
        bufferParts = in.createTypedArrayList(BufferPart.CREATOR);
        lastModified = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(downloadUrl);
        dest.writeString(fileMd5);
        dest.writeLong(fileLength);
        dest.writeLong(bufferLength);
        dest.writeTypedList(bufferParts);
        dest.writeLong(lastModified);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public long getBufferLength() {
        return bufferLength;
    }

    public void setBufferLength(long bufferLength) {
        this.bufferLength = bufferLength;
    }

    public List<BufferPart> getBufferParts() {
        return bufferParts;
    }

    public void setBufferParts(List<BufferPart> bufferParts) {
        this.bufferParts = bufferParts;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public String toString() {
        return "DownlaodBuffer{" +
                "downloadUrl='" + downloadUrl + '\'' +
                ", fileMd5='" + fileMd5 + '\'' +
                ", fileLength=" + fileLength +
                ", bufferLength=" + bufferLength +
                ", shuntParts=" + bufferParts +
                ", lastModified=" + lastModified +
                '}';
    }

    public static class BufferPart implements Parcelable {
        public static final Creator<BufferPart> CREATOR = new Creator<BufferPart>() {
            @Override
            public BufferPart createFromParcel(Parcel in) {
                return new BufferPart(in);
            }

            @Override
            public BufferPart[] newArray(int size) {
                return new BufferPart[size];
            }
        };
        private long startLength;
        private long endLength;

        public BufferPart() {
        }

        public BufferPart(long startLength, long endLength) {
            this.startLength = startLength;
            this.endLength = endLength;
        }

        protected BufferPart(Parcel in) {
            startLength = in.readLong();
            endLength = in.readLong();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(startLength);
            dest.writeLong(endLength);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public long getStartLength() {
            return startLength;
        }

        public void setStartLength(long startLength) {
            this.startLength = startLength;
        }

        public long getEndLength() {
            return endLength;
        }

        public void setEndLength(long endLength) {
            this.endLength = endLength;
        }

        @Override
        public String toString() {
            return "BufferPart{" +
                    "startLength=" + startLength +
                    ", endLength=" + endLength +
                    '}';
        }
    }

}
