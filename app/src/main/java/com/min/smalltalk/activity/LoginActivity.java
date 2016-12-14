package com.min.smalltalk.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.min.mylibrary.util.T;
import com.min.mylibrary.widget.dialog.LoadDialog;
import com.min.smalltalk.App;
import com.min.smalltalk.MainActivity;
import com.min.smalltalk.R;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.bean.Code;
import com.min.smalltalk.bean.LoginBean;
import com.min.smalltalk.network.HttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        }else if(user!=null && password!=null) {
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
                        String uid=code.getMsg().getUserid();
                        String token=code.getMsg().getToken();
                        String nickName=code.getMsg().getNickname();
                        String portraitUri=code.getMsg().getPortrait();
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
                        finish();
                    }else if(code1==0){
                        Toast.makeText(LoginActivity.this, "账号不存在！", Toast.LENGTH_SHORT).show();
                        LoadDialog.dismiss(mContext);
                    }else if(code1==1001){
                        T.showShort(mContext,"密码错误");
                    }else if(code1==1000){
                        T.showShort(mContext,"账号禁止登录");
                    }
                }
            });
        }else {
            Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
        }
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














