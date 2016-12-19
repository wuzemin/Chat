package com.min.smalltalk.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.min.mylibrary.util.PhotoUtils;
import com.min.mylibrary.util.T;
import com.min.mylibrary.widget.dialog.BottomMenuDialog;
import com.min.mylibrary.widget.image.CircleImageView;
import com.min.smalltalk.R;
import com.min.smalltalk.activity.LoginActivity;
import com.min.smalltalk.activity.MyPopuWindow;
import com.min.smalltalk.activity.ZxingActivity;
import com.min.smalltalk.bean.Code;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.db.FriendInfoDAOImpl;
import com.min.smalltalk.db.GroupMemberDAOImpl;
import com.min.smalltalk.db.GroupsDAOImpl;
import com.min.smalltalk.network.HttpUtils;
import com.min.smalltalk.utils.CameraUtils;
import com.min.smalltalk.utils.DateUtils;
import com.min.smalltalk.wedget.Wheel.JudgeDate;
import com.min.smalltalk.wedget.Wheel.ScreenInfo;
import com.min.smalltalk.wedget.Wheel.WheelMain;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imageloader.core.ImageLoader;
import okhttp3.Call;

import static android.content.Context.MODE_PRIVATE;
import static com.min.smalltalk.R.id.civ_icon;


public class PersonalFragment extends Fragment {

    @BindView(civ_icon)
    CircleImageView civIcon;
    @BindView(R.id.btn_exit)
    Button btnExit;
    @BindView(R.id.tv_userid)
    TextView tvUserid;
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
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.rl_address)
    RelativeLayout rlAddress;
    @BindView(R.id.rl_phone)
    RelativeLayout rlPhone;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_age)
    TextView tvAge;

    private SharedPreferences sp;

    private GroupsDAOImpl groupsDAO;
    private GroupMemberDAOImpl groupMemberDAO;
    private FriendInfoDAOImpl friendInfoDAO;

    private String userId, nickName, phone,userPortraitUri;
    private String email;
    private String sex1;
    private int sex;
    private String birthday;
    private String address;
    private int age;

    private PhotoUtils photoUtils;
    private Uri selectUri;
    private File imageFile;
    private String imageUrl;
    private BottomMenuDialog dialog;
    private WheelMain wheelMainDate;
    private String beginTime;
    private EditText editText;
    private int str;
    private SharedPreferences.Editor editor;
