package com.example.adm.bmob.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adm.bmob.R;

import java.util.List;

import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;

/**
 * Created by ADM on 2015/10/22.
 */
public class ConversationAdapter extends BaseAdapter {

    private List<BmobRecent> recentList;
    private Context mContext;
    private LayoutInflater mInflater;
    public ConversationAdapter(Context context,List<BmobRecent> recentList){
        this.recentList = recentList;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        Log.d("ConversationAdapter","recentList size:"+recentList.size());
    }
    public void remove(int position){
        recentList.remove(position);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return this.recentList.size();
    }

    @Override
    public BmobRecent getItem(int position) {
        return recentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BmobRecent recent = recentList.get(position);
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.adapter_conversation_items,null);
        }
        ImageView iv_avatar = (ImageView)convertView.findViewById(R.id.iv_avatar);
        TextView tv_name = (TextView)convertView.findViewById(R.id.tv_name);
        TextView tv_content = (TextView)convertView.findViewById(R.id.tv_content);
        TextView tv_time = (TextView)convertView.findViewById(R.id.tv_time);
        TextView tv_unread = (TextView)convertView.findViewById(R.id.tv_unread);

        tv_name.setText(recent.getUserName());
        if(recent.getType() == BmobConfig.TYPE_TEXT)
             tv_content.setText(recent.getMessage());
        int num = BmobDB.create(mContext).getUnreadCount(recent.getTargetid());
        if (num > 0) {
            tv_unread.setVisibility(View.VISIBLE);
            tv_unread.setText(num + "");
        } else {
            tv_unread.setVisibility(View.GONE);
        }
        return convertView;
    }
}
