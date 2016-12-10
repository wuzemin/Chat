package com.min.smalltalk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.min.mylibrary.util.L;
import com.min.smalltalk.activity.NewFriendListActivity;
import com.min.smalltalk.bean.ContactNotificationMessageData;
import com.min.smalltalk.server.broadcast.BroadcastManager;

import java.lang.reflect.Type;
import java.util.ArrayList;

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

/**
 * Created by Min on 2016/11/24.
 * 融云相关监听 事件集合类
 */

public class AppContext implements RongIMClient.ConnectionStatusListener,
         RongIM.ConversationBehaviorListener, RongIM.ConversationListBehaviorListener
        ,RongIMClient.OnReceiveMessageListener{

    public static final String UPDATE_FRIEND = "update_friend";
    public static final String UPDATE_RED_DOT = "update_red_dot";
    private Context mContext;

    private static AppContext mRongCloudInstance;

    private static ArrayList<Activity> mActivities;

    public AppContext(Context mContext) {
        this.mContext = mContext;
        initListener();
        mActivities = new ArrayList<>();
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
//        RongIM.setUserInfoProvider(this,true);  //用户信息提供者
        RongIM.setConversationListBehaviorListener(this);  //会话列表界面
//        RongIM.setGroupInfoProvider(this, true);  //群组用户提供者
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

        /*InputProvider.ExtendProvider[] singleProvider = {
                new ImageInputProvider(RongContext.getInstance()),
                new FileInputProvider(RongContext.getInstance())//文件消息
        };

        InputProvider.ExtendProvider[] muiltiProvider = {
                new ImageInputProvider(RongContext.getInstance()),
                new LocationInputProvider(RongContext.getInstance()),//地理位置
                new FileInputProvider(RongContext.getInstance()),//文件消息
                new CameraInputProvider(RongContext.getInstance()), //相机
                new ContactsProvider(RongContext.getInstance()),  //自定义
                new SpeechProvider(RongContext.getInstance()),  //语音
                new TestProvider(RongContext.getInstance()),  //开始录音
                new TestEndProvider(RongContext.getInstance()) //结束录音
        };

        InputProvider.ExtendProvider[] provider= {
                new ImageInputProvider(RongContext.getInstance()), //图片
                new CameraInputProvider(RongContext.getInstance()), //相机
                new FileInputProvider(RongContext.getInstance()),   //文件
                new LocationInputProvider(RongContext.getInstance()), //位置
                new ContactsProvider(RongContext.getInstance()),  //自定义
                new SpeechProvider(RongContext.getInstance()),  //语音
                new TestProvider(RongContext.getInstance()),  //开始录音
                new TestEndProvider(RongContext.getInstance()) //结束录音
        };

        RongIM.resetInputExtensionProvider(Conversation.ConversationType.PRIVATE, muiltiProvider);
        RongIM.resetInputExtensionProvider(Conversation.ConversationType.DISCUSSION, muiltiProvider);
        RongIM.resetInputExtensionProvider(Conversation.ConversationType.CUSTOMER_SERVICE, muiltiProvider);
        RongIM.resetInputExtensionProvider(Conversation.ConversationType.GROUP, muiltiProvider);
        RongIM.resetInputExtensionProvider(Conversation.ConversationType.CHATROOM, muiltiProvider);*/
//        RongIM.resetInputExtensionProvider(Conversation.ConversationType.PRIVATE,provider);
//        RongIM.resetInputExtensionProvider(Conversation.ConversationType.GROUP,provider);
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
        if (message.getContent() instanceof ImageMessage) {
//            Intent intent = new Intent(context, PhotoActivity.class);
//            intent.putExtra("message", message);
//            context.startActivity(intent);
        }

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
}
