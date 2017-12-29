package fanjh.mine.library.ui;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import fanjh.mine.library.R;


/**
* @author fanjh
* @date 2017/12/22 14:00
* @description 预览标题视图
**/
public class PreviewTitleView extends FrameLayout {
    public static final int MODE_COMMON = 1;
    public static final int MODE_SELECT = 2;
    private View commonTitle;
    private View selectTitle;
    private TextView commonCountView;
    private TextView selectCountView;
    private ImageView ivSelectView;
    private ImageView ivBackView;
    private int mode;
    private int index;
    private int sum;
    private OnImageSelectListener onImageSelectListener;
    private OnClickListener onBackListener;

    public void setOnBackListener(OnClickListener onBackListener) {
        this.onBackListener = onBackListener;
    }

    public void setOnImageSelectListener(OnImageSelectListener onImageSelectListener) {
        this.onImageSelectListener = onImageSelectListener;
    }

    public interface OnImageSelectListener{
        boolean onSelect(boolean newSelected);
    }

    public PreviewTitleView(@NonNull Context context) {
        this(context,null);
    }

    public PreviewTitleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreviewTitleView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void changeSelected(boolean isSelect){
        if(null != ivSelectView){
            ivSelectView.setSelected(isSelect);
        }
    }

    public void changeMode(int mode){
        if(this.mode == mode){
            return;
        }
        this.mode = mode;
        switch (mode){
            case MODE_COMMON:
                if(null != selectTitle){
                    removeView(selectTitle);
                }
                createCommonView();
                addView(commonTitle);
                break;
            case MODE_SELECT:
                if(null != commonTitle){
                    removeView(commonTitle);
                }
                createSelectView();
                addView(selectTitle);
                break;
            default:
                break;
        }
    }

    public void initCount(int index,int sum){
        this.index = index;
        this.sum = sum;
        setTitle();
    }

    public void changeIndex(int index){
        this.index = index;
        setTitle();
    }

    private void setTitle(){
        if(null != selectCountView){
            selectCountView.setText(getContext().getString(R.string.picture_preview_count,index,sum));
        }
        if(null != commonCountView){
            commonCountView.setText(getContext().getString(R.string.picture_preview_count,index,sum));
        }
    }

    private void createCommonView(){
        if(null == commonTitle) {
            commonTitle = LayoutInflater.from(getContext()).inflate(R.layout.view_preview_common_title, this, false);
            commonCountView = (TextView) commonTitle;
        }
    }

    private void createSelectView(){
        if(null == selectTitle) {
            selectTitle = LayoutInflater.from(getContext()).inflate(R.layout.view_preview_select_title, this, false);
            selectCountView = (TextView) selectTitle.findViewById(R.id.tv_title);
            ivSelectView = (ImageView) selectTitle.findViewById(R.id.iv_select);
            ivBackView = (ImageView) selectTitle.findViewById(R.id.iv_back);
            ivBackView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null != onBackListener){
                        onBackListener.onClick(v);
                    }
                }
            });
            ivSelectView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isSelected = ivSelectView.isSelected();
                    boolean shouldChange = true;
                    if(null != onImageSelectListener){
                        shouldChange = !onImageSelectListener.onSelect(!isSelected);
                    }
                    if(shouldChange) {
                        ivSelectView.setSelected(!isSelected);
                    }
                }
            });
        }
    }

}
