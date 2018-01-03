package fanjh.mine.library.preview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.List;

/**
* @author fanjh
* @date 2017/12/21 10:23
* @description 普通的预览,，没有选择功能，一般用于小数据量的操作
* @note intent一次性携带数据不可过多，所以这个用于小数据量操作
**/
public class CommonPreviewPresenter extends BasePreviewPresenter{
    public static final String EXTRA_PICTURES = "pictures";
    public static final String EXTRA_INIT_INDEX = "index";
    private List<String> pictures;
    private int index;
    private Activity context;

    public CommonPreviewPresenter(Activity context) {
        this.context = context;
    }

    @Override
    public void receiverIntent(Intent intent, Bundle bundle) {
        pictures = (List<String>) intent.getSerializableExtra(EXTRA_PICTURES);
        if(null == bundle) {
            index = intent.getIntExtra(EXTRA_INIT_INDEX, -1);
        } else {
            index = bundle.getInt(EXTRA_INIT_INDEX,-1);
        }
    }

    @Override
    public void loadData() {
        if(null != getView()) {
            getView().showCommonPager(pictures, index);
        }
    }

    @Override
    public boolean onSelect(boolean newSelected, int index) {
        return false;
    }

    @Override
    public boolean isSelected(int index) {
        return false;
    }

    @Override
    public void back() {
        context.finish();
    }

    @Override
    public void send(int position) {
        context.finish();
    }

    @Override
    public int getSelectedCount() {
        return 0;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void saveInstance(Bundle bundle,int nowIndex) {
        bundle.putInt(EXTRA_INIT_INDEX,nowIndex < 0?index:nowIndex);
    }

}
