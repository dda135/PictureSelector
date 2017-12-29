package fanjh.mine.library.loader;

import android.content.Context;
import android.content.CursorLoader;
import android.provider.MediaStore;

/**
* @author fanjh
* @date 2017/12/19 18:07
* @description 本地媒体相册游标读取者
* @note 目前是全量拉取
**/
public class LocalAlbumCursorLoader extends CursorLoader {
    public static final String COLUMN_COUNT = "count";
    private static final String[] PROJECTION = {MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media._ID, "COUNT(*) AS " + COLUMN_COUNT};
    private static final String BUCKET_GROUP_BY = "1) GROUP BY 1,(2";
    private static final String BUCKET_ORDER_BY = "MAX(" + MediaStore.Images.Media.DATE_TAKEN + ") DESC";

    public LocalAlbumCursorLoader(Context context) {
        super(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PROJECTION, BUCKET_GROUP_BY, null,
                BUCKET_ORDER_BY);
    }

}
