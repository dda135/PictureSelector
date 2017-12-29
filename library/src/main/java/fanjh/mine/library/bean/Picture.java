package fanjh.mine.library.bean;

import android.content.ContentUris;
import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;

/**
* @author fanjh
* @date 2017/12/20 9:12
* @description 本地图片实体
**/
public class Picture extends BasePicture implements Serializable {
    private static final long serialVersionUID = 8153100252603332898L;
    public long _id;
    public String mimeType;
    public long size;
    public String uri;

    public static Picture parseFromMediaCursor(Cursor cursor){
        Picture picture = new Picture();
        picture._id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
        picture.mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
        picture.size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
        picture.uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, picture._id).toString();
        return picture;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Picture)){
            return false;
        }
        Picture temp = (Picture) o;
        return temp._id == _id;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode
                    + (int) (_id ^ (_id >>> 32));
        return hashCode;
    }

    @Override
    public String getImageUrl() {
        return null != uri?uri: "";
    }

    public boolean isGif(){
        return "image/gif".equals(mimeType);
    }

}
