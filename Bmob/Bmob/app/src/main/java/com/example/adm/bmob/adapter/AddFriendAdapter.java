package com.example.adm.bmob.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adm.bmob.R;

import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.PushListener;

/**
 * Created by ADM on 2015/10/14.
 */
public class AddFriendAdapter extends BaseAdapter {
    public LayoutInflater mInflater;
    protected  Integer layoutResources;
    protected List<BmobChatUser> List;
    protected  Context mContext;
    public AddFriendAdapter(Context context,Integer layoutResources,List<BmobChatUser> list){
        this.layoutResources = layoutResources;
        this.List = list;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return List.size();
    }

    @Override
    public Object getItem(int position) {
        return List.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(this.layoutResources, null);
        }
        final BmobChatUser user = List.get(position);
        TextView name = (TextView)convertView.findViewById(R.id.tv_name);
        Button btn = (Button)convertView.findViewById(R.id.btn_add);
        ImageView iv_avatar = (ImageView)convertView.findViewById(R.id.avatar);
        String avatar = user.getAvatar();
        if(avatar!=null && !avatar.equals("")){

        }else{
            iv_avatar.setImageResource(R.mipmap.default_head);
        }
        name.setText(user.getUsername());
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog progress = new ProgressDialog(mContext);
                progress.setMessage("正在添加...");
                progress.setCanceledOnTouchOutside(false);
                progress.show();
                //发送tag请求
                BmobChatManager.getInstance(mContext).sendTagMessage(BmobConfig.TAG_ADD_CONTACT, user.getObjectId(),new PushListener() {
                    @Override
                    public void onSuccess() {
                        progress.dismiss();
                        Toast.makeText(mContext,"发送请求成功，等待对方验证!",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int arg0, final String arg1) {
                        progress.dismiss();
                        Toast.makeText(mContext, "发送请求失败,请重新添加!", Toast.LENGTH_SHORT).show();
                        Log.e("Add Friend","发送请求失败:"+arg1);
                    }
                });
            }
        });
        return convertView;
    }
}
