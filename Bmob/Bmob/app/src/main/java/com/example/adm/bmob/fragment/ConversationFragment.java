package com.example.adm.bmob.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.adm.bmob.R;
import com.example.adm.bmob.adapter.ConversationAdapter;
import com.example.adm.bmob.ui.ChatActivity;

import java.util.List;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;


public class ConversationFragment extends Fragment {
    private View view;
    private int selectItemPos;
    private ListView lv_conversation;
    private ConversationAdapter adapter;
    final static private int MENU_DEL = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_conversation, container, false);
        initView();
        return view;
    }
    private void initView(){
        lv_conversation = (ListView)view.findViewById(R.id.lv_conversation);
        adapter = new ConversationAdapter(getActivity(), BmobDB.create(getActivity()).queryRecents());
        lv_conversation.setAdapter(adapter);
        lv_conversation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BmobRecent recent = adapter.getItem(position);
                //重置未读消息
                System.out.println("initView 重置未读消息");
                BmobDB.create(getActivity()).resetUnread(recent.getTargetid());
                //组装聊天对象
                BmobChatUser user = new BmobChatUser();
                user.setAvatar(recent.getAvatar());
                user.setNick(recent.getNick());
                user.setUsername(recent.getUserName());
                user.setObjectId(recent.getTargetid());
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
        lv_conversation.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo)menuInfo;
                selectItemPos = contextMenuInfo.position;
                menu.add(0,MENU_DEL,0,"删除该聊天");
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        BmobRecent recent = adapter.getItem(selectItemPos);
        switch (item.getItemId()){
            case MENU_DEL:
                adapter.remove(selectItemPos);
                BmobDB.create(getActivity()).deleteRecent(recent.getTargetid());
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void refreshView(){
        adapter = new ConversationAdapter(getActivity(), BmobDB.create(getActivity()).queryRecents());
        lv_conversation.setAdapter(adapter);
    }
    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }
}
