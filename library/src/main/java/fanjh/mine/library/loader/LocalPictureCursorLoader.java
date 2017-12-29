package fanjh.mine.library.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
* @author fanjh
* @date 2017/12/19 18:07
* @description 本地媒体图片游标读取者
* @note 目前是分段加载，并没有监听本地数据库的变化，不需要那么实时
 * 比方说 20->20->20->20相对于60ms来说虽然总时长变高了，但是第一页加载快，体验更好
**/
public class LocalPictureCursorLoader<T> extends AsyncTaskLoader<List<T>> {
    public static final String[] PROJECTION = {MediaStore.Images.Media._ID, MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.SIZE};
    private static final String ORDER_BY = MediaStore.Images.Media._ID + " DESC";

    private String currentAlbumID;
    private long firstID;
    private int pageSize;

    private MediaCursorParser<T> mediaCursorParser;

    private LocalPictureCursorLoader(Context context, MediaCursorParser<T> mediaCursorParser) {
        super(context);
        this.mediaCursorParser = mediaCursorParser;
    }

    public void startLoad(String currentAlbumID, long firstID, int pageSize){
        this.currentAlbumID = currentAlbumID;
        this.firstID = firstID;
        this.pageSize = pageSize;
        forceLoad();
    }

    @Override
    public List<T> loadInBackground() {
        final List<T> data = new ArrayList<>();
        String selection = null;
        String[]selectArgs = null;
        if (!TextUtils.isEmpty(currentAlbumID)) {
            if(firstID <= 0) {
                selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
                selectArgs = new String[]{currentAlbumID};
            }else{
                selection = MediaStore.Images.Media.BUCKET_ID + " = ? and " + MediaStore.Images.Media._ID + " < ?";
                selectArgs = new String[]{currentAlbumID,firstID+""};
            }
        }else if(firstID > 0){
            selection = MediaStore.Images.Media._ID + " < ?";
            selectArgs = new String[]{firstID+""};
        }

        String orderBy = null;
        if(pageSize > 0){
            orderBy = ORDER_BY + " LIMIT " + pageSize;
        }else{
            orderBy = ORDER_BY;
        }
        Cursor cursor = getContext().getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mediaCursorParser.getIndex(), selection, selectArgs, orderBy);

        if (cursor == null) {
            return data;
        }

        try {
            while (cursor.moveToNext()) {
                data.add(mediaCursorParser.parseCursor(cursor));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        } finally {
            cursor.close();
        }
        return data;
    }

    public static <T> LocalPictureCursorLoader getInstance(Context context, MediaCursorParser<T> mediaCursorParser){
        return new LocalPictureCursorLoader<T>(context,mediaCursorParser);
    }

}
