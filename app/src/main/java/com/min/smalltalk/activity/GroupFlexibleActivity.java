package com.min.smalltalk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.min.smalltalk.bean.GroupFlexible;
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

/**
 * 群活动
 */
public class GroupFlexibleActivity extends BaseActivity {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;
    @BindView(R.id.rv_group_activity)
    RecyclerView rvGroupActivity;

    private BaseRecyclerAdapter<GroupFlexible> adapter;
    private List<GroupFlexible> list = new ArrayList<>();

    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_flexible);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");
        initView();
        initListView();
    }

    private void initListView() {
        String userid=getSharedPreferences("config",MODE_PRIVATE).getString(Const.LOGIN_ID,"");
        HttpUtils.postGroupActiivity("/listActives", userid, groupId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext, "/group/groupAcitivity------onError");
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code<List<GroupFlexible>>>() {
                }.getType();
                Code<List<GroupFlexible>> code = gson.fromJson(response, type);
                if (code.getCode() == 200) {
                    List<GroupFlexible> flexibleList = code.getMsg();
                    for (GroupFlexible groupFlexible : flexibleList) {
                        String flexibleId = groupFlexible.getActives_id();
                        String flexibleName = groupFlexible.getActives_title();
                        String flexiblePort = groupFlexible.getActives_image();
                        String flexibleStartTime = groupFlexible.getActives_start();
                        String flexibleEndTime=groupFlexible.getActives_end();
                        String flexiblePlace = groupFlexible.getActives_address();
                        String flexibleContent = groupFlexible.getActives_content();
                        list.add(new GroupFlexible(flexibleId, flexibleName, flexiblePort,
                                flexibleStartTime,flexibleEndTime, flexiblePlace, flexibleContent));
                    }
                    initAdapter();
                }else{
                    T.showShort(mContext,"没有群活动");
                }
            }
        });
    }

    private void initAdapter() {
        adapter = new BaseRecyclerAdapter<GroupFlexible>(mContext, list, R.layout.item_group_activity) {
            @Override
            public void convert(BaseRecyclerHolder holder, GroupFlexible item, int position, boolean isScrolling) {
                String activityPort = list.get(position).getActives_image();
                if (!TextUtils.isEmpty(activityPort)) {
                    holder.setImageByUrl(R.id.iv_group_activity_head, item.getActives_image());
                } else {
                    holder.setImageResource(R.id.iv_group_activity_head, R.mipmap.load_image_2);
                }
                holder.setText(R.id.activity_name, item.getActives_title());
                holder.setText(R.id.tv_activity_start_time, item.getActives_start());
                holder.setText(R.id.tv_activity_end_time,item.getActives_end());
                holder.setText(R.id.tv_activity_place, item.getActives_address());
                holder.setText(R.id.tv_activity_content, item.getActives_content());
            }
        };
        rvGroupActivity.setAdapter(adapter);
        LinearLayoutManager lm=new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        rvGroupActivity.setLayoutManager(lm);
        rvGroupActivity.addItemDecoration(new ItemDivider(mContext,ItemDivider.VERTICAL_LIST));

        //点击事件
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                Intent intent=new Intent(GroupFlexibleActivity.this,FlexibleDetailActivity.class);
                intent.putExtra("flexible",list.get(position));
                startActivity(intent);
            }
        });
    }

    private void initView() {
        tvTitle.setText("群活动");
        tvTitleRight.setVisibility(View.VISIBLE);
        tvTitleRight.setText("添加");
    }

    @OnClick({R.id.iv_title_back, R.id.tv_title_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.tv_title_right:
                Intent intent=new Intent(mContext,GroupAddFlexibleActivity.class);
                intent.putExtra("group_id",groupId);
                startActivity(intent);
                break;
        }
    }
}
