package com.example.adm.bmob.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.adm.bmob.R;
import com.example.adm.bmob.demo.CustomApplication;
import com.example.adm.bmob.demo.MyMessageReceiver;
import com.example.adm.bmob.fragment.ContactFragment;
import com.example.adm.bmob.fragment.ConversationFragment;
import com.example.adm.bmob.fragment.SettingsFragment;



import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;

public class MainActivity extends BaseActivity implements EventListener {
    private Button[] mTabs;
    private ContactFragment contactFragment;//联系人
    private ConversationFragment conversationFragment;//会话
    private SettingsFragment settingFragment;//设置
    private Fragment[] fragments;
    private Button[] bTabs;
    private int currentSelectIndex = 0;
    private int index;
    ImageView iv_recent_tips,iv_contact_tips;//消息提示
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewInit();
        initFragment();
        //开启广播接收器
        initNewMessageBroadCast();
        initTagMessageBroadCast();
    }
    private void viewInit(){
        bTabs = new Button[3];
        iv_recent_tips = (ImageView)findViewById(R.id.iv_recent_tips);
        iv_contact_tips = (ImageView)findViewById(R.id.iv_contact_tips);
        bTabs[0] = (Button)findViewById(R.id.btn_conversation);
        bTabs[1] = (Button)findViewById(R.id.btn_contact);
        bTabs[2] = (Button)findViewById(R.id.btn_set);
        bTabs[0].setSelected(true);
        currentSelectIndex = 0;
    }

    private void initFragment(){
        contactFragment = new ContactFragment();
        conversationFragment = new ConversationFragment();
        settingFragment = new SettingsFragment();
        fragments = new Fragment[] {conversationFragment, contactFragment, settingFragment };
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.id_content, conversationFragment);
        transaction.commit();
    }
    public void onTabSelect(View view){
        switch(view.getId()){
            case R.id.btn_conversation:
                index = 0;
                break;
            case R.id.btn_contact:
                index = 1;
                break;
            case R.id.btn_set:
                index = 2;
                break;
        }
        if(currentSelectIndex!=index){
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.id_content,fragments[index]);
            transaction.commit();
        }
        bTabs[currentSelectIndex].setSelected(false);
        currentSelectIndex = index;
        bTabs[currentSelectIndex].setSelected(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //小圆点提示
        if(BmobDB.create(this).hasUnReadMsg()){
            iv_recent_tips.setVisibility(View.VISIBLE);
        }else{
            iv_recent_tips.setVisibility(View.GONE);
        }
        if(BmobDB.create(this).hasNewInvite()){
            iv_contact_tips.setVisibility(View.VISIBLE);
        }else{
            iv_contact_tips.setVisibility(View.GONE);
        }
        MyMessageReceiver.ehList.add(this);// 监听推送的消息
        //清空
        MyMessageReceiver.mNewNum=0;
    }

    /** 刷新好友请求
     * @Title: notifyAddUser
     * @Description: TODO
     * @param @param message
     * @return void
     * @throws
     */
    private void refreshInvite(BmobInvitation message){
    //  //  boolean isAllow = CustomApplication.getInstance().getSpUtil().isAllowVoice();
     //   if(isAllow){
     //       CustomApplcation.getInstance().getMediaPlayer().start();
     //   }
        iv_contact_tips.setVisibility(View.VISIBLE);
        if(currentSelectIndex==1){
            if(contactFragment != null){
                contactFragment.refresh();
            }
        }else{
            //同时提醒通知
            String tickerText = message.getFromname()+"请求添加好友";
          //  boolean isAllowVibrate = CustomApplication.getInstance().getSpUtil().isAllowVibrate();
            BmobNotifyManager.getInstance(this).showNotify(true, true, R.drawable.welcome, tickerText, message.getFromname(), tickerText.toString(), NewFriendActivity.class);

        }
    }
    /** 刷新界面
     * @Title: refreshNewMsg
     * @Description: TODO
     * @param @param message
     * @return void
     * @throws
     */
    private void refreshNewMsg(BmobMsg message){
        // 声音提示
      //  boolean isAllow = CustomApplication.getInstance().getSpUtil().isAllowVoice();
     //   if(isAllow){
     //       CustomApplcation.getInstance().getMediaPlayer().start();
     //   }
        iv_recent_tips.setVisibility(View.VISIBLE);
        //也要存储起来
        if(message!=null){
            BmobChatManager.getInstance(MainActivity.this).saveReceiveMessage(false,message);
        }
        if(currentSelectIndex==0){
            //当前页面如果为会话页面，刷新此页面
            if(conversationFragment != null){
                conversationFragment.refreshView();
            }
        }
    }

    NewBroadcastReceiver  newReceiver;

    private void initNewMessageBroadCast(){
        // 注册接收消息广播
        newReceiver = new NewBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
        //优先级要低于ChatActivity
        intentFilter.setPriority(3);
        registerReceiver(newReceiver, intentFilter);
    }


    /**
     * 新消息广播接收者
     *
     */
    private class NewBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("NewBroadcastReceiver");
            //刷新界面
            refreshNewMsg(null);
            // 记得把广播给终结掉
            abortBroadcast();
        }
    }

    TagBroadcastReceiver  userReceiver;

    private void initTagMessageBroadCast(){
        // 注册接收消息广播
        userReceiver = new TagBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_ADD_USER_MESSAGE);
        //优先级要低于ChatActivity
        intentFilter.setPriority(3);
        registerReceiver(userReceiver, intentFilter);
    }

    /**
     * 标签消息广播接收者
     */
    private class TagBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            BmobInvitation message = (BmobInvitation) intent.getSerializableExtra("invite");
            refreshInvite(message);
            // 记得把广播给终结掉
            abortBroadcast();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
    }

    @Override
    public void onMessage(BmobMsg bmobMsg) {
        refreshNewMsg(bmobMsg);
    }

    @Override
    public void onReaded(String s, String s1) {

    }

    @Override
    public void onNetChange(boolean isNetConnected) {
        // TODO Auto-generated method stub
        if(isNetConnected){
            Toast.makeText(this,R.string.network_tips,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAddUser(BmobInvitation bmobInvitation) {
        refreshInvite(bmobInvitation);
    }

    @Override
    public void onOffline() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(newReceiver);
        } catch (Exception e) {
        }
        try {
            unregisterReceiver(userReceiver);
        } catch (Exception e) {
        }
    }
}
