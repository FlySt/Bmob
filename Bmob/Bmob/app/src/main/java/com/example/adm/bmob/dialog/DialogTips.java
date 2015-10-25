package com.example.adm.bmob.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by ADM on 2015/10/15.
 */
public class DialogTips extends BaseDialog {
    protected OnClickListener onSuccessListener;
    public DialogTips(Context context,String title,int iconId,String msg) {
        super(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setIcon(iconId)
                .setMessage(msg);
        setPositiveButton(builder);
        setNegativeButton(builder)
                .create()
                .show();;
    }
   private AlertDialog.Builder setPositiveButton(AlertDialog.Builder builder){
       return builder.setPositiveButton("取消", new OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               DialogTips.this.dismiss();
           }
       });
   }
   private AlertDialog.Builder setNegativeButton(AlertDialog.Builder builder) {
       return builder.setNegativeButton("确定", new OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               if(OnClickNegativeButton())
                   DialogTips.this.dismiss();
           }
       });
   }
    /**
     * 设置成功事件监听，用于提供给调用者的回调函数
     * @param listener 成功事件监听
     */
    public void SetOnSuccessListener(OnClickListener listener){
        onSuccessListener = listener;
    }

    protected boolean OnClickNegativeButton() {
        if(onSuccessListener != null){
            onSuccessListener.onClick(this, 1);
        }
        return true;
    }
}
