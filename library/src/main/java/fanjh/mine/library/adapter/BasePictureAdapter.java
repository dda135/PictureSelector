package fanjh.mine.library.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import fanjh.mine.library.R;
import fanjh.mine.library.bean.BasePicture;
import fanjh.mine.library.bean.BaseSelectorSpec;
import fanjh.mine.library.utils.Logger;


/**
 * @author fanjh
 * @date 2017/12/20 9:45
 * @description 照片适配器基类，封装选中等行为
 **/
public abstract class BasePictureAdapter<T extends BasePicture> extends RecyclerView.Adapter {
    private ArrayList<T> pictures = new ArrayList<>();
    protected Context context;
    private int imageSize;
    private OnPictureSelectCallback onPictureSelectCallback;
    private OnPictureClickListener onPictureClickListener;
    private BaseSelectorSpec<T> selectorSpec;

    public BasePictureAdapter(Context context, int imageSize, int maxSelectCount) {
        this.context = context;
        this.imageSize = imageSize;
        selectorSpec = createSelectorSpec(maxSelectCount);
    }

    public ArrayList<T> getAllPictures() {
        return pictures;
    }

    public void update(List<T> newItems) {
        if(null == newItems){
            return;
        }
        pictures.clear();
        pictures.addAll(newItems);
        notifyDataSetChanged();
    }

    public void add(List<T> items) {
        if(null == items){
            return;
        }
        pictures.addAll(items);
        notifyDataSetChanged();
    }

    public void clear() {
        pictures.clear();
        notifyDataSetChanged();
    }

    public void setOnPictureSelectCallback(OnPictureSelectCallback onPictureSelectCallback) {
        this.onPictureSelectCallback = onPictureSelectCallback;
    }

    public void setOnPictureClickListener(OnPictureClickListener onPictureClickListener) {
        this.onPictureClickListener = onPictureClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PictureHolder(LayoutInflater.from(context).inflate(R.layout.item_select_picture, parent, false), imageSize);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final PictureHolder pictureHolder = (PictureHolder) holder;
        final T picture = pictures.get(position);
        final boolean isSelected = selectorSpec.isSelected(position, picture);
        pictureHolder.ivSelect.setSelected(isSelected);
        pictureHolder.flSelectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newSelectedState = !pictureHolder.ivSelect.isSelected();
                @BaseSelectorSpec.SelectState
                int code = selectorSpec.changeSelect(position,newSelectedState,picture);
                switch (code) {
                    case BaseSelectorSpec.CODE_FAIL_EMPTY_IMAGE:
                        Logger.showToastLong(context, context.getString(R.string.select_picture_error_hint));
                        break;
                    case BaseSelectorSpec.CODE_FAIL_GIF_SIZE:
                        Logger.showToastLong(context, context.getString(R.string.gif_max_size_hint));
                        break;
                    case BaseSelectorSpec.CODE_FAIL_MAX_COUNT:
                        Logger.showToastLong(context, context.getString(R.string.max_select_picture_hint, selectorSpec.getMaxSelectedCount()));
                        break;
                    case BaseSelectorSpec.CODE_SUCCESS:
                        pictureHolder.ivSelect.setSelected(newSelectedState);
                        if (null != onPictureSelectCallback) {
                            onPictureSelectCallback.onCall(getSelectedCount());
                        }
                        break;
                    default:
                        Logger.showToastLong(context, context.getString(R.string.select_picture_error_hint));
                        break;
                }
            }
        });
        pictureHolder.ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPictureClickListener.onClick(v, position, picture.getImageUrl());
            }
        });
        Glide.with(context).load(picture.getImageUrl()).
                apply(RequestOptions.
                        diskCacheStrategyOf(DiskCacheStrategy.RESOURCE).
                        downsample(DownsampleStrategy.AT_LEAST).centerCrop().
                        override(imageSize, imageSize).fallback(R.drawable.ic_picture_failed).
                        error(R.drawable.ic_picture_failed).placeholder(R.drawable.ic_picture_failed)).
                into(pictureHolder.ivPicture);
    }

    @Override
    public int getItemCount() {
        return null != pictures ? pictures.size() : 0;
    }

    static class PictureHolder extends RecyclerView.ViewHolder {
        ImageView ivPicture;
        ImageView ivSelect;
        FrameLayout flSelectLayout;

        PictureHolder(View itemView, int imageSize) {
            super(itemView);
            ivPicture = itemView.findViewById(R.id.iv_picture);
            ivSelect = itemView.findViewById(R.id.iv_select);
            flSelectLayout = itemView.findViewById(R.id.fl_select_layout);
            ViewGroup.LayoutParams params = ivPicture.getLayoutParams();
            params.height = imageSize;
            params.width = imageSize;
            ivPicture.requestLayout();
        }
    }

    public interface OnPictureSelectCallback {
        void onCall(int selectCount);
    }

    public interface OnPictureClickListener {
        /**
         * 图片点击回调通知
         */
        void onClick(View view, int position, String picture);
    }

    public abstract BaseSelectorSpec<T> createSelectorSpec(int maxSelectCount);

    public BaseSelectorSpec<T> getSelectorSpec() {
        return selectorSpec;
    }

    public int getSelectedCount(){
        return selectorSpec.getSelectedCount();
    }

    public ArrayList<String> getSelectedImageUrl(){
        return selectorSpec.getSelectedImageUrl();
    }

    public ArrayList<T> getSelectedPicture(){
        return selectorSpec.getSelectedPicture();
    }

    public void setSelectorSpec(BaseSelectorSpec<T> selectorSpec) {
        this.selectorSpec = selectorSpec;
        notifyDataSetChanged();
    }
}
