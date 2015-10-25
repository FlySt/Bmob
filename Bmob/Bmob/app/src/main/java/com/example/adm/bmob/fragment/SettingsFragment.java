package com.example.adm.bmob.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.adm.bmob.R;
import com.example.adm.bmob.bean.User;
import com.example.adm.bmob.demo.CustomApplication;
import com.example.adm.bmob.ui.LoginActivity;
import com.example.adm.bmob.ui.MyselfInfoActivity;

import cn.bmob.im.BmobUserManager;


public class SettingsFragment extends Fragment implements View.OnClickListener{

    private View view;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        initView();
        return view;
    }
    private void initView(){
        TextView tv_username = (TextView)view.findViewById(R.id.tv_username);
        Button btn_logout = (Button) view.findViewById(R.id.btn_logout);
        RelativeLayout layout_info = (RelativeLayout)view.findViewById(R.id.layout_info);
        tv_username.setText(BmobUserManager.getInstance(getActivity()).getCurrentUserName());
        btn_logout.setOnClickListener(this);
        layout_info.setOnClickListener(this);

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_logout:
                CustomApplication.getInstance().logout();
                getActivity().finish();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.layout_info:
                Intent intent = new Intent(getActivity(), MyselfInfoActivity.class);
                intent.putExtra("from","me");
                startActivity(intent);
        }
    }

}
