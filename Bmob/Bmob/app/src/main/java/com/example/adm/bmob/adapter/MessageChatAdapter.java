package com.example.adm.bmob.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.adm.bmob.R;

import java.util.List;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;

/**
 * Created by ADM on 2015/10/21.
 */
public class MessageChatAdapter extends BaseAdapter {
    //8种Item的类型
    //文本
    private final int TYPE_RECEIVER_TXT = 0;
    private final int TYPE_SEND_TXT = 1;
    //图片
    private final int TYPE_SEND_IMAGE = 2;
    private final int TYPE_RECEIVER_IMAGE = 3;
    //位置
    private final int TYPE_SEND_LOCATION = 4;
    private final int TYPE_RECEIVER_LOCATION = 5;
    //语音
    private final int TYPE_SEND_VOICE =6;
    private final int TYPE_RECEIVER_VOICE = 7;
    private Context context;
    private List<BmobMsg> msgList;
    private LayoutInflater mInflater;
    String currentObjectId = "";
    public MessageChatAdapter(Context context,List<BmobMsg> msgList){
        currentObjectId = BmobUserManager.getInstance(context).getCurrentUserObjectId();
        this.context = context;
        this.msgList = msgList;
        this.mInflater = LayoutInflater.from(context);
    }
    public List<BmobMsg> getList() {
        return msgList;
    }
    public void add(BmobMsg msg) {
        this.msgList.add(msg);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return this.msgList.size();
    }

    @Override
    public BmobMsg getItem(int position) {
        return this.msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public int getItemViewType(int position) {

        BmobMsg msg = msgList.get(position);
        if(msg.getMsgType()==BmobConfig.TYPE_IMAGE){
            return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_IMAGE: TYPE_RECEIVER_IMAGE;
        }else if(msg.getMsgType()==BmobConfig.TYPE_LOCATION){
            return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_LOCATION: TYPE_RECEIVER_LOCATION;
        }else if(msg.getMsgType()==BmobConfig.TYPE_VOICE){
            return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_VOICE: TYPE_RECEIVER_VOICE;
        }else{
            return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_TXT: TYPE_RECEIVER_TXT;
        }
    }
    private View createViewByType(BmobMsg message, int position){
        int type = message.getMsgType();
        if(type == BmobConfig.TYPE_IMAGE){
                Log.d("createViewByType","TYPE_IMAGE");
                return null;
        }else{
            Log.d("createViewByType","TYPE"+getItemViewType(position));
            return getItemViewType(position) == TYPE_RECEIVER_TXT ?
                    mInflater.inflate(R.layout.item_chat_received_message, null)
                    :
                    mInflater.inflate(R.layout.item_chat_send_message, null);
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BmobMsg msg = this.msgList.get(position);
        Log.d("getView","position"+position);
      //  if(convertView == null){
            convertView = createViewByType(msg,position);
      //  }
        ImageView iv_avatar = (ImageView)convertView.findViewById(R.id.iv_avatar);
        TextView tv_message = (TextView)convertView.findViewById(R.id.tv_message);
        final TextView tv_send_status = (TextView)convertView.findViewById(R.id.tv_send_status);
        TextView tv_time = (TextView)convertView.findViewById(R.id.tv_time);
        final  ImageView iv_fail_resend = (ImageView)convertView.findViewById(R.id.iv_fail_resend);
        final ProgressBar progress = (ProgressBar)convertView.findViewById(R.id.progress_load);
        if(getItemViewType(position) == TYPE_SEND_TXT
                || getItemViewType(position) == TYPE_SEND_LOCATION
                || getItemViewType(position) == TYPE_SEND_VOICE){
            if(msg.getStatus() == BmobConfig.STATUS_SEND_SUCCESS){
                 progress.setVisibility(View.INVISIBLE);
                iv_fail_resend.setVisibility(View.INVISIBLE);
                tv_send_status.setVisibility(View.VISIBLE);
                tv_send_status.setText("已发送");
            }else if(msg.getStatus()==BmobConfig.STATUS_SEND_FAIL){//服务器无响应或者查询失败等原因造成的发送失败，均需要重发
                progress.setVisibility(View.INVISIBLE);
                iv_fail_resend.setVisibility(View.VISIBLE);
                tv_send_status.setVisibility(View.INVISIBLE);
            }else if(msg.getStatus()==BmobConfig.STATUS_SEND_RECEIVERED){//对方已接收到
                progress.setVisibility(View.INVISIBLE);
                iv_fail_resend.setVisibility(View.INVISIBLE);
                if(msg.getMsgType()==BmobConfig.TYPE_VOICE){
                    tv_send_status.setVisibility(View.GONE);
                 //   tv_voice_length.setVisibility(View.VISIBLE);
                }else{
                    tv_send_status.setVisibility(View.VISIBLE);
                    tv_send_status.setText("已阅读");
                }
            }
        }
        //根据类型显示内容
        final String text = msg.getContent();
        switch (msg.getMsgType()) {
            case BmobConfig.TYPE_TEXT:
                try {
                        tv_message.setText(text.toString());
                     } catch (Exception e) {
                     }
                 break;
              }
            return convertView;
        }
}
