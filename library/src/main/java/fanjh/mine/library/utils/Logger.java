package fanjh.mine.library.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Toast;


public class Logger {

    /************************
     * Toast  start
     ***************************/
    private static Toast toast;

    /**
     * LENGTH_SHORT toast
     *
     * @param mContext
     * @param context  内容
     */
    public static void showToastShort(Context mContext, String context) {
        if (!TextUtils.isEmpty(context)) {
            showToast(mContext, context, Toast.LENGTH_SHORT);
        }
    }

    /**
     * LENGTH_LONG toast
     *
     * @param mContext
     * @param context  内容
     */
    public static void showToastLong(Context mContext, String context) {
        if (!TextUtils.isEmpty(context)) {
            showToast(mContext, context, Toast.LENGTH_LONG);
        }
    }

    /**
     * 展现toast
     *
     * @param context  上下文
     * @param msg      内容
     * @param duration 展现时长
     */
    private static void showToast(Context context, CharSequence msg,
                                  int duration) {
        showToast(context,msg,duration,-1);
    }

    /**
     * 初始化 toast
     *
     * @param mContext
     * update by fanjh on 2016/9/21
     * 这里不需要static持有view，因为makeText内部会new Toast，同时设置视图。
     */
    private static void getToast(Context mContext) {
        if (toast == null) {
            toast = Toast.makeText(mContext.getApplicationContext(), "", Toast.LENGTH_LONG);
        }
    }
    /************************ Toast  end***************************/

    private static void showToast(Context context,CharSequence msg,int duration,int gravity){
        try {
            getToast(context);
            toast.setText(msg);
            toast.setDuration(duration);
            if(gravity != -1) {
                toast.setGravity(gravity, 0, 0);
            }else{
                toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0 , (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,48,context.getResources().getDisplayMetrics()));
            }
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 居中显示
     */
    public static void showToastLongCenter(Context mContext, String context) {
        if (!TextUtils.isEmpty(context)) {
            showToast(mContext, context, Toast.LENGTH_LONG, Gravity.CENTER);
        }
    }

}
