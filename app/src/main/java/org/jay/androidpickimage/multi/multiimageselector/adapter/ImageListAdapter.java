package org.jay.androidpickimage.multi.multiimageselector.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.jay.androidpickimage.R;
import org.jay.androidpickimage.multi.multiimageselector.ImgSelConfig;
import org.jay.androidpickimage.multi.multiimageselector.bean.Image;
import org.jay.androidpickimage.multi.multiimageselector.common.Constant;
import org.jay.androidpickimage.multi.multiimageselector.common.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author yuyh.
 * @date 2016/8/5.
 */
public class ImageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @BindView(R.id.ivImage)
    ImageView mIvImage;
    @BindView(R.id.ivPhotoCheaked)
    ImageView mIvPhotoCheaked;
    private boolean showCamera;
    private boolean multiSelect;
    private ImgSelConfig config;
    private Context context;
    private OnItemClickListener listener;
    List<Image> mList;

    public ImageListAdapter(Context context, List<Image> list, ImgSelConfig config) {
        this.context = context;
        this.config = config;
        mList=list;
//        super(context, list, R.layout.img_item_sel, R.layout.img_item_sel_take_photo);

    }

    protected void onBindData(final RecyclerView.ViewHolder viewHolder, final int position, final Image item) {

        if (position == 0 && showCamera) {
            CameraViewHolder holder = (CameraViewHolder) viewHolder;
            holder.mIvTakePhoto.setImageResource(R.drawable.ic_img_take_photo);
            holder.mIvTakePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onImageClick(position, item);
                }
            });
            return;
        }

        final ImageViewHolder holder = (ImageViewHolder) viewHolder;
        if (multiSelect) {
            holder.mIvPhotoCheaked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int ret = listener.onCheckedClick(position, item);
                        if (ret == 1) { // 局部刷新
                            if (Constant.imageList.contains(item.path)) {
                                holder.mIvPhotoCheaked.setImageResource(R.drawable.ic_img_checked);
                            } else {
                                holder.mIvPhotoCheaked.setImageResource(R.drawable.ic_img_uncheck);
                            }
                        }
                    }
                }
            });
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onImageClick(position, item);
            }
        });

        config.loader.displayImage(context, item.path, holder.mIvImage, true);

        if (multiSelect) {
            holder.mIvPhotoCheaked.setVisibility(View.VISIBLE);
            if (Constant.imageList.contains(item.path)) {
                holder.mIvPhotoCheaked.setImageResource(R.drawable.ic_img_checked);
            } else {
                holder.mIvPhotoCheaked.setImageResource(R.drawable.ic_img_uncheck);
            }
        } else {
            holder.mIvPhotoCheaked.setVisibility(View.GONE);
        }
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0 && showCamera) {
            return 1;
        }
        return 0;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new ImageViewHolder(LayoutInflater.from(context).inflate(R.layout.img_item_sel, null));
        } else {
            return new CameraViewHolder(LayoutInflater.from(context).inflate(R.layout.img_item_sel_take_photo, null));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Image image = mList.get(position);
        onBindData(holder, position, image);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivImage)
        ImageView mIvImage;
        @BindView(R.id.ivPhotoCheaked)
        ImageView mIvPhotoCheaked;

        ImageViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    static class CameraViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivTakePhoto)
        ImageView mIvTakePhoto;

        CameraViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
