package org.jay.androidpickimage.multi.multiimageselector.common;


import org.jay.androidpickimage.multi.multiimageselector.bean.Image;

/**
 * @author yuyh.
 * @date 2016/8/5.
 */
public interface OnItemClickListener {

    int onCheckedClick(int position, Image image);

    void onImageClick(int position, Image image);
}
