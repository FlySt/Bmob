package com.example.adm.bmob.ui;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.adm.bmob.R;
import com.example.adm.bmob.adapter.AddFriendAdapter;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;

public class SearchFriendActivity extends BaseActivity {
    private List<BmobChatUser> users = new ArrayList<BmobChatUser>();
    private AddFriendAdapter adapter ;
    private ListView lv_friend;
    private EditText et_searchname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        et_searchname = (EditText)findViewById(R.id.et_searchname);
        lv_friend = (ListView)findViewById(R.id.lv_newfriend);
        adapter = new AddFriendAdapter(this, R.layout.adapter_new_friends_items, users);
        lv_friend.setAdapter(adapter);
        et_searchname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchFriendActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout search_layout = (LinearLayout)findViewById(R.id.search_layout);
        search_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchFriendActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });
    }
    ProgressDialog progress ;
    private void searchByname(String searchName){
            progress = new ProgressDialog(SearchFriendActivity.this);
            progress.setMessage("正在搜索...");
            progress.setCanceledOnTouchOutside(true);
            progress.show();
            userManager.queryUserByName(searchName, new FindListener<BmobChatUser>() {
                @Override
                public void onError(int arg0, String arg1) {
                    Toast.makeText(SearchFriendActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();
                    if (users != null) {
                        users.clear();
                    }
                    progress.dismiss();
                }

                @Override
                public void onSuccess(List<BmobChatUser> arg0) {
                    if (users != null) {
                        users.clear();
                    }
                    if (arg0 != null && arg0.size() > 0) {
                        users.addAll(arg0);
                        System.out.println("添加结束" + users.size());
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(SearchFriendActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();
                    }
                    progress.dismiss();
                }
            });
    }
    public void onClick_Search(View view){
        searchByname(et_searchname.getText().toString());
    }


}
