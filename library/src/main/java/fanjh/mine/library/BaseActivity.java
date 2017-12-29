package fanjh.mine.library;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

/**
* @author fanjh
* @date 2017/12/29 11:22
* @description activity基类
* @note
**/
public class BaseActivity extends FragmentActivity {
    protected Context mContext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }
}
