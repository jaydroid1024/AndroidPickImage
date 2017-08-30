package org.jay.androidpickimage.multi.postimage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.jay.androidpickimage.R;
import org.jay.androidpickimage.application.MyApplication;
import org.jay.androidpickimage.helper.PickImageHelper;
import org.jay.androidpickimage.multi.multi_image_selector.MultiImageSelector;
import org.jay.androidpickimage.multi.multi_image_selector.MultiImageSelectorActivity;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class PostImagesActivity extends AppCompatActivity {

    public static final int IMAGE_SIZE = 9;
    private static final int REQUEST_IMAGE = 1002;

    private ArrayList<String> originImages;//原始图片
    private ArrayList<String> dragImages;//压缩长宽后图片
    private Context mContext;
    private PostArticleImgAdapter postArticleImgAdapter;
    private ItemTouchHelper itemTouchHelper;
    private RecyclerView rcvImg;
    private TextView tv;//删除区域提示


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_images);
        initData();
        initView();
    }

    private void initData() {
        originImages = getIntent().getStringArrayListExtra("img");
        if (originImages == null) {
            originImages=new ArrayList<>();
        }
        mContext = getApplicationContext();
        //添加按钮图片资源
        String plusPath = getString(R.string.glide_plus_icon_string) +getPackageInfo(mContext).packageName + "/mipmap/" + R.mipmap.mine_btn_plus;
        dragImages = new ArrayList<>();
        originImages.add(plusPath);//添加按键，超过9张时在adapter中隐藏
        dragImages.addAll(originImages);
        new Thread(new MyRunnable(this,dragImages, originImages, dragImages, myHandler, false)).start();//开启线程，在新线程中去压缩图片
    }
    public static PackageInfo getPackageInfo(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            return pm.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
        }
        return new PackageInfo();
    }

    private void initView() {
        rcvImg = (RecyclerView) findViewById(R.id.rcv_img);
        tv = (TextView) findViewById(R.id.tv);
        initRcv();
    }

    private void initRcv() {

        postArticleImgAdapter = new PostArticleImgAdapter(mContext, dragImages);
        rcvImg.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        rcvImg.setAdapter(postArticleImgAdapter);
        MyCallBack myCallBack = new MyCallBack(postArticleImgAdapter, dragImages, originImages);
        itemTouchHelper = new ItemTouchHelper(myCallBack);
        itemTouchHelper.attachToRecyclerView(rcvImg);//绑定RecyclerView

        //事件监听
        rcvImg.addOnItemTouchListener(new OnRecyclerItemClickListener(rcvImg) {

            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                if (originImages.get(vh.getAdapterPosition()).contains(getString(R.string.glide_plus_icon_string))) {//打开相册
                    MultiImageSelector.create()
                            .showCamera(true)
                            .count(IMAGE_SIZE - originImages.size() + 1)
                            .multi()
                            .start(PostImagesActivity.this, REQUEST_IMAGE);
                } else {
                    Toast.makeText(mContext, "Review", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh) {
                //如果item不是最后一个，则执行拖拽
                if (vh.getLayoutPosition() != dragImages.size() - 1) {
                    itemTouchHelper.startDrag(vh);
                }
            }
        });

        myCallBack.setDragListener(new MyCallBack.DragListener() {
            @Override
            public void deleteState(boolean delete) {
                if (delete) {
                    tv.setBackgroundResource(R.color.holo_red_dark);
                    tv.setText(getResources().getString(R.string.post_delete_tv_s));
                } else {
                    tv.setText(getResources().getString(R.string.post_delete_tv_d));
                    tv.setBackgroundResource(R.color.holo_red_light);
                }
            }

            @Override
            public void dragState(boolean start) {
                if (start) {
                    tv.setVisibility(View.VISIBLE);
                } else {
                    tv.setVisibility(View.GONE);
                }
            }
        });
    }

    //------------------图片相关-----------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {//从相册选择完图片
            //压缩图片
            new Thread(new MyRunnable(PostImagesActivity.this,data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT),
                    originImages, dragImages, myHandler, true)).start();
        }
    }

    /**
     * 另起线程压缩图片
     */
    static class MyRunnable implements Runnable {

        ArrayList<String> images;
        ArrayList<String> originImages;
        ArrayList<String> dragImages;
        Handler handler;
        boolean add;//是否为添加图片
        private final Context mContext;

        public MyRunnable(Context context,ArrayList<String> images, ArrayList<String> originImages, ArrayList<String> dragImages, Handler handler, boolean add) {
            this.images = images;
            this.originImages = originImages;
            this.dragImages = dragImages;
            this.handler = handler;
            this.add = add;
            mContext = context;
        }

        @Override
        public void run() {
            int addIndex = originImages.size() - 1;
            for (int i = 0; i < images.size(); i++) {
                if (images.get(i).contains(MyApplication.getInstance().getString(R.string.glide_plus_icon_string))) {//说明是添加图片按钮
                    continue;
                }
                Bitmap bitmap;
                try {
                    bitmap = PickImageHelper.getImageSampleOutput(mContext, Uri.fromFile(new File(images.get(i))));
                    Bitmap thumbNail = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
                    String fileName = PickImageHelper.GenerateNameWithUUID();
                    File file = PickImageHelper.createFileFromBitmap(mContext, fileName, thumbNail);
                    if (!add) {
                        images.set(i, file.getPath());
                    } else {//添加图片，要更新
                        dragImages.add(addIndex, file.getPath());
                        originImages.add(addIndex++, file.getPath());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (OutOfMemoryError ome) {
                    ome.printStackTrace();
                }
//                //压缩
//                newBitmap = ImageUtils.compressScaleByWH(images.get(i),
//                        DensityUtils.dp2px(MyApplication.getInstance().getContext(), 100),
//                        DensityUtils.dp2px(MyApplication.getInstance().getContext(), 100));
//                //文件地址
//                filePath = sdcardUtils.getSDPATH() + FILE_DIR_NAME + "/"
//                        + FILE_IMG_NAME + "/" + String.format("img_%d.jpg", System.currentTimeMillis());
//                //保存图片
//                ImageUtils.save(newBitmap, filePath, Bitmap.CompressFormat.JPEG, true);
//                //设置值
//                if (!add) {
//                    images.set(i, filePath);
//                } else {//添加图片，要更新
//                    dragImages.add(addIndex, filePath);
//                    originImages.add(addIndex++, filePath);
//                }
            }
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    }

    private MyHandler myHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private WeakReference<Activity> reference;

        public MyHandler(Activity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            PostImagesActivity activity = (PostImagesActivity) reference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        activity.postArticleImgAdapter.notifyDataSetChanged();
                        break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacksAndMessages(null);
    }

}
