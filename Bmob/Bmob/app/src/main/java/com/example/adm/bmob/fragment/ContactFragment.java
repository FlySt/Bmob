package com.example.adm.bmob.fragment;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.adm.bmob.R;
import com.example.adm.bmob.adapter.FriendsAdapter;
import com.example.adm.bmob.bean.User;
import com.example.adm.bmob.demo.CustomApplication;
import com.example.adm.bmob.dialog.DialogTips;
import com.example.adm.bmob.ui.MyselfInfoActivity;
import com.example.adm.bmob.ui.NewFriendActivity;
import com.example.adm.bmob.ui.SearchFriendActivity;
import com.example.adm.bmob.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;


public class ContactFragment extends Fragment {
    private List<User> friends = new ArrayList<User>();
    private FriendsAdapter adapter;
    private ListView lv_friends;
    private ImageView iv_new_msg;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        view.findViewById(R.id.add_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchFriendActivity.class);
                startActivity(intent);
            }
        });

        lv_friends = (ListView)view.findViewById(R.id.lv_friends);

        LinearLayout headView = (LinearLayout) inflater.inflate(R.layout.include_new_friends, null);
        iv_new_msg = (ImageView)headView.findViewById(R.id.iv_new_msg);
        RelativeLayout layout_new = (RelativeLayout)headView.findViewById(R.id.layout_new);
        RelativeLayout layout_near = (RelativeLayout)headView.findViewById(R.id.layout_near);
        layout_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewFriendActivity.class);
                startActivity(intent);
            }
        });
        layout_near.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        lv_friends.addHeaderView(headView);
        initView();
        return view;
    }
    private void initView(){

        System.out.println("ContactFragment initView");
        adapter = new FriendsAdapter(getActivity(),R.layout.adapter_friends_items,friends);
        lv_friends.setAdapter(adapter);
        lv_friends.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                User user = friends.get(position - 1);
                Log.i("onItemLongClick", "长按");
                shouwDeleteDialog(user);
                return false;
            }
        });
        lv_friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = adapter.getItem(position - 1);
                Intent intent = new Intent(getActivity(), MyselfInfoActivity.class);
                intent.putExtra("from", "others");
                intent.putExtra("username", user.getUsername());
                startActivity(intent);
            }
        });
    }
    private void shouwDeleteDialog(final User user){
        DialogTips dilog = new DialogTips(getActivity(),user.getUsername(),R.mipmap.default_head,"删除联系人");
        dilog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("shouwDeleteDialog", "确认删除联系人" + user.getUsername());
                deleteContact(user);
            }
        });
    }
    private void updateFriends(){
        friends.clear();
        CustomApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(getActivity()).getContactList()));
        Map<String,BmobChatUser> users = CustomApplication.getInstance().getContactList();
        List<BmobChatUser> lists = CollectionUtils.map2list(users);
        System.out.println("ContactFragment " + lists.size());
        for(BmobChatUser list:lists){
            User user = new User();
            System.out.println("ContactFragment "+list.getUsername());
            user.setAvatar(list.getAvatar());
            user.setUsername(list.getUsername());
            user.setNick(list.getNick());
            user.setObjectId(list.getObjectId());
            user.setContacts(list.getContacts());
            friends.add(user);
        }
    }
    /** 删除联系人
     * deleteContact
     * @return void
     * @throws
     */
    private void deleteContact(final User user){
        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setMessage("正在删除...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        BmobUserManager.getInstance(getActivity()).deleteContact(user.getObjectId(), new UpdateListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                //删除内存
                CustomApplication.getInstance().getContactList().remove(user.getUsername());
                //更新界面
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        progress.dismiss();
                        adapter.remove(user);
                    }
                });
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                Toast.makeText(getActivity(), "删除失败：" + arg1, Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        });
    }
    private void queryMyfriends(){
        //是否有好友请求
        if(BmobDB.create(getActivity()).hasNewInvite()){
            System.out.println("有好友请求......");
            iv_new_msg.setVisibility(View.VISIBLE);
        }else{
            System.out.println("无好友请求......");
            iv_new_msg.setVisibility(View.INVISIBLE);
        }
        updateFriends();
        adapter.notifyDataSetChanged();
    }
    public void refresh(){
        try {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    System.out.println("正在refresh......");
                    queryMyfriends();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
         refresh();
    }
}
