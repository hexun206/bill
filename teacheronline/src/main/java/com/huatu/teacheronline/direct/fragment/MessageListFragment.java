package com.huatu.teacheronline.direct.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baijia.player.playback.PBRoom;
import com.baijiahulian.livecore.context.LPConstants;
import com.baijiahulian.livecore.models.imodels.IMessageModel;
import com.baijiahulian.livecore.utils.DisplayUtils;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.utils.GsonUtils;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by kinndann on 2018/7/2/002.
 * description:
 */

public class MessageListFragment extends Fragment {
    @BindView(R.id.rcv_message)
    RecyclerView mRcvMessage;
    Unbinder unbinder;
    private PBRoom mRoom;
    private View mRoot_view;
    private int emojiSize;

    public void setRoom(PBRoom room) {
        mRoom = room;
        mRoom.getChatVM(); // viewModel 都是懒加载, 不初始化，整个消息模块都不会运行起来。
    }

    public static MessageListFragment newInstance(PBRoom room) {
        MessageListFragment fragment = new MessageListFragment();
        fragment.setRoom(room);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRoot_view = inflater.inflate(R.layout.fragment_message, container, false);
        unbinder = ButterKnife.bind(this, mRoot_view);
        initUI();
        return mRoot_view;
    }

    private void initUI() {
        emojiSize = (int) (DisplayUtils.getScreenDensity(getContext()) * 32);

        mRcvMessage.setHasFixedSize(true);
        mRcvMessage.setLayoutManager(new LinearLayoutManager(getActivity()));


        mRcvMessage.setAdapter(messageAdapter);
        mRoom.getChatVM().getObservableOfNotifyDataChange()
                .onBackpressureBuffer(1000)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<IMessageModel>>() {
                    @Override
                    public void call(List<IMessageModel> iMessageModels) {
                        messageAdapter.notifyDataSetChanged();
                        mRcvMessage.scrollToPosition(messageAdapter.getItemCount()-1);
                    }
                });


    }

    private RecyclerView.Adapter messageAdapter = new RecyclerView.Adapter<ViewHolder>() {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatmsglist_layout, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            IMessageModel messageModel = mRoom.getChatVM().getMessage(position);
            Logger.e("messageModel:"+ GsonUtils.toJson(messageModel));

            String time = messageModel.getTimestamp() == null?"":formatData("HH:mm:ss", messageModel.getTimestamp().getTime());
            boolean isTeacher = messageModel.getFrom().getType() == LPConstants.LPUserType.Teacher;

            holder.name.setText(messageModel.getFrom().getName());
            holder.time.setText(time);
//            holder.record.setText(messageModel.getContent());
            holder.name.setTextColor(isTeacher ? getResources().getColor(R.color.blue005) : getResources().getColor(R.color.black013));

            if (messageModel.getMessageType() == LPConstants.MessageType.Emoji) {
                holder.img.setVisibility(View.VISIBLE);
                Picasso.with(getContext()).load(messageModel.getUrl())
                        .placeholder(R.drawable.live_ic_emoji_holder)
                        .error(R.drawable.live_ic_emoji_holder)
                        .resize(emojiSize, emojiSize)
                        .into(holder.img);
                holder.record.setVisibility(View.GONE);

            } else {
                holder.record.setVisibility(View.VISIBLE);
                holder.img.setVisibility(View.GONE);
                holder.record.setText(messageModel.getContent());
            }

        }

        @Override
        public int getItemCount() {
            return mRoom.getChatVM().getMessageCount();
        }
    };

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView time;
        private TextView record;
        private ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            record = (TextView) itemView.findViewById(R.id.tv_record);
            img = (ImageView) itemView.findViewById(R.id.img_content);
        }
    }


    private String formatData(String dataFormat, long timeStamp) {
        if (timeStamp == 0) {
            return "";
        }
        String result = "";
        SimpleDateFormat format = new SimpleDateFormat(dataFormat);
        result = format.format(new Date(timeStamp));
        return result;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
