package fanjh.mine.library.preview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.request.RequestOptions;

import fanjh.mine.library.R;
import fanjh.mine.library.photoview.PhotoView;
import fanjh.mine.library.photoview.PhotoViewAttacher;

/**
 * @author fanjh
 * @date 2017/12/20 17:05
 * @description 图片预览碎片
 * @note
 **/
public class ImagePreviewFragment extends Fragment {
    public static final String EXTRA_URI = "uri";
    public static final String EXTRA_SHOULD_CACHE = "cache";

    private PhotoView pvImage;

    private View view;
    private String uri;
    private boolean shouldCache;
    private InteractiveListener interactiveListener;
    private boolean isVisible;

    public interface InteractiveListener{
        /**
         * 图片长点击事件
         * @param view 当前点击的视图
         * @param uri 当前图片地址
         * @param shouldCache 当前图片是否允许硬盘缓存
         */
        void onLongClick(View view, String uri, boolean shouldCache);
        /**
         * 图片点击事件
         * @param view 当前点击的视图
         * @param uri 当前图片地址
         * @param shouldCache 当前图片是否允许硬盘缓存
         */
        void onClick(View view, String uri, boolean shouldCache);
        /**
         * 当图片处于标准状态时的快速滑动事件
         * @param view 当前视图
         */
        void onFling(View view);
    }

    public static ImagePreviewFragment newInstance(String uri, boolean shouldCache) {
        ImagePreviewFragment fragment = new ImagePreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_URI, uri);
        bundle.putBoolean(EXTRA_SHOULD_CACHE,shouldCache);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        interactiveListener = (InteractiveListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        uri = bundle.getString(EXTRA_URI);
        shouldCache = bundle.getBoolean(EXTRA_SHOULD_CACHE,true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == view) {
            view = inflater.inflate(R.layout.fragment_picture_preiew, container, false);
            pvImage = view.findViewById(R.id.pv_image);
        }
        loadImage();
        pvImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(null != interactiveListener){
                    interactiveListener.onLongClick(v,uri,shouldCache);
                    return true;
                }
                return false;
            }
        });
        pvImage.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if(null != interactiveListener){
                    interactiveListener.onClick(view,uri,shouldCache);
                }
            }
        });
        pvImage.setFlingListener(new PhotoViewAttacher.OnFlingListener() {
            @Override
            public void onFling() {
                if(null != interactiveListener){
                    interactiveListener.onFling(pvImage);
                }
            }
        });
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
    }

    private void loadImage(){
        Glide.with(getContext()).
                load(uri).
                apply(RequestOptions.
                        errorOf(R.drawable.ic_picture_failed).
                        fallback(R.drawable.ic_picture_failed).
                        placeholder(R.drawable.ic_picture_failed).
                        downsample(DownsampleStrategy.CENTER_INSIDE).
                        diskCacheStrategy(DiskCacheStrategy.RESOURCE).
                        priority(isVisible? Priority.HIGH:Priority.LOW).
                        override(getResources().getDisplayMetrics().widthPixels,getResources().getDisplayMetrics().heightPixels).
                        centerInside()).
                into(pvImage);
    }

}
