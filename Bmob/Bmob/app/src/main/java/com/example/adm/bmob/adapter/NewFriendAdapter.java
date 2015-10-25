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
import com.example.adm.bmob.demo.CustomApplication;
import com.example.adm.bmob.util.CollectionUtils;

import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by ADM on 2015/10/14.
 */
public class NewFriendAdapter extends BaseAdapter {
    public LayoutInflater mInflater;
    protected  Integer layoutResources;
    protected java.util.List<BmobInvitation> List;
    protected Context mContext;
    public NewFriendAdapter(Context context,Integer layoutResources,List<BmobInvitation> list){
        this.layoutResources = layoutResources;
        this.List = list;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }
    public void remove(int position){
        this.List.remove(position);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return List.size();
    }

    @Override
    public BmobInvitation getItem(int position) {
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
        final BmobInvitation msg = List.get(position);
        TextView name = (TextView)convertView.findViewById(R.id.tv_name);
        final Button btn = (Button)convertView.findViewById(R.id.btn_add);
        ImageView iv_avatar = (ImageView)convertView.findViewById(R.id.avatar);
        String avatar = msg.getAvatar();
        if(avatar!=null && !avatar.equals("")){

        }else{
            iv_avatar.setImageResource(R.mipmap.default_head);
        }
        name.setText(msg.getFromname());
        int status = msg.getStatus();
        System.out.println("status:"+status);
        if(status==BmobConfig.INVITE_ADD_NO_VALIDATION||status==BmobConfig.INVITE_ADD_NO_VALI_RECEIVED){
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BmobLog.i("点击同意按钮:" + msg.getFromid());
                    agressAdd(btn, msg);
                }
            });
        }else if(status==BmobConfig.INVITE_ADD_AGREE){
            btn.setText("已添加");
            btn.setBackgroundDrawable(null);
            btn.setTextColor(mContext.getResources().getColor(R.color.base_color_text_black));
            btn.setTextSize(15);
            btn.setEnabled(false);
            btn.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
    private void agressAdd(final Button btn,final BmobInvitation msg){
        final ProgressDialog progress = new ProgressDialog(mContext);
        progress.setMessage("正在添加...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        try {
            //同意添加好友
            BmobUserManager.getInstance(mContext).agreeAddContact(msg, new UpdateListener() {

                @Override
                public void onSuccess() {
                    // TODO Auto-generated method stub
                    progress.dismiss();
                    btn.setText("已同意");
                    btn.setBackgroundDrawable(null);
                   // btn.setTextColor(mContext.getResources().getColor(R.color.base_color_text_black));
                    btn.setEnabled(false);
                    //保存到application中方便比较
                    CustomApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(mContext).getContactList()));
                }

                @Override
                public void onFailure(int arg0, final String arg1) {
                    // TODO Auto-generated method stub
                    progress.dismiss();
                    Toast.makeText(mContext,"添加失败"+arg1,Toast.LENGTH_SHORT).show();
                }
            });
        } catch (final Exception e) {
            progress.dismiss();
            Toast.makeText(mContext,"添加失败"+ e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}
