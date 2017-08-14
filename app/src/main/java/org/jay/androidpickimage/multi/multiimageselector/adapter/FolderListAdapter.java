package org.jay.androidpickimage.multi.multiimageselector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.jay.androidpickimage.R;
import org.jay.androidpickimage.multi.multiimageselector.ImgSelConfig;
import org.jay.androidpickimage.multi.multiimageselector.bean.Folder;
import org.jay.androidpickimage.multi.multiimageselector.common.OnFolderChangeListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author yuyh.
 * @date 2016/8/5.
 */
public class FolderListAdapter extends BaseAdapter {

    private Context context;
    private List<Folder> folderList;
    private ImgSelConfig config;

    private int selected = 0;
    private OnFolderChangeListener listener;

    public FolderListAdapter(Context context, List<Folder> folderList, ImgSelConfig config) {
        this.context = context;
        this.folderList = folderList;
        this.config = config;
    }

    public void convert(ViewHolder holder, final int position, Folder folder) {
        if (position == 0) {
            holder.mTvFolderName.setText(R.string.all_image);
            holder.mTvImageNum.setText(context.getString(R.string.total) + getTotalImageSize() + context.getString(R.string.piece));
            if (folderList.size() > 0) {
                config.loader.displayImage(context, folder.cover.path, holder.mIvFolder, true);
            }
        } else {
            holder.mTvFolderName.setText(folder.name);
            holder.mTvImageNum.setText(context.getString(R.string.total)  + folder.images.size()  + context.getString(R.string.piece));
            if (folderList.size() > 0) {
                config.loader.displayImage(context, folder.cover.path, holder.mIvFolder, true);
            }
        }

        if (selected == position) {
            holder.mIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.mIndicator.setVisibility(View.GONE);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectIndex(position);
            }
        });
    }

    public void setData(List<Folder> folders) {
        folderList.clear();
        if (folders != null && folders.size() > 0) {
            folderList.addAll(folders);
        }
        notifyDataSetChanged();
    }

    private int getTotalImageSize() {
        int result = 0;
        if (folderList != null && folderList.size() > 0) {
            for (Folder folder : folderList) {
                result += folder.images.size();
            }
        }
        return result;
    }

    public void setSelectIndex(int position) {
        if (selected == position)
            return;
        if (listener != null)
            listener.onChange(position, folderList.get(position));
        selected = position;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return selected;
    }

    public void setOnFloderChangeListener(OnFolderChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return folderList.size();
    }

    @Override
    public Object getItem(int position) {
        return folderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_img_sel_folder, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Folder folder=folderList.get(position);
        convert(holder,position,folder);
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.ivFolder)
        ImageView mIvFolder;
        @BindView(R.id.tvFolderName)
        TextView mTvFolderName;
        @BindView(R.id.tvImageNum)
        TextView mTvImageNum;
        @BindView(R.id.indicator)
        ImageView mIndicator;
        View mView;

        ViewHolder(View view) {
            mView=view;
            ButterKnife.bind(this, view);
        }
    }
}
