package fanjh.mine.library.bean;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import fanjh.mine.library.loader.LocalAlbumCursorLoader;


/**
* @author fanjh
* @date 2017/12/19 18:16
* @description 相册实体
**/
public class Album {
    public String bucketID;
    public long _id;
    public String displayName;
    public long count;
    public Uri uri;

    public static Album parseFromMediaCursor(Cursor cursor){
        Album album = new Album();
        album.bucketID = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
        album._id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
        album.displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
        album.count = cursor.getLong(cursor.getColumnIndex(LocalAlbumCursorLoader.COLUMN_COUNT));
        album.uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, album._id);
        return album;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
