package com.min.smalltalk.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.min.mylibrary.util.T;
import com.min.mylibrary.widget.image.CircleImageView;
import com.min.smalltalk.R;
import com.min.smalltalk.activity.LoginActivity;
import com.min.smalltalk.activity.PersonSettingActivity;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.db.FriendInfoDAOImpl;
import com.min.smalltalk.db.GroupMemberDAOImpl;
import com.min.smalltalk.db.GroupsDAOImpl;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imageloader.core.ImageLoader;


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
    private String userid, username, userphone,userPortraitUri;

    private GroupsDAOImpl groupsDAO;
    private GroupMemberDAOImpl groupMemberDAO;
    private FriendInfoDAOImpl friendInfoDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        ButterKnife.bind(this, view);

        groupsDAO=new GroupsDAOImpl(getActivity());
        friendInfoDAO=new FriendInfoDAOImpl(getActivity());

        sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        userid = sp.getString(Const.LOGIN_ID, "");
        username=sp.getString(Const.LOGIN_NICKNAME,"");
        userphone = sp.getString(Const.LOGIN_PHONE, "");
        userPortraitUri=sp.getString(Const.LOGIN_PORTRAIT,"");
        initView();
        /*BroadcastManager.getInstance(getActivity()).addAction(Const.CHANGEINFO, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                initView();
            }
        });*/
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        userid = sp.getString(Const.LOGIN_ID, "");
        username=sp.getString(Const.LOGIN_NICKNAME,"");
        userphone = sp.getString(Const.LOGIN_PHONE, "");
        userPortraitUri=sp.getString(Const.LOGIN_PORTRAIT,"");
        initView();
    }

    private void initView() {
        tvUserid.setText(userid);
        tvUser.setText(username);
        if(TextUtils.isEmpty(userPortraitUri)){
            civIcon.setImageResource(R.mipmap.default_portrait);
        }else {
            ImageLoader.getInstance().displayImage(userPortraitUri, civIcon);
        }

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
                                groupsDAO.delete(userid);
                                friendInfoDAO.delete(userid);
                                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                                T.showShort(getActivity(),"退出成功");
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
                Intent intent=new Intent(getActivity(),PersonSettingActivity.class);
                getActivity().startActivity(intent);
//                startActivity(new Intent(getActivity(), PersonSettingActivity.class));
                break;
            default:
                break;
        }
    }


    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==0){
            initView();
        }
    }*/
}
