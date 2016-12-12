package com.min.smalltalk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.min.mylibrary.util.T;
import com.min.smalltalk.R;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.base.BaseRecyclerAdapter;
import com.min.smalltalk.base.BaseRecyclerHolder;
import com.min.smalltalk.bean.GroupFlexible;
import com.min.smalltalk.bean.GroupMember;
import com.min.smalltalk.wedget.ItemDivider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imageloader.core.ImageLoader;

/**
 * 群活动的详细信息
 */
public class FlexibleDetailActivity extends BaseActivity {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_group_activity_head)
    ImageView ivGroupActivityHead;
    @BindView(R.id.activity_name)
    TextView tvActivityName;
    @BindView(R.id.tv_activity_start_time)
    TextView tvActivityStartTime;
    @BindView(R.id.tv_activity_end_time)
    TextView tvActivityEndTime;
    @BindView(R.id.tv_activity_place)
    TextView tvActivityPlace;
    @BindView(R.id.tv_activity_content)
    TextView tvActivityContent;
    @BindView(R.id.btn_join_activity)
    Button btnJoinActivity;
    @BindView(R.id.rv_activity_user)
    RecyclerView rvActivityUser;

    private GroupFlexible groupFlexible;
    private String flexibleId,flexibleName,flexiblePort,flexibleStartTime,flexibleEndTime,flexiblePlace,flexibleContent;
    private List<GroupMember> list;
    private BaseRecyclerAdapter<GroupMember> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flexible_detail);
        ButterKnife.bind(this);
        Intent intent=getIntent();
        groupFlexible=intent.getParcelableExtra("flexible");
        flexibleId=groupFlexible.getFlexibleId();
        flexibleName=groupFlexible.getFlexibleName();
        flexibleStartTime=groupFlexible.getFlexibleStartTime();
        flexibleEndTime=groupFlexible.getFlexibleEndTime();
        flexiblePlace=groupFlexible.getFlexiblePlace();
        flexiblePort=groupFlexible.getFlexiblePortrait();
        flexibleContent=groupFlexible.getFlexibleContent();
        list=groupFlexible.getFlexibleList();
        initView();
        initAdapter();
    }

    private void initAdapter() {
        adapter=new BaseRecyclerAdapter<GroupMember>(mContext,list,R.layout.item_group) {
            @Override
            public void convert(BaseRecyclerHolder holder, GroupMember item, int position, boolean isScrolling) {
                holder.setImageByUrl(R.id.siv_group_head,item.getGroupPortraitUri());
                holder.setText(R.id.tv_group_name,item.getUserName());
            }
        };
        rvActivityUser.setAdapter(adapter);
        LinearLayoutManager lm=new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        rvActivityUser.setLayoutManager(lm);
        rvActivityUser.addItemDecoration(new ItemDivider(mContext,ItemDivider.VERTICAL_LIST));
    }

    private void initView() {
        if(!TextUtils.isEmpty(flexiblePort)){
            ImageLoader.getInstance().displayImage(flexiblePort,ivGroupActivityHead);
        }else {
            ivGroupActivityHead.setImageResource(R.mipmap.load_image_2);
        }
        tvTitle.setText("群活动");
        tvActivityName.setText(flexibleName);
        tvActivityStartTime.setText(flexibleStartTime);
        tvActivityEndTime.setText(flexibleEndTime);
        tvActivityPlace.setText(flexiblePlace);
        tvActivityContent.setText(flexibleContent);
    }

    @OnClick({R.id.iv_title_back, R.id.btn_join_activity})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.btn_join_activity:
                T.showShort(mContext,"no");
                break;
        }
    }
}
