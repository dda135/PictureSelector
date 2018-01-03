package fanjh.mine.library.preview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import fanjh.mine.library.BaseActivity;
import fanjh.mine.library.R;
import fanjh.mine.library.bean.BaseSelectorSpec;
import fanjh.mine.library.bean.Picture;
import fanjh.mine.library.ui.MyButton;
import fanjh.mine.library.ui.PreviewOptionWindows;
import fanjh.mine.library.ui.PreviewTitleView;
import fanjh.mine.library.utils.AlbumDownLoader;
import fanjh.mine.library.utils.Logger;


/**
 * @author fanjh
 * @date 2017/12/20 16:54
 * @description 图片预览页面
 **/
public class PicturePreviewActivity extends BaseActivity implements IPreviewView,
        AlbumDownLoader.OnDownloadPictureToDCIMCallback, PreviewTitleView.OnImageSelectListener,
        ImagePreviewFragment.InteractiveListener {
    public static final String EXTRA_MODE = "mode";
    public static final int ERROR_COUNT = 1000;
    public static final int MODE_COMMON = 0;
    public static final int MODE_PICTURE_SELECTOR = 1;

    public static final String EXTRA_SELECT_SPEC = "0";
    public static final int MODE_BACK_SEND = 1;
    public static final int MODE_BACK_NORMAL = 2;
    ViewPager vpContent;
    ImageView ivDownload;
    MyButton tvFinish;
    FrameLayout flSelectBottomLayout;
    PreviewTitleView ptvTitle;
    FrameLayout flParentLayout;

    private BasePreviewPresenter presenter;
    private int mode;
    private AlbumDownLoader albumDownLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_preview);
        albumDownLoader = new AlbumDownLoader(mContext);
        vpContent = findViewById(R.id.vp_content);
        ivDownload = findViewById(R.id.iv_download);
        tvFinish = findViewById(R.id.tv_finish);
        flSelectBottomLayout = findViewById(R.id.fl_select_bottom_layout);
        ptvTitle = findViewById(R.id.ptv_title);
        flParentLayout = findViewById(R.id.fl_parent_layout);

        presenter = initPresenter(getIntent());
        presenter.attachView(this);
        presenter.receiverIntent(getIntent(),savedInstanceState);
        presenter.loadData();
    }

    private BasePreviewPresenter initPresenter(Intent intent) {
        mode = intent.getIntExtra(EXTRA_MODE, MODE_COMMON);
        switch (mode) {
            case MODE_COMMON:
                return new CommonPreviewPresenter(this);
            case MODE_PICTURE_SELECTOR:
                return new PictureSelectorPreviewPresenter(this);
            default:
                throw new IllegalArgumentException("不识别的预览模式！");
        }
    }

    @Override
    public void initView() {

    }

    @Override
    public void resumeView() {

    }

    @Override
    public void destroyView() {

    }

    @Override
    public void onDownloadSuccess(String filePath) {
        Logger.showToastLong(mContext, getString(R.string.image_download_success));
        ivDownload.setVisibility(View.GONE);
    }

    @Override
    public void onDownloadError() {
        Logger.showToastLong(mContext, getString(R.string.image_download_fail));
    }

    @Override
    public boolean onSelect(boolean newSelected) {
        return presenter.onSelect(newSelected, vpContent.getCurrentItem());
    }

    private void initPager(PagerAdapter pagerAdapter, int index, final boolean canSelector) {
        vpContent.setAdapter(pagerAdapter);
        if (index != -1) {
            vpContent.setCurrentItem(index);
        }
        vpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ptvTitle.changeIndex(position + 1);
                if (!canSelector) {
                    ivDownload.setVisibility(View.VISIBLE);
                } else {
                    ptvTitle.changeSelected(presenter.isSelected(position));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void showSelectPager(PagerAdapter adapter, int index) {
        initPager(adapter, index, true);
        ptvTitle.changeMode(PreviewTitleView.MODE_SELECT);
        ptvTitle.setOnImageSelectListener(this);
        ptvTitle.setOnBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        tvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.send(vpContent.getCurrentItem());
                overridePendingTransition(0, R.anim.activity_picture_selector_out);
            }
        });
        ptvTitle.initCount(index + 1, adapter.getCount());
        ptvTitle.changeSelected(presenter.isSelected(index));
        selectStateChanged(presenter.getSelectedCount());
        ptvTitle.setVisibility(View.VISIBLE);
        flSelectBottomLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showCommonPager(final List<String> pictures, int index) {
        initPager(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return ImagePreviewFragment.newInstance(pictures.get(position), true);
            }

            @Override
            public int getCount() {
                return pictures.size();
            }
        }, index, false);
        ptvTitle.changeMode(PreviewTitleView.MODE_COMMON);
        ivDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumDownLoader.downLoadPictureToDCIM(pictures.get(vpContent.getCurrentItem()), PicturePreviewActivity.this);
            }
        });
        ptvTitle.initCount(index + 1, pictures.size());
        ptvTitle.setVisibility(View.VISIBLE);
        ivDownload.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            back();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void selectStateChanged(int selectCount) {
        tvFinish.setText(selectCount > 0 ? getString(R.string.preview_finish, "(" + selectCount + ")") : getString(R.string.complete), -1, 0);
    }

    @Override
    public void enabledSendPicture(boolean canSend) {
        tvFinish.setEnabled(canSend);
    }

    private void back() {
        presenter.back();
        overridePendingTransition(0, R.anim.activity_picture_selector_out);
    }

    @Override
    public void onLongClick(View view, final String uri, boolean shouldCache) {
        switch (mode) {
            case MODE_COMMON:
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(200);
                new PreviewOptionWindows(this, flParentLayout, new PreviewOptionWindows.OnPopClickListener() {
                    @Override
                    public void onPopClick(PreviewOptionWindows photoWindows) {
                        albumDownLoader.downLoadPictureToDCIM(uri,PicturePreviewActivity.this);
                        photoWindows.dismiss();
                    }
                }, new PreviewOptionWindows.OnPopClickListener() {
                    @Override
                    public void onPopClick(PreviewOptionWindows photoWindows) {
                        photoWindows.dismiss();
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view, String uri, boolean shouldCache) {
        switch (mode) {
            case MODE_COMMON:
                finish();
                overridePendingTransition(0, R.anim.activity_picture_selector_out);
                break;
            default:
                break;
        }
    }

    @Override
    public void onFling(View view) {
        back();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(null != presenter){
            presenter.saveInstance(outState,vpContent.getCurrentItem());
        }
    }

    public static void start(Activity context, ArrayList<String> uris, int initIndex) {
        if (uris.size() > ERROR_COUNT) {
            throw new IllegalArgumentException("Intent单次不允许携带过多的数据，请使用其他方式实现！");
        }
        Intent intent = new Intent(context, PicturePreviewActivity.class);
        intent.putExtra(EXTRA_MODE, MODE_COMMON);
        intent.putExtra(CommonPreviewPresenter.EXTRA_PICTURES, uris);
        intent.putExtra(CommonPreviewPresenter.EXTRA_INIT_INDEX, initIndex);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.activity_picture_selector_in, 0);
    }

    public static void pictureSelectorStart(Activity activity, String albumID, int initPosition,
                                            BaseSelectorSpec selectorSpec, int maxSelectCount, int requestCode) {
        Intent intent = new Intent(activity, PicturePreviewActivity.class);
        intent.putExtra(EXTRA_MODE, MODE_PICTURE_SELECTOR);
        intent.putExtra(PictureSelectorPreviewPresenter.EXTRA_ALBUM_ID, albumID);
        intent.putExtra(PictureSelectorPreviewPresenter.EXTRA_INIT_POSITION, initPosition);
        intent.putExtra(PictureSelectorPreviewPresenter.EXTRA_SELECT_SPEC, selectorSpec);
        intent.putExtra(PictureSelectorPreviewPresenter.EXTRA_MAX_SELECT_COUNT, maxSelectCount);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void pictureSelectorStart(Activity activity, ArrayList<Picture> allPictures,
                                            int initPosition, BaseSelectorSpec selectorSpec,
                                            int maxSelectCount, int requestCode) {
        Intent intent = new Intent(activity, PicturePreviewActivity.class);
        intent.putExtra(EXTRA_MODE, MODE_PICTURE_SELECTOR);
        intent.putExtra(PictureSelectorPreviewPresenter.EXTRA_ALL_PICTURES, allPictures);
        intent.putExtra(PictureSelectorPreviewPresenter.EXTRA_INIT_POSITION, initPosition);
        intent.putExtra(PictureSelectorPreviewPresenter.EXTRA_SELECT_SPEC, selectorSpec);
        intent.putExtra(PictureSelectorPreviewPresenter.EXTRA_MAX_SELECT_COUNT, maxSelectCount);
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.activity_picture_selector_in, 0);
    }

}
