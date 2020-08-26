package com.example.notificationtest.upload;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lining on 2017/6/8.
 */

public class SvFileUtils {

    public static final String uploadFileDir = getAppFilePath()+"/fission/"+"short_video/";
    /**
     * 拆分文件
     *
     * @param parentFile 待拆分的完整文件名
     * @param byteSize   按多少字节大小拆分
     * @return 拆分后的子文件
     * @throws IOException
     */
    public static List<FilePartInfo> splitPartFileBySize(File parentFile, long byteSize) {
        String md5 = getMd5ByFile(parentFile);
        List<FilePartInfo> partFiles = new ArrayList<>();
        try {
            int count = (int) Math.ceil(parentFile.length() / (double) byteSize);
            for (int i = 0; i < count; i++) {
                int startPos = (int) (i * byteSize);
                int endPos = (int) ((i + 1) * byteSize - 1);
                if ((i + 1) == count) {
                    endPos = (int) (parentFile.length() - 1);
                }
                FilePartInfo filePartInfo = new FilePartInfo();
                filePartInfo.parentFilePath = parentFile.getAbsolutePath();
                filePartInfo.startPos = startPos;
                filePartInfo.endPos = endPos;
                filePartInfo.parentMd5 = md5;
                filePartInfo.totalSize = byteSize;
                partFiles.add(filePartInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return partFiles;
    }

    /**
     * 拆分文件
     *
     * @param parentFile 待拆分的完整文件名
     * @param partNum    分片数拆分文件
     * @return 拆分后的子文件
     * @throws IOException
     */
    public static List<FilePartInfo> splitPartFileByNum(File parentFile, int partNum) {
        long partSize = parentFile.length() / partNum;
        return splitPartFileBySize(parentFile, partSize);
    }

    /**
     * 输入流转字符串
     */
    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 创建单个文件
     */
    public static boolean createFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {// 判断文件是否存在
            System.out.println("目标文件已存在" + filePath);
            return false;
        }
        if (filePath.endsWith(File.separator)) {// 判断文件是否为目录
            System.out.println("目标文件不能为目录！");
            return false;
        }
        if (!file.getParentFile().exists()) {// 判断目标文件所在的目录是否存在
            // 如果目标文件所在的文件夹不存在，则创建父文件夹
            System.out.println("目标文件所在目录不存在，准备创建它！");
            if (!file.getParentFile().mkdirs()) {// 判断创建目录是否成功
                System.out.println("创建目标文件所在的目录失败！");
                return false;
            }
        }
        try {
            if (file.createNewFile()) {// 创建目标文件
                System.out.println("创建文件成功:" + filePath);
                return true;
            } else {
                System.out.println("创建文件失败！");
                return false;
            }
        } catch (IOException e) {// 捕获异常
            e.printStackTrace();
            System.out.println("创建文件失败！" + e.getMessage());
            return false;
        }
    }

    /**
     * 创建文件目录
     */
    public static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            System.out.println("创建目录" + destDirName + "失败，目标目录已经存在");
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        //创建目录
        if (dir.mkdirs()) {
            System.out.println("创建目录" + destDirName + "成功！");
            return true;
        } else {
            System.out.println("创建目录" + destDirName + "失败！");
            return false;
        }
    }

    /**
     * 获取内设sd卡路径
     */
    public static String getAppFilePath() {
        File nameFile = null;
        try {
            boolean sdCardExist = Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
            if (sdCardExist) {
                nameFile = Environment.getExternalStorageDirectory();// 获取跟目录
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nameFile.getAbsolutePath();
    }

    /**
     * 获取文件MD5
     */
    public static String getMd5ByFile(File file) {
        String value = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    /**
     * 获取外设sd卡路径
     */
    public static String getAppSdPath() {
        File nameFile = null;
        try {
            nameFile = new File(System.getenv("SECONDARY_STORAGE")).getAbsoluteFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nameFile.getAbsolutePath();
    }

    /**
     * 复制asset文件到指定目录
     *
     * @param context Context 使用CopyFiles类的Activity
     * @param oldPath String  asset下的路径
     * @param newPath String  SD卡下保存路径
     */
    public static void copyFilesFassets(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);//获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {//如果是目录
                File file = new File(newPath);
                file.mkdirs();//如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyFilesFassets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {//如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                    fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                }
                fos.flush();//刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件信息已json格式保存到本地文件
     */
    public static void saveFileInfoByJsonToFile(FileInfo fileInfo) {
//        String fileStr = new Gson().toJson(fileInfo).toString();
        String fileStr = JSON.toJSONString(fileInfo).toString();
        try {
            createDir(uploadFileDir);
            // 创建文件对象
            File fileText = new File(uploadFileDir+fileInfo.Md5);
            // 向文件写入对象写入信息
            FileWriter fileWriter = new FileWriter(fileText);
            // 写文件
            fileWriter.write(fileStr);
            // 关闭
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**根据文件的md5查找对应的FileInfo*/
    public static FileInfo getFileInfoFormFileByMd5(String Md5){
        FileInfo fileInfo = null;
        File fileDir = new File(uploadFileDir);
        File fileInfoFile = null;
        if(fileDir.exists()){
            File[] files = fileDir.listFiles();
            for (File file : files) {
                if(TextUtils.equals(file.getName(), Md5)){
                    fileInfoFile = file;
                    break;
                }
            }
            if(fileInfoFile != null){
                try {
                    FileInputStream in=new FileInputStream(fileInfoFile);
                    // size  为字串的长度 ，这里一次性读完
                    int size=in.available();
                    byte[] buffer=new byte[size];
                    in.read(buffer);
                    in.close();
                    String str =new String(buffer,"UTF-8");
                    fileInfo = JSON.parseObject(str, FileInfo.class);
//                fileInfo = new Gson().fromJson(str, FileInfo.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fileInfo;
    }

    /**根据Md5删除对应文件*/
    public static void deleteFileInfoFileByMd5(String Md5){
        File fileDir = new File(uploadFileDir);
        if(fileDir.exists()){
            File[] files = fileDir.listFiles();
            for (File file : files) {
                if(TextUtils.equals(file.getName(), Md5)){
                    file.delete();
                    break;
                }
            }
        }
    }

    /**
     * @param  assetPath "/assets/文件名"
     * */
    public static String getAccetPath(Activity activity, String assetPath){
        InputStream abpath = activity.getClass().getResourceAsStream(assetPath);
        String path = new String(InputStreamToByte(abpath));
        return path;
    }
    private static byte[] InputStreamToByte(InputStream is){
        try {
            ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
            int ch;
            while ((ch = is.read()) != -1) {
                bytestream.write(ch);
            }
            byte imgdata[] = bytestream.toByteArray();
            bytestream.close();
            return imgdata;
        }catch (Exception e){

        }
        return null;
    }
}
