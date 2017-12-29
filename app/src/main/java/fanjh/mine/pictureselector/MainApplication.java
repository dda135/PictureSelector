package fanjh.mine.pictureselector;

import android.app.Application;
import android.util.Log;

/**
* @author fanjh
* @date 2017/12/29 14:19
* @description
* @note
**/
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());
    }

    private class CrashHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            Log.i("tag1",thread.getId()+"");
        }
    }

}
