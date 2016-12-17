package com.min.smalltalk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.min.mylibrary.widget.dialog.LoadDialog;
import com.min.smalltalk.R;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.base.BaseRecyclerAdapter;
import com.min.smalltalk.base.BaseRecyclerHolder;
import com.min.smalltalk.bean.Groups;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.db.GroupsDAOImpl;
import com.min.smalltalk.wedget.ItemDivider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;

/**
 * 群列表
 */
public class GroupListActivity extends BaseActivity {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_group_list)
    RecyclerView rvGroupList;
    @BindView(R.id.tv_no_group)
    TextView tvNoGroup;

    private BaseRecyclerAdapter<Groups> adapter;
    private List<Groups> list = new ArrayList<>();
    private String groupName;
    private String groupId;
    private String groupPortraitUri;

    private GroupsDAOImpl sqLiteDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        ButterKnife.bind(this);

        sqLiteDAO= new GroupsDAOImpl(mContext);

        tvTitle.setText("我的群组");
        ivTitleRight.setImageResource(R.mipmap.add_more);
        LoadDialog.show(mContext);
        initData();
    }

    private void initData() {
        String userId=getSharedPreferences("config",MODE_PRIVATE).getString(Const.LOGIN_ID,"");
        list=sqLiteDAO.findAll(userId);
        if(list.size()>0){
            initAdapter();
        }else {
            rvGroupList.setVisibility(View.GONE);
            tvNoGroup.setVisibility(View.VISIBLE);
            tvNoGroup.setText("你暂时未加入任何一个群组");
            LoadDialog.dismiss(mContext);
        }
    }

    private void initAdapter() {
        adapter = new BaseRecyclerAdapter<Groups>(mContext, list, R.layout.item_group) {
            @Override
            public void convert(BaseRecyclerHolder holder, Groups item, int position, boolean isScrolling) {
//                groupPortraitUri=list.get(position).getGroupPortraitUri();
                if (list.get(position).getGroupPortraitUri() != null) {
                    holder.setImageByUrl(R.id.siv_group_head, list.get(position).getGroupPortraitUri());
                } else {
                    holder.setImageResource(R.id.siv_group_head, R.mipmap.default_chatroom);
                }
                holder.setText(R.id.tv_group_name, list.get(position).getGroupName());
            }
        };
        rvGroupList.setAdapter(adapter);
        LinearLayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rvGroupList.setLayoutManager(lm);
        rvGroupList.addItemDecoration(new ItemDivider(this, ItemDivider.VERTICAL_LIST));
        adapter.notifyDataSetChanged();
        initListItemClick();
        LoadDialog.dismiss(mContext);
    }

    private void initListItemClick() {
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                RongIM.getInstance().startGroupChat(mContext, list.get(position).getGroupId(), list.get(position).getGroupName());
            }
        });
    }


    @OnClick({R.id.iv_title_back, R.id.iv_title_right})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_title_back:
                GroupListActivity.this.finish();
                break;
            case R.id.iv_title_right:
                startActivity(new Intent(mContext,SelectFriendsActivity.class));
                break;
        }
    }
}
