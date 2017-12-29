package fanjh.mine.library.loader;

import android.database.Cursor;

import fanjh.mine.library.bean.Picture;


/**
* @author fanjh
* @date 2017/12/27 10:35
* @description
* @note
**/
public class PictureMediaCursorParser implements MediaCursorParser<Picture> {
    @Override
    public Picture parseCursor(Cursor cursor) {
        return Picture.parseFromMediaCursor(cursor);
    }

    @Override
    public String[] getIndex() {
        return LocalPictureCursorLoader.PROJECTION;
    }
}
