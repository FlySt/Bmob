package com.example.adm.bmob.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.bmob.im.bean.BmobChatUser;

/**
 * Created by SWan on 2015/10/14.
 */
public class CollectionUtils {
    public static Map<String, BmobChatUser> list2map(List<BmobChatUser> list){
        System.out.println("list.size::"+list.size());
        Map<String, BmobChatUser> friends = new HashMap<String,BmobChatUser>();
        for(BmobChatUser user : list){
            friends.put(user.getUsername(),user);
        }
        return friends;
    }
    /** mapתlist
     * @Title: map2list
     * @return List<BmobChatUser>
     * @throws
     */
    public static List<BmobChatUser> map2list(Map<String,BmobChatUser> maps){
        List<BmobChatUser> users = new ArrayList<BmobChatUser>();
        Iterator<Map.Entry<String, BmobChatUser>> iterator = maps.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, BmobChatUser> entry = iterator.next();
            users.add(entry.getValue());
        }
        return users;
    }
}
