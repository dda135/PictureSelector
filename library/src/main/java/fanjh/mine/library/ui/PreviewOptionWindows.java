package fanjh.mine.library.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.MotionEventCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import fanjh.mine.library.R;


/**
* @author fanjh
* @date 2017/12/26 9:28
* @description 图片预览选项框
* @note
**/
public class PreviewOptionWindows extends PopupWindow {
    private Context context;
    private View background;
    private View buttons;

    public interface OnPopClickListener{
        void onPopClick(PreviewOptionWindows photoWindows);
    }

    public PreviewOptionWindows(final Activity mContext, View parent, final OnPopClickListener popClickListener1,
                                final OnPopClickListener popClickListener2) {
        context = mContext;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        background = inflater.inflate(R.layout.view_preview_options, null);
        buttons = background.findViewById(R.id.ll_popup);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.transparent)));
        setFocusable(true);
        setOutsideTouchable(true);
        setTouchable(true);
        setContentView(background);
        if(mContext.getCurrentFocus() != null) {
            ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow
                    (mContext.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        //改成部分滑动动画，避免黑色背景一起移动
        Animation dropdown_in = AnimationUtils.loadAnimation(mContext, R.anim.image_popup_in);
        AlphaAnimation animation = new AlphaAnimation(0,1);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setDuration(150);
        background.startAnimation(animation);
        buttons.startAnimation(dropdown_in);

        Button bt1 = (Button) background.findViewById(R.id.item_popupwindows_photo);
        Button bt2 = (Button) background.findViewById(R.id.item_popupwindows_cancel);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != popClickListener1) {
                    popClickListener1.onPopClick(PreviewOptionWindows.this);
                }
                dismiss();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != popClickListener2) {
                    popClickListener2.onPopClick(PreviewOptionWindows.this);
                }
                dismiss();
            }
        });
        //add by fanjh on 2016/10/25 点击空白区域关闭
        FrameLayout parentLayout = (FrameLayout) background.findViewById(R.id.fl_parent_layout);
        parentLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_DOWN == MotionEventCompat.getActionMasked(event)){
                    dismiss();
                }
                return false;
            }
        });

    }

    @Override
    public void dismiss() {
        //改成部分滑动动画，避免黑色背景一起移动
        Animation out = AnimationUtils.loadAnimation(context, R.anim.image_popup_out);
        AlphaAnimation animation = new AlphaAnimation(1,0.4f);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                trueDismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setDuration(300);
        background.startAnimation(animation);
        buttons.startAnimation(out);
    }

    private void trueDismiss(){
        super.dismiss();
    }

}
