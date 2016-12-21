package com.min.smalltalk.message.module;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;
import com.min.mylibrary.util.T;
import com.min.smalltalk.R;
import com.min.smalltalk.wedget.audio.AudioRecordFunc;

import java.util.HashMap;
import java.util.LinkedHashMap;

import io.rong.imkit.RongExtension;
import io.rong.imkit.plugin.IPluginModule;

/**
 * Created by Min on 2016/12/21.
 */

public class MyStartRecognizePlugin implements IPluginModule {

    private Context context;

    HandlerThread mWorkThread;
    Handler mUploadHandler;
    //
    private SpeechRecognizer mIat;  //语音听写对象
    private HashMap<String,String> mIatResults=new LinkedHashMap<>();

    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private Toast mToast;

    private String message;
    private AudioRecordFunc audioRecordFunc;

    private boolean flag=false;

    /**
     *
     * @param context 上下文
     */
    public void init(Context context) {
        this.context=context;
        /*mSharedPreferences = context.getSharedPreferences(IatSettings.PREFER_NAME,
                Activity.MODE_PRIVATE);*/
        mWorkThread = new HandlerThread("RongDemo");
        mWorkThread.start();
        mUploadHandler = new Handler(mWorkThread.getLooper());

    }

    @Override
    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.mipmap.ic_launcher);
    }

    @Override
    public String obtainTitle(Context context) {
        return context.getString(R.string.start_audio);
    }

    @Override
    public void onClick(Fragment fragment, RongExtension rongExtension) {
        T.showShort(context,"开始说话");
        audioRecordFunc= AudioRecordFunc.getInstance();
        audioRecordFunc.startRecordAndFile();
    }

    @Override
    public void onActivityResult(int i, int i1, Intent intent) {

    }
}
