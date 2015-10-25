package com.example.adm.bmob.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adm.bmob.R;
import com.example.adm.bmob.bean.User;

import java.util.List;

/**
 * Created by SWan on 2015/10/14.
 */
public class FriendsAdapter extends BaseAdapter {
    protected  Context mContext;
    protected  int layout;
    protected  List<User> datalist;
    public LayoutInflater mInflater;
    public FriendsAdapter(Context context,int layoutResources,List<User> list){
        this.mContext = context;
        this.layout = layoutResources;
        this.datalist = list;
        this.mInflater = LayoutInflater.from(context);
    }
    public void remove(User user){
        this.datalist.remove(user);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public User getItem(int i) {
        return datalist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = mInflater.inflate(this.layout, null);
        }
        User user = datalist.get(i);
        ImageView avatar = (ImageView)convertView.findViewById(R.id.tv_friend_avatar);
        TextView name = (TextView)convertView.findViewById(R.id.tv_friend_name);
        System.out.println("朋友名为:"+user.getUsername());
        name.setText(user.getUsername());
        return convertView;
    }
}
