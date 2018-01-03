package fanjh.mine.pictureselector;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fanjh.mine.library.PictureSelectorActivity;

/**
 * Created by faker on 2017/12/29.
 */

public class AActivity extends Activity {
    public static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.tv_start);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(AActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE:
                if(Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[0])){
                    if(PackageManager.PERMISSION_GRANTED == grantResults[0]){
                        //3 selectCount
                        //1 requestCode
                        PictureSelectorActivity.start(this,3,1);
                    }else{
                        Toast.makeText(getApplicationContext(),"请打开存储权限后重试！",Toast.LENGTH_LONG).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case 1:
                    //requestCode
                    ArrayList<String> result = (ArrayList<String>) data.getSerializableExtra(PictureSelectorActivity.EXTRA_SELECT_PICTURES);
                    //img uri,common is content://...
                    for(String temp:result){
                        Log.i(getClass().getSimpleName(),temp);
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
