package com.example.adm.bmob.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bmob.utils.BmobLog;
import com.example.adm.bmob.R;
import com.example.adm.bmob.bean.User;
import com.example.adm.bmob.config.BmobConstants;
import com.example.adm.bmob.util.ImageLoadOptions;
import com.example.adm.bmob.util.PhotoUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class MyselfInfoActivity extends BaseActivity implements View.OnClickListener{
    private String from,username;
    private ImageView iv_avatar;
    private TextView  tv_info_name,tv_info_nick,tv_info_sex,tv_title;
    private Button btn_create,btn_add,btn_blacklist;
    RelativeLayout layout_avatar,layout_nick,layout_sex,layout_info;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself_info);
        from = getIntent().getStringExtra("from");
        username = getIntent().getStringExtra("username");
        initView();
    }
    private void initView(){
        layout_info = (RelativeLayout)findViewById(R.id.layout_info);
        tv_title = (TextView)findViewById(R.id.tv_title);
        iv_avatar = (ImageView)findViewById(R.id.iv_info_avatar);
        tv_info_name = (TextView)findViewById(R.id.tv_info_name);
        tv_info_nick = (TextView)findViewById(R.id.tv_info_nick);
        tv_info_sex = (TextView)findViewById(R.id.tv_info_sex);
        btn_create = (Button)findViewById(R.id.btn_create);
        btn_add = (Button)findViewById(R.id.btn_add);
        btn_blacklist = (Button)findViewById(R.id.btn_blacklist);
        layout_avatar = (RelativeLayout)findViewById(R.id.layout_avatar);
        layout_nick = (RelativeLayout)findViewById(R.id.layout_nick);
        layout_sex = (RelativeLayout)findViewById(R.id.layout_sex);
        btn_create.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_blacklist.setOnClickListener(this);

        if(from.equals("me")){
            layout_avatar.setOnClickListener(this);
            layout_nick.setOnClickListener(this);
            layout_sex.setOnClickListener(this);
            tv_title.setText("个人资料".toString());
            btn_create.setVisibility(View.INVISIBLE);
            btn_blacklist.setVisibility(View.INVISIBLE);
            btn_add.setVisibility(View.INVISIBLE);
        }else if(from.equals("add")){
            btn_create.setVisibility(View.INVISIBLE);
            btn_blacklist.setVisibility(View.INVISIBLE);
            btn_add.setVisibility(View.VISIBLE);
        }else{
            btn_create.setVisibility(View.VISIBLE);
        }
        initData(username);
    }
    private void initData(String username){

        userManager.queryUser(username, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if (list != null && list.size() > 0) {
                    user = list.get(0);
                    tv_info_name.setText(user.getUsername());
                    tv_info_nick.setText(user.getNick());
                    tv_info_sex.setText(user.getSex() == true ? "男" : "女");
                    // 更改
                    refreshAvatar(user.getAvatar());
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }
    private void initMeData(){
        initData(userManager.getCurrentUserName());
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(from.equals("me")){
            initMeData();
        }
    }
    RelativeLayout layout_photo;
    RelativeLayout layout_choose;
    public String filePath = "";
    PopupWindow popupAvatar;
    private void showAvatarPop(View view){
        System.out.println("showAvatarPop");
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_showavatar,null);
        layout_photo = (RelativeLayout)contentView.findViewById(R.id.layout_photo);
         layout_choose = (RelativeLayout)contentView.findViewById(R.id.layout_choose);
        layout_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                layout_choose.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));
                layout_photo.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pop_bg_press));
                File dir = new File(BmobConstants.MyAvatarDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                // 原图
                File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss")
                        .format(new Date()));
                filePath = file.getAbsolutePath();// 获取相片的保存路径
                Uri imageUri = Uri.fromFile(file);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent,
                        BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA);
            }
        });
        popupAvatar = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupAvatar.setBackgroundDrawable(new BitmapDrawable());
         popupAvatar.showAsDropDown(view);
        // 动画效果 从底部弹起
     //   popupAvatar.setAnimationStyle(R.style.Animation_AppCompat_DropDownUp);
     //   popupAvatar.showAtLocation(layout_info, Gravity.BOTTOM, 0, 0);
    }
    private void updateUserAvatar(final String url) {
        User  u =new User();
        u.setAvatar(url);
        updateUserData(u,new UpdateListener() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
              //  ShowToast("头像更新成功！");
                Toast.makeText(MyselfInfoActivity.this,"头像更新成功！",Toast.LENGTH_SHORT).show();
                // 更新头像
                refreshAvatar(url);
            }

            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
              //  ShowToast("头像更新失败：" + msg);
                Toast.makeText(MyselfInfoActivity.this,"头像更新失败：" + msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void uploadAvatar() {
        BmobLog.i("头像地址：" + path);
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.upload(this, new UploadFileListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                String url = bmobFile.getFileUrl(MyselfInfoActivity.this);
                // 更新BmobUser对象
                updateUserAvatar(url);
            }

            @Override
            public void onProgress(Integer arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFailure(int arg0, String msg) {
                // TODO Auto-generated method stub
                // ShowToast("头像上传失败：" + msg);
                Toast.makeText(MyselfInfoActivity.this,"头像上传失败"+msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * @Title: startImageAction
     * @return void
     * @throws
     */
    private void startImageAction(Uri uri, int outputX, int outputY,
                                  int requestCode, boolean isCrop) {
        Intent intent = null;
        if (isCrop) {
            intent = new Intent("com.android.camera.action.CROP");
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }
    Bitmap newBitmap;
    boolean isFromCamera = false;// 区分拍照旋转
    int degree = 0;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case  BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA:
                if (resultCode == RESULT_OK) {
                    if (!Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                       // ShowToast("SD不可用");
                        Toast.makeText(this,"SD卡不可用",Toast.LENGTH_SHORT).show();;
                        return;
                    }
                    isFromCamera = true;
                    File file = new File(filePath);
                    degree = PhotoUtils.readPictureDegree(file.getAbsolutePath());
                    Log.i("life", "拍照后的角度：" + degree);
                    startImageAction(Uri.fromFile(file), 200, 200,
                            BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
                }
                break;
            case BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP:// 裁剪头像返回
                // TODO sent to crop
                if (popupAvatar != null) {
                    popupAvatar.dismiss();
                }
                if (data == null) {
                    // Toast.makeText(this, "取消选择", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    saveCropAvator(data);
                }
                // 初始化文件路径
                filePath = "";
                // 上传头像
                uploadAvatar();
                break;
        }
    }
    String path;
    /**
     * 保存裁剪的头像
     *
     * @param data
     */
    private void saveCropAvator(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            Log.i("life", "avatar - bitmap = " + bitmap);
            if (bitmap != null) {
                bitmap = PhotoUtils.toRoundCorner(bitmap, 10);
                if (isFromCamera && degree != 0) {
                    bitmap = PhotoUtils.rotaingImageView(degree, bitmap);
                }
                iv_avatar.setImageBitmap(bitmap);
                // 保存图片
                String filename = new SimpleDateFormat("yyMMddHHmmss")
                        .format(new Date())+".png";
                path = BmobConstants.MyAvatarDir + filename;
                PhotoUtils.saveBitmap(BmobConstants.MyAvatarDir, filename,
                        bitmap, true);
                // 上传头像
                if (bitmap != null && bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_create:
                Intent intent = new Intent(MyselfInfoActivity.this,ChatActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
                break;
            case R.id.btn_add:
                addContact();
                break;
            case R.id.layout_avatar:
                showAvatarPop(v);
                break;
        }

    }
    private void addContact(){
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("正在添加...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        //发送tag请求
        BmobChatManager.getInstance(this).sendTagMessage(BmobConfig.TAG_ADD_CONTACT, user.getObjectId(),new PushListener() {
            @Override
            public void onSuccess() {
                progress.dismiss();
                Toast.makeText(MyselfInfoActivity.this, "发送请求成功，等待对方验证!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int arg0, final String arg1) {
                progress.dismiss();
                Toast.makeText(MyselfInfoActivity.this, "发送请求失败,请重新添加!", Toast.LENGTH_SHORT).show();
                Log.e("Add Friend", "发送请求失败:" + arg1);
            }
        });
    }
    /**
     * 更新头像 refreshAvatar
     *
     * @return void
     * @throws
     */
    private void refreshAvatar(String avatar) {
        if (avatar != null && !avatar.equals("")) {
            ImageLoader.getInstance().displayImage(avatar, iv_avatar,
                    ImageLoadOptions.getOptions());
        } else {
            iv_avatar.setImageResource(R.mipmap.default_head);
        }
    }
    private void updateUserData(User user,UpdateListener listener){
        User current = (User) userManager.getCurrentUser(User.class);
        user.setObjectId(current.getObjectId());
        user.update(this, listener);
    }
}
