package com.example.notificationtest.upload;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lining on 2017/5/15.
 * 上传文件统一入口管理
 */

public class SvUploadFileBiz {

    /**按照固定大小分片*/
    public static final int TYPE_PART_SIZE = 0;
    /**按照固定片数分片*/
    public static final int TYPE_PART_NUM = 1;

    public static final int PART_MUM = 5;
    public static final int PART_SIZE = 1024*1024;

    private ExecutorService executorService = Executors.newCachedThreadPool();
    private static SvUploadFileBiz uploadFileBiz;
    public static SvUploadFileBiz getInstance() {
        if (uploadFileBiz == null) {
            synchronized (SvUploadFileBiz.class) {
                if (uploadFileBiz == null)
                    uploadFileBiz = new SvUploadFileBiz();
            }
        }
        return uploadFileBiz;
    }


    /**根据文件路径添加新任务
     *@param typePart 分片方式 0 按照固定大小分片，1 按照固定片数分片
     *@param filePath 添加任务的文件路径
     *@param uploadListener 文件加载进度监听
     * */
    public void addLoadTask(final int typePart, final String filePath, final SvUploadManager.FileUploadListener uploadListener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                File loadFile = new File(filePath);
                if(loadFile.exists()){
                    String loadFileMd5 = SvFileUtils.getMd5ByFile(loadFile);
                    FileInfo fileInfo = SvFileUtils.getFileInfoFormFileByMd5(loadFileMd5);
                    if(fileInfo == null){
                        fileInfo = new FileInfo();
                        fileInfo.Md5 = loadFileMd5;
                        fileInfo.filePath = filePath;
                        switch (typePart){
                            case TYPE_PART_SIZE://按照大小分片
                                fileInfo.filePartInfos = SvFileUtils.splitPartFileBySize(loadFile, PART_SIZE);
                                break;
                            case TYPE_PART_NUM://按照片数分片
                                fileInfo.filePartInfos = SvFileUtils.splitPartFileByNum(loadFile, PART_MUM);
                                break;
                        }
                        SvFileUtils.saveFileInfoByJsonToFile(fileInfo);
                    }
                    SvUploadManager uploadManager = new SvUploadManager(fileInfo, uploadListener, executorService);
                    uploadManager.upLoadFile();
                }
            }
        }).start();
    }

    /**根据存储文件继续任务
     *@param fileInfo 存储文件信息
     * */
    public void addLoadTaskAsFileInfo(FileInfo fileInfo, SvUploadManager.FileUploadListener uploadListener){

        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }
}
