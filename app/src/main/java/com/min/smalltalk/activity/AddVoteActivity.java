package com.min.smalltalk.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.min.mylibrary.util.T;
import com.min.mylibrary.widget.ClearWriteEditText;
import com.min.smalltalk.R;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.base.BaseRecyclerAdapter;
import com.min.smalltalk.base.BaseRecyclerHolder;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.wedget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddVoteActivity extends BaseActivity {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_vote_title)
    ClearWriteEditText etVoteTitle;
    @BindView(R.id.rv_vote)
    RecyclerView rvVote;
    @BindView(R.id.sw_vote_multiselect)
    SwitchButton swVoteMultiselect;
    @BindView(R.id.tv_vote_period)
    TextView tvVotePeriod;
    @BindView(R.id.ll_vote_period)
    LinearLayout llVotePeriod;
    @BindView(R.id.btn_vote)
    Button btnVote;

    private BaseRecyclerAdapter<String> adapter;
    private SharedPreferences sp;
    private String userId;
    private List<String> contentList=new ArrayList<>(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vote);
        ButterKnife.bind(this);
        initView();
        initAdapter();
    }

    private void initView() {
        sp=getSharedPreferences("config",MODE_PRIVATE);
        userId=sp.getString(Const.LOGIN_ID,"");
    }

    private void initAdapter() {
        adapter=new BaseRecyclerAdapter<String>(mContext,contentList,R.id.layout_container) {
            @Override
            public void convert(BaseRecyclerHolder holder, String item, int position, boolean isScrolling) {

            }
        };
    }

    @OnClick({R.id.iv_title_back, R.id.sw_vote_multiselect, R.id.ll_vote_period, R.id.btn_vote})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.sw_vote_multiselect:
                break;
            case R.id.ll_vote_period:
                break;
            case R.id.btn_vote:
                String title=etVoteTitle.getText().toString();
                if(TextUtils.isEmpty(title)){
                    T.showShort(mContext,"标题不能为空");
                    return;
                }

                break;
        }
    }
}
