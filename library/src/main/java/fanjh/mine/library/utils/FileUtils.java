package fanjh.mine.library.utils;

import java.io.Closeable;

/**
* @author fanjh
* @date 2017/12/29 13:31
* @description 文件相关工具类
* @note
**/
public class FileUtils {

    public static void close(Closeable closeable){
        if(null != closeable){
            try{
                closeable.close();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

}
