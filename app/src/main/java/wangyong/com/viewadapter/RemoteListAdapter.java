package wangyong.com.viewadapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import wangyong.com.model.Mp3Info;
import wangyong.com.mp3player.PlayerActivity;
import wangyong.com.mp3player.R;
import wangyong.com.service.DownloadService;

/**
 * Created by ASUS on 2017/4/30.
 */

public class RemoteListAdapter extends RecyclerView.Adapter<RemoteListAdapter.ViewHolder> {
    Context context;

    private List<Mp3Info> mp3InfoList;
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mp3Name;
        TextView mp3Size;
        public ViewHolder(View view) {
            super(view);
            //得到外层布局实例
            mp3Name = (TextView) view.findViewById(R.id.mp3_name);
            mp3Size = (TextView) view.findViewById(R.id.mp3_size);
        }
    }

    public RemoteListAdapter(List<Mp3Info> mp3InfoList , Context context) {
        this.mp3InfoList = mp3InfoList;
    }


    @Override
    public RemoteListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //加载布局
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mp3info_item,parent,false);
        //把加进来的布局传到构造函数当中
        final ViewHolder holder = new ViewHolder(view);
        //添加监听事件
        holder.mp3Name.setOnClickListener( new View.OnClickListener(){
            //分别对最外层的布局实例 和 ImageView注册了点击事件
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Mp3Info mp3Info = mp3InfoList.get(position);
                //根据用户点击的对象，下载对应的文件
                Intent intent = new Intent();
                intent.putExtra("mp3Info", mp3Info);
                intent.setClass(view.getContext(), DownloadService.class);
                v.getContext().startService(intent);
                Toast.makeText(v.getContext(),"这首歌的歌名为："
                        + mp3Info.getMp3Name(),Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent();

                intent1.putExtra("mp3Info",mp3Info);
                intent1.setClass(view.getContext(), PlayerActivity.class);
                v.getContext().startActivity(intent1);
            }
        });
        holder.mp3Size.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Mp3Info mp3Info = mp3InfoList.get(position);
                Toast.makeText(v.getContext(),"这首歌的作者是："
                        + mp3Info.getAuthor()+" bytes",Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(RemoteListAdapter.ViewHolder holder, int position) {

        Mp3Info mp3Info = mp3InfoList.get(position);
        //设置资源数据
        holder.mp3Name.setText(mp3Info.getMp3Name());
        holder.mp3Size.setText(mp3Info.getAuthor());
    }

    @Override
    public int getItemCount() {
        return mp3InfoList.size();
    }
}
