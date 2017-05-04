package wangyong.com.download;

/**
 * Created by ASUS on 2017/4/30.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import wangyong.com.util.FileUtils;

/**
 * 根据URL下载文件，前提是这个文件当中的内容是文本，函数返回文件内的文本
 * 1.创建一个URL对象
 * 2.通过URL对象，创建一个HTTPURLConnection对象
 * 3.得到inputStream
 * 4.从inputStream当中读取数据
 * 5.得到resource.xml文本 再进行xml解析
 */
public class HttpDownloader {
    private  URL url = null;

    public String download(String urlStr) {


        StringBuilder sb = new StringBuilder();
        String line = null;
        BufferedReader buffer = null;

        try

        {
            //创建一个URL对象
             url = new URL(urlStr);
            //创建一个HTTP链接
            HttpURLConnection urlConnection = (HttpURLConnection) url.
                    openConnection();
            //使用IO流读取数据
            buffer = new BufferedReader(
                    new InputStreamReader(
                            urlConnection.getInputStream()));
            while ((line = buffer.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {

            e.printStackTrace();
        }finally {
            try {
                buffer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 返回-1：代表文件下载出错 0：代码文件下载成功 1：代表文件已经存在
     * 参数：下载地址  保存路径  文件名
     */
    public int downFile(String urlStr, String path, String fileName) {
        InputStream inputStream = null;
        try {
            FileUtils fileUtils = new FileUtils();
            if (fileUtils.isFileExist(fileName,path)) {
                return 1;
            } else {
                inputStream = getInputStreamFromUrl(urlStr);
                File resultFile = fileUtils.write2SDFromInput(path,fileName, inputStream);
                if (resultFile == null) {
                    return -1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public InputStream getInputStreamFromUrl(String urlStr)
            throws MalformedURLException, IOException {
        url = new URL(urlStr);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        InputStream inputStream = urlConn.getInputStream();
        return inputStream;
    }
}
