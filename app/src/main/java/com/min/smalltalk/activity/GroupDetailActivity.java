package com.min.smalltalk.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.min.mylibrary.util.L;
import com.min.mylibrary.util.PhotoUtils;
import com.min.mylibrary.util.T;
import com.min.mylibrary.widget.dialog.BottomMenuDialog;
import com.min.mylibrary.widget.dialog.LoadDialog;
import com.min.mylibrary.widget.image.SelectableRoundedImageView;
import com.min.smalltalk.App;
import com.min.smalltalk.R;
import com.min.smalltalk.adapter.MyGridView;
import com.min.smalltalk.base.BaseActivity;
import com.min.smalltalk.bean.Code;
import com.min.smalltalk.bean.GroupMember;
import com.min.smalltalk.bean.Groups;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.db.DBOpenHelper;
import com.min.smalltalk.db.GroupsDAOImpl;
import com.min.smalltalk.network.HttpUtils;
import com.min.smalltalk.server.broadcast.BroadcastManager;
import com.min.smalltalk.wedget.DemoGridView;
import com.min.smalltalk.wedget.Generate;
import com.min.smalltalk.wedget.Operation;
import com.min.smalltalk.wedget.SwitchButton;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import okhttp3.Call;

/**
 * 群组详情
 */
public class GroupDetailActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.iv_title_back)
    ImageView ivTitleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.gridview)
    DemoGridView mGridView;
    @BindView(R.id.tv_group_member_size)
    TextView tvGroupMemberSize;
    @BindView(R.id.rl_group_member_size_item)    //全体成员
    RelativeLayout rlGroupMemberSizeItem;
    @BindView(R.id.ll_search_chatting_records)   //查看聊天记录
    LinearLayout llSearchChattingRecords;
    @BindView(R.id.siv_group_header)
    SelectableRoundedImageView sivGroupHeader;
    @BindView(R.id.ll_group_port)                //群头像
    LinearLayout llGroupPort;
    @BindView(R.id.tv_group_name)
    TextView tvGroupName;
    @BindView(R.id.ll_group_name)               //群名
    LinearLayout llGroupName;
    @BindView(R.id.ll_group_announcement_divider)  //线
    LinearLayout llGroupAnnouncementDivider;
    @BindView(R.id.ll_group_announcement)       //公告
    LinearLayout llGroupAnnouncement;
    @BindView(R.id.ll_group_code)               //群二维码
    LinearLayout llGroupCode;
    @BindView(R.id.sw_group_notfaction)         //消息免打扰
    SwitchButton swGroupNotfaction;
    @BindView(R.id.sw_group_top)                //消息置顶
    SwitchButton swGroupTop;
    @BindView(R.id.ll_group_clean)              //清除聊天记录
    LinearLayout llGroupClean;
    @BindView(R.id.tv_group_displayname_text)
    TextView tvGroupDisplaynameText;
    @BindView(R.id.ll_group_displayname)
    LinearLayout llGroupDisplayname;
    @BindView(R.id.btn_group_quit)
    Button btnGroupQuit;
    @BindView(R.id.btn_group_dismiss)
    Button btnGroupDismiss;
    @BindView(R.id.iv_group_code)
    ImageView ivGroupCode;
    @BindView(R.id.ll_group_activity)
    LinearLayout llGroupActivity;
    @BindView(R.id.ll_my_nickname)
    LinearLayout llMyNickname;
    @BindView(R.id.tv_my_name)
    TextView tvMyName;


    private List<GroupMember> mGroupMember;
    private String groupId;    //群ID
    private Conversation.ConversationType mConversationType;
    private boolean isFromConversation;
    private Groups mGroup;
    private boolean isCreated = false;   //群主
    private String isCreator;
    private PhotoUtils photoUtils;
    private String imageUrl;
    private File imageFile;              //群头像
