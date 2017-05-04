package wangyong.com.mp3player;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import wangyong.com.download.HttpDownloader;
import wangyong.com.model.Mp3Info;
import wangyong.com.util.FileUtils;
import wangyong.com.viewadapter.RemoteListAdapter;
import wangyong.com.xml.Mp3ListContentHandler;

public class Mp3ListActivity extends AppCompatActivity {
    List<Mp3Info> mp3InfoList;

    //执行权限回调函数
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    updateListView();
                }else {
                    Toast.makeText(this, "you denied the permission", Toast.LENGTH_LONG);

                }
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_list);
        Button local = (Button)findViewById(R.id.about);
        Button server  = (Button)findViewById(R.id.server);

        //查看是否拥有SD卡读写权限
        if (ContextCompat.checkSelfPermission(Mp3ListActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                !=PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(Mp3ListActivity.this, android.Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)
                !=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Mp3ListActivity.this,new String[]{android.Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,android.Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        }else{
            Log.d("permission","Ok");
            updateListView();
        }

        //About

        local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(v.getContext(),"MP3demo 安卓期末作业 作者：王勇 商务16A",Toast.LENGTH_SHORT).show();

            }
        });



        //点击音乐库
        server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(Mp3ListActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        !=PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(Mp3ListActivity.this, android.Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)
                        !=PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Mp3ListActivity.this,new String[]{android.Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,android.Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

                }else{
                    //如果已经有了权限，访问服务器，刷新歌单
                    Log.d("permission","Ok");
                    updateListView();
                }
            }
        });

    }

    /**
     * 下载得到服务器.xml文件内容
     * @param urlStr
     * @return
     */
    private String downloadXml (String urlStr) {
        HttpDownloader httpDownloader = new HttpDownloader();
        //本地服务器环境
        String result = httpDownloader.download(urlStr);
        return  result;
    }

    /**
     * 用 XMLReader解析XML文件
     * @param xmlStr
     * @return
     */
    private List<Mp3Info> parse(String xmlStr) {
        SAXParserFactory saxparserFactory = SAXParserFactory.newInstance();
        List<Mp3Info> infos =  new ArrayList<>();
        try {
            XMLReader xmlReader = saxparserFactory.newSAXParser().getXMLReader();

            Mp3ListContentHandler  handler = new Mp3ListContentHandler(infos);
            xmlReader.setContentHandler(handler);
            try {
                xmlReader.parse(new InputSource(new StringReader(xmlStr)));

                Log.d("infos",infos.toString());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return infos;
    }

    //设置布局
    private void setView (final List mp3InfoList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                StaggeredGridLayoutManager staggeredGridLayoutManager = new
                        StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(staggeredGridLayoutManager);
                //向adapter传入一个mp3InfoList设置adapter
                RemoteListAdapter adapter = new RemoteListAdapter(mp3InfoList,Mp3ListActivity.this);
                recyclerView.setAdapter(adapter);
            }
        });

    }

    //开启新的线程 读xml文件
    private void updateListView() {
        new Thread() {
            public synchronized void run() {
                String xml = downloadXml("http://112.74.76.91:8080/javawebshop/mp3/resources.xml");
                mp3InfoList = parse(xml);

            }
        }.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //设置布局
       setView(mp3InfoList);
    }


}
