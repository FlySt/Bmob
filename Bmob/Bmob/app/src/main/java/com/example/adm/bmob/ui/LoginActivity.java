package com.example.adm.bmob.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.example.adm.bmob.R;
import com.example.adm.bmob.bean.User;
import com.example.adm.bmob.util.BaseUtils;
import com.example.adm.bmob.util.CommonUtils;

import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends BaseActivity {
    private EditText editTextusername,editTextpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextusername = (EditText)findViewById(R.id.et_username);
        editTextpassword = (EditText)findViewById(R.id.et_password);
    }
    public void login(){
        String name = editTextusername.getText().toString();
        String password = editTextpassword.getText().toString();

        if (TextUtils.isEmpty(name)) {
            BaseUtils.ShowToast(LoginActivity.this, "用户名为空");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            BaseUtils.ShowToast(LoginActivity.this, "密码为空");
            return;
        }
        final ProgressDialog progress = new ProgressDialog(
                LoginActivity.this);
        progress.setMessage("正在登陆...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        User user = new User();
        user.setUsername(name);
        user.setPassword(password);
        userManager.login(user, new SaveListener() {
            @Override
            public void onSuccess() {
                 new Thread(new Runnable(){
                    @Override
                    public void run() {
                        progress.setMessage("正在获取好友列表...");
                    }
                });
                updateUserInfos();
                progress.dismiss();
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                progress.dismiss();
                BaseUtils.ShowToast(LoginActivity.this, s);
            }
        });
    }
    public void onClick_Register(View view){
        Intent intent = new Intent(LoginActivity.this,
                RegisterActivity.class);
        startActivity(intent);
    }
    public void onClick_Login(View view){
        boolean isNetConnected = CommonUtils.isNetworkConnected(LoginActivity.this);
        if(!isNetConnected){
            BaseUtils.ShowToast(LoginActivity.this,R.string.network_tips);
            return;
        }
        login();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
