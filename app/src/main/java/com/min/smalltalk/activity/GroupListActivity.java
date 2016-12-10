package com.min.smalltalk.activity;

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
import com.min.smalltalk.bean.Groups;
import com.min.smalltalk.network.HttpUtils;
import com.min.smalltalk.wedget.ItemDivider;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import okhttp3.Call;

/**
 * 群列表
 */
public class GroupListActivity extends BaseActivity {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_group_list)
    RecyclerView rvGroupList;
    @BindView(R.id.tv_no_group)
    TextView tvNoGroup;

    private BaseRecyclerAdapter<Groups> adapter;
    private List<Groups> list=new ArrayList<>();
    private String groupName;
    private String groupId;
    private String groupPortraitUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        ButterKnife.bind(this);
        tvTitle.setText("我的群组");
        initData();
    }

    private void initData() {
        HttpUtils.postGroupListRequest("/group/grouplist", new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext,R.string.error_network);
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson=new Gson();
                Type type=new TypeToken<Code<List<Groups>>>(){}.getType();
                Code<List<Groups>> code=gson.fromJson(response,type);
                if(code.getCode()==200){
                    List<Groups> groups=code.getMsg();
                    if(groups!=null && groups.size()>1) {
                        for (Groups groups1 : groups)
                            list.add(new Groups(groups1.getGroupId(), groups1.getGroupName(), groups1.getGroupPortraitUri()));
                    }else{
                        tvNoGroup.setVisibility(View.VISIBLE);
                        return;
                    }
                    initAdapter();
                }
            }
        });
    }

    private void initAdapter() {
        adapter = new BaseRecyclerAdapter<Groups>(mContext,list,R.layout.item_group) {
            @Override
            public void convert(BaseRecyclerHolder holder, Groups item, int position, boolean isScrolling) {
//                groupPortraitUri=list.get(position).getGroupPortraitUri();
                if(list.get(position).getGroupPortraitUri()!=null){
                    holder.setImageByUrl(R.id.siv_group_head, list.get(position).getGroupPortraitUri());
                }else {
                    holder.setImageResource(R.id.siv_group_head,R.mipmap.default_chatroom);
                }
                holder.setText(R.id.tv_group_name,list.get(position).getGroupName());
            }
        };
        rvGroupList.setAdapter(adapter);
        LinearLayoutManager lm=new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        rvGroupList.setLayoutManager(lm);
        rvGroupList.addItemDecoration(new ItemDivider(this,ItemDivider.VERTICAL_LIST));
        adapter.notifyDataSetChanged();
        initListItemClick();
    }

    private void initListItemClick() {
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                RongIM.getInstance().startGroupChat(mContext,list.get(position).getGroupId(),list.get(position).getGroupName());
            }
        });
    }



    @OnClick(R.id.iv_title_back)
    public void onClick() {
        GroupListActivity.this.finish();
    }
}
