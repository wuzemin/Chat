package com.min.smalltalk.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.min.smalltalk.R;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.constant.Const;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.bitmap;

public class ZxingActivity extends BaseActivity {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_zxing)
    ImageView ivZxing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zxing);
        ButterKnife.bind(this);
        Intent intent=getIntent();
        String input=intent.getStringExtra("Id");
        String port=getSharedPreferences("config",MODE_PRIVATE).getString(Const.LOGIN_PORTRAIT,"");
        Bitmap bitmap = null;
            bitmap = EncodingUtils.createQRCode(input, 500, 500,
                    BitmapFactory.decodeResource(getResources(), 0));
        ivZxing.setImageBitmap(bitmap);
    }

    @OnClick(R.id.iv_title_back)
    public void onClick() {
        ZxingActivity.this.finish();
    }
}
