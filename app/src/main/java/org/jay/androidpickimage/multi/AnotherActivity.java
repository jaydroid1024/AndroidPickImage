package org.jay.androidpickimage.multi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.jay.androidpickimage.R;
import org.jay.androidpickimage.multi.multi_image_selector.MultiImageSelector;
import org.jay.androidpickimage.multi.postimage.PostImagesActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AnotherActivity extends AppCompatActivity {
    @BindView(R.id.choice_mode)
    RadioGroup mChoiceMode;
    @BindView(R.id.request_num)
    EditText mRequestNum;
    @BindView(R.id.show_camera)
    RadioGroup mShowCamera;
    @BindView(R.id.button)
    Button mButton;
    @BindView(R.id.button2)
    Button mButton2;
    @BindView(R.id.result)
    TextView mResult;
    private static final int REQUEST_IMAGE = 2;
    private ArrayList<String> mSelectPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another);
        ButterKnife.bind(this);
        mChoiceMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.multi) {
                    mRequestNum.setEnabled(true);
                } else {
                    mRequestNum.setEnabled(false);
                    mRequestNum.setText("");
                }
            }
        });
    }

    @OnClick({R.id.button, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button:
                pickImage();
                break;
            case R.id.button2:
                pick();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                mSelectPath = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                StringBuilder sb = new StringBuilder();
                for (String p : mSelectPath) {
                    sb.append(p);
                    sb.append("\n");
                }
                mResult.setText(sb.toString());
                Intent intent = new Intent(AnotherActivity.this, PostImagesActivity.class);
                intent.putStringArrayListExtra("img", mSelectPath);
                startActivity(intent);
            }
        }
    }

    private void pickImage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

        } else {
            boolean showCamera = mShowCamera.getCheckedRadioButtonId() == R.id.show;
            int maxNum = 9;

            if (!TextUtils.isEmpty(mRequestNum.getText())) {
                try {
                    maxNum = Integer.valueOf(mRequestNum.getText().toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            MultiImageSelector selector = MultiImageSelector.create(AnotherActivity.this);
            selector.showCamera(showCamera);
            selector.count(maxNum);
            if (mChoiceMode.getCheckedRadioButtonId() == R.id.single) {
                selector.single();
            } else {
                selector.multi();
            }
            selector.origin(mSelectPath);
            selector.start(AnotherActivity.this, REQUEST_IMAGE);
        }
    }

    private void pick() {
        MultiImageSelector.create()
                .showCamera(true) // show camera or not. true by default
                .count(9) // max select image size, 9 by default. used width #.multi()
                .single() // single mode
                .multi() // multi mode, default mode;
                .origin(mSelectPath) // original select data set, used width #.multi()
                .start(this, REQUEST_IMAGE);

//        Intent intent = new Intent(this, MultiImageSelectorActivity.class);
//        // whether show camera
//        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
//        // max select image amount
//        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
//        // select mode (MultiImageSelectorActivity.MODE_SINGLE OR MultiImageSelectorActivity.MODE_MULTI)
//        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
//        // default select images (support array list)
//        intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
//        startActivityForResult(intent, REQUEST_IMAGE);
    }
}
