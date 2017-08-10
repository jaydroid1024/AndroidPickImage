package org.jay.androidpickimage.single;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.jay.androidpickimage.R;
import org.jay.androidpickimage.helper.PermissionHelper;
import org.jay.androidpickimage.helper.PickImageHelper;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class SinglePickActivity extends AppCompatActivity {
    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
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
    @BindView(R.id.iv_avatar)
    CircleImageView mIvAvatar;
    @BindView(R.id.pb_progress)
    ProgressBar mPbProgress;
    @BindView(R.id.checkbox)
    CheckBox mCheckbox;
    @BindView(R.id.tv_image_size)
    TextView mTvImageSize;
    @BindView(R.id.image)
    ImageView mImage;
    private boolean mIsNeedCrop = false;
    private BmobFile mBmobFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_pick);
        ButterKnife.bind(this);
        mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsNeedCrop = isChecked;
            }
        });
    }

    public void onPhotoClick(View view) {
        CropImage.startPickImageActivity(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mPbProgress.setVisibility(View.VISIBLE);
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                requestPermissions();
            } else {
                if (mIsNeedCrop) {
                    CropImage.activity(imageUri).start(SinglePickActivity.this);
                } else {
                    upLoadFile(imageUri);
                }
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                upLoadFile(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                if (error != null)
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void upLoadFile(Uri resultUri) {
        Bitmap bitmap;
        try {
            bitmap = PickImageHelper.getImageSampleOutput(this, resultUri);
            Bitmap thumbNail = Bitmap.createScaledBitmap(bitmap, 600, 800, false);
            mTvImageSize.setText("大小："+thumbNail.getByteCount()/1024/1024);
            String fileName = PickImageHelper.GenerateNameWithUUID();
            File file = PickImageHelper.createFileFromBitmap(this, fileName, thumbNail);
            uploadProfileImage(file, fileName);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (OutOfMemoryError ome) {
            ome.printStackTrace();
            Toast.makeText(getApplicationContext(), ome.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadProfileImage(final File file, final String fileName) {
        mPbProgress.setVisibility(View.VISIBLE);
        mBmobFile = new BmobFile(file);
        mBmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Picasso.with(SinglePickActivity.this).load(mBmobFile.getUrl()).into(mImage);
                    Log.d("jay", "done: [e]=" + mBmobFile.getUrl());
                    Picasso.with(SinglePickActivity.this).load(mBmobFile.getUrl()).into(mIvAvatar, new Callback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getApplicationContext(), "load success", Toast.LENGTH_SHORT).show();
                            mPbProgress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            mPbProgress.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "error load image", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    mPbProgress.setVisibility(View.GONE);
                    Toast.makeText(SinglePickActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
                Log.d("jay", "onProgress: [value]=" + value);
            }
        });
    }

    //1
    private void requestPermissions() {
        PermissionHelper
                .with(this)
                .permissions(PERMISSIONS)
                .CallBack(new PermissionHelper.OnPermissionRequestListener() {
                    @Override
                    public void onGranted() {
//                        Toast.makeText(MainActivity.this, "permission granted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDenied() {
                        PermissionHelper.showDeniedTipDialog(SinglePickActivity.this);
                        Toast.makeText(SinglePickActivity.this, "permission denied", Toast.LENGTH_SHORT).show();
                    }
                })
                .request();
    }

    //2
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
