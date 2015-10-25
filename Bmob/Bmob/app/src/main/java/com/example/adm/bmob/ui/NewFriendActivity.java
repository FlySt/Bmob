package com.example.adm.bmob.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.adm.bmob.R;
import com.example.adm.bmob.adapter.AddFriendAdapter;
import com.example.adm.bmob.adapter.NewFriendAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.db.BmobDB;

public class NewFriendActivity extends BaseActivity {
    private List<BmobInvitation> newfriends = new ArrayList<BmobInvitation>();
    private NewFriendAdapter adapter;
    private ListView lv_newFriends;
    private int selectItemPos;
    final private static  int MENU_DEL = 0;
    String from="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);
        from = getIntent().getStringExtra("from");
        viewInit();
    }
    private void viewInit(){
        lv_newFriends = (ListView)findViewById(R.id.lv_newfriends);
        newfriends = BmobDB.create(this).queryBmobInviteList();
        adapter = new NewFriendAdapter(this,R.layout.adapter_new_friends_items,newfriends);
        lv_newFriends.setAdapter(adapter);
        lv_newFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BmobInvitation invitation = adapter.getItem(position);
                Intent intent = new Intent(NewFriendActivity.this, MyselfInfoActivity.class);
                intent.putExtra("username", invitation.getFromname());
                intent.putExtra("from", "other");
                startActivity(intent);
            }
        });
        lv_newFriends.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo mInfo = (AdapterView.AdapterContextMenuInfo)menuInfo;
                selectItemPos = mInfo.position;

                menu.add(0,MENU_DEL,0,"删除");
            }
        });
        if(from==null){//若来自通知栏的点击，则定位到最后一条
            lv_newFriends.setSelection(adapter.getCount());
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
         BmobInvitation invitation = newfriends.get(selectItemPos);
        switch (item.getItemId()){
            case MENU_DEL:
                adapter.remove(selectItemPos);
                BmobDB.create(NewFriendActivity.this).deleteInviteMsg(invitation.getFromid(),Long.toString(invitation.getTime()));
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
}
