package com.example.adm.bmob.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.adm.bmob.R;
import com.example.adm.bmob.bean.User;
import com.example.adm.bmob.util.BaseUtils;
import com.example.adm.bmob.util.CommonUtils;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends BaseActivity {
    private EditText editTextusername,editTextpassword,editTextpasswordconfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userManager = BmobUserManager.getInstance(this);
        editTextusername = (EditText)findViewById(R.id.et_username);
        editTextpassword = (EditText)findViewById(R.id.et_password);
        editTextpasswordconfirm = (EditText)findViewById(R.id.et_passwordconfirm);
    }
    private void register(){
        String name = editTextusername.getText().toString();
        String password = editTextpassword.getText().toString();
        String pwd_again = editTextpasswordconfirm.getText().toString();

        if (TextUtils.isEmpty(name)) {
            BaseUtils.ShowToast(RegisterActivity.this,"用户名为空");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            BaseUtils.ShowToast(RegisterActivity.this, "密码为空");
            return;
        }
        if (!pwd_again.equals(password)) {
            BaseUtils.ShowToast(RegisterActivity.this, "两次密码不一致");
            return;
        }

        boolean isNetConnected = CommonUtils.isNetworkConnected(RegisterActivity.this);
        if(!isNetConnected){
            BaseUtils.ShowToast(RegisterActivity.this, "网络连接出错");
            return;
        }

        final ProgressDialog progress = new ProgressDialog(RegisterActivity.this);
        progress.setMessage("正在注册...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        //由于每个应用的注册所需的资料都不一样，故IM sdk未提供注册方法，用户可按照bmod SDK的注册方式进行注册。
        //注册的时候需要注意两点：1、User表中绑定设备id和type，2、设备表中绑定username字段
        final User bu = new User();
        bu.setUsername(name);
        bu.setPassword(password);
        //将user和设备id进行绑定aa
        bu.setSex(true);
        bu.setDeviceType("android");
        bu.setInstallId(BmobInstallation.getInstallationId(this));
        bu.signUp(RegisterActivity.this, new SaveListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                progress.dismiss();
                //BaseUtils.ShowToast(RegisterActivity.this, "注册成功");
                Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                // 将设备与username进行绑定
                userManager.bindInstallationForRegister(bu.getUsername());
                //更新地理位置信息
               // updateUserLocation();
                //发广播通知登陆页面退出
               // sendBroadcast(new Intent(BmobConstants.ACTION_REGISTER_SUCCESS_FINISH));
                // 启动主页
                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                BmobLog.i(arg1);
                BaseUtils.ShowToast(RegisterActivity.this, "注册失败"+arg1);
                progress.dismiss();
            }
        });
    }
    public void onClick_Register(View view){
        register();
    }

}
