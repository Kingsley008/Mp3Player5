package wangyong.com.xml;


import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

import wangyong.com.model.Mp3Info;

/**
 * SAX解析XML文件内容
 */

public class Mp3ListContentHandler extends DefaultHandler {
    public Mp3ListContentHandler(List<Mp3Info> infos) {
        this.infos = infos;
    }

    private List<Mp3Info> infos = null;
    private Mp3Info mp3Info = null;
    private String tagName = null;

    public List<Mp3Info> getInfos() {
        return infos;
    }

    public void setInfos(List<Mp3Info> infos) {
        this.infos = infos;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //记录当前节点名
        this.tagName = localName;
        if (tagName.equals("resource")){
            mp3Info = new Mp3Info();
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);

        String temp = new String(ch,start,length);
        //根据当前节点名判断将内容添加到MP3info对象中去
        if (tagName.equals("id")){
            mp3Info.setId(temp);
        }
        else if (tagName.equals("mp3.name")){
            mp3Info.setMp3Name(temp);
        }
        else if (tagName.equals("mp3.size")){
            mp3Info.setMp3Size(temp);
        }
        else if (tagName.equals("lrc.name")){
            mp3Info.setLrcName(temp);
        }
        else if (tagName.equals("lrc.size")){
            mp3Info.setLrcSize(temp);
        }
        else if (tagName.equals("mp3.author")){
            mp3Info.setAuthor(temp);
        }
    }
    @Override
    public void endDocument() throws SAXException {

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        //如果读到resource标签，那么说明一个MP3info对象的设置已经完成
        if (qName.equals("resource")){
            //放到一个ArrayList中
            infos.add(mp3Info);
        }

        //将TageName清空
        tagName="";
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }



}
