package fanjh.mine.library.preview;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.ArrayList;

import fanjh.mine.library.R;
import fanjh.mine.library.bean.BaseSelectorSpec;
import fanjh.mine.library.bean.Picture;
import fanjh.mine.library.loader.LocalPictureLoader;
import fanjh.mine.library.loader.PictureMediaCursorParser;
import fanjh.mine.library.utils.Logger;


/**
 * @author fanjh
 * @date 2017/12/21 11:10
 * @description 图片选择器预览调度器
 * @note 目前支持两种模式
 * 1.预览模式，因为可选择图片少，这种可以支持传递图片列表
 * 2.全部选择模式，因为图片可能大量，所以需要重新读取数据
 **/
public class PictureSelectorPreviewPresenter extends BasePreviewPresenter implements LocalPictureLoader.Callback<Picture> {
    public static final String EXTRA_INIT_POSITION = "0";
    public static final String EXTRA_ALBUM_ID = "1";
    public static final String EXTRA_ALL_PICTURES = "2";
    public static final String EXTRA_SELECT_SPEC = "3";
    public static final String EXTRA_MAX_SELECT_COUNT = "4";
    private int initPosition;
    private String albumID;
    private LocalPictureLoader localPictureLoader;
    private FragmentActivity activity;
    private ArrayList<Picture> allPictures;
    private int maxSelectCount;
    private BaseSelectorSpec<Picture> selectorSpec;

    public PictureSelectorPreviewPresenter(FragmentActivity activity) {
        this.activity = activity;
        localPictureLoader = new LocalPictureLoader<Picture>(activity, this, new PictureMediaCursorParser());
    }

    @Override
    public void receiverIntent(Intent intent) {
        initPosition = intent.getIntExtra(EXTRA_INIT_POSITION, -1);
        albumID = intent.getStringExtra(EXTRA_ALBUM_ID);
        selectorSpec = (BaseSelectorSpec<Picture>) intent.getSerializableExtra(EXTRA_SELECT_SPEC);
        allPictures = (ArrayList<Picture>) intent.getSerializableExtra(EXTRA_ALL_PICTURES);
        maxSelectCount = intent.getIntExtra(EXTRA_MAX_SELECT_COUNT,1);
    }

    @Override
    public void loadData() {
        if(null == getView()){
            return;
        }
        if(null != allPictures){
            getView().showSelectPager(createPagerAdapter(), initPosition);
        }else {
            localPictureLoader.start(albumID, 0,0);
        }
        getView().enabledSendPicture(maxSelectCount > 0);
    }

    @Override
    public boolean onSelect(boolean newSelected, int index) {
        if(newSelected && selectorSpec.getSelectedCount() >= maxSelectCount){
            Logger.showToastLong(activity, activity.getString(R.string.max_select_picture_hint, maxSelectCount));
            return true;
        }
        Picture picture = allPictures.get(index);
        selectorSpec.changeSelect(index,newSelected,picture);
        if(null != getView()){
            getView().selectStateChanged(selectorSpec.getSelectedCount());
        }
        return false;
    }

    @Override
    public boolean isSelected(int index) {
        return selectorSpec.isSelected(index,allPictures.get(index));
    }

    @Override
    public void back() {
        Intent intent = new Intent();
        intent.putExtra(PicturePreviewActivity.EXTRA_SELECT_SPEC,selectorSpec);
        intent.putExtra(PicturePreviewActivity.EXTRA_MODE,PicturePreviewActivity.MODE_BACK_NORMAL);
        activity.setResult(Activity.RESULT_OK,intent);
        activity.finish();
    }

    @Override
    public void send(int position) {
        int selectCount = selectorSpec.getSelectedCount();
        Intent intent = new Intent();
        if(selectCount == 0){
            selectorSpec.changeSelect(position,true,allPictures.get(position));
        }
        intent.putExtra(PicturePreviewActivity.EXTRA_SELECT_SPEC,selectorSpec);
        intent.putExtra(PicturePreviewActivity.EXTRA_MODE,PicturePreviewActivity.MODE_BACK_SEND);
        activity.setResult(Activity.RESULT_OK,intent);
        activity.finish();
    }

    @Override
    public int getSelectedCount() {
        return selectorSpec.getSelectedCount();
    }

    @Override
    public void destroy() {
        if(null != activity) {
            localPictureLoader.destroy();
        }
    }

    @Override
    public void onPictureReady(ArrayList<Picture> pictures) {
        if (null != getView()) {
            allPictures = pictures;
            getView().showSelectPager(createPagerAdapter(), initPosition);
        }
    }

    private PagerAdapter createPagerAdapter(){
        return new FragmentStatePagerAdapter(activity.getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return ImagePreviewFragment.newInstance(allPictures.get(position).uri,false);
            }

            @Override
            public int getCount() {
                return allPictures.size();
            }
        };
    }

}
