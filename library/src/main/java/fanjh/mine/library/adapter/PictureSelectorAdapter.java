package fanjh.mine.library.adapter;

import android.content.Context;

import fanjh.mine.library.bean.BaseSelectorSpec;
import fanjh.mine.library.bean.Picture;
import fanjh.mine.library.bean.PictureSelectorSpec;


/**
 * @author fanjh
 * @date 2017/12/27 9:59
 * @description 图片选择器适配器
 **/
public class PictureSelectorAdapter extends BasePictureAdapter<Picture> {

    public PictureSelectorAdapter(Context context, int imageSize, int maxSelectedCount) {
        super(context, imageSize, maxSelectedCount);
    }

    @Override
    public BaseSelectorSpec<Picture> createSelectorSpec(int maxSelectCount) {
        return new PictureSelectorSpec(maxSelectCount);
    }

}
