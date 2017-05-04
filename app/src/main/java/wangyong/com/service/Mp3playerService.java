package wangyong.com.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;

import java.io.File;

import wangyong.com.model.Mp3Info;
import wangyong.com.mp3player.AppConstant;

public class Mp3playerService extends Service {
    public Mp3playerService() {
    }
    private boolean isPlaying = false;
    private boolean isPause = false;
    private boolean isReleased = false;
    private MediaPlayer mediaPlayer = null;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Mp3Info mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");
        int MSG = intent.getIntExtra("MSG", 0);
        if (mp3Info != null) {
            if(MSG == AppConstant.PlayerMsg.PLAY_MSG){
                play(mp3Info);
            }
        } else {
            if(MSG == AppConstant.PlayerMsg.PAUSE_MSG){
                pause();
            }
            else if(MSG == AppConstant.PlayerMsg.STOP_MSG){
                stop();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void play(Mp3Info mp3Info) {

        String path = getMp3Path(mp3Info);
        mediaPlayer = MediaPlayer.create(this, Uri.parse("file://" + path));
        mediaPlayer.setLooping(false);
        mediaPlayer.start();
        isPlaying = true;
        isReleased = false;

    }

    private void pause() {

        if (mediaPlayer != null) {
            if (!isReleased) {
                if (!isPause) {
                    mediaPlayer.pause();
                    isPause = true;
                    isPlaying = true;
                } else {
                    mediaPlayer.start();
                    isPause = false;
                }
            }
        }
    }


    private void stop() {
        if (mediaPlayer != null) {
            if (isPlaying) {
                  isPlaying = false;
                if (!isReleased) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    isReleased = true;
                }

            }
        }
    }

    private String getMp3Path(Mp3Info mp3Info) {
        String SDCardRoot = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        String path = SDCardRoot + File.separator + "mp3" + File.separator
                + mp3Info.getMp3Name();
        return path;
    }
}
