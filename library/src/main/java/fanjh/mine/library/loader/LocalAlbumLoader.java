package fanjh.mine.library.loader;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import fanjh.mine.library.R;
import fanjh.mine.library.bean.Album;


/**
* @author fanjh
* @date 2017/12/19 18:03
* @description 本地媒体相册加载者
**/
public class LocalAlbumLoader implements LoaderManager.LoaderCallbacks<Cursor>{
    /**
     * 后台运行的阈值
     */
    private static final int BACKGROUND_LIMIT = 500;
    /**
     * Loader的ID
     */
    private static final int LOADER_ID = 10001;
    private WeakReference<Context> weak;
    private WeakReference<Callback> callbackWeak;

    public LocalAlbumLoader(Context context, Callback callback) {
        this.weak = new WeakReference<>(context);
        this.callbackWeak = new WeakReference<>(callback);
    }

    public void start(LoaderManager manager){
        if(null == manager){
            return;
        }
        try {
            manager.restartLoader(LOADER_ID, null, this);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void destroy(LoaderManager manager){
        if(null == manager){
            return;
        }
        try {
            manager.destroyLoader(LOADER_ID);
            weak.clear();
            callbackWeak.clear();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Context context = weak.get();
        if(null != context){
            return new LocalAlbumCursorLoader(context);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(null == data || null == weak.get()){
            return;
        }
        int count = data.getCount();
        if(count > BACKGROUND_LIMIT){
            new AsyncTask<Cursor, Integer, List<Album>>() {
                @Override
                protected List<Album> doInBackground(Cursor... params) {
                    return parseCursor(params[0]);
                }

                @Override
                protected void onPostExecute(List<Album> alba) {
                    super.onPostExecute(alba);
                    if(null != weak.get() && null != callbackWeak.get()){
                        callbackWeak.get().onAlbumReady(alba);
                    }
                }
            }.execute(data);
        }else if(null != callbackWeak.get()){
            callbackWeak.get().onAlbumReady(parseCursor(data));
        }
    }

    private List<Album> parseCursor(@NonNull Cursor cursor){
        List<Album> alba = new ArrayList<>();
        int totalCount = 0;
        while(cursor.moveToNext()){
            Album album = Album.parseFromMediaCursor(cursor);
            alba.add(album);
            totalCount += album.count;
        }
        Album allAlbum = new Album();
        allAlbum.displayName = weak.get().getString(R.string.all);
        allAlbum.count = totalCount;
        allAlbum.uri = alba.size() > 0?alba.get(0).uri:null;
        allAlbum.bucketID = null;
        alba.add(0,allAlbum);
        return alba;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(null != weak.get() && null != callbackWeak.get()){
            callbackWeak.get().onReset();
        }
    }

    public interface Callback{
        /**
         * 相册准备完成
         * @param albums 获得的相册列表
         */
        void onAlbumReady(List<Album> albums);

        /**
         * 当前需要释放资源
         */
        void onReset();
    }

}
