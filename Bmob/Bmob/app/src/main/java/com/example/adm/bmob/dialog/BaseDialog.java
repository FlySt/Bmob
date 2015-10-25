package com.example.adm.bmob.dialog;

import android.app.Dialog;
import android.content.Context;

/**
 * Created by ADM on 2015/10/15.
 */
public class BaseDialog  extends Dialog{
    public BaseDialog(Context context) {
        super(context);

    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
    }

    protected BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
