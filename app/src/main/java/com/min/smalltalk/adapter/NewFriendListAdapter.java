package com.min.smalltalk.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.min.mylibrary.widget.image.CircleImageView;
import com.min.smalltalk.App;
import com.min.smalltalk.R;
import com.min.smalltalk.base.BaseAdapters;
import com.min.smalltalk.bean.AllAddFriends;
import com.min.smalltalk.network.HttpUtils;
import com.min.smalltalk.wedget.Generate;

import io.rong.imageloader.core.ImageLoader;

/**
 * Created by Min on 2016/12/10.
 */

public class NewFriendListAdapter  extends BaseAdapters {

    public NewFriendListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_new_friends, parent, false);
            holder.mHead = (CircleImageView) convertView.findViewById(R.id.civ_icon);
            holder.mName = (TextView) convertView.findViewById(R.id.tv_nickname);
            holder.mMessage = (TextView) convertView.findViewById(R.id.tv_message);
            holder.tvAgree = (TextView) convertView.findViewById(R.id.tv_agree);
            holder.tvRefuse= (TextView) convertView.findViewById(R.id.tv_refuse);
            holder.tvIgnore= (TextView) convertView.findViewById(R.id.tv_ignore);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final AllAddFriends bean = (AllAddFriends) dataSet.get(position);
        holder.mName.setText(bean.getNickname());
        if (TextUtils.isEmpty(bean.getPortraitUri())) {
            ImageLoader.getInstance().displayImage(Generate.generateDefaultAvatar(bean.getNickname(), bean.getUserid()), holder.mHead, App.getOptions());
        } else {
            ImageLoader.getInstance().displayImage(HttpUtils.IMAGE_RUL+bean.getPortraitUri(), holder.mHead, App.getOptions());
        }
        holder.mMessage.setText(bean.getAddFriendMessage());
        holder.tvAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemButtonClick != null) {
                    mOnItemButtonClick.onButtonClick(position, v, bean.getStatus());
                }
            }
        });

        switch (bean.getStatus()) {
            case 3: //收到了好友邀请
                holder.tvAgree.setText(R.string.agree);
                break;
            case 1: // 发出了好友邀请
                holder.tvAgree.setText("已同意");
                break;
            case 2: // 忽略好友邀请
                holder.tvAgree.setText("已忽略");
                break;
            case 0: // 已是好友
                holder.tvAgree.setText("已拒绝");
                break;
        }
        return convertView;
    }

    class ViewHolder {
        CircleImageView mHead;
        TextView mName;
        TextView tvAgree;
        TextView tvRefuse;
        TextView tvIgnore;
        TextView mMessage;
    }

    OnItemButtonClick mOnItemButtonClick;


    public void setOnItemButtonClick(OnItemButtonClick onItemButtonClick) {
        this.mOnItemButtonClick = onItemButtonClick;
    }

    public interface OnItemButtonClick {
        boolean onButtonClick(int position, View view, int status);

    }
}
