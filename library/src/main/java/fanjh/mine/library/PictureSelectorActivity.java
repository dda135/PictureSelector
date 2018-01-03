package fanjh.mine.library;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fanjh.mine.library.adapter.AlbumAdapter;
import fanjh.mine.library.adapter.BasePictureAdapter;
import fanjh.mine.library.adapter.PictureSelectorAdapter;
import fanjh.mine.library.bean.Album;
import fanjh.mine.library.bean.BaseSelectorSpec;
import fanjh.mine.library.bean.Picture;
import fanjh.mine.library.bean.PictureSelectorSpec;
import fanjh.mine.library.loader.LocalAlbumLoader;
import fanjh.mine.library.loader.LocalPictureLoader;
import fanjh.mine.library.loader.PictureMediaCursorParser;
import fanjh.mine.library.preview.PicturePreviewActivity;
import fanjh.mine.library.ui.MyButton;

/**
 * @author fanjh
 * @date 2017/12/20 11:28
 * @description 图片选择页面
 * @note
 **/
public class PictureSelectorActivity extends BaseActivity implements LocalAlbumLoader.Callback,
        LocalPictureLoader.Callback<Picture>, BasePictureAdapter.OnPictureSelectCallback, AlbumAdapter.OnItemClickListener,
        BasePictureAdapter.OnPictureClickListener {
    public static final int MAX_INTENT_COUNT = 500;
    private static final int PAGE_COUNT = 100;
    private static final String SAVE_ALBUM_ID = "album_id";
    public static final String EXTRA_SELECT_COUNT = "count";
    public static final String EXTRA_SELECT_PICTURES = "extra_pictures";
    private static final String SAVE_SELECT_SPEC = "select_spec";
    public static final int SPAN_COUNT = 3;
    public static final int PAGE_ALBUM = 0;
    public static final int PAGE_PICTURE = 1;
    public static final int REQUEST_CODE_FROM_PREVIEW = 1;
    TextView btnTopBack;
    TextView tvSelectAlbum;
    TextView tvPreview;
    MyButton tvFinish;
    ViewPager vpContent;
    TextView tvCancel;

    RecyclerView rvPicture;
    RecyclerView rvAlbum;
    RelativeLayout rlFinishLayout;

    private LocalPictureLoader localPictureLoader;
    private LocalAlbumLoader localAlbumLoader;
    private AlbumAdapter albumAdapter;
    private PictureSelectorAdapter basePictureAdapter;

    private int maxSelectCount;
    private String currentAlbumID;
    private boolean hasPermission;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_selector);

        int result = PermissionChecker.checkSelfPermission(mContext,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        switch (result){
            case PermissionChecker.PERMISSION_DENIED:
                Toast.makeText(getApplicationContext(),"请校验权限之后再启动！",Toast.LENGTH_LONG).show();
                break;
            case PermissionChecker.PERMISSION_GRANTED:
                hasPermission = true;
                break;
            default:
                Toast.makeText(getApplicationContext(),"请打开存储权限！",Toast.LENGTH_LONG).show();
                break;
        }

        btnTopBack = findViewById(R.id.btn_top_back);
        btnTopBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vpContent.setCurrentItem(0, true);
            }
        });
        tvSelectAlbum = findViewById(R.id.tv_select_album);
        tvPreview = findViewById(R.id.tv_preview);
        tvPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PicturePreviewActivity.pictureSelectorStart((Activity) mContext, basePictureAdapter.getSelectedPicture(), 0, basePictureAdapter.getSelectorSpec(),
                        maxSelectCount, REQUEST_CODE_FROM_PREVIEW);
            }
        });
        tvFinish = findViewById(R.id.tv_finish);
        tvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_SELECT_PICTURES, basePictureAdapter.getSelectedImageUrl());
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(0, R.anim.activity_picture_selector_out);
            }
        });
        tvCancel = findViewById(R.id.btn_top_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        vpContent = findViewById(R.id.vp_content);
        rlFinishLayout = findViewById(R.id.rl_finish_layout);

        localAlbumLoader = new LocalAlbumLoader(this, this);
        localPictureLoader = new LocalPictureLoader<Picture>(this, this, new PictureMediaCursorParser());

        BaseSelectorSpec<Picture> selectorSpec = null;
        if (null != savedInstanceState) {
            currentAlbumID = savedInstanceState.getString(SAVE_ALBUM_ID);
            selectorSpec = (BaseSelectorSpec<Picture>) savedInstanceState.getSerializable(SAVE_SELECT_SPEC);
        }
        maxSelectCount = getIntent().getIntExtra(EXTRA_SELECT_COUNT, 1);

        initPictureView(selectorSpec);
        initAlbumView();

        changeSelectState(null == selectorSpec?0:selectorSpec.getSelectedCount());

        vpContent.setAdapter(new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = null;
                switch (position) {
                    case PAGE_ALBUM:
                        view = rvAlbum;
                        break;
                    case PAGE_PICTURE:
                        view = rvPicture;
                        break;
                    default:
                        break;
                }
                container.removeView(view);
                container.addView(view);
                return view;
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                super.destroyItem(container, position, object);
                container.removeView((View) object);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
        vpContent.setCurrentItem(1);
        vpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case PAGE_ALBUM:
                        tvSelectAlbum.setText(getString(R.string.album));
                        btnTopBack.setVisibility(View.GONE);
                        rlFinishLayout.setVisibility(View.GONE);
                        break;
                    case PAGE_PICTURE:
                        rlFinishLayout.setVisibility(View.VISIBLE);
                        btnTopBack.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if(hasPermission) {
            localPictureLoader.start(currentAlbumID, 0, PAGE_COUNT);
            localAlbumLoader.start(getLoaderManager());
        }
    }

    private void initPictureView(BaseSelectorSpec<Picture> selectorSpec) {
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,1,getResources().getDisplayMetrics());
        int total = getResources().getDisplayMetrics().widthPixels;
        int imageSize = (total - (SPAN_COUNT + 1) * padding) / SPAN_COUNT;
        basePictureAdapter = new PictureSelectorAdapter(mContext, imageSize, maxSelectCount);
        if(null != selectorSpec){
            basePictureAdapter.setSelectorSpec(selectorSpec);
            changeSelectState(basePictureAdapter.getSelectedCount());
        }
        basePictureAdapter.setOnPictureClickListener(this);
        basePictureAdapter.setOnPictureSelectCallback(this);
        rvPicture = new RecyclerView(mContext);
        rvPicture.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rvPicture.setBackgroundColor(getResources().getColor(R.color.consultation_bg));
        rvPicture.setLayoutManager(new GridLayoutManager(mContext, SPAN_COUNT));
        rvPicture.addItemDecoration(new PictureItemDecoration(padding, SPAN_COUNT));
        rvPicture.setHasFixedSize(true);
        rvPicture.setAdapter(basePictureAdapter);
    }

    private void initAlbumView() {
        albumAdapter = new AlbumAdapter(mContext);
        albumAdapter.setOnItemClickListener(this);

        rvAlbum = new RecyclerView(mContext);
        rvAlbum.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rvAlbum.setBackgroundColor(getResources().getColor(R.color.white));
        rvAlbum.setLayoutManager(new LinearLayoutManager(mContext));
        rvAlbum.setAdapter(albumAdapter);

    }


    @Override
    public void onAlbumReady(List<Album> albums) {
        albumAdapter.update(albums);
    }

    @Override
    public void onPictureReady(ArrayList<Picture> pictures) {
        basePictureAdapter.add(pictures);
        if(pictures.size() == PAGE_COUNT){
            localPictureLoader.start(currentAlbumID , pictures.get(pictures.size()-1)._id, PAGE_COUNT);
        }
    }

    @Override
    public void onReset() {

    }


    private void back() {
        if (vpContent.getCurrentItem() == 0) {
            vpContent.setCurrentItem(1, true);
            return;
        }
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(0, R.anim.activity_picture_selector_out);
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
    public void onCall(int selectCount) {
        changeSelectState(selectCount);
    }

    private void changeSelectState(int selectCount){
        if (selectCount > 0) {
            tvPreview.setEnabled(true);
            tvFinish.setEnabled(true);
            tvFinish.setText(getString(R.string.preview_finish, "(" + selectCount + ")"), -1, 0);
        } else {
            tvPreview.setEnabled(false);
            tvFinish.setEnabled(false);
            tvFinish.setText(getString(R.string.preview_finish, ""), -1, 0);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != localPictureLoader && null != localPictureLoader.getCurrentAlbumID()) {
            outState.putSerializable(SAVE_ALBUM_ID, localPictureLoader.getCurrentAlbumID());
        }
        if(null != basePictureAdapter.getSelectorSpec()){
            outState.putSerializable(SAVE_SELECT_SPEC,basePictureAdapter.getSelectorSpec());
        }
    }

    @Override
    public void onClick(View view, Album album) {
        currentAlbumID = album.bucketID;
        basePictureAdapter.clear();
        localPictureLoader.start(currentAlbumID, 0, PAGE_COUNT);
        vpContent.setCurrentItem(1, true);
        tvSelectAlbum.setText(null != album.displayName ? album.displayName : getString(R.string.unknown));
    }

    @Override
    public void onClick(View view, int position, String picture) {
        ArrayList<Picture> pictures = basePictureAdapter.getAllPictures();
        if(pictures.size() < MAX_INTENT_COUNT){
            PicturePreviewActivity.pictureSelectorStart((Activity) mContext, pictures, position, basePictureAdapter.getSelectorSpec(),
                    maxSelectCount, REQUEST_CODE_FROM_PREVIEW);
        }else {
            PicturePreviewActivity.pictureSelectorStart((Activity) mContext, localPictureLoader.getCurrentAlbumID(),
                    position, basePictureAdapter.getSelectorSpec(), maxSelectCount, REQUEST_CODE_FROM_PREVIEW);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_CODE_FROM_PREVIEW:
                    int mode = data.getIntExtra(PicturePreviewActivity.EXTRA_MODE,PicturePreviewActivity.MODE_BACK_NORMAL);
                    PictureSelectorSpec newSelectSpec = (PictureSelectorSpec) data.getSerializableExtra(PicturePreviewActivity.EXTRA_SELECT_SPEC);
                    switch (mode){
                        case PicturePreviewActivity.MODE_BACK_NORMAL:
                            basePictureAdapter.setSelectorSpec(newSelectSpec);
                            changeSelectState(newSelectSpec.getSelectedCount());
                            break;
                        default:
                            Intent intent = new Intent();
                            intent.putExtra(EXTRA_SELECT_PICTURES, newSelectSpec.getSelectedImageUrl());
                            setResult(RESULT_OK, intent);
                            finish();
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localPictureLoader.destroy();
        localAlbumLoader.destroy(getLoaderManager());
        localPictureLoader = null;
        localAlbumLoader = null;
    }

    public static void start(Activity activity, int selectCount, int requestCode) {
        Intent intent = new Intent(activity, PictureSelectorActivity.class);
        intent.putExtra(EXTRA_SELECT_COUNT, selectCount);
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.activity_picture_selector_in,0);
    }

}
