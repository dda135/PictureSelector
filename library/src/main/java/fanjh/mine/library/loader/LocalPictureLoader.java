package fanjh.mine.library.loader;

import android.content.Context;
import android.content.Loader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
* @author fanjh
* @date 2017/12/19 18:03
* @description 本地媒体相册图片加载者
**/
public class LocalPictureLoader<T>{

    private WeakReference<Context> weak;
    private WeakReference<Callback<T>> callbackWeak;
    private String currentAlbumID;
    private MediaCursorParser<T> mediaCursorParser;
    private LocalPictureCursorLoader<T> loader;

    public LocalPictureLoader(Context context, Callback<T> callback, MediaCursorParser<T> mediaCursorParser) {
        this.weak = new WeakReference<>(context);
        this.callbackWeak = new WeakReference<>(callback);
        this.mediaCursorParser = mediaCursorParser;
    }

    public void start(String albumID, long firstPictureID, int pageSize){
        if(weak.get() == null){
            return;
        }
        try {
            currentAlbumID = albumID;
            if(null == loader){
                loader = LocalPictureCursorLoader.getInstance(weak.get(),mediaCursorParser);
                loader.registerListener(0, new Loader.OnLoadCompleteListener<List<T>>() {
                    @Override
                    public void onLoadComplete(Loader<List<T>> loader, List<T> data) {
                        if(null == data || null == weak.get()){
                            return;
                        }
                        if(null != callbackWeak.get()){
                            callbackWeak.get().onPictureReady((ArrayList<T>) data);
                        }
                    }
                });
            }
            loader.startLoad(currentAlbumID,firstPictureID,pageSize);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void destroy(){
        try {
            weak.clear();
            callbackWeak.clear();
            currentAlbumID = null;
            loader = null;
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public interface Callback<T>{
        /**
         * 相册图片准备完成
         * @param pictures 获得的相册图片列表
         */
        void onPictureReady(ArrayList<T> pictures);

    }

    public String getCurrentAlbumID() {
        return currentAlbumID;
    }
}
