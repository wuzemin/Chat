package com.min.smalltalk.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.min.mylibrary.util.T;
import com.min.smalltalk.R;
import com.min.smalltalk.adapter.ClaimFriendsAdapter;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.bean.ClaimFriends;
import com.min.smalltalk.bean.Code;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.network.HttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class ClaimFriendsActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, ClaimFriendsAdapter.OnItemButtonClick {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.listView)
    ListView listView;

    private List<ClaimFriends> list;
    private ClaimFriendsAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_friends);
        ButterKnife.bind(this);
        swipeRefresh.setOnRefreshListener(this);
        initData();
    }

    private void initData() {
        String userId=getSharedPreferences("config",MODE_PRIVATE).getString(Const.LOGIN_ID,"");
        HttpUtils.postClaimFriends("/claimFriends",  new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext,"onError---"+e);
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type=new TypeToken<Code<List<ClaimFriends>>>(){}.getType();
                Code<List<ClaimFriends>> code=gson.fromJson(response,type);
                if(code.getCode()==200){
                    list=new ArrayList<ClaimFriends>();
                    list=code.getMsg();
                }else {
                    T.showShort(mContext,"没有");
                }
                initAdapter();
            }
        });
    }

    private void initAdapter() {
        adapter = new ClaimFriendsAdapter(mContext,list);
        listView.setAdapter(adapter);
        adapter.setOnItemButtonClick(ClaimFriendsActivity.this);
    }

    @OnClick(R.id.iv_title_back)
    public void onClick() {
        finish();
    }

    @Override
    public void onRefresh() {
        initData();
        if (list!=null && list.size()!=0 ){
            adapter.notifyDataSetChanged();
        }
        swipeRefresh.setRefreshing(false);
    }

    //认领点击事件
    @Override
    public boolean onButtonClaimClick(final int position, View view, int status) {
        final EditText editText=new EditText(mContext);
        new AlertDialog.Builder(mContext)
                .setTitle("验证信息")
                .setMessage(list.get(position).getQuestion())
                .setView(editText)
                .setPositiveButton("验证", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String text=editText.getText().toString();
                        if(text.equals(list.get(position).getAnswer())){
                            T.showShort(mContext,"认领成功");
                        }else {
                            T.showShort(mContext,"认领失败");
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
        return false;
    }
}
