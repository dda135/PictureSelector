package fanjh.mine.library.loader;

import android.database.Cursor;

/**
* @author fanjh
* @date 2017/12/26 18:08
* @description 从本地数据库中获取图片cursor后的处理
**/
public interface MediaCursorParser<T> {
    /**
     * 用于将当前游标转换为指定的对象
     * @param cursor 当前查询后获得的游标
     * @return 对象
     */
    T parseCursor(Cursor cursor);

    /**
     * 指定当前要查询的列
     * 比方说{MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME,
     MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.SIZE}
     * @return 当前要查询的列
     */
    String[] getIndex();
}
