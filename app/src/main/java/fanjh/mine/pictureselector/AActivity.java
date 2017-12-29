package fanjh.mine.pictureselector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

import fanjh.mine.library.PictureSelectorActivity;

/**
 * Created by faker on 2017/12/29.
 */

public class AActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PictureSelectorActivity.start(this,3,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case 1:
                    ArrayList<String> result = (ArrayList<String>) data.getSerializableExtra(PictureSelectorActivity.EXTRA_SELECT_PICTURES);
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
