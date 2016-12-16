package com.min.smalltalk.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.min.mylibrary.util.CommonUtils;
import com.min.mylibrary.util.L;
import com.min.mylibrary.util.T;
import com.min.mylibrary.widget.dialog.LoadDialog;
import com.min.smalltalk.App;
import com.min.smalltalk.MainActivity;
import com.min.smalltalk.R;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.bean.Code;
import com.min.smalltalk.bean.GroupMember;
import com.min.smalltalk.bean.Groups;
import com.min.smalltalk.bean.LoginBean;
import com.min.smalltalk.db.DBOpenHelper;
import com.min.smalltalk.db.FriendInfoDAOImpl;
import com.min.smalltalk.db.GroupMemberDAOImpl;
import com.min.smalltalk.db.GroupsDAOImpl;
import com.min.smalltalk.network.HttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import okhttp3.Call;

/**
 * 登录
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private AutoCompleteTextView et_user;
    private EditText et_pwd;
    private TextView tv_register;
    private Button btn_login;
    private String user;
    private String password;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private List<Groups> list = new ArrayList<>();
    private DBOpenHelper dbOpenHelper;  //SQLite
    private GroupsDAOImpl groupsDAO;
    private FriendInfoDAOImpl friendInfoDAO;
    private GroupMemberDAOImpl groupMemberDAO;
    private Groups mGroup;
    private List<GroupMember> mGroupMember;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbOpenHelper = new DBOpenHelper(mContext, "talk.db", null, 2);// 创建数据库文件
        dbOpenHelper.getWritableDatabase();
        groupsDAO= new GroupsDAOImpl(mContext);
        friendInfoDAO=new FriendInfoDAOImpl(mContext);
        groupMemberDAO=new GroupMemberDAOImpl(mContext);

        sharedPreferences=getSharedPreferences("config",this.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        initView();
    }

    private void initView() {
        et_user = (AutoCompleteTextView) findViewById(R.id.user);
        et_pwd = (EditText) findViewById(R.id.password);
        tv_register= (TextView) findViewById(R.id.tv_register);
        btn_login = (Button) findViewById(R.id.sign_in_button);
        tv_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_register:
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivityForResult(intent,0);
                break;
            case R.id.sign_in_button:
                LoadDialog.show(mContext);
                if(!CommonUtils.isNetConnect(mContext)){
                    T.showShort(mContext,R.string.no_network);
                    return;
                }
                user= et_user.getText().toString().trim();
                password = et_pwd.getText().toString().trim();
                login(user, password);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0){
            if(resultCode==RESULT_OK){
                Bundle bundle=data.getExtras();
                String phone=bundle.getString("phone");
                String password=bundle.getString("password");
                et_user.setText(phone);
                et_pwd.setText(password);
            }
        }
    }

    private void login(final String user, final String password) {
        if("".equals(user) || "".equals(password)) {
            Toast.makeText(LoginActivity.this,"用户名和密码不能为空",Toast.LENGTH_SHORT).show();
        }else if(user!=null && password!=null) {   ///?phone=18819493906&password=123456
            HttpUtils.postLoginRequest("/login", user, password, new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    T.showShort(mContext,"/login----"+e);
                }
                @Override
                public void onResponse(String response, int id) {
                    HttpUtils.setCookie(LoginActivity.this);
                    Gson gson=new Gson();
//                    Type type=new TypeToken<LoginResult>(){}.getType();
//                    LoginResult loginResult=gson.fromJson(response,type);
                    Type type=new TypeToken<Code<LoginBean>>(){}.getType();
                    Code<LoginBean> code=gson.fromJson(response,type);
                    int code1=code.getCode();
                    if(code1==200){
                        T.showShort(mContext,"登录成功");
                        uid=code.getMsg().getUserid();
                        String token=code.getMsg().getToken();
                        String nickName=code.getMsg().getNickname();
                        String portraitUri=HttpUtils.IMAGE_RUL+code.getMsg().getPortrait();
                        connect(token);
                        editor.putString("user",user);
                        editor.putString("password",password);
                        editor.putBoolean("login_message",true);
                        editor.putString("loginphone", user);
                        editor.putString("loginpassword", password);
                        editor.putString("loginid",uid);
                        editor.putString("loginToken", token);
                        editor.putString("loginnickname",nickName);
                        editor.putString("loginPortrait",portraitUri);
                        editor.commit();
                        LoadDialog.dismiss(mContext);
//                        initFriendInfo();
                        initGroups(uid);
//                        initData(uid);
//                        getGroupMembers();
//                        initGroupMember();
                        finish();
                    }else if(code1==0){
                        Toast.makeText(LoginActivity.this, "账号不存在！", Toast.LENGTH_SHORT).show();
                        LoadDialog.dismiss(mContext);
                    }else if(code1==1001){
                        T.showShort(mContext,"密码错误");
                        LoadDialog.dismiss(mContext);
                    }else if(code1==1000){
                        T.showShort(mContext,"账号禁止登录");
                        LoadDialog.dismiss(mContext);
                    }
                }
            });
        }else {
            Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
        }
    }

    private String groupId;
//    private String userId = getSharedPreferences("config", MODE_PRIVATE).getString(Const.LOGIN_ID, "");

    /**
     * 获取群组列表
     */
    private void initGroups(final String userId) {
        HttpUtils.postGroupListRequest("/group_data", userId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext, "/group_data-----" + e);
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code<List<Groups>>>() {
                }.getType();
                Code<List<Groups>> code = gson.fromJson(response, type);
                if (code.getCode() == 200) {
                    List<Groups> groups = code.getMsg();
                    for (Groups groups1 : groups) {
                        groupId=groups1.getGroupId();
                        String groupName=groups1.getGroupName();
                        String groupPort=HttpUtils.IMAGE_RUL + groups1.getGroupPortraitUri();
                        String role=groups1.getRole();
//                        list.add(new Groups(groupid, groupName, groupPort));
                        Groups groups2 = new Groups();
                        groups2.setUserId(userId);
                        groups2.setGroupId(groupId);  //groupId
                        groups2.setGroupName(groupName);  //groupName
                        groups2.setGroupPortraitUri(groupPort);
                        groups2.setRole(role);
                        groupsDAO.save(groups2);
                        L.e("-------------==-=-", "插入成功");// 用日志记录一个我们自定义的输出。可以在LogCat窗口中查看，
                    }
                    LoadDialog.dismiss(mContext);
                } else {
                    LoadDialog.dismiss(mContext);
                }
            }
        });
    }

    /**
     * 好友列表
     *//*
    private void initData(final String myId) {
        HttpUtils.postRequest("/friends",myId , new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext, "friends-----" + e);
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code<List<FriendInfo>>>() {
                }.getType();
                Code<List<FriendInfo>> code = gson.fromJson(response, type);
                if (code.getCode() == 200) {
                    List<FriendInfo> list = code.getMsg();
                    for (FriendInfo friend : list) {
                        String userId=friend.getUserId();
                        String name=friend.getName();
                        String portrait=HttpUtils.IMAGE_RUL+friend.getPortraitUri();
                        String displayName=friend.getDisplayName();
                        String phone=friend.getPhone();
                        String email=friend.getEmail();
                        FriendInfo friendInfo=new FriendInfo();
                        friendInfo.setMyId(myId);
                        friendInfo.setUserId(userId);
                        friendInfo.setName(name);
                        friendInfo.setPortraitUri(portrait);
                        friendInfo.setDisplayName(displayName);
                        friendInfo.setPhone(phone);
                        friendInfo.setEmail(email);
                        //存进Sqlite
                        friendInfoDAO.save(friendInfo);
                        L.e("---------===", "插入成功");
                    }

                } else {
                    T.showShort(mContext,"您暂没好友，请点击有上角按钮来添加好友吧！");
                }
            }
        });
    }*/


    /**
     * 群组成员
     */
    private void getGroupMembers() {
        HttpUtils.postGroupsRequest("/group_member", groupId,uid, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext, "group_member------" + "网络连接错误");
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code<List<GroupMember>>>() {}.getType();
                Code<List<GroupMember>> code = gson.fromJson(response, type);
                if (code.getCode() == 200) {
                    mGroupMember = code.getMsg();
                    for(GroupMember member:mGroupMember){
                        String userId=member.getUserId();
                        String userName=member.getUserName();
                        String userPortraitUri=member.getUserPortraitUri();
                        GroupMember groupMember=new GroupMember();
                        groupMember.setUserId(userId);
                        groupMember.setUserName(userName);
                        groupMember.setUserPortraitUri(userPortraitUri);
                        groupMemberDAO.save(groupMember);
                        Log.i("-------------==-=-", "插入成功");// 用日志记录一个我们自定义的输出。可以在LogCat窗口中查看，
                    }
                    /*if (mGroupMember != null && mGroupMember.size() > 0) {
                        tvGroupMemberSize.setText("全部成员" + "(" + mGroupMember.size() + ")");
                        mGridView.setAdapter(new MyGridView(mContext, mGroupMember, isCreator,mGroup));
                    } else {
                        return;
                    }
                    for (GroupMember member : mGroupMember) {
                        String s = member.getUserId();
                        if (userId.equals(s)) {
                            if (!TextUtils.isEmpty(member.getDisplayName())) {
                                tvGroupName.setText(member.getDisplayName());
                            } else {
                                tvGroupName.setText("无");
                            }
                        }
                    }*/
                }
            }
        });
    }





    private void connect(String token) {
        final Message message=new Message();
        if(getApplicationInfo().packageName.equals(App.getCurProcessName(getApplicationContext()))){
            /**
             * IMKit SDK调用第二步,建立与服务器的连接
             */
            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                //Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                @Override
                public void onTokenIncorrect() {
                    message.what=0;
                    handler.sendMessage(message);
                }
                //连接融云成功
                @Override
                public void onSuccess(String s) {
                    message.what=1;
                    message.obj=s;
                    handler.sendMessage(message);
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 * http://www.rongcloud.cn/docs/android.html#常见错误码
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    message.what=2;
                    message.obj=errorCode;
                    handler.sendMessage(message);
                }
            });
        }
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    T.showShort(mContext,"Token 错误，Token 已经过期");
                    break;
                case 1:
                    T.showShort(mContext, "--onSuccess--" + msg.obj);
                    startActivity(new Intent(mContext, MainActivity.class));
                    finish();
                    break;
                case 2:
                    T.showShort(mContext,"--"+msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){  //按下的如果是BACK，同时没有重复
            LoginActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}














