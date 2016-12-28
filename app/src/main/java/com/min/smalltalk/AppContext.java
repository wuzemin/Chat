package com.min.smalltalk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.min.mylibrary.util.L;
import com.min.smalltalk.activity.AMAPLocationActivity;
import com.min.smalltalk.activity.GroupVoteActivity;
import com.min.smalltalk.activity.NewFriendListActivity;
import com.min.smalltalk.bean.ContactNotificationMessageData;
import com.min.smalltalk.bean.FriendInfo;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.db.FriendInfoDAOImpl;
import com.min.smalltalk.message.module.TalkExtensionModule;
import com.min.smalltalk.server.broadcast.BroadcastManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.IExtensionModule;
import io.rong.imkit.RongExtensionManager;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.GroupNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.RichContentMessage;

/**
 * Created by Min on 2016/11/24.
 * 融云相关监听 事件集合类
 */

public class AppContext implements RongIMClient.ConnectionStatusListener,
         RongIM.ConversationBehaviorListener, RongIM.ConversationListBehaviorListener
        ,RongIMClient.OnReceiveMessageListener, RongIM.LocationProvider, RongIM.UserInfoProvider {

    public static final String UPDATE_FRIEND = "update_friend";
    public static final String UPDATE_RED_DOT = "update_red_dot";
    private Context mContext;
    private SharedPreferences sp;
    private List<FriendInfo> list;
    private FriendInfoDAOImpl friendInfoDAO;

    private static AppContext mRongCloudInstance;
    private RongIM.LocationProvider.LocationCallback mLastLocationCallback;

    private static ArrayList<Activity> mActivities;

    public AppContext(Context mContext) {
        this.mContext = mContext;
        initListener();
        mActivities = new ArrayList<>();
        sp = mContext.getSharedPreferences("config", Context.MODE_PRIVATE);
        friendInfoDAO= new FriendInfoDAOImpl(mContext);
        UserInfoManager.init(mContext);
//        initPortrait();
    }

    /**
     * 初始化 RongCloud.
     *
     * @param context 上下文。
     */
    public static void init(Context context) {

        if (mRongCloudInstance == null) {

            synchronized (AppContext.class) {

                if (mRongCloudInstance == null) {
                    mRongCloudInstance = new AppContext(context);
                }
            }
        }

    }

    /**
     * 获取RongCloud 实例。
     *
     * @return RongCloud。
     */
    public static AppContext getInstance() {
        return mRongCloudInstance;
    }

    /**
     * init 后就能设置的监听
     */
    private void initListener() {
        RongIM.setConversationBehaviorListener(this);//设置会话界面操作的监听器。
        RongIM.setUserInfoProvider(this,true);  //用户信息提供者
        RongIM.setConversationListBehaviorListener(this);  //会话列表界面
//        RongIM.setGroupInfoProvider(this, true);  //群组用户提供者
        RongIM.setLocationProvider(this);//设置地理位置提供者,不用位置的同学可以注掉此行代码
        setInputProvider();
//        setUserInfoEngineListener();
        setReadReceiptConversationType();
//        RongIM.setGroupUserInfoProvider(this, true);
    }

    private void setReadReceiptConversationType() {
        Conversation.ConversationType[] types = new Conversation.ConversationType[] {
                Conversation.ConversationType.PRIVATE,
                Conversation.ConversationType.GROUP,
                Conversation.ConversationType.DISCUSSION
        };
        RongIM.getInstance().setReadReceiptConversationTypeList(types);
    }

    private void setInputProvider() {

        RongIM.setOnReceiveMessageListener(this);
        RongIM.setConnectionStatusListener(this);

        List<IExtensionModule> moduleList = RongExtensionManager.getInstance().getExtensionModules();
        IExtensionModule defaultModule = null;
        if (moduleList != null) {
            for (IExtensionModule module : moduleList) {
                if (module instanceof DefaultExtensionModule) {
                    defaultModule = module;
                    break;
                }
            }
            if (defaultModule != null) {
                RongExtensionManager.getInstance().unregisterExtensionModule(defaultModule);
                RongExtensionManager.getInstance().registerExtensionModule(new TalkExtensionModule());
            }
        }
    }

    @Override
    public void onChanged(ConnectionStatus connectionStatus) {
        if (connectionStatus.getMessage().equals(ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT)) {

        }
    }

    //会话界面操作
    @Override
    public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        if (conversationType == Conversation.ConversationType.CUSTOMER_SERVICE || conversationType == Conversation.ConversationType.PUBLIC_SERVICE || conversationType == Conversation.ConversationType.APP_PUBLIC_SERVICE) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        return false;
    }

    //点击消息
    @Override
    public boolean onMessageClick(final Context context, final View view, final Message message) {
        /**
         * demo 代码  开发者需替换成自己的代码。
         */
        if (message.getContent() instanceof LocationMessage) {
            Intent intent = new Intent(context, AMAPLocationActivity.class);
            intent.putExtra("location", message.getContent());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        if(message.getContent() instanceof RichContentMessage){
            Intent intent=new Intent(context, GroupVoteActivity.class);
            context.startActivity(intent);
        }
//        if (message.getContent() instanceof ImageMessage) {
//            Intent intent = new Intent(context, PhotoActivity.class);
//            intent.putExtra("message", message);
//            context.startActivity(intent);
//        }

        return false;
    }

    @Override
    public boolean onMessageLinkClick(Context context, String s) {
        return false;
    }

    @Override
    public boolean onMessageLongClick(Context context, View view, Message message) {
        return false;
    }

    public void pushActivity(Activity activity) {
        mActivities.add(activity);
    }

    public void popActivity(Activity activity) {
        if (mActivities.contains(activity)) {
            activity.finish();
            mActivities.remove(activity);
        }
    }

    public void popAllActivity() {
        try {
            for (Activity activity : mActivities) {
                if (activity != null) {
                    activity.finish();
                }
            }
            mActivities.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onConversationPortraitClick(Context context, Conversation.ConversationType conversationType, String s) {
        return false;
    }

    @Override
    public boolean onConversationPortraitLongClick(Context context, Conversation.ConversationType conversationType, String s) {
        return false;
    }

    @Override
    public boolean onConversationLongClick(Context context, View view, UIConversation uiConversation) {
        return false;
    }

    @Override
    public boolean onConversationClick(Context context, View view, UIConversation uiConversation) {
        MessageContent messageContent = uiConversation.getMessageContent();
        if (messageContent instanceof ContactNotificationMessage) {
            ContactNotificationMessage contactNotificationMessage = (ContactNotificationMessage) messageContent;
            if (contactNotificationMessage.getOperation().equals("AcceptResponse")) {
                // 被加方同意请求后
                if (contactNotificationMessage.getExtra() != null) {
                    ContactNotificationMessageData bean = null;
                    contactNotificationMessage.getExtra();
                    Gson gson=new Gson();
                    Type type=new TypeToken<ContactNotificationMessageData>(){}.getType();
                    bean=gson.fromJson(contactNotificationMessage.getExtra(),type);
                    RongIM.getInstance().startPrivateChat(context, uiConversation.getConversationSenderId(),
                            bean.getSourceNickName());

                }
            } else {
                context.startActivity(new Intent(context, NewFriendListActivity.class));
            }
            return true;
        }
        return false;
    }

    /**
     * 好友请求
     * @param message
     * @param i
     * @return
     */
    @Override
    public boolean onReceived(Message message, int i) {
        MessageContent messageContent = message.getContent();
        if (messageContent instanceof ContactNotificationMessage) {
            ContactNotificationMessage contactNotificationMessage = (ContactNotificationMessage) messageContent;
            if (contactNotificationMessage.getOperation().equals("Request")) {
                //对方发来好友邀请
                BroadcastManager.getInstance(mContext).sendBroadcast(AppContext.UPDATE_RED_DOT);
            } else if (contactNotificationMessage.getOperation().equals("AcceptResponse")) {
                //对方同意我的好友请求
                ContactNotificationMessageData c = null;

                Gson gson=new Gson();
                Type type=new TypeToken<ContactNotificationMessageData>(){}.getType();
                c=gson.fromJson(contactNotificationMessage.getExtra(),type);
                /*try {

                    c = JsonMananger.jsonToBean(contactNotificationMessage.getExtra(), ContactNotificationMessageData.class);
                } catch (HttpException e) {
                    e.printStackTrace();
                }*/
//                if (c != null) {
//                    DBManager.getInstance(mContext).getDaoSession().getFriendDao().insertOrReplace(new Friend(contactNotificationMessage.getSourceUserId(), c.getSourceUserNickname(), null, null, null, null));
//                }
//                DBManager.getInstance(mContext).getDaoSession().getFriendDao().insertOrReplace(
//                        new FriendInfo(contactNotificationMessage.getSourceUserId(), c.getSourceUserNickname(), null, null, null, null));
                BroadcastManager.getInstance(mContext).sendBroadcast(UPDATE_FRIEND);
                BroadcastManager.getInstance(mContext).sendBroadcast(AppContext.UPDATE_RED_DOT);
            }
//                // 发广播通知更新好友列表
            BroadcastManager.getInstance(mContext).sendBroadcast(UPDATE_RED_DOT);
        } else if (messageContent instanceof GroupNotificationMessage) {
            GroupNotificationMessage groupNotificationMessage = (GroupNotificationMessage) messageContent;
            L.e("" + groupNotificationMessage.getMessage());
            if (groupNotificationMessage.getOperation().equals("Kicked")) {
            } else if (groupNotificationMessage.getOperation().equals("Add")) {
            } else if (groupNotificationMessage.getOperation().equals("Quit")) {
            } else if (groupNotificationMessage.getOperation().equals("Rename")) {
            }

        } else if (messageContent instanceof ImageMessage) {
            ImageMessage imageMessage = (ImageMessage) messageContent;
            Log.e("imageMessage", imageMessage.getRemoteUri().toString());
        }
        return false;
    }

    /**
     * 定位
     * @param context
     * @param locationCallback
     */
    @Override
    public void onStartLocation(Context context, LocationCallback locationCallback) {
        AppContext.getInstance().setLastLocationCallback(locationCallback);

        Intent intent = new Intent(context, AMAPLocationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    public RongIM.LocationProvider.LocationCallback getLastLocationCallback() {
        return mLastLocationCallback;
    }

    public void setLastLocationCallback(RongIM.LocationProvider.LocationCallback lastLocationCallback) {
        this.mLastLocationCallback = lastLocationCallback;
    }

    /**
     * 头像
     */
    private void initPortrait() {
        //用户头像
        list = new ArrayList<>();
        String userId=sp.getString(Const.LOGIN_ID,"");
        String name=sp.getString(Const.LOGIN_NICKNAME,"");
        String portrait;
        if(TextUtils.isEmpty(sp.getString(Const.LOGIN_PORTRAIT,""))){
            portrait="http://192.168.0.209/public/effect/assets/avatars/avatar.jpg";
        }else {
            portrait = sp.getString(Const.LOGIN_PORTRAIT, "");
            L.e("-----------",portrait);
        }
        String displayName="";
        String phone=sp.getString(Const.LOGIN_PHONE,"");
        String email=sp.getString(Const.LOGIN_EMAIL,"");
        list.clear();
        list=friendInfoDAO.findAll(userId);
        list.add(new FriendInfo(userId,name,portrait,displayName,phone,email));
        RongIM.setUserInfoProvider(this, true);
    }

    @Override
    public UserInfo getUserInfo(String s) {
        UserInfoManager.getInstance().getUserInfo(s);
        return null;
        /*if (TextUtils.isEmpty(s)) {
            return null;
        }
        if (list != null) {
            UserInfo userInfo = mUserInfoCache.get(userId);
            if (userInfo != null) {
                RongIM.getInstance().refreshUserInfoCache(userInfo);
                L.e("-------", "SealUserInfoManager getUserInfo from cache " + s + " "
                        + userInfo.getName() + " " + userInfo.getPortraitUri());
                return userInfo;
            }
        }*/
    }

        /*for (FriendInfo i : list) {
            if (i.getUserId().equals(s)) {
                UserInfo userInfo = new UserInfo(i.getUserId(), i.getName(), Uri.parse(i.getPortraitUri()));
                return userInfo;
            }
        }

        if (mUserInfoCache != null) {
            UserInfo userInfo = mUserInfoCache.get(userId);
            if (userInfo != null) {
                RongIM.getInstance().refreshUserInfoCache(userInfo);
                NLog.d(TAG, "SealUserInfoManager getUserInfo from cache " + userId + " "
                        + userInfo.getName() + " " + userInfo.getPortraitUri());
                return;
            }
        }
        mWorkHandler.post(new Runnable() {
            @Override
            public void run() {
                UserInfo userInfo;
                Friend friend = getFriendByID(userId);
                if (friend != null) {
                    String name = friend.getName();
                    if (friend.isExitsDisplayName()) {
                        name = friend.getDisplayName();
                    }
                    userInfo = new UserInfo(friend.getUserId(), name,
                            Uri.parse(friend.getPortraitUri()));
                    NLog.d(TAG, "SealUserInfoManager getUserInfo from Friend db " + userId + " "
                            + userInfo.getName() + " " + userInfo.getPortraitUri());
                    RongIM.getInstance().refreshUserInfoCache(userInfo);
                    return;
                }
                List<GroupMember> groupMemberList = getGroupMembersWithUserId(userId);
                if (groupMemberList != null && groupMemberList.size() > 0) {
                    GroupMember groupMember = groupMemberList.get(0);
                    userInfo = new UserInfo(groupMember.getUserId(), groupMember.getName(),
                            Uri.parse(groupMember.getPortraitUri()));
                    NLog.d(TAG, "SealUserInfoManager getUserInfo from GroupMember db " + userId + " "
                            + userInfo.getName() + " " + userInfo.getPortraitUri());
                    RongIM.getInstance().refreshUserInfoCache(userInfo);
                    return;
                }
                UserInfoEngine.getInstance(mContext).startEngine(userId);
            }
        });*/
//    }
}
