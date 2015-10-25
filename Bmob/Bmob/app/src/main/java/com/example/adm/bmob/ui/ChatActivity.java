package com.example.adm.bmob.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.adm.bmob.R;
import com.example.adm.bmob.adapter.MessageChatAdapter;
import com.example.adm.bmob.demo.MyMessageReceiver;
import com.example.adm.bmob.util.CommonUtils;

import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;

public class ChatActivity extends BaseActivity implements View.OnClickListener,EventListener {
    BmobChatUser targetUser;
    String targetId;
    ListView listView;
    EditText et_user_commit;
    private static int MsgPagerNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        targetUser =(BmobChatUser)getIntent().getSerializableExtra("user");
        targetId = targetUser.getObjectId();

        //注册广播接收器
        initNewMessageBroadCast();
        initView();
    }
    private void initBottomView(){
        final Button btn_chat_add = (Button)findViewById(R.id.btn_chat_add);
        final Button btn_chat_emo = (Button)findViewById(R.id.btn_chat_emo);
        final Button btn_chat_send = (Button)findViewById(R.id.btn_chat_send);
        final Button btn_chat_voice = (Button)findViewById(R.id.btn_chat_voice);
        btn_chat_send.setOnClickListener(this);
        et_user_commit = (EditText)findViewById(R.id.edit_user_commit);
        et_user_commit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    btn_chat_voice.setVisibility(View.GONE);
                    btn_chat_send.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    /**
     * 加载消息历史，从数据库中读出
     */
    private List<BmobMsg> initMsgData() {
        List<BmobMsg> list = BmobDB.create(this).queryMessages(targetId,MsgPagerNum);
        return list;
    }
    MessageChatAdapter mAdapter;
    private void refreshOrInit(){
        if(mAdapter != null){
            if (MyMessageReceiver.mNewNum != 0) {// 用于更新当在聊天界面锁屏期间来了消息，这时再回到聊天页面的时候需要显示新来的消息
                int news=  MyMessageReceiver.mNewNum;//有可能锁屏期间，来了N条消息,因此需要倒叙显示在界面上
                int size = initMsgData().size();
                for(int i=(news-1);i>=0;i--){
                    mAdapter.add(initMsgData().get(size-(i+1)));// 添加最后一条消息到界面显示
                }
                listView.setSelection(mAdapter.getCount() - 1);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }else{
            mAdapter = new MessageChatAdapter(this,initMsgData());
            listView.setAdapter(mAdapter);
        }
    }
    private void initListView(){
        //加载数据
        refreshOrInit();
        listView.setSelection(mAdapter.getCount() - 1);
    }
    private void initView(){
        TextView tv_title = (TextView)findViewById(R.id.tv_title);
        listView = (ListView)findViewById(R.id.lv_chatmsg);
        tv_title.setText("与"+targetUser.getUsername()+"对话");
        initBottomView();
        initListView();
    }
    NewBroadcastReceiver  receiver;

    private void initNewMessageBroadCast(){
        // 注册接收消息广播
        receiver = new NewBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
        //设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
        intentFilter.setPriority(5);
        registerReceiver(receiver, intentFilter);
    }
    /**
     * 刷新界面
     * @Title: refreshMessage
     * @Description: TODO
     * @param @param message
     * @return void
     * @throws
     */
    private void refreshMessage(BmobMsg msg) {
        // 更新界面
        mAdapter.add(msg);
        listView.setSelection(mAdapter.getCount() - 1);
        et_user_commit.setText("");
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.edit_user_commit:
                listView.setSelection(mAdapter.getCount() - 1);
                break;
            case R.id.btn_chat_send:
                final String msg = et_user_commit.getText().toString();
                if (msg.equals("")) {
                    Toast.makeText(this,"请输入发送消息!",Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean isNetConnected = CommonUtils.isNetworkConnected(this);
                if (!isNetConnected) {
                    Toast.makeText(this,R.string.network_tips,Toast.LENGTH_SHORT).show();
                    // return;
                }
                // 组装BmobMessage对象
                BmobMsg message = BmobMsg.createTextSendMsg(this, targetId, msg);
                System.out.println("targetId::::::"+targetId);
                message.setExtra("Bmob");
                // 默认发送完成，将数据保存到本地消息表和最近会话表中
                BmobChatManager.getInstance(this).sendTextMessage(targetUser, message);
                // 刷新界面
                refreshMessage(message);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 新消息到达，重新刷新界面
        refreshOrInit();
        MyMessageReceiver.ehList.add(this);// 监听推送的消息
        // 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知和清空未读消息数
        BmobNotifyManager.getInstance(this).cancelNotify();
        System.out.println("onResume 重置未读消息");
        BmobDB.create(this).resetUnread(targetId);
        //清空消息未读数-这个要在刷新之后
        MyMessageReceiver.mNewNum=0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyMessageReceiver.ehList.remove(this);// 监听推送的消息
    }

    /**
     * 新消息广播接收者
     *
     */
    private class NewBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String from = intent.getStringExtra("fromId");
            String msgId = intent.getStringExtra("msgId");
            String msgTime = intent.getStringExtra("msgTime");
            // 收到这个广播的时候，message已经在消息表中，可直接获取
            if(TextUtils.isEmpty(from)&&TextUtils.isEmpty(msgId)&&TextUtils.isEmpty(msgTime)){
                BmobMsg msg = BmobChatManager.getInstance(ChatActivity.this).getMessage(msgId, msgTime);
                if (!from.equals(targetId))// 如果不是当前正在聊天对象的消息，不处理
                    return;
                //添加到当前页面
                mAdapter.add(msg);
                // 定位
               listView.setSelection(mAdapter.getCount() - 1);
                //取消当前聊天对象的未读标示
                System.out.println("NewBroadcastReceiver 重置未读消息");
                BmobDB.create(ChatActivity.this).resetUnread(targetId);
            }
            // 记得把广播给终结掉
            abortBroadcast();
        }
    }
    public static final int NEW_MESSAGE = 0x001;// 收到消息
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == NEW_MESSAGE) {
                BmobMsg message = (BmobMsg) msg.obj;
                String uid = message.getBelongId();
                BmobMsg m = BmobChatManager.getInstance(ChatActivity.this).getMessage(message.getConversationId(), message.getMsgTime());
                if (!uid.equals(targetId))// 如果不是当前正在聊天对象的消息，不处理
                    return;
                mAdapter.add(m);
                // 定位
                listView.setSelection(mAdapter.getCount() - 1);
                //取消当前聊天对象的未读标示
                System.out.println("handleMessage 重置未读消息");
                BmobDB.create(ChatActivity.this).resetUnread(targetId);
            }
        }
    };

    @Override
    public void onMessage(BmobMsg message) {
        // TODO Auto-generated method stub
        Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
        handlerMsg.obj = message;
        handler.sendMessage(handlerMsg);
    }

    @Override
    public void onReaded(String conversionId, String msgTime) {
        // TODO Auto-generated method stub
        // 此处应该过滤掉不是和当前用户的聊天的回执消息界面的刷新
        if (conversionId.split("&")[1].equals(targetId)) {
            // 修改界面上指定消息的阅读状态
            for (BmobMsg msg : mAdapter.getList()) {
                if (msg.getConversationId().equals(conversionId)
                        && msg.getMsgTime().equals(msgTime)) {
                    System.out.println("onReaded 消息已阅读");
                    msg.setStatus(BmobConfig.STATUS_SEND_RECEIVERED);
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onNetChange(boolean isNetConnected) {
        // TODO Auto-generated method stub
        if (!isNetConnected) {
            Toast.makeText(this,R.string.network_tips,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAddUser(BmobInvitation bmobInvitation) {

    }

    @Override
    public void onOffline() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
        }
    }
}
