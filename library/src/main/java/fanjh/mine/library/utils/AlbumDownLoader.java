package fanjh.mine.library.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import fanjh.mine.library.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
* @author fanjh
* @date 2017/12/28 15:22
* @description 用于处理保存图片到相册
* @note 优先从glide缓存中获取，否则重新下载
**/
public class AlbumDownLoader {
    public static final String EXTRA_FILENAME = "filename";
    public static final String EXTRA_URL = "url";
    public static final String EXTRA_PREFIX = "prefix";
    public static final int MSG_WRITE_ERROR = 1;
    public static final int MSG_SUCCESS = 2;
    public static final int MSG_NETWORK_ERROR = 3;
    public static final int MSG_TRY_DOWNLOAD_FROM_NETWORK = 4;
    private Context context;
    private static final OkHttpClient CLIENT = new OkHttpClient.Builder().build();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            OnDownloadPictureToDCIMCallback callback = null;
            Bundle bundle = null;
            switch (msg.what){
                case MSG_WRITE_ERROR:
                    callback = (OnDownloadPictureToDCIMCallback) msg.obj;
                    callback.onDownloadError();
                    break;
                case MSG_SUCCESS:
                    callback = (OnDownloadPictureToDCIMCallback) msg.obj;
                    bundle = msg.getData();
                    String filePath = bundle.getString(EXTRA_FILENAME);
                    callback.onDownloadSuccess(filePath);
                    break;
                case MSG_NETWORK_ERROR:
                    Logger.showToastShort(context,context.getString(R.string.no_connection_error));
                    break;
                case MSG_TRY_DOWNLOAD_FROM_NETWORK:
                    bundle = msg.getData();
                    String uri = bundle.getString(EXTRA_URL);
                    String prefix = bundle.getString(EXTRA_PREFIX);
                    callback = (OnDownloadPictureToDCIMCallback) msg.obj;
                    downloadFileToDCIM(context, uri, prefix, callback);
                    break;
                default:
                    break;
            }
        }
    };

    public AlbumDownLoader(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * 保存到相册回调
     */
    public interface OnDownloadPictureToDCIMCallback{
        /**
         * 保存成功
         * @param filePath 保存后的文件路径
         */
        void onDownloadSuccess(String filePath);

        /**
         * 保存失败
         */
        void onDownloadError();
    }

    /**
     * 保存图片到相册
     * @param uri 图片地址
     * @param callback 结果回调
     */
    public void downLoadPictureToDCIM(final String uri, final OnDownloadPictureToDCIMCallback callback){
        final boolean isGif = uri.endsWith("gif");
        final String prefix = isGif?".gif":".jpg";
        Glide.with(context).download(uri).listener(new RequestListener<File>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                downloadFileToDCIM(context,uri,prefix,callback);
                return false;
            }

            @Override
            public boolean onResourceReady(final File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        File file = getDCIMOutputFile(resource,prefix);
                        if(null == file) {
                            Message message = handler.obtainMessage();
                            message.what = MSG_TRY_DOWNLOAD_FROM_NETWORK;
                            message.obj = callback;
                            Bundle bundle = new Bundle();
                            bundle.putString(EXTRA_URL,uri);
                            bundle.putString(EXTRA_PREFIX,prefix);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }else{
                            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                            callSuccess(callback,file.getAbsolutePath());
                        }
                    }
                });
                return false;
            }
        }).into(new SimpleTarget<File>() {
            @Override
            public void onResourceReady(File resource, Transition<? super File> transition) {

            }
        });
    }

    private void downloadFileToDCIM(final Context context, final String uri, final String prefix, final OnDownloadPictureToDCIMCallback callback){
        if(uri.startsWith("http")){
            Request request = new Request.Builder().get().url(uri).build();
            CLIENT.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.sendEmptyMessage(MSG_NETWORK_ERROR);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        File newFile = getDCIMOutputFile(response.body().byteStream(),prefix);
                        if(null == newFile){
                            callError(callback);
                            return;
                        }
                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(newFile)));
                        callSuccess(callback,newFile.getAbsolutePath());
                    }catch (Exception ex){
                        ex.printStackTrace();
                        callError(callback);
                    }
                }
            });
        } else{
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    FileInputStream resource = null;
                    try {
                        resource = new FileInputStream(uri);
                        File newFile = getDCIMOutputFile(resource, prefix);
                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(newFile)));
                        callSuccess(callback,uri);
                    }catch (Exception ex){
                        ex.printStackTrace();
                        callError(callback);
                    }finally {
                        FileUtils.close(resource);
                    }
                }
            });
        }
    }

    private void callError(OnDownloadPictureToDCIMCallback callback){
        if(null == callback){
            return;
        }
        Message message = handler.obtainMessage();
        message.what = MSG_WRITE_ERROR;
        message.obj = callback;
        handler.sendMessage(message);
    }

    private void callSuccess(OnDownloadPictureToDCIMCallback callback,String filePath){
        if(null == callback){
            return;
        }
        Message message = handler.obtainMessage();
        message.what = MSG_SUCCESS;
        message.obj = callback;
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_FILENAME,filePath);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    private File getDCIMOutputFile(InputStream resource, String prefix){
        File outputFile = null;
        BufferedSource bufferedSource = null;
        BufferedSink bufferedSink = null;
        boolean isSuccess = false;
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File dirs = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), context.getString(R.string.app_name));
                if (!dirs.exists()) {
                    dirs.mkdirs();
                } else if (dirs.isFile()) {
                    dirs.delete();
                    dirs.mkdirs();
                }

                outputFile = new File(dirs, UUID.randomUUID().toString() + prefix);
                outputFile.createNewFile();
                bufferedSink = Okio.buffer(Okio.sink(outputFile));
                bufferedSource = Okio.buffer(Okio.source(resource));
                bufferedSink.writeAll(bufferedSource);
                bufferedSink.flush();
                isSuccess = true;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            FileUtils.close(bufferedSink);
            FileUtils.close(bufferedSource);
        }
        return isSuccess?outputFile:null;
    }

    private File getDCIMOutputFile(File resource, String prefix){
        File outputFile = null;
        BufferedSource bufferedSource = null;
        BufferedSink bufferedSink = null;
        boolean isSuccess = false;
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File dirs = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), context.getString(R.string.app_name));
                if (!dirs.exists()) {
                    dirs.mkdirs();
                } else if (dirs.isFile()) {
                    dirs.delete();
                    dirs.mkdirs();
                }

                outputFile = new File(dirs, UUID.randomUUID().toString() + prefix);
                outputFile.createNewFile();
                bufferedSink = Okio.buffer(Okio.sink(outputFile));
                bufferedSource = Okio.buffer(Okio.source(resource));
                long result = bufferedSink.writeAll(bufferedSource);
                if(result > 0) {
                    bufferedSink.flush();
                    isSuccess = true;
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            FileUtils.close(bufferedSink);
            FileUtils.close(bufferedSource);
        }
        return isSuccess?outputFile:null;
    }

}