//    private String changeGroupName;
    private BottomMenuDialog dialog;
    private Bitmap bitmap;
    private String userId;
    private String nickname;
    private String groupName;
    private EditText editText;

    private SharedPreferences sp;

    private DBOpenHelper dbOpenHelper;  //SQLite
    private GroupsDAOImpl sqLiteDAO;

    private MyGridView adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        ButterKnife.bind(this);

        initSQLite();
        initView();
        setPortraitChangListener();

        //群组会话界面点进群组详情---groupId
        groupId = getIntent().getStringExtra("TargetId");
        //----GROUP
        mConversationType = (Conversation.ConversationType) getIntent().getSerializableExtra("conversationType");

        if (!TextUtils.isEmpty(groupId)) {
            isFromConversation = true;
        }

        if (isFromConversation) {  //群主会话页进入
            LoadDialog.show(mContext);
//            getGroups();  //群组信息
            getGroupsSqlite();
            LoadDialog.dismiss(mContext);
        }
    }

    //数据库
    private void initSQLite() {
        dbOpenHelper = new DBOpenHelper(mContext, "talk.db", null, 2);// 创建数据库文件
        dbOpenHelper.getWritableDatabase();
        sqLiteDAO= new GroupsDAOImpl(mContext);
    }


    private void initView() {
        tvTitle.setText("群组信息");
        sp=getSharedPreferences("config",MODE_PRIVATE);
        userId = sp.getString(Const.LOGIN_ID, "");   //个人id
        nickname= sp.getString(Const.LOGIN_NICKNAME,"");  //个人昵称
        tvMyName.setText(nickname);
        swGroupTop.setOnCheckedChangeListener(this);
        swGroupNotfaction.setOnCheckedChangeListener(this);
    }

    /**
     * 图片
     */
    private void setPortraitChangListener() {
        photoUtils=new PhotoUtils(new PhotoUtils.OnPhotoResultListener() {
            @Override
            public void onPhotoResult(Uri uri) {
                if(uri!=null && !TextUtils.isEmpty(uri.getPath())){
                    imageFile=new File(uri.getPath());
                    imageUrl=uri.toString();
                    ImageLoader.getInstance().displayImage(imageUrl,sivGroupHeader);
                    HttpUtils.postChangeGroupName("/change_groupName", groupId, "",imageFile, new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            T.showShort(mContext,"/group/change_groupHead-------网络连接错误");
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Gson gson=new Gson();
                            Type type=new TypeToken<Code<Integer>>(){}.getType();
                            Code<Integer> code=gson.fromJson(response,type);
                            if(code.getCode()==200){
                                T.showShort(mContext,"修改成");
                            }else {
                                T.showShort(mContext,"修改失败");
                            }
                        }
                    });
                }
            }

            @Override
            public void onPhotoCancel() {

            }
        });
    }

    /**
     * 群组信息
     */

    private void getGroupsSqlite(){
        Groups groups=sqLiteDAO.find(groupId);
        groupName=groups.getGroupName();
        tvGroupName.setText(groupName);   //群名
        mGroup = new Groups(groups.getGroupId(), groupName,
                groups.getGroupPortraitUri(), groups.getRole());
        initGroupData();
        L.e("--------------====",mGroup+"");
    }



    /*private void getGroups() {
        HttpUtils.postGroupsRequest("/group_info", groupId,userId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext, "group_data------" + "网络连接错误");
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code<Groups>>() {}.getType();
                Code<Groups> code = gson.fromJson(response, type);
                Groups groups=code.getMsg();
                if (code.getCode() == 200) {
                    mGroup = new Groups(groups.getGroupId(), groups.getGroupName(),
                            HttpUtils.IMAGE_RUL+groups.getGroupPortraitUri(), groups.getRole());
                    initGroupData();
                }
            }
        });
    }*/

    //群组信息
    private void initGroupData() {
        if (TextUtils.isEmpty(mGroup.getGroupPortraitUri())) {
            ImageLoader.getInstance().displayImage(Generate.generateDefaultAvatar(
                    groupName, mGroup.getGroupId()), sivGroupHeader, App.getOptions());
        } else {
            ImageLoader.getInstance().displayImage(mGroup.getGroupPortraitUri(), sivGroupHeader, App.getOptions());
        }
        /**
         * 会话置顶
         */
        if (RongIM.getInstance() != null) {
            RongIM.getInstance().getConversation(Conversation.ConversationType.GROUP,
                    mGroup.getGroupId(), new RongIMClient.ResultCallback<Conversation>() {
                        @Override
                        public void onSuccess(Conversation conversation) {
                            if (conversation == null) {
                                return;
                            }
                            if (conversation.isTop()) {
                                swGroupTop.setChecked(true);
                            } else {
                                swGroupTop.setChecked(false);
                            }

                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
            /**
             * 消息免打扰
             */
            RongIM.getInstance().getConversationNotificationStatus(Conversation.ConversationType.GROUP,
                    mGroup.getGroupId(), new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
                        @Override
                        public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {

                            if (conversationNotificationStatus == Conversation.ConversationNotificationStatus.DO_NOT_DISTURB) {
                                swGroupNotfaction.setChecked(true);
                            } else {
                                swGroupNotfaction.setChecked(false);
                            }
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
        }
        //成员角色---1：群主

        if (mGroup.getRole().equals("1")) {
              isCreated = true;
//            isCreator=mGroup.getRole();
        }
        if (!isCreated) {
            llGroupAnnouncementDivider.setVisibility(View.GONE);
            llGroupAnnouncement.setVisibility(View.GONE);
        } else {
            llGroupAnnouncementDivider.setVisibility(View.VISIBLE);
            llGroupAnnouncement.setVisibility(View.VISIBLE);
            btnGroupDismiss.setVisibility(View.VISIBLE);
            btnGroupQuit.setVisibility(View.GONE);
        }
        getGroupMembers(isCreated); //成员信息
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(groupId)) {
            isFromConversation = true;
        }

        if (isFromConversation) {  //群主会话页进入
            LoadDialog.show(mContext);
//            getGroups();  //群组信息
            getGroupsSqlite();
            LoadDialog.dismiss(mContext);
        }
    }

    /**
     * 群组成员
     */
    private void getGroupMembers(final boolean isCreator) {
        HttpUtils.postGroupsRequest("/group_member", groupId,userId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext, "group_member------" + "网络连接错误");
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code<List<GroupMember>>>() {}.getType();
                Code<List<GroupMember>> code = gson.fromJson(response, type);
                if (code.getCode() == 200) {
                    mGroupMember = code.getMsg();
                    if (mGroupMember != null && mGroupMember.size() > 0) {
                        tvGroupMemberSize.setText("全部成员" + "(" + mGroupMember.size() + ")");
                        adapter=new MyGridView(mContext, mGroupMember, isCreator,mGroup);
                        mGridView.setAdapter(adapter);
                    } else {
                        return;
                    }
                    /*for (GroupMember member : mGroupMember) {
                        String s = member.getUserId();
                        if (userId.equals(s)) {
                            if (!TextUtils.isEmpty(member.getDisplayName())) {
                                tvGroupName.setText(member.getDisplayName());
                            } else {
                                tvGroupName.setText("无");
                            }
                        }
                    }*/
                }
            }
        });
    }

    //点击事件
    @OnClick({R.id.iv_title_back, R.id.rl_group_member_size_item, R.id.ll_group_port, R.id.ll_group_name,
                R.id.ll_group_code, R.id.sw_group_top, R.id.sw_group_notfaction,R.id.btn_group_quit,
                R.id.btn_group_dismiss, R.id.ll_group_announcement, R.id.ll_group_activity ,R.id.ll_my_nickname})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_back:  //返回
                GroupDetailActivity.this.finish();
                break;
            case R.id.rl_group_member_size_item:  //群成员
                Intent intent=new Intent(mContext,GroupMemberActivity.class);
                intent.putExtra("groupId", groupId);
                startActivity(intent);
                break;
            case R.id.ll_group_port:   //群头像
                if(isCreated){
                    if(dialog!=null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                    dialog=new BottomMenuDialog(mContext);
                    dialog.setPhotographListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(dialog!=null && dialog.isShowing()){
                                dialog.dismiss();
                            }
                            photoUtils.takePicture(GroupDetailActivity.this);
                        }
                    });
                    dialog.setLocalphotoListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(dialog!=null && dialog.isShowing()){
                                dialog.dismiss();
                            }
                            photoUtils.selectPicture(GroupDetailActivity.this);
                        }
                    });
                    dialog.show();
                    break;
                }else {
                    T.showShort(mContext,"只有群主才能修改群头像");
                }
                break;
            case R.id.ll_group_name:   //群名称
                if(isCreated){
                    final EditText editText=new EditText(mContext);
                    AlertDialog dialog = new AlertDialog.Builder(mContext)
                            .setTitle("修改群名称")
                            .setView(editText)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String et=editText.getText().toString();
                                    changGroupName(et);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();
                }else {
                    T.showShort(mContext,"只有群主才能修改群名称");
                    break;
                }
                break;
            case R.id.ll_group_code:   //二维码
                Intent intent1=new Intent(mContext,ZxingActivity.class);
                intent1.putExtra("Id", groupId);
                intent1.putExtra("Head",bitmap);
                startActivity(intent1);
                break;
            case R.id.btn_group_quit:   //退出群
                AlertDialog quit=new AlertDialog.Builder(mContext)
                        .setTitle("退出群")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                quitGroup();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .show();
                break;
            case R.id.btn_group_dismiss:  //解散群
                AlertDialog dismiss=new AlertDialog.Builder(mContext)
                        .setTitle("退出群")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dismissGroup();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .show();
                break;
            case R.id.ll_group_announcement:  //群公告
                Intent intent2=new Intent(mContext,GroupNoticeActivity.class);
                intent2.putExtra("conversationType", Conversation.ConversationType.GROUP.getValue());
                intent2.putExtra("targetId", groupId);
                startActivity(intent2);
                break;
            case R.id.ll_group_activity:   //群活动
                Intent intent3=new Intent(mContext,GroupFlexibleActivity.class);
                intent3.putExtra("actCreator",userId);
                intent3.putExtra("groupId", groupId);
                startActivity(intent3);
                break;
            case R.id.ll_my_nickname:  //修改群个人昵称
                editText=new EditText(mContext);
                AlertDialog dialog3=new AlertDialog.Builder(mContext)
                        .setTitle("修改个人昵称")
                        .setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                changeMyName();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .show();
                break;
            default:
                break;
        }
    }

    //修改群个人你昵称
    private void changeMyName() {
        HttpUtils.postChangeNameGroup("/change_userName", groupId, userId, groupName, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext,"/change_userName------"+e);
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code<Integer>>() {
                }.getType();
                Code<Integer> code = gson.fromJson(response, type);
                if (code.getCode() == 200) {
                    tvMyName.setText(editText.getText());
                }else {
                    T.showShort(mContext,"修改失败");
                }
            }
        });
    }

    //退出群
    private void quitGroup() {
        HttpUtils.postQuitGroup("/GroupPullUser", groupId, userId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext,"/quit_group------"+e);
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson=new Gson();
                Type type=new TypeToken<Code<Integer>>(){}.getType();
                Code<Integer> code=gson.fromJson(response,type);
                if(code.getCode()==200){
                    RongIM.getInstance().getConversation(Conversation.ConversationType.GROUP, groupId,
                            new RongIMClient.ResultCallback<Conversation>(){

                                @Override
                                public void onSuccess(Conversation conversation) {
                                    RongIM.getInstance().clearMessages(Conversation.ConversationType.GROUP,
                                            groupId, new RongIMClient.ResultCallback<Boolean>() {
                                                @Override
                                                public void onSuccess(Boolean aBoolean) {
                                                    RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, groupId,null);
                                                }

                                                @Override
                                                public void onError(RongIMClient.ErrorCode errorCode) {

                                                }
                                            });
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {

                                }
                            });
                    BroadcastManager.getInstance(mContext).sendBroadcast(Const.GROUP_LIST_UPDATE);
                    T.showShort(mContext,"退出成功");
                    finish();
                }
            }
        });

    }

    //解散群
    private void dismissGroup(){
        HttpUtils.postDismissGroup("/dissolutionGroup", groupId, userId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext, "/dismiss_group------"+e);
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code<Integer>>() {
                }.getType();
                Code<Integer> code = gson.fromJson(response, type);
                if (code.getCode() == 200) {
                    RongIM.getInstance().getConversation(Conversation.ConversationType.GROUP, groupId,
                            new RongIMClient.ResultCallback<Conversation>() {

                                @Override
                                public void onSuccess(Conversation conversation) {
                                    RongIM.getInstance().clearMessages(Conversation.ConversationType.GROUP,
                                            groupId, new RongIMClient.ResultCallback<Boolean>() {
                                                @Override
                                                public void onSuccess(Boolean aBoolean) {
                                                    RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, groupId, null);
                                                }

                                                @Override
                                                public void onError(RongIMClient.ErrorCode errorCode) {

                                                }
                                            });
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {

                                }
                            });
                    T.showShort(mContext, "解散群成功");
//                    BroadcastManager.getInstance(mContext).sendBroadcast(Const.GROUP_LIST_UPDATE);
                    finish();
                }else {
                    T.showShort(mContext,"解散群失败");
                }
            }
        });

    }

    //修改群名称
    private void changGroupName(String groupName) {
        HttpUtils.postChangeGroupName("/change_groupName", groupId, groupName,imageFile, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext,"/change_groupName------网络连接错误");
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson=new Gson();
                Type type=new TypeToken<Code<Groups>>(){}.getType();
                Code<Groups> code = gson.fromJson(response,type);
                if(code.getCode()==200){
                    tvGroupName.setText(code.getMsg().getGroupName());
                }else {
                    T.showShort(mContext,"连接错误");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case PhotoUtils.INTENT_CROP:
            case PhotoUtils.INTENT_TAKE:
            case PhotoUtils.INTENT_SELECT:
                photoUtils.onActivityResult(GroupDetailActivity.this, requestCode, resultCode, data);
        }
    }

    //消息设置
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()){
             case R.id.sw_group_top:  //消息置顶
                if (isChecked) {
                    if (mGroup != null) {
                        Operation.setConversationTop(mContext, Conversation.ConversationType.GROUP, mGroup.getGroupId(), true);
                    }
                } else {
                    if (mGroup != null) {
                        Operation.setConversationTop(mContext, Conversation.ConversationType.GROUP, mGroup.getGroupId(), false);
                    }
                }
                break;
            case R.id.sw_group_notfaction:  //消息免打扰
                if (isChecked) {
                    if (mGroup != null) {
                        Operation.setConverstionNotif(mContext, Conversation.ConversationType.GROUP, mGroup.getGroupId(), true);
                    }
                } else {
                    if (mGroup != null) {
                        Operation.setConverstionNotif(mContext, Conversation.ConversationType.GROUP, mGroup.getGroupId(), false);
                    }
                }
                break;
            default:
                break;
        }
    }
}
