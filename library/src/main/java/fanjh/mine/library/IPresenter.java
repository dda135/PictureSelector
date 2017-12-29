package fanjh.mine.library;

/**
* @author fanjh
* @date 2017/8/10 14:33
* @description 控制器基础接口
* @note
**/
public interface IPresenter<V extends IView> {
    void attachView(V view);
    V getView();
}
