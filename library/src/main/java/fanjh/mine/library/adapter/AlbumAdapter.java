package fanjh.mine.library.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import fanjh.mine.library.R;
import fanjh.mine.library.bean.Album;


/**
 * @author fanjh
 * @date 2017/12/20 10:34
 * @description 相册item适配器
 **/
public class AlbumAdapter extends RecyclerView.Adapter {
    private List<Album> albums;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AlbumHolder(LayoutInflater.from(context).inflate(R.layout.item_album, parent, false));
    }

    public AlbumAdapter(Context context) {
        this.context = context;
    }

    public void update(List<Album> albums){
        this.albums = albums;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AlbumHolder albumHolder = (AlbumHolder) holder;
        final Album album = albums.get(position);
        Glide.with(context).asBitmap().
                load(album.uri).
                apply(RequestOptions.errorOf(R.drawable.ic_picture_failed).
                        fallback(R.drawable.ic_picture_failed)).
                into(albumHolder.ivAlbum);
        albumHolder.tvAlbumName.setText(TextUtils.isEmpty(album.displayName)? "":album.displayName);
        albumHolder.tvAlbumPictureCount.setText(String.valueOf(album.count));
        if (null != onItemClickListener) {
            albumHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(v, album);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return null != albums?albums.size():0;
    }

    public interface OnItemClickListener {
        void onClick(View view, Album album);
    }

    static class AlbumHolder extends RecyclerView.ViewHolder {
        ImageView ivAlbum;
        TextView tvAlbumName;
        TextView tvAlbumPictureCount;

        AlbumHolder(View view) {
            super(view);
            ivAlbum = view.findViewById(R.id.iv_album);
            tvAlbumName = view.findViewById(R.id.tv_album_name);
            tvAlbumPictureCount = view.findViewById(R.id.tv_album_picture_count);
        }
    }
}
