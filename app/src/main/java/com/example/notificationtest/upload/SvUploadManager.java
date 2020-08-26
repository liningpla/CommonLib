package com.example.notificationtest.upload;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by lining on 2017/3/15.
 * 文件上传管理类，一个文件对应一个
 */
public class SvUploadManager {

    public static String UPLOAD_URI = "http://10.10.32.145:8086/files/shortvideo/upload/file";
    private Thread workThread;
    private ExecutorService cachedThreadPool;
    private List<FilePartInfo> partInfos;
    private FileUploadListener uploadListener;
    private FileInfo fileInfo;
    private File loadFile;
    public static volatile boolean isPause;
    public SvUploadManager(FileInfo fileInfo, FileUploadListener uploadListener, ExecutorService cachedThreadPool) {
        this.uploadListener = uploadListener;
        this.fileInfo = fileInfo;
        this.loadFile = new File(fileInfo.filePath);
        fileInfo.Md5 = (SvFileUtils.getMd5ByFile(loadFile));
        partInfos = fileInfo.filePartInfos;
        this.cachedThreadPool = cachedThreadPool;
    }
    /**上传文件
     * @loadFile 要上传的文件
     * */
    public void upLoadFile(){
        LoadRunable loadRunable = new LoadRunable(loadFile);
        workThread = new Thread(loadRunable);
        workThread.start();
    }
    /**分片上传文件处理类*/
    private class LoadRunable implements Runnable{
        private File loadFile;
        public LoadRunable(File loadFile) {
            this.loadFile = loadFile;
        }
        @Override
        public void run() {
            try {
                //根据分片创建线程，提交到线程池进行分片上传
                for (FilePartInfo filePart:partInfos){
                    cachedThreadPool.submit(new SvUploadUtils.HttpUploadTask(filePart, new SvUploadUtils.PartFileUploadListener() {
                        @Override
                        public void callBackPartFile(FilePartInfo filePartInfo) {
                            synchronized (SvUploadManager.class){
                                fileInfo.loadSize += filePartInfo.num;
                                uploadListener.onPrograess(fileInfo.loadSize);
                                if(loadFile.length() == fileInfo.loadSize){//下载完成
                                }
                            }
                        }
                    }));
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
    /**暂停*/
    public void puseLoad(){
        SvUploadManager.isPause = true;
    }
    /**继续*/
    public void continueLoad(){
        SvUploadManager.isPause = false;
        upLoadFile();
    }
    public interface FileUploadListener{
        void onPrograess(long num);
    }
}
