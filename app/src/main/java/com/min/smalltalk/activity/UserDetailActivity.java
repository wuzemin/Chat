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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.min.mylibrary.util.T;
import com.min.mylibrary.widget.image.CircleImageView;
import com.min.smalltalk.R;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.bean.Code;
import com.min.smalltalk.bean.FriendInfo;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.network.HttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;
import okhttp3.Call;

/**
 * 用户详情页
 */
public class UserDetailActivity extends BaseActivity {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;
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

    @OnClick({R.id.iv_title_back, R.id.ll_call, R.id.ll_send_sms, R.id.ll_send_email,
            R.id.btn_send_message, R.id.tv_title_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.ll_call:   //打电话
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
            case R.id.ll_send_sms:   //发短信
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + userPhone));
                intent.putExtra("sms_body", "");
                startActivity(intent);
                break;
            case R.id.ll_send_email:  //发邮件
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
            case R.id.btn_send_message:   //发信息
                RongIM.getInstance().startPrivateChat(mContext, userId, userName);
                break;
            case R.id.tv_title_right:
                tvTitleRight.setVisibility(View.VISIBLE);
                tvTitleRight.setText("修改备注名");
                final EditText editText=new EditText(mContext);
                AlertDialog dialog1=new AlertDialog.Builder(mContext)
                        .setTitle("修改备注名")
                        .setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String et=editText.getText().toString();
                                changeDiaplayName(et);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            default:
                break;
        }
    }

    private void changeDiaplayName(final String string) {
        String myId=getSharedPreferences("config",MODE_PRIVATE).getString(Const.LOGIN_ID,"");
        HttpUtils.postChangeFriendName("/editFriendName", myId, userId, userName, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext,"/editFriendName----"+e);
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson=new Gson();
                Type type=new TypeToken<Code<Integer>>(){}.getType();
                Code<Integer> code=gson.fromJson(response,type);
                if(code.getCode()==200){
                    tvUsername.setText(string);
                    T.showShort(mContext,"修改成功");
                }else {
                    T.showShort(mContext,"修改失败");
                }
            }
        });
    }
}
