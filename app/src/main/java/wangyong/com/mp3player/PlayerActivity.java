package wangyong.com.mp3player;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Queue;

import wangyong.com.model.Mp3Info;
import wangyong.com.service.Mp3playerService;
import wangyong.com.util.LrcProcessor;



public class PlayerActivity extends AppCompatActivity {
    ImageButton beginButton = null;
    ImageButton pauseButton = null;
    ImageButton stopButton = null;
    MediaPlayer mediaPlayer = null;

    private boolean isPause = false;
    private boolean isReleased = false;

    private ArrayList<Queue> queues = null;
    private TextView lrcTextView = null;
    private Handler handler = new Handler();
    private UpdateTimeCallback updateTimeCallback = null;
    private long begin = 0;
    private long nextTimeMill = 0;
    private long currentTimeMill = 0;
    private String message = null;
    private long pauseTimeMills = 0;
    private boolean isPlaying = false;

    private Mp3Info mp3Info = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Intent intent = getIntent();
         mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");
        TextView textView = (TextView)findViewById(R.id.mp3name);
        textView.setText(mp3Info.getMp3Name());
        beginButton = (ImageButton)findViewById(R.id.begin);
        pauseButton = (ImageButton)findViewById(R.id.pause);
        stopButton = (ImageButton)findViewById(R.id.stop);

        beginButton.setOnClickListener(new BeginButtonListener());
        pauseButton.setOnClickListener(new PauseButtonListener());
        stopButton.setOnClickListener(new StopButtonListener());

        lrcTextView = (TextView)findViewById(R.id.lrcText);

    }

    private void prepareLrc(String lrcName){
        try {
            InputStream inputStream = new FileInputStream(Environment.getExternalStorageDirectory().getAbsoluteFile() +File.separator + "mp3/" + mp3Info.getLrcName());
            LrcProcessor lrcProcessor = new LrcProcessor();
            //把这个流交给lrcProcessor去处理，得到arrayList
            queues = lrcProcessor.process(inputStream);
            //滚动核心代码  UpdateTimeCallback
            updateTimeCallback = new UpdateTimeCallback(queues);
            begin = 0 ;
            currentTimeMill = 0 ;
            nextTimeMill = 0 ;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    class BeginButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
                 //创建一个Intent对象，用于同时service开始播放MP3
                Intent intent = new Intent();
                intent.setClass(PlayerActivity.this, Mp3playerService.class);
                intent.putExtra("mp3Info", mp3Info);
                intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
                //先读取LRC文件
                prepareLrc(mp3Info.getLrcName());
                //再启动service
                startService(intent);
                begin = System.currentTimeMillis();
                //延后5毫秒
                handler.postDelayed(updateTimeCallback, 5);
                isPlaying = true;

        }


    }

    class PauseButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            Intent intent = new Intent();
            intent.setClass(PlayerActivity.this, Mp3playerService.class);
            intent.putExtra("MSG", AppConstant.PlayerMsg.PAUSE_MSG);
            startService(intent);

            if(isPlaying){
                handler.removeCallbacks(updateTimeCallback);
                //记录当前暂停的时间
                pauseTimeMills = System.currentTimeMillis();
            }
            else{
                handler.postDelayed(updateTimeCallback, 5);
                //当前时间 减去 暂停时间 得到停了多少秒
                begin = System.currentTimeMillis() - pauseTimeMills + begin;
            }
            isPlaying = isPlaying ? false : true;
        }


    }

    class StopButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //通知service停止播放MP3文件
            Intent intent = new Intent();
            intent.setClass(PlayerActivity.this,  Mp3playerService.class);
            intent.putExtra("MSG", AppConstant.PlayerMsg.STOP_MSG);
            startService(intent);
            //从handler当中移除updateTimeCallback
            handler.removeCallbacks(updateTimeCallback);
        }


    }
    private String getMp3Path(Mp3Info mp3Info){
        String SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        String path = SDCardRoot + File.separator + "mp3" + File.separator + mp3Info.getMp3Name();
        return path;
    }

    class UpdateTimeCallback implements Runnable{
        Queue times = null;
        Queue messages = null;
        public UpdateTimeCallback(ArrayList<Queue> queues) {

            times = queues.get(0);
            messages = queues.get(1);
        }

        @Override
        public void run() {
            //偏移量：从开始播放MP3到现在消耗了多少时间，以毫秒为单位
            long offset = System.currentTimeMillis() - begin;
            if(currentTimeMill == 0){ //第一次执行
                //下一次更新歌词的时间点
                nextTimeMill = (Long)times.poll();
                message = (String)messages.poll();
            }
            //现在的时间超过了更新的时间
            if(offset >= nextTimeMill){
                //更新
                lrcTextView.setText(message);
                //将下一次需要更新的内容 取出来
                message = (String)messages.poll();
                nextTimeMill = (Long)times.poll();
            }
            currentTimeMill = currentTimeMill + 10;
            //每十毫秒更新一次
            handler.postDelayed(updateTimeCallback, 10);
        }

    }
}
