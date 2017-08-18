package org.jay.androidpickimage.multi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.jay.androidpickimage.R;
import org.jay.androidpickimage.multi.multiimageselector.ImageLoader;
import org.jay.androidpickimage.multi.multiimageselector.ImgSelActivity;
import org.jay.androidpickimage.multi.multiimageselector.ImgSelConfig;

import java.util.List;

public class MultiPickActivity extends AppCompatActivity {


    private static final int REQUEST_CODE = 0;
    private TextView tvResult;
    private int mW;
    private int mH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_pick);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mW = dm.widthPixels;
        mH = dm.heightPixels;

        tvResult = (TextView) findViewById(R.id.tvResult);
    }

    private ImageLoader loader = new ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView, boolean isThumb) {
            if(isThumb){
                Glide.with(context).load(path).override(200,200).animate(R.anim.image_fade_in).into(imageView);
            }else{
                Glide.with(context).load(path).override(mW,mH).animate(R.anim.image_fade_in).into(imageView);
            }
        }
    };

    public void MultiSelect(View view) {
        tvResult.setText("");
        ImgSelConfig config = new ImgSelConfig.Builder(this, loader)
                .multiSelect(true)
                // 是否记住上次选中记录
                .rememberSelected(false)
                // 使用沉浸式状态栏
                .statusBarColor(Color.parseColor("#3F51B5")).build();

        ImgSelActivity.startActivity(this, config, REQUEST_CODE);
    }

    public void Single(View view) {
        tvResult.setText("");
        ImgSelConfig config = new ImgSelConfig.Builder(this, loader)
                // 是否多选
                .multiSelect(false)
                .btnText("Confirm")
                // 确定按钮背景色
                //.btnBgColor(Color.parseColor(""))
                // 确定按钮文字颜色
                .btnTextColor(Color.WHITE)
                // 使用沉浸式状态栏
                .statusBarColor(Color.parseColor("#3F51B5"))
                // 返回图标ResId
                .title("Images")
                .titleColor(Color.WHITE)
                .titleBgColor(Color.parseColor("#3F51B5"))
                .allImagesText("All Images")
                .needCrop(true)
                .cropSize(1, 1, 200, 200)
                // 第一个是否显示相机
                .needCamera(true)
                // 最大选择图片数量
                .maxNum(9)
                .build();

        ImgSelActivity.startActivity(this, config, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);

            for (String path : pathList) {
                tvResult.append(path + "\n");
            }
        }
    }

    public void onAnother(View view) {
        startActivity(new Intent(this,AnotherActivity.class));
    }
}
