package com.min.smalltalk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.min.mylibrary.util.T;
import com.min.smalltalk.R;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.base.BaseRecyclerAdapter;
import com.min.smalltalk.base.BaseRecyclerHolder;
import com.min.smalltalk.bean.Code;
import com.min.smalltalk.bean.GroupVote;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.network.HttpUtils;
import com.min.smalltalk.wedget.ItemDivider;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class GroupVoteActivity extends BaseActivity {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;
    @BindView(R.id.rv_group_vote)
    RecyclerView rvGroupVote;

    private BaseRecyclerAdapter<GroupVote> adapter;
    private List<GroupVote> list = new ArrayList<>();

    private String groupId;
    private String voteId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_vote);
        ButterKnife.bind(this);
        initView();
        initListView();

    }

    private void initView() {
        tvTitle.setText("投票活动");
        tvTitleRight.setVisibility(View.VISIBLE);
        tvTitleRight.setText("添加");
    }

    private void initListView() {
        String userid=getSharedPreferences("config",MODE_PRIVATE).getString(Const.LOGIN_ID,"");
        HttpUtils.postGroupActiivity("/listVote", userid, groupId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext, "/group/groupAcitivity------onError");
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code<List<GroupVote>>>() {}.getType();
                Code<List<GroupVote>> code = gson.fromJson(response, type);
                if (code.getCode() == 200) {
                    List<GroupVote> voteList = code.getMsg();
                    for (GroupVote groupVote : voteList) {
                        voteId=groupVote.getVoteId();
                        String voteTitle=groupVote.getVoidTitle();
                        String voteCreate=groupVote.getCreateTime();
                        int status=groupVote.getStatus();
                        List<String> contentList=groupVote.getContentList();
                        String groupId=groupVote.getGroupId();

                    }
                    initAdapter();
                }else{
                    T.showShort(mContext,"没有群活动");
                }
            }
        });
    }

    private void initAdapter() {
        adapter = new BaseRecyclerAdapter<GroupVote>(mContext, list, R.layout.item_group_vote) {
            @Override
            public void convert(BaseRecyclerHolder holder, GroupVote item, int position, boolean isScrolling) {
                holder.setText(R.id.tv_vote_title, item.getVoidTitle());
                holder.setText(R.id.tv_vote_time,item.getCreateTime());
                int status=item.getStatus();
                if(status==0){
                    holder.setText(R.id.tv_vote_status,"进行中");
                }else {
                    holder.setText(R.id.tv_vote_status,"已结束");
                }
            }
        };
        rvGroupVote.setAdapter(adapter);
        LinearLayoutManager lm=new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        rvGroupVote.setLayoutManager(lm);
        rvGroupVote.addItemDecoration(new ItemDivider(mContext,ItemDivider.VERTICAL_LIST));

        //点击事件
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                Intent intent=new Intent(mContext,VoteDetailActivity.class);
                intent.putExtra("voteId",voteId);
                startActivity(intent);
            }
        });
    }

    @OnClick({R.id.iv_title_back, R.id.tv_title_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.tv_title_right:
                break;
        }
    }
}
