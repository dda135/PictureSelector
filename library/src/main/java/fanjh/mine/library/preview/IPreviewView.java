package fanjh.mine.library.preview;

import android.support.v4.view.PagerAdapter;

import java.util.List;

import fanjh.mine.library.IView;


/**
* @author fanjh
* @date 2017/12/21 10:20
* @description 预览抽象视图
**/
public interface IPreviewView extends IView {
    /**
     * 展示可选择的预览视图
     * @param adapter 当前ViewPager要关联的适配器
     * @param index 初始选择位置
     */
    void showSelectPager(PagerAdapter adapter, int index);

    /**
     * 纯粹的展示图片
     * @param uris 图片地址列表
     * @param index 初始选择位置
     */
    void showCommonPager(List<String> uris, int index);

    /**
     * 选中状态变化时调用
     * @param selectCount 当前被选择的图片总数
     */
    void selectStateChanged(int selectCount);

    /**
     * 是否允许发送图片
     * @param canSend
     */
    void enabledSendPicture(boolean canSend);
}