//    private BitmapUtils bitmapUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        ButterKnife.bind(this, view);
//        bitmapUtils=new BitmapUtils(getContext());
        groupsDAO=new GroupsDAOImpl(getActivity());
        friendInfoDAO=new FriendInfoDAOImpl(getActivity());
        sp = getActivity().getSharedPreferences("config", MODE_PRIVATE);
        editor=sp.edit();
        initView();
        return view;
    }


    /*@Override
    public void onResume() {
        super.onResume();
        sp = getActivity().getSharedPreferences("config", MODE_PRIVATE);
        userId = sp.getString(Const.LOGIN_ID, "");
        nickName=sp.getString(Const.LOGIN_NICKNAME,"");
        phone = sp.getString(Const.LOGIN_PHONE, "");
        userPortraitUri=sp.getString(Const.LOGIN_PORTRAIT,"");
        initView();
    }*/

    private void initView() {
        userId = sp.getString(Const.LOGIN_ID, "");
        nickName=sp.getString(Const.LOGIN_NICKNAME,"");
        phone = sp.getString(Const.LOGIN_PHONE, "");
        userPortraitUri=sp.getString(Const.LOGIN_PORTRAIT,"");
        sex1=sp.getString(Const.LOGIN_SEX,"");
        birthday=sp.getString(Const.LOGIN_BIRTHDAY,"");
        age=sp.getInt(Const.LOGIN_AGE,0);
        address=sp.getString(Const.LOGIN_ADDRESS,"");
        email=sp.getString(Const.LOGIN_EMAIL,"");

        tvUserid.setText(userId);
        tvNickname.setText(nickName);
        tvSex.setText(sex1);
        tvBirthday.setText(birthday);
        tvAge.setText(age+"岁");
        tvAddress.setText(address);
        tvPhone.setText(phone);
        tvEmail.setText(email);
        if(TextUtils.isEmpty(userPortraitUri)){
            civIcon.setImageResource(R.mipmap.default_portrait);
        }else {
            ImageLoader.getInstance().displayImage(userPortraitUri, civIcon);
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        str = Integer.parseInt(formatter.format(curDate));

    }

    @OnClick({civ_icon, R.id.btn_exit, R.id.rl_nickname, R.id.rl_sex, R.id.rl_birthday,
            R.id.rl_QR_code, R.id.rl_email, R.id.rl_phone, R.id.rl_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case civ_icon:
                MyPopuWindow myPopuWindow = new MyPopuWindow(getActivity(),getContext(),this);
                myPopuWindow.showPopupWindow();
                break;
            case R.id.btn_exit:   //退出
                new AlertDialog.Builder(getActivity())
                        .setTitle("退出")
                        .setPositiveButton("确定退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sp.edit().clear().commit();
                                groupsDAO.delete(userId);
                                friendInfoDAO.delete(userId);
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
            case R.id.rl_nickname:   //昵称
                editText=new EditText(getActivity());
                new android.app.AlertDialog.Builder(getActivity())
                        .setTitle("修改昵称")
                        .setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                nickName=editText.getText().toString();
                                changePerson(1);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                break;
            case R.id.rl_sex:   //性别
                final String[] test = new String[]{"男", "女","保密"};
                android.app.AlertDialog.Builder dialog_sex = new android.app.AlertDialog.Builder(getActivity());
                dialog_sex.setTitle("选择性别");
                dialog_sex.setSingleChoiceItems(test, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String string=test[i];
                        if("男".equals(string)){
                            sex1=string;
                            sex=1;
                        }else if("女".equals(string)){
                            sex1=string;
                            sex=2;
                        }else {
                            sex1=string;
                            sex=0;
                        }
                        dialogInterface.dismiss();
                        tvSex.setText(string);
                        changePerson(2);
                    }
                });
                dialog_sex.create().show();
                break;
            case R.id.rl_birthday:   //生日
                showSTimePopupWindow();
                break;
            case R.id.rl_QR_code:
                Intent intent1=new Intent(getActivity(),ZxingActivity.class);
                intent1.putExtra("Id",userId);
                startActivity(intent1);
                break;
            case R.id.rl_email:  //邮箱
                editText=new EditText(getActivity());
                new android.app.AlertDialog.Builder(getActivity())
                        .setTitle("修改邮箱")
                        .setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                email=editText.getText().toString();
                                tvEmail.setText(email);
                                changePerson(3);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                break;
            case R.id.rl_address:  //地址
                editText=new EditText(getActivity());
                new android.app.AlertDialog.Builder(getActivity())
                        .setTitle("修改地址")
                        .setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                address=editText.getText().toString();
                                tvAddress.setText(address);
                                changePerson(4);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                break;
            case R.id.rl_phone:  //手机
                new AlertDialog.Builder(getActivity())
                        .setTitle("修改手机号")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                phone=editText.getText().toString();
                                changePerson(6);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
                break;
            default:
                break;
        }
    }

    /**
     * 修改个人资料
     */
    private void changePerson(final int index) {
        JSONArray jsonArray=new JSONArray();
        JSONObject row=new JSONObject();
        try {
            row.put("userId",userId);
            row.put("nickname",nickName);
            row.put("sex",sex);
            row.put("email",email);
            row.put("phone",phone);
            row.put("address",address);
            row.put("birth_date",birthday);
            row.put("age",age);
            jsonArray.put(row);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String string=jsonArray.toString();
        if(index!=0){
            HttpUtils.postChangePerson("/editUserInfo", string, new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    T.showShort(getActivity(),"/editUserInfo----"+e);
                }

                @Override
                public void onResponse(String response, int id) {
                    Gson gson=new Gson();
                    Type type=new TypeToken<Code<Integer>>(){}.getType();
                    Code<Integer> code = gson.fromJson(response,type);
                    if(code.getCode()==200){
                        switch (index){
                            case 1:   //昵称
                                editor.putString(Const.LOGIN_NICKNAME, nickName);
                                editor.commit();
                                tvNickname.setText(nickName);
                                T.showShort(getActivity(),"修改昵称成功");
                                break;
                            case 2:   //性别
                                editor.putString(Const.LOGIN_SEX, sex1);
                                editor.commit();
                                T.showShort(getActivity(),"修改性别成功");
                                break;
                            case 3:   //邮箱
                                editor.putString(Const.LOGIN_EMAIL, email);
                                editor.commit();
                                T.showShort(getActivity(),"修改邮箱成功");
                                break;
                            case 4:   //地址
                                editor.putString(Const.LOGIN_ADDRESS, address);
                                editor.commit();
                                T.showShort(getActivity(),"修改地址成功");
                                break;
                            case 5:   //生日
                                tvAge.setText(age+"");
                                editor.putString(Const.LOGIN_BIRTHDAY, birthday);
                                editor.putInt(Const.LOGIN_AGE,0);
                                editor.commit();
                                T.showShort(getActivity(),"修改生日成功");
                                break;
                            case 6:   //电话
                                editor.putString(Const.LOGIN_PHONE, phone);
                                editor.commit();
                                tvPhone.setText(phone);
                                T.showShort(getActivity(),"修改电话成功");
                                break;
                            default:
                                break;

                        }
                    }else {
                        T.showShort(getActivity(),"修改失败");
                    }
                }
            });
        }else {
            HttpUtils.postChangePerson("/editUserInfo", string, imageFile, new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    T.showShort(getActivity(), "/editUserInfo----" + e);
                }

                @Override
                public void onResponse(String response, int id) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<Code<Integer>>() {}.getType();
                    Code<Integer> code = gson.fromJson(response, type);
                    if (code.getCode() == 200) {
                        editor.putString(Const.LOGIN_PORTRAIT, imageUrl);
                        editor.commit();
                        T.showShort(getActivity(), "修改成功");
                    } else {
                        T.showShort(getActivity(), "修改失败");
                    }
                }
            });
        }
    }

    /**
     * 时间
     */
    private void showSTimePopupWindow() {
        WindowManager manager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = manager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        View menuView = LayoutInflater.from(getActivity()).inflate(R.layout.popupwindow_select_time, null);
        final PopupWindow mPopupWindow = new PopupWindow(menuView, (int) (width * 0.8),
                ActionBar.LayoutParams.WRAP_CONTENT);
        ScreenInfo screenInfoDate = new ScreenInfo(getActivity());
        wheelMainDate = new WheelMain(menuView, true);
        wheelMainDate.screenheight = screenInfoDate.getHeight();
        String time = DateUtils.currentMonth().toString();
        Calendar calendar = Calendar.getInstance();
        if (JudgeDate.isDate(time, "yyyy-MM-DD")) {
            try {
                calendar.setTime(new Date(time));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        wheelMainDate.initDateTimePicker(year, month, day, hours, minute);
        final String currentTime = wheelMainDate.getTime().toString();
        mPopupWindow.setAnimationStyle(R.style.AnimationPreview);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.showAtLocation(rlBirthday, Gravity.CENTER, 0, 0);
        mPopupWindow.setOnDismissListener(new poponDismissListener());
        backgroundAlpha(0.6f);
        TextView tv_cancle = (TextView) menuView.findViewById(R.id.tv_cancle);
        TextView tv_ensure = (TextView) menuView.findViewById(R.id.tv_ensure);
        TextView tv_pop_title = (TextView) menuView.findViewById(R.id.tv_pop_title);
        tv_pop_title.setText("选择时间");
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mPopupWindow.dismiss();
                backgroundAlpha(1f);
            }
        });
        tv_ensure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                beginTime = wheelMainDate.getTime().toString();
                birthday=DateUtils.formateStringH(beginTime, DateUtils.yyyyMMddHHmm);
                tvBirthday.setText(birthday);
                int birth= Integer.parseInt(birthday.substring(0,4));
                age= str-birth;
                changePerson(5);
                mPopupWindow.dismiss();
                backgroundAlpha(1f);
            }
        });
    }

    class poponDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getActivity().getWindow().setAttributes(lp);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("234", "5555555555555555555555555555555555555555555555555555");
        if (requestCode == CameraUtils.CHOOSE_FROM_CAMERA) {  //调用摄像头
            if (resultCode == Activity.RESULT_OK) {
                //呼叫裁剪应用程序
                Log.i("234", "111111111111111111111111111111111111111111111");
                CameraUtils.cropPhotos(getContext(), this, CameraUtils.imgUri);
            }
        }
        if (requestCode == CameraUtils.PHOTO_CROP) {  //图片裁剪
            if (resultCode == Activity.RESULT_OK) {
                CameraUtils.saveMyPhoto(getContext(), CameraUtils.img.getAbsolutePath());
//                imageView.setImageBitmap(CameraUtils.getBitmap(this));
                imageFile=new File(CameraUtils.getMyPhoto(getContext()));
                Log.i("1256", imageFile+" 11");
                changePerson(0);
                ImageLoader.getInstance().displayImage("file://" + CameraUtils.getMyPhoto(getActivity()), civIcon);
//                bitmapUtils.display(civIcon, CameraUtils.getMyPhoto(getContext()));
            }
        }
        if (requestCode == CameraUtils.CHOOSE_FROM_ALBUM) {  //从图库取出照片
            if (resultCode == Activity.RESULT_OK && data != null) {

                CameraUtils.cropPhotos(getContext(), this, data.getData());
//                civIcon.setImageBitmap(CameraUtils.getBitmap(getActivity()));
                ImageLoader.getInstance().displayImage("file://"+CameraUtils.getMyPhoto(getActivity()),civIcon);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

      /**
     * 图片*/

    /*private void setPortraitChangListener() {
        photoUtils = new PhotoUtils(new PhotoUtils.OnPhotoResultListener() {
            @Override
            public void onPhotoResult(Uri uri) {
                if (uri != null && !TextUtils.isEmpty(uri.getPath())) {
                    selectUri = uri;
                    LoadDialog.show(getActivity());
                    imageFile = new File(selectUri.getPath());
                    imageUrl = selectUri.toString();
                    ImageLoader.getInstance().displayImage(imageUrl, civIcon);
                    changePerson(0);
                    LoadDialog.dismiss(getActivity());
                }
            }

            @Override
            public void onPhotoCancel() {

            }
        });
    }*/
}
