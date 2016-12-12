package com.min.smalltalk.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.min.mylibrary.util.T;
import com.min.mylibrary.widget.image.SelectableRoundedImageView;
import com.min.smalltalk.App;
import com.min.smalltalk.AppContext;
import com.min.smalltalk.R;
import com.min.smalltalk.activity.GroupListActivity;
import com.min.smalltalk.activity.NewFriendListActivity;
import com.min.smalltalk.activity.UserDetailActivity;
import com.min.smalltalk.adapter.FriendListAdapter;
import com.min.smalltalk.bean.Code;
import com.min.smalltalk.bean.FriendInfo;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.network.HttpUtils;
import com.min.smalltalk.server.broadcast.BroadcastManager;
import com.min.smalltalk.wedget.CharacterParser;
import com.min.smalltalk.wedget.Generate;
import com.min.smalltalk.wedget.PinyinComparator;
import com.min.smalltalk.wedget.SideBar;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rong.imageloader.core.ImageLoader;
import okhttp3.Call;

public class FriendFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.lv_friends)
    ListView mListView;
    @BindView(R.id.tv_group_dialog)
    TextView tvGroupDialog;  //中部展示的字母提示
    @BindView(R.id.sb)
    SideBar sb;
    @BindView(R.id.tv_show_no_friend)
    TextView tvShowNoFriend;
    /*@BindView(R.id.rl_me_item)
    RelativeLayout rlMeItem;
    @BindView(R.id.activity_select_friends)
    LinearLayout activitySelectFriends;
    @BindView(R.id.rl_newfriends)
    RelativeLayout rlNewfriends;
    @BindView(R.id.rl_group)
    RelativeLayout rlGroup;
    @BindView(R.id.rl_publicservice)
    RelativeLayout rlPublicservice;
    @BindView(R.id.siv_me)
    SelectableRoundedImageView sivMe;
    @BindView(R.id.tv_me)
    TextView tvMe;
    @BindView(R.id.tv_unread)
    TextView tvUnread;*/

    private PinyinComparator mPinyinComparator;


    private List<FriendInfo> mSourceFriendList;
    private List<FriendInfo> mFriendList;
    private List<FriendInfo> mFilteredFriendList;
    /**
     * 好友列表的 mFriendListAdapter
     */
    private FriendListAdapter mFriendListAdapter;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser mCharacterParser;
    /**
     * 根据拼音来排列ListView里面的数据类
     */

    private String mId;
    private String mCacheName;
    private SharedPreferences sp;

    private View mHeadView;

    private TextView tvUnread,tvMe;
    private RelativeLayout rlNewfriends,rlGroup,rlPublicservice,rlMeItem;
    private SelectableRoundedImageView sivMe;
