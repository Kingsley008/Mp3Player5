package wangyong.com.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import wangyong.com.model.Mp3Info;

/**
 * Created by ASUS on 2017/5/2.
 */

public class FileUtils {

    private String SDCardRoot;

    public FileUtils() {
        //得到当前外部存储设备的目录
        SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
    }
    /**
     * 在SD卡上创建文件
     *
     */
    public File createFileInSDCard(String fileName, String dir) throws IOException{

        File file = new File(SDCardRoot + dir + File.separator +fileName);
        System.out.println("file---->" + file);
        file.createNewFile();
        return file;
    }

    /**ls
     *
     * 在SD卡上创建目录
     * @param dir
     * @return
     */
    public File createSDDir(String dir) {
        File dirFile = new File(SDCardRoot + dir + File.separator);
        System.out.println(dirFile.mkdir());
        return dirFile;
    }

    /**
     * 判断SD卡上的文件是否存在
     */
    public boolean isFileExist(String fileName, String path) {
        File file = new File(SDCardRoot + path + File.separator + fileName);

        Log.d("fileExist",SDCardRoot + path + File.separator + fileName);

        return file.exists();
    }
    /**
     * 将一个InputStream写入到SD卡中
     */
    public File write2SDFromInput(String path, String fileName, InputStream input){

        File file = null;
        OutputStream outputStream = null;
        try {
            createSDDir(path);
            file = createFileInSDCard(fileName,path);
            outputStream = new FileOutputStream(file);
            byte buffer [] = new byte[4*1024];
            int temp;
            while((temp = input.read(buffer))!= -1){
                outputStream.write(buffer,0,temp);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    /**
     * 读取目录中的Mp3文件的名字和大小
     */
    public List<Mp3Info> getMp3Files(String path) {
        List<Mp3Info> mp3InfoList = new ArrayList<>();
        File file = new File(SDCardRoot + File.separator + path);
        File [] files = file.listFiles();
        Log.d("list",files.toString());
        for ( int i = 0; i < files.length; i++) {
            if (files[i].getName().endsWith(".mp3")) {
                Mp3Info mp3Info = new Mp3Info();
                mp3Info.setMp3Name(files[i].getName());
                mp3Info.setMp3Size(files[i].length() + "");
                mp3InfoList.add(mp3Info);
            }
        }
        return mp3InfoList;
    }


}
