package com.min.smalltalk.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.min.mylibrary.util.T;
import com.min.mylibrary.widget.image.CircleImageView;
import com.min.smalltalk.R;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.bean.FriendInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;

/**
 * 用户详情页
 */
public class UserDetailActivity extends BaseActivity {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.civ_user_head)
    CircleImageView civUserHead;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.ll_call)
    LinearLayout llCall;
    @BindView(R.id.ll_send_sms)
    LinearLayout llSendSms;
    @BindView(R.id.ll_send_email)
    LinearLayout llSendEmail;
    @BindView(R.id.textView1)
    TextView textView1;
    @BindView(R.id.tv_telephone)
    TextView tvTelephone;
    @BindView(R.id.tv_email)
    TextView tvEmail;
    @BindView(R.id.btn_send_message)
    Button btnSendMessage;
    @BindView(R.id.ll_email)
    LinearLayout llEmail;

    private String userId, userName, userPort, userPhone, userEmail;
    private FriendInfo friendInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        friendInfo = intent.getParcelableExtra("friends");
        userId = friendInfo.getUserId();
        userName = friendInfo.getName();
        userPort = friendInfo.getPortraitUri();
        userPhone = friendInfo.getPhone();
        userEmail = friendInfo.getEmail();
        initView();

    }

    private void initView() {
        tvTitle.setText(userName);
        ImageLoader.getInstance().displayImage(userPort, civUserHead);
        tvUsername.setText(userName);
        tvTelephone.setText(userPhone);
        if (TextUtils.isEmpty(userEmail)) {
            llEmail.setVisibility(View.GONE);
        } else {
            tvEmail.setText(userEmail);
        }
    }

    @OnClick({R.id.iv_title_back, R.id.ll_call, R.id.ll_send_sms, R.id.ll_send_email, R.id.btn_send_message})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.ll_call:
                AlertDialog dialog = new AlertDialog.Builder(mContext)
                        .setTitle("拨打电话")
                        .setPositiveButton("拨打", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                Uri uri = Uri.parse("tel" + userPhone);
                                intent.setData(uri);
                                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                break;
            case R.id.ll_send_sms:
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + userPhone));
                intent.putExtra("sms_body", "");
                startActivity(intent);
                break;
            case R.id.ll_send_email:
                if (TextUtils.isEmpty(userEmail)) {
                    T.showShort(mContext, "邮箱未知");
                    break;
                }
                Intent intent1 = new Intent(Intent.ACTION_SENDTO);
                intent1.setData(Uri.parse("mailto:" + userEmail));
                intent1.putExtra(Intent.EXTRA_SUBJECT, "标题");
                intent1.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(intent1);
                break;
            case R.id.btn_send_message:
                RongIM.getInstance().startPrivateChat(mContext, userId, userName);
                break;
        }
    }
}
