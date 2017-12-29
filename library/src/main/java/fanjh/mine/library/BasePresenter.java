package fanjh.mine.library;

import java.lang.ref.WeakReference;

/**
* @author fanjh
* @date 2017/8/10 14:34
* @description 控制器基类
* @note
**/
public class BasePresenter<V extends IView> implements IPresenter<V> {
    protected WeakReference<V> mRef;

    @Override
    public void attachView(V view) {
        if(null == mRef){
            mRef = new WeakReference<V>(view);
        }
        view.initView();
    }

    public void onlyAttachView(V view){
        if(null == mRef){
            mRef = new WeakReference<V>(view);
        }
    }

    @Override
    public V getView() {
        return mRef.get();
    }

}
