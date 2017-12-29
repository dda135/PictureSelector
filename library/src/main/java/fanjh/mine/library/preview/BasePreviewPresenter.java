package fanjh.mine.library.preview;

import android.content.Intent;

import fanjh.mine.library.BasePresenter;


/**
* @author fanjh
* @date 2017/12/21 10:29
* @description 预览页调度器基类
**/
public abstract class BasePreviewPresenter extends BasePresenter<IPreviewView> {
    /**
     * 处理之前页面传递过来的数据
     * @param intent 用于获取数据
     */
    public abstract void receiverIntent(Intent intent);

    /**
     * 加载当前预览页要显示的数据
     */
    public abstract void loadData();

    /**
     * 当前位置的图片选中状态变化，此方法可用于拦截状态变化
     * @param newSelected 当前位置图片新的选中状态
     * @param index 位置
     * @return true表示拦截当前选中状态，即当前选中状态变化无效
     */
    public abstract boolean onSelect(boolean newSelected,int index);

    /**
     * 当前位置的图片是否选中
     * @param index 位置
     * @return 当前位置的图片是否选中
     */
    public abstract boolean isSelected(int index);

    /**
     * 返回到上一个页面
     */
    public abstract void back();

    /**
     * 图片选择完成，发送给指定对象
     * @param position 当前点击发送时的位置
     */
    public abstract void send(int position);

    /**
     * 获取当前选中图片的总数
     * @return 当前选中图片的总数
     */
    public abstract int getSelectedCount();

    /**
     * 页面销毁
     */
    public abstract void destroy();
}
