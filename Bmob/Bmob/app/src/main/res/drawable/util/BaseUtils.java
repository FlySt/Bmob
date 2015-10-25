package drawable.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by SWan on 2015/10/12.
 */
public class BaseUtils {
    private static Toast mToast;
    public static void ShowToast(final Context context, final String text){
        if (!TextUtils.isEmpty(text)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if (mToast == null) {
                        mToast = Toast.makeText(context, text,
                                Toast.LENGTH_LONG);
                    } else {
                        mToast.setText(text);
                    }
                    mToast.show();
                }
            });

        }
    }

    public static void ShowToast(final Context context, final int resId){
        new Thread(){
            @Override
            public void run() {
                Toast.makeText(context,resId,Toast.LENGTH_SHORT).show();
            }
        }.start();
    }
}
