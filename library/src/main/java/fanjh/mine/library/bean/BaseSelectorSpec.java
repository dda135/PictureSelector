package fanjh.mine.library.bean;

import android.support.annotation.IntDef;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
* @author fanjh
* @date 2017/12/27 10:56
* @description 选择方案基类
**/
public abstract class BaseSelectorSpec<T> implements Serializable {
    private static final long serialVersionUID = 7417904866724442663L;
    public static final int CODE_SUCCESS = 1;
    public static final int CODE_FAIL_MAX_COUNT = 2;
    public static final int CODE_FAIL_GIF_SIZE = 3;
    public static final int CODE_FAIL_EMPTY_IMAGE = 4;

    @IntDef({CODE_SUCCESS,CODE_FAIL_GIF_SIZE,CODE_FAIL_MAX_COUNT,CODE_FAIL_EMPTY_IMAGE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SelectState{}

    private int maxSelectedCount;

    public BaseSelectorSpec(int maxSelectedCount) {
        this.maxSelectedCount = maxSelectedCount;
    }

    /**
     * 当前是否选中
     * @param position 当前item位置,0...n-1
     * @param item 当前item
     * @return true表示选中，false没选中
     */
    public abstract boolean isSelected(int position,T item);

    /**
     * 改变当前item的选中状态
     * @param position 当前item的位置,0...n-1
     * @param isSelected 当前item新的选中状态
     * @param item 当前item
     * @return true表示当前能够修改选中状态，否则不允许修改
     */
    @SelectState
    public abstract int changeSelect(int position,boolean isSelected,T item);

    /**
     * 获得当前选中的item总数
     * @return 选中的总数
     */
    public abstract int getSelectedCount();

    /**
     * 按选中顺序返回当前所有选中的图片的地址列表
     * @return 选中图片的地址列表
     */
    public abstract ArrayList<String> getSelectedImageUrl();

    /**
     * 按选中顺序返回当前所有选中的与图片相关的实体类
     * 因为一个对象中，可能图片地址只是其中的一个属性
     * @return 实与图片有关的体类列表
     */
    public abstract ArrayList<T> getSelectedPicture();

    public int getMaxSelectedCount() {
        return maxSelectedCount;
    }
}
