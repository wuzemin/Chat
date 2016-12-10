package com.min.smalltalk.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.min.smalltalk.R;
import com.min.smalltalk.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonSettingActivity extends BaseActivity {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_myHead)
    ImageView ivMyHead;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.rl_nickname)
    RelativeLayout rlNickname;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.rl_sex)
    RelativeLayout rlSex;
    @BindView(R.id.tv_birthday)
    TextView tvBirthday;
    @BindView(R.id.rl_birthday)
    RelativeLayout rlBirthday;
    @BindView(R.id.rl_QR_code)
    RelativeLayout rlQRCode;
    @BindView(R.id.tv_email)
    TextView tvEmail;
    @BindView(R.id.rl_email)
    RelativeLayout rlEmail;
    @BindView(R.id.activity_person_setting)
    LinearLayout activityPersonSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_setting);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.iv_title_back, R.id.iv_myHead, R.id.rl_nickname, R.id.rl_sex, R.id.rl_birthday, R.id.rl_QR_code, R.id.rl_email})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                break;
            case R.id.iv_myHead:
                break;
            case R.id.rl_nickname:
                break;
            case R.id.rl_sex:
                break;
            case R.id.rl_birthday:
                break;
            case R.id.rl_QR_code:
                break;
            case R.id.rl_email:
                break;
        }
    }
}
