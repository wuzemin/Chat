package com.min.smalltalk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.min.mylibrary.util.CommonUtils;
import com.min.mylibrary.util.T;
import com.min.mylibrary.widget.dialog.LoadDialog;
import com.min.smalltalk.R;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.base.BaseRecyclerAdapter;
import com.min.smalltalk.base.BaseRecyclerHolder;
import com.min.smalltalk.bean.AllAddFriends;
import com.min.smalltalk.bean.Code;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.network.HttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class NewFriendListActivity extends BaseActivity {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;
    @BindView(R.id.isData)
    TextView isData;
    @BindView(R.id.rv_new_friends)
    RecyclerView rvNewFriends;

    private BaseRecyclerAdapter<AllAddFriends> adapter;
    private AllAddFriends allAddFriends;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend_list);
        ButterKnife.bind(this);
        initView();
        if (!CommonUtils.isNetConnect(mContext)) {
            T.showShort(mContext,R.string.no_network);
            return;
        }
        userid=getSharedPreferences("config",MODE_PRIVATE).getString(Const.LOGIN_ID,"");
        LoadDialog.show(mContext);
        initData();
    }

    private void initData() {
        HttpUtils.postAddFriendsRequest("/all_addfriend_request",userid ,new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext,"/all_addfriend_request--------"+e);
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson=new Gson();
                Type type=new TypeToken<Code<List<AllAddFriends>>>(){}.getType();
                Code<List<AllAddFriends>> code=gson.fromJson(response,type);
                if(code.getCode()==200){
                    List<AllAddFriends> list=code.getMsg();
                    if(list.size()==0){
                        isData.setVisibility(View.VISIBLE);
                        LoadDialog.dismiss(mContext);
                        return;
                    }
                    Collections.sort(list, new Comparator<AllAddFriends>() {
                        @Override
                        public int compare(AllAddFriends allAddFriends, AllAddFriends t1) {
                            Date date1=stringToDate(allAddFriends);
                            Date date2=stringToDate(t1);
                            if(date1.before(date2)){
                                return 1;
                            }
                            return -1;
                        }
                    });
                    initAdapter(list);

                }else {
                    LoadDialog.dismiss(mContext);
                    isData.setVisibility(View.VISIBLE);
//                    T.showShort(mContext,"未知错误");
                }
            }
        });
    }

    //数据
    private void initAdapter(List<AllAddFriends> list) {
        adapter = new BaseRecyclerAdapter<AllAddFriends>(mContext,list,R.layout.item_new_friends) {
            @Override
            public void convert(BaseRecyclerHolder holder, AllAddFriends item, int position, boolean isScrolling) {
                holder.setImageByUrl(R.id.civ_icon,item.getPortraitUri());
                holder.setText(R.id.tv_nickname,item.getNickname());
                holder.setText(R.id.tv_message,item.getAddFriendMessage());
                int status=item.getStatus();
                switch (status){
                    case 0:
                        holder.setText(R.id.btn_agree,"已拒绝");
                        break;
                    case 1:
                        holder.setText(R.id.btn_agree,"已同意");
                        break;
                    case 2:
                        holder.setText(R.id.btn_agree,"已忽略");
                        break;
                    case 3:
                        holder.setText(R.id.btn_agree,"添加");
                        break;
                }
            }

        };
    }

    private Date stringToDate(AllAddFriends allAddFriends) {
        String updatedAt = allAddFriends.getUpdatedAt();
        String updatedAtDateStr = updatedAt.substring(0, 10) + " " + updatedAt.substring(11, 16);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date updateAtDate = null;
        try {
            updateAtDate = simpleDateFormat.parse(updatedAtDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return updateAtDate;
    }



    private void initView() {
        tvTitle.setText("新的朋友");
        ivTitleRight.setVisibility(View.VISIBLE);
        ivTitleRight.setImageResource(R.mipmap.de_address_new_friend);
    }

    @OnClick({R.id.iv_title_back, R.id.iv_title_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.iv_title_right:
                startActivity(new Intent(mContext,SearchFriendActivity.class));
                break;
        }
    }
}
