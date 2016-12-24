package com.min.smalltalk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.min.mylibrary.util.T;
import com.min.smalltalk.R;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.bean.Code;
import com.min.smalltalk.bean.GroupVote;
import com.min.smalltalk.network.HttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class VoteDetailActivity extends BaseActivity {

    @BindView(R.id.btn)
    Button btn;

    private String group_id;
    private String vote_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_detail);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Intent intent=getIntent();
        group_id=intent.getStringExtra("group_id");
        vote_id=intent.getStringExtra("vote_id");

    }

    @OnClick(R.id.btn)
    public void onClick() {
        HttpUtils.postVoteDetails("/vote_details", group_id, vote_id, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext,"/vote_details------"+e);
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson=new Gson();
                Type type=new TypeToken<Code<GroupVote>>(){}.getType();
                Code<GroupVote> code=gson.fromJson(response,type);
                if(code.getCode()==200){
                    GroupVote groupVote=code.getMsg();
                    String vote_id=groupVote.getVote_id();
                    String vote_title=groupVote.getVote_title();
                    String add_time=groupVote.getAdd_time();
                    String end_time=groupVote.getEnd_time();
                    List<String> option=groupVote.getOption();
                }else {
                    T.showShort(mContext,"空");
                }
            }
        });
    }
}
