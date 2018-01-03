package fanjh.mine.library.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
* @author fanjh
* @date 2017/12/27 10:56
* @description 图片选择器选择方案
* @note 通过本地数据的id作为唯一标识
**/
public class PictureSelectorSpec extends BaseSelectorSpec<Picture> implements Serializable {
    private static final long serialVersionUID = -9155026107883892813L;
    public static final int MAX_GIF_SIZE = (int) (1.5 * 1024 * 1024);
    private HashMap<Long,Picture> selectedPictures;
    private LinkedList<Long> selectedIndex;

    public PictureSelectorSpec(int maxSelectedCount) {
        super(maxSelectedCount);
        selectedPictures = new HashMap<>();
        selectedIndex = new LinkedList<>();
    }

    @Override
    public boolean isSelected(int position, Picture item) {
        return null != item && null != selectedPictures.get(item._id);
    }

    @Override
    public int changeSelect(int position, boolean isSelected, Picture item) {
        if(null == item){
            return CODE_FAIL_EMPTY_IMAGE;
        }
        if (!isSelected) {
            selectedPictures.remove(item._id);
            selectedIndex.remove(item._id);
        } else if (getSelectedCount() == getMaxSelectedCount()) {
            return CODE_FAIL_MAX_COUNT;
        } else if(item.isGif() && item.size > MAX_GIF_SIZE){
            return CODE_FAIL_GIF_SIZE;
        } else {
            selectedPictures.put(item._id,item);
            selectedIndex.add(item._id);
        }
        return CODE_SUCCESS;
    }

    @Override
    public int getSelectedCount() {
        return selectedIndex.size();
    }

    @Override
    public ArrayList<String> getSelectedImageUrl() {
        ArrayList<String> result = new ArrayList<>();
        Iterator<Long> iterator = selectedIndex.iterator();
        while (iterator.hasNext()){
            long id = iterator.next();
            String url = selectedPictures.get(id).uri;
            if(null != url) {
                result.add(url);
            }
        }
        return result;
    }

    @Override
    public ArrayList<Picture> getSelectedPicture(){
        ArrayList<Picture> result = new ArrayList<>();
        Iterator<Long> iterator = selectedIndex.iterator();
        while (iterator.hasNext()){
            long id = iterator.next();
            Picture entity = selectedPictures.get(id);
            if(null != entity) {
                result.add(entity);
            }
        }
        return result;
    }

    @Override
    public void verifyReasonable(ArrayList<Picture> list) {
        if(null == list){
            return;
        }
        Iterator<Long> iterator = selectedIndex.iterator();
        boolean shouldRemove;
        while (iterator.hasNext()){
            shouldRemove = true;
            long id = iterator.next();
            for(Picture picture:list){
                if(picture._id == id){
                    shouldRemove = false;
                    break;
                }
            }
            if(shouldRemove) {
                iterator.remove();
                selectedPictures.remove(id);
            }
        }
    }

}
