package wangyong.com.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.icu.text.LocaleDisplayNames;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.net.URL;

import wangyong.com.download.HttpDownloader;
import wangyong.com.model.Mp3Info;
import wangyong.com.mp3player.AppConstant;
import wangyong.com.util.FileUtils;

/**
 * 用service 下载MP3文件
 */
public class DownloadService extends Service {
    int mp3Result;
    public DownloadService() {

    }

    /**
     *每次用户点击ListActivity中的一个目标，就会调用该方法
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //从itent对象当中将Mp3Ifo对象取出
        Mp3Info mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");
        //生成一个下载线程，并将Mp3Info对象传入
        FileUtils fileUtils = new FileUtils();
        boolean fileExist =  fileUtils.isFileExist(mp3Info.getMp3Name(),"/mp3");
        Log.d("FlieExist",fileExist+"");
        if (fileExist){
            return super.onStartCommand(intent, flags, startId);
        }

        Downloadmp3 downloadThread = new Downloadmp3(mp3Info);
        //启动一个新线程
        Thread thread1 = new Thread(downloadThread);
        thread1.start();


        Downloadlrc downloadlrc = new Downloadlrc(mp3Info);
        Thread thread2 = new Thread(downloadlrc);
        thread2.start();

        if (mp3Result == 0) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder notification = new NotificationCompat.Builder(DownloadService.this);
            notification.setSmallIcon(android.support.v7.appcompat.R.drawable.notification_template_icon_bg);
            notification.setContentTitle("下载提示！！！！");
            notification.setContentText("您下载的歌曲" + mp3Info.getMp3Name() + "已经下载成功");
            notification.setAutoCancel(false);

            manager.notify(1, notification.build());
        }

        return super.onStartCommand(intent, flags, startId);

    }

    class Downloadmp3 implements Runnable{
        private Mp3Info mp3Info;
        public  Downloadmp3(Mp3Info mp3Info){
            this.mp3Info = mp3Info;
        }

        @Override
        public void run() {

            //String mp3Url = "http://112.74.76.91:8080/javawebshop/mp3/
            String mp3Url = AppConstant.URL.BASE_URL + mp3Info.getMp3Name();

            //生成下载文件所用的对象
            HttpDownloader httpDownloader = new HttpDownloader();
            //将文件下载下来并存到SD卡当中
             mp3Result = httpDownloader.downFile(mp3Url, "/mp3", mp3Info.getMp3Name());
        }
    }
    class Downloadlrc implements Runnable{

        private Mp3Info mp3Info;
        public  Downloadlrc(Mp3Info mp3Info){
            this.mp3Info = mp3Info;
        }

        @Override
        public void run() {

            String lrcUrl = AppConstant.URL.BASE_URL + mp3Info.getLrcName();
            //生成下载文件所用的对象
            HttpDownloader httpDownloader = new HttpDownloader();
            //将文件下载下来并存到SD卡当中
            int lrcResult = httpDownloader.downFile(lrcUrl, "/mp3", mp3Info.getLrcName());
        }
    }




    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
