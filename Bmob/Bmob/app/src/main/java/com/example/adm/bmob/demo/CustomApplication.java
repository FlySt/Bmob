package com.example.adm.bmob.demo;

import android.app.Application;
import android.content.Context;

import com.example.adm.bmob.bean.User;
import com.example.adm.bmob.util.CollectionUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by SWan on 2015/10/14.
 */
public class CustomApplication extends Application {
    public static CustomApplication mInstance;

    private Map<String, BmobChatUser> contactList = new HashMap<String, BmobChatUser>();
    @Override
    public void onCreate() {
        super.onCreate();
        // 是否开启debug模式--默认开启状态
        BmobChat.DEBUG_MODE = true;
        mInstance = this;
        System.out.println("CustomApplication onCreate-------------------------------------------");
        init();
    }
    private void init(){
        initImageLoader(getApplicationContext());
        // 若用户登陆过，则先从好友数据库中取出好友list存入内存中
        if (BmobUserManager.getInstance(getApplicationContext())
                .getCurrentUser() != null) {
            // 获取本地好友user list到内存,方便以后获取好友list
            contactList = CollectionUtils.list2map(BmobDB.create(getApplicationContext()).getContactList());
            System.out.println("contactList.size::"+contactList.size());
        }
    }
    /** 初始化ImageLoader */
    public static void initImageLoader(Context context) {
        File cacheDir = StorageUtils.getOwnCacheDirectory(context,
                "bmobim/Cache");// 获取到缓存的目录地址
        // 创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLACATION里面，设置为全局的配置参数
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                // 线程池内加载的数量
                .threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                        // 将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCache(new UnlimitedDiscCache(cacheDir))// 自定义缓存路径
                        // .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);// 全局初始化此配置
    }
    public   Map<String, BmobChatUser> getcontactList(){
        return contactList;
    }
    /**
     * 设置好友user list到内存中
     * @param contactList
     */
    public void setContactList(Map<String, BmobChatUser> contactList) {
        if (this.contactList != null) {
            this.contactList.clear();
        }
        this.contactList = contactList;
    }
    public static CustomApplication getInstance() {
        return mInstance;
    }
    /**
     * 获取内存中好友user list
     *
     * @return
     */
    public Map<String, BmobChatUser> getContactList() {
        return contactList;
    }

    public void remove(User user){
            BmobUserManager.getInstance(getApplicationContext()).deleteContact(user.getObjectId(),new UpdateListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }
    /**
     * 退出登录,清空缓存数据
     */

    public void logout() {
        BmobUserManager.getInstance(getApplicationContext()).logout();
      //  setContactList(null);
      //  setLatitude(null);
     //   setLongtitude(null);
    }
}
