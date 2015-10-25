package com.example.adm.bmob.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adm.bmob.R;
import com.example.adm.bmob.bean.User;
import com.example.adm.bmob.demo.CustomApplication;
import com.example.adm.bmob.util.CollectionUtils;

import java.util.List;
import java.util.Map;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.FindListener;

public class SearchActivity extends BaseActivity {
    EditText et_search;
    TextView tv_name;
    ImageView iv_search;
    RelativeLayout layout_search;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }
    private void initView(){
        et_search = (EditText)findViewById(R.id.et_search);
        tv_name = (TextView)findViewById(R.id.tv_name);
        iv_search = (ImageView)findViewById(R.id.iv_search);
        layout_search = (RelativeLayout)findViewById(R.id.search_layout);
        layout_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("layout_search click");
                SearchContact(et_search.getText().toString());

            }
        });
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println(et_search.getText().toString());
                layout_search.setVisibility(View.VISIBLE);
                tv_name.setText("搜索：" + et_search.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    //检查是否已经是好友
    private boolean isContact(final String username){
        CustomApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(SearchActivity.this).getContactList()));
        Map<String,BmobChatUser> users = CustomApplication.getInstance().getContactList();
        List<BmobChatUser> lists = CollectionUtils.map2list(users);
        for(BmobChatUser list:lists){
            if(list.getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }
    ProgressDialog progress;
    private void SearchContact(final String username){
        progress = new ProgressDialog(SearchActivity.this);
        progress.setMessage("正在搜索...");
        progress.setCanceledOnTouchOutside(true);
        progress.show();
        userManager.queryUserByName(username, new FindListener<BmobChatUser>() {
            @Override
            public void onError(int arg0, String arg1) {
                Toast.makeText(SearchActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }

            @Override
            public void onSuccess(List<BmobChatUser> arg0) {
                if (arg0 != null && arg0.size() > 0) {
                    Intent intent = new Intent(SearchActivity.this, MyselfInfoActivity.class);
                    if(userManager.getCurrentUserName().equals(username)){
                        Toast.makeText(SearchActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                        return;
                    }
                    else if(isContact(username)){
                        intent.putExtra("from","other");
                    }else {
                        intent.putExtra("from","add");
                    }
                    intent.putExtra("username",username);
                    startActivity(intent);
                } else {
                    Toast.makeText(SearchActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();
                }
                progress.dismiss();
            }
        });
    }

}
