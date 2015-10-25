package com.example.adm.bmob.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.bmob.utils.BmobLog;
import com.example.adm.bmob.R;
import com.example.adm.bmob.ui.MainActivity;
import com.example.adm.bmob.ui.NewFriendActivity;
import com.example.adm.bmob.util.CollectionUtils;
import com.example.adm.bmob.util.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.config.BmobConstant;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnReceiveListener;
import cn.bmob.im.util.BmobJsonUtil;
import cn.bmob.v3.listener.FindListener;

public class MyMessageReceiver extends BroadcastReceiver {
    // 事件监听
    public static ArrayList<EventListener> ehList = new ArrayList<EventListener>();

    public static final int NOTIFY_ID = 0x000;
    public static int mNewNum = 0;//
    BmobUserManager userManager;
    BmobChatUser currentUser;
    public MyMessageReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // TODO Auto-generated method stub
        String json = intent.getStringExtra("msg");
        BmobLog.i("收到的message = " + json);
        Toast.makeText(context,"MyMessageReceiver",Toast.LENGTH_SHORT).show();
        // an Intent broadcast.
        userManager = BmobUserManager.getInstance(context);
        currentUser = userManager.getCurrentUser();
        boolean isNetConnected = CommonUtils.isNetworkConnected(context);
        if(isNetConnected){
            parseMessage(context, json);
        }else{
            for (int i = 0; i < ehList.size(); i++)
                ((EventListener) ehList.get(i)).onNetChange(isNetConnected);
        }
    }
    /** 解析Json字符串
     * @Title: parseMessage
     * @Description: TODO
     * @param @param context
     * @param @param json
     * @return void
     * @throws
     */
    private void parseMessage(final Context context, String json) {
        JSONObject jo;
        try {
            jo = new JSONObject(json);
            String fromId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TARGETID);
            String tag = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TAG);
            final String toId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TOID);
            String msgTime = BmobJsonUtil.getString(jo,BmobConstant.PUSH_READED_MSGTIME);
            System.out.println("toId:"+toId);
            System.out.println("currentUser.getObjectId():"+currentUser.getObjectId());
          //  if(toId.equals(currentUser.getObjectId())){//检查目标ID是否是当前用户ID
            if(fromId!=null && !BmobDB.create(context,toId).isBlackUser(fromId)){//该消息发送方不为黑名单用户
                    if(TextUtils.isEmpty(tag)){//不携带tag标签--此可接收陌生人的消息
                       BmobChatManager.getInstance(context).createReceiveMsg(json, new OnReceiveListener() {

                            @Override
                            public void onSuccess(BmobMsg msg) {
                                // TODO Auto-generated method stub
                                if (ehList.size() > 0) {// 有监听的时候，传递下去
                                    for (int i = 0; i < ehList.size(); i++) {
                                        ((EventListener) ehList.get(i)).onMessage(msg);
                                    }
                                } else {
                                   // boolean isAllow = CustomApplication.getInstance().getSpUtil().isAllowPushNotify();
                                    if(true && currentUser!=null && currentUser.getObjectId().equals(toId)){//当前登陆用户存在并且也等于接收方id
                                        mNewNum++;
                                        showMsgNotify(context,msg);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(int code, String arg1) {
                                // TODO Auto-generated method stub
                                BmobLog.i("获取接收的消息失败："+arg1);
                            }
                        });

                    }
                else {
                    if(tag.equals(BmobConfig.TAG_ADD_CONTACT)){
                        System.out.println("TAG_ADD_CONTACT::"+tag);
                        //保存好友请求道本地，并更新后台的未读字段
                        BmobInvitation message = BmobChatManager.getInstance(context).saveReceiveInvite(json, toId);
                        if(currentUser!=null){//有登陆用户
                            System.out.println("currentUser::"+currentUser.getUsername());
                            if(toId.equals(currentUser.getObjectId())){
                                if (ehList.size() > 0) {// 有监听的时候，传递下去
                                    for (EventListener handler : ehList)
                                        handler.onAddUser(message);
                                }else{
                                    System.out.println("currentUser::"+currentUser.getUsername()+"::"+currentUser.getObjectId());
                                    BmobNotifyManager.getInstance(context).showNotify(true, true, R.drawable.welcome, message.getFromname() + "请求添加好友",
                                            message.getFromname(), message.getFromname() + "请求添加好友".toString(), NewFriendActivity.class);

                                }
                            }
                        }
                    }else if(tag.equals(BmobConfig.TAG_ADD_AGREE)){

                        if(currentUser!=null){//有登陆用户
                            System.out.println("TAG_ADD_AGREE::"+tag);
                            if(toId.equals(currentUser.getObjectId())){
                                    String username = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TARGETUSERNAME);
                                    //收到对方的同意请求之后，就得添加对方为好友--已默认添加同意方为好友，并保存到本地好友数据库
                                    BmobUserManager.getInstance(context).addContactAfterAgree(username, new FindListener<BmobChatUser>() {

                                        @Override
                                        public void onError(int arg0, final String arg1) {
                                            // TODO Auto-generated method stub

                                        }

                                        @Override
                                        public void onSuccess(List<BmobChatUser> arg0) {
                                            // TODO Auto-generated method stub
                                            //保存到内存中
                                            CustomApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(context).getContactList()));
                                        }
                                    });
                                    BmobNotifyManager.getInstance(context).showNotify(true, true, R.drawable.welcome, username + "同意添加您为好友",
                                            username, username + "同意添加您为好友".toString(), MainActivity.class);

                                    //创建一个临时验证会话--用于在会话界面形成初始会话
                                    BmobMsg.createAndSaveRecentAfterAgree(context, json);

                            }
                        }

                    }
                    else if(tag.equals(BmobConfig.TAG_READED)){//已读回执
                        String conversionId = BmobJsonUtil.getString(jo,BmobConstant.PUSH_READED_CONVERSIONID);
                        if(currentUser!=null){
                            //更改某条消息的状态
                            BmobChatManager.getInstance(context).updateMsgStatus(conversionId, msgTime);
                            if(toId.equals(currentUser.getObjectId())){
                                if (ehList.size() > 0) {// 有监听的时候，传递下去--便于修改界面
                                    for (EventListener handler : ehList)
                                        handler.onReaded(conversionId, msgTime);
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    /**
     *  显示与聊天消息的通知
     * @Title: showNotify
     * @return void
     * @throws
     */
    public void showMsgNotify(Context context,BmobMsg msg) {
        // 更新通知栏
        int icon = R.drawable.welcome;
        String trueMsg = "";
        if(msg.getMsgType()==BmobConfig.TYPE_TEXT && msg.getContent().contains("\\ue")){
            trueMsg = "[表情]";
        }else if(msg.getMsgType()==BmobConfig.TYPE_IMAGE){
            trueMsg = "[图片]";
        }else if(msg.getMsgType()==BmobConfig.TYPE_VOICE){
            trueMsg = "[语音]";
        }else if(msg.getMsgType()==BmobConfig.TYPE_LOCATION){
            trueMsg = "[位置]";
        }else{
            trueMsg = msg.getContent();
        }
        CharSequence tickerText = msg.getBelongUsername() + ":" + trueMsg;
        String contentTitle = msg.getBelongUsername()+ " (" + mNewNum + "条新消息)";

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

     //   boolean isAllowVoice = CustomApplication.getInstance().getSpUtil().isAllowVoice();
    //    boolean isAllowVibrate = CustomApplication.getInstance().getSpUtil().isAllowVibrate();

        BmobNotifyManager.getInstance(context).showNotifyWithExtras(true,true,icon, tickerText.toString(), contentTitle, tickerText.toString(),intent);
    }
}
