package com.min.smalltalk.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.min.mylibrary.widget.image.CircleImageView;
import com.min.smalltalk.R;
import com.min.smalltalk.activity.LoginActivity;
import com.min.smalltalk.activity.PersonSettingActivity;
import com.min.smalltalk.constant.Const;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PersonalFragment extends Fragment {

    @BindView(R.id.civ_icon)
    CircleImageView civIcon;
    @BindView(R.id.tv_user)
    TextView tvUser;
    @BindView(R.id.ll_password_setting)
    LinearLayout llPasswordSetting;
    @BindView(R.id.ll_setting)
    LinearLayout llSetting;
    @BindView(R.id.btn_exit)
    Button btnExit;
    @BindView(R.id.tv_userid)
    TextView tvUserid;
    @BindView(R.id.rl_person_setting)
    RelativeLayout rlPersonSetting;

    private SharedPreferences sp;
    private String userid, username, userphone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        ButterKnife.bind(this, view);
        sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        userid = sp.getString(Const.LOGIN_ID, "");
        username=sp.getString(Const.LOGIN_NICKNAME,"");
        userphone = sp.getString(Const.LOGIN_PHONE, "");
        initView();
        return view;
    }

    private void initView() {
        tvUserid.setText(userid);
        tvUser.setText(username);
    }

    @OnClick({R.id.civ_icon, R.id.btn_exit, R.id.ll_password_setting, R.id.ll_setting, R.id.rl_person_setting})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.civ_icon:
                break;
            case R.id.btn_exit:
                AlertDialog dialog=new AlertDialog.Builder(getActivity())
                        .setTitle("退出")
                        .setPositiveButton("确定退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sp.edit().clear().commit();
                                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
                break;
            case R.id.rl_person_setting:
                startActivity(new Intent(getActivity(), PersonSettingActivity.class));
                break;
            default:
                break;
        }
    }
}
