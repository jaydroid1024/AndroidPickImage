package org.jay.androidpickimage.single;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import org.jay.androidpickimage.R;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class SinglePickActivity extends AppCompatActivity {
    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    //请求相册
    private static final int REQUEST_GALLERY = 101;
    //请求截图
    private static final int REQUEST_CROP_PHOTO = 102;
    //请求访问外部存储
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 103;
    //请求写入外部存储
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 104;
    private CircleImageView mHeadImage;
    private File mFile;
    private Uri mUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_pick);
    }

    public void onPhotoClick(View view) {
        Toast.makeText(this, "jay", Toast.LENGTH_SHORT).show();
    }
}
