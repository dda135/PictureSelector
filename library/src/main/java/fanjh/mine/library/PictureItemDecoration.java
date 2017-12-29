package fanjh.mine.library;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author fanjh
 * @date 2017/12/21 18:09
 * @description 照片装饰
 * @note 实际上就是一个田格
 **/
public class PictureItemDecoration extends RecyclerView.ItemDecoration {
    private int mSpacing;
    private int mSpanCount;

    public PictureItemDecoration(int mSpacing, int mSpanCount) {
        this.mSpacing = mSpacing;
        this.mSpanCount = mSpanCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);
        int column = (position % mSpanCount) + 1;
        int row = (position / mSpanCount) + 1;

        if(row == 1){
            if(column == 1){
                outRect.set(mSpacing,mSpacing,mSpacing,mSpacing);
            }else{
                outRect.set(0,mSpacing,mSpacing,mSpacing);
            }
        }else{
            if(column == 1){
                outRect.set(mSpacing,0,mSpacing,mSpacing);
            }else{
                outRect.set(0,0,mSpacing,mSpacing);
            }
        }
    }



}
