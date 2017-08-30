package org.jay.androidpickimage;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import org.jay.androidpickimage.helper.PermissionHelper;
import org.jay.androidpickimage.multi.postimage.PostImagesActivity;
import org.jay.androidpickimage.single.SinglePickActivity;

public class MainActivity extends AppCompatActivity {
    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
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
                        PermissionHelper.showDeniedTipDialog(MainActivity.this);
                        Toast.makeText(MainActivity.this, "permission denied", Toast.LENGTH_SHORT).show();
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

    public void OnSingleSelect(View view) {
        startActivity(new Intent(this, SinglePickActivity.class));
    }

    public void OnMultiSelect(View view) {
        startActivity(new Intent(this, PostImagesActivity.class));
    }
}