//    tvUnread,tvMe, rlNewfriends,rlGroup,rlPublicservice,rlMeItem sivMe

    private static final int CLICK_CONTACT_FRAGMENT_FRIEND = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend, container, false);
        ButterKnife.bind(this, view);
        initView();
        initData();
        refreshUIListener();
        return view;
    }

    private void initView() {
        sb.setTextView(tvGroupDialog);
        sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        mId = sp.getString(Const.LOGIN_ID, "");
        mCacheName = sp.getString(Const.LOGIN_NICKNAME, "");
        final String header = sp.getString(Const.LOGIN_PORTRAIT, "");
        //自己信息

        /*tvMe.setText(mCacheName);
        ImageLoader.getInstance().displayImage(TextUtils.isEmpty(header) ?
                Generate.generateDefaultAvatar(mCacheName, mId) : header, sivMe, App.getOptions());*/


        //设置右侧触摸监听
        sb.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = mFriendListAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mListView.setSelection(position);
                }

            }
        });
    }

    private void initData() {
        mSourceFriendList = new ArrayList<>();
        mFriendList = new ArrayList<>();
        mFilteredFriendList = new ArrayList<>();
        HttpUtils.postRequest("/friends", mId,new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(getActivity(), "friends-----"+e);
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code<List<FriendInfo>>>() {}.getType();
                Code<List<FriendInfo>> code = gson.fromJson(response, type);
                if (code.getCode() == 200) {
                    List<FriendInfo> list = code.getMsg();
                    if (list != null && list.size() > 0) {
                        for (FriendInfo friend : list) {
//                            Log.e("===========", friend.getUserId());
                            mSourceFriendList.add(new FriendInfo(friend.getUserId(), friend.getName(), HttpUtils.IMAGE_RUL+friend.getPortraitUri(),
                                    friend.getDisplayName(), friend.getPhone(), friend.getEmail()));
                        }
                        //实例化汉字转拼音类
                        mCharacterParser = CharacterParser
                                .getInstance();
                        mPinyinComparator = PinyinComparator.getInstance();
                        initList();
                    }else {
                        tvShowNoFriend.setVisibility(View.VISIBLE);
                    }
                } else {
                    T.showShort(getActivity(), "数据获取错误");
                }
            }
        });
    }

    private void initList() {
        if (mSourceFriendList != null && mSourceFriendList.size() > 0) {
            mFriendList = labelSourceFriendList(mSourceFriendList); //过滤数据为有字母的字段  现在有字母 别的数据没有
        } else {
            tvShowNoFriend.setVisibility(View.VISIBLE);
        }
        //还原除了带字母字段的其他数据
        for (int i = 0; i < mSourceFriendList.size(); i++) {
            mFriendList.get(i).setName(mSourceFriendList.get(i).getName());
            mFriendList.get(i).setUserId(mSourceFriendList.get(i).getUserId());
            mFriendList.get(i).setPortraitUri(mSourceFriendList.get(i).getPortraitUri());
            mFriendList.get(i).setDisplayName(mSourceFriendList.get(i).getDisplayName());
        }
        // 根据a-z进行排序源数据
        Collections.sort(mFriendList, mPinyinComparator);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        mHeadView = inflater.inflate(R.layout.item_friend_list_header, null);
        tvUnread= (TextView) mHeadView.findViewById(R.id.tv_unread);
        rlNewfriends= (RelativeLayout) mHeadView.findViewById(R.id.rl_newfriends);
        rlGroup= (RelativeLayout) mHeadView.findViewById(R.id.rl_group);
        rlPublicservice= (RelativeLayout) mHeadView.findViewById(R.id.rl_publicservice);
        rlMeItem= (RelativeLayout) mHeadView.findViewById(R.id.rl_me_item);
        sivMe= (SelectableRoundedImageView) mHeadView.findViewById(R.id.siv_me);
        tvMe= (TextView) mHeadView.findViewById(R.id.tv_me);

        sp=getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        mId=sp.getString(Const.LOGIN_ID,"");
        mCacheName=sp.getString(Const.LOGIN_NICKNAME,"");
        final String header=sp.getString(Const.LOGIN_PORTRAIT,"");
        //自己信息
        tvMe.setText(mCacheName);
        if(!TextUtils.isEmpty(header)){
            ImageLoader.getInstance().displayImage(header,sivMe);
        }else {
            sivMe.setImageResource(R.mipmap.default_portrait);
        }
//        ImageLoader.getInstance().displayImage();
        /*ImageLoader.getInstance().displayImage(TextUtils.isEmpty(header) ?
                Generate.generateDefaultAvatar(mCacheName,mId):header,sivMe, App.getOptions());*/

        rlMeItem.setOnClickListener(this);  //me
        rlNewfriends.setOnClickListener(this);
        rlGroup.setOnClickListener(this);
        rlPublicservice.setOnClickListener(this);

        mFriendListAdapter = new FriendListAdapter(getActivity(), mFriendList);
        mListView.addHeaderView(mHeadView);
        mListView.setAdapter(mFriendListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListView.getHeaderViewsCount() > 0) {
                    startFriendDetailsPage(mFriendList.get(position - 1));
                } else {
                    startFriendDetailsPage(mFilteredFriendList.get(position));
                }
//                startFriendDetailsPage(mFilteredFriendList.get(position));
            }
        });

        //长按删除好友
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                FriendInfo bean = mFriendList.get(position - 1);
                startFriendDetailsPage(bean);
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("删除该好友")
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteFriends(mFriendList.get(i));
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                return true;
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    if (mListView.getHeaderViewsCount() > 0) {
                        mListView.removeHeaderView(mHeadView);
                    }
                } else {
                    if (mListView.getHeaderViewsCount() == 0) {
                        mListView.addHeaderView(mHeadView);
                    }
                }
            }
        });
    }

    //删除好友
    private void deleteFriends(FriendInfo friendInfo) {
        HttpUtils.postDelFriendRequest("/deleteUser", mId, friendInfo.getUserId(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(getActivity(), "deleteUser-----"+e);
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code<FriendInfo>>() {
                }.getType();
                Code<FriendInfo> code = gson.fromJson(response, type);
                if (code.getCode() == 200) {
                    T.showShort(getActivity(), "删除成功");
                    mFriendListAdapter.notifyDataSetChanged();
                } else {
                    T.showShort(getActivity(), "删除失败");
                }
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (tvGroupDialog != null) {
            tvGroupDialog.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 为ListView填充数据
     */
    private List<FriendInfo> labelSourceFriendList(List<FriendInfo> list) {
        List<FriendInfo> mFriendInfoList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            FriendInfo friendInfoModel = new FriendInfo();
            friendInfoModel.setName(list.get(i).getName());
            //汉字转换成拼音
            String pinyin = mCharacterParser.getSpelling(list.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                friendInfoModel.setLetters(sortString.toUpperCase());
            } else {
                friendInfoModel.setLetters("#");
            }

            mFriendInfoList.add(friendInfoModel);
        }
        return mFriendInfoList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr 需要过滤的 String
     */
    private void filterData(String filterStr) {
        List<FriendInfo> filterDateList = new ArrayList<>();


        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = mFriendList;
        } else {
            filterDateList.clear();
            for (FriendInfo friendInfoModel : mFriendList) {
                String name = friendInfoModel.getName();
                String displayName = friendInfoModel.getDisplayName();
                if (!TextUtils.isEmpty(displayName)) {
                    if (name.contains(filterStr) || mCharacterParser.getSpelling(name).startsWith(filterStr) || displayName.contains(filterStr) || mCharacterParser.getSpelling(displayName).startsWith(filterStr)) {
                        filterDateList.add(friendInfoModel);
                    }
                } else {
                    if (name.contains(filterStr) || mCharacterParser.getSpelling(name).startsWith(filterStr)) {
                        filterDateList.add(friendInfoModel);
                    }
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, mPinyinComparator);
        mFilteredFriendList = filterDateList;
        mFriendListAdapter.updateListView(filterDateList);
    }


    private void startFriendDetailsPage(FriendInfo friend) {
        Intent intent = new Intent(getActivity(), UserDetailActivity.class);
        intent.putExtra("type", CLICK_CONTACT_FRAGMENT_FRIEND);
        intent.putExtra("friends", friend);
        startActivity(intent);
    }

    private void refreshUIListener() {
        BroadcastManager.getInstance(getActivity()).addAction(AppContext.UPDATE_FRIEND, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String command = intent.getAction();
                if (!TextUtils.isEmpty(command)) {
//                    updateUI();
                }
            }
        });
        BroadcastManager.getInstance(getActivity()).addAction(AppContext.UPDATE_RED_DOT, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String command = intent.getAction();
                if (!TextUtils.isEmpty(command)) {
                    tvUnread.setVisibility(View.INVISIBLE);
                }
            }
        });
        BroadcastManager.getInstance(getActivity()).addAction(Const.CHANGEINFO, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
                mId = sp.getString(Const.LOGIN_ID, "");
                mCacheName = sp.getString(Const.LOGIN_NICKNAME, "");
                String header = sp.getString(Const.LOGIN_PORTRAIT, "");
                tvMe.setText(mCacheName);
                ImageLoader.getInstance().displayImage(TextUtils.isEmpty(header) ?
                        Generate.generateDefaultAvatar(mCacheName, mId) : header, sivMe, App.getOptions());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            BroadcastManager.getInstance(getActivity()).destroy(AppContext.UPDATE_FRIEND);
            BroadcastManager.getInstance(getActivity()).destroy(AppContext.UPDATE_RED_DOT);
            BroadcastManager.getInstance(getActivity()).destroy(Const.CHANGEINFO);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_newfriends:
                tvUnread.setVisibility(View.GONE);
                Intent intent = new Intent(getActivity(), NewFriendListActivity.class);
                startActivityForResult(intent, 20);
                break;
            case R.id.rl_group:
                startActivity(new Intent(getActivity(), GroupListActivity.class));
                break;
            case R.id.rl_publicservice:
                T.showShort(getActivity(), "PublicServiceActivity.class");
                break;
            case R.id.rl_me_item:
                T.showShort(getActivity(),"这是自己");
//                RongIM.getInstance().startPrivateChat(getActivity(), mId, mCacheName);
                break;
        }
    }

    /*@OnClick({R.id.rl_newfriends, R.id.rl_group, R.id.rl_publicservice, R.id.rl_me_item})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_newfriends:
                tvUnread.setVisibility(View.GONE);
                Intent intent = new Intent(getActivity(), NewFriendListActivity.class);
                startActivityForResult(intent, 20);
                break;
            case R.id.rl_group:
                startActivity(new Intent(getActivity(), GroupListActivity.class));
                break;
            case R.id.rl_publicservice:
                T.showShort(getActivity(), "PublicServiceActivity.class");
                break;
            case R.id.rl_me_item:
                RongIM.getInstance().startPrivateChat(getActivity(), mId, mCacheName);
                break;
        }
    }*/
}
