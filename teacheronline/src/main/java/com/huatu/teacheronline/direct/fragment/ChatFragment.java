package com.huatu.teacheronline.direct.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.baijiahulian.livecore.context.LPConstants;
import com.baijiahulian.livecore.context.LiveRoom;
import com.baijiahulian.livecore.models.imodels.IExpressionModel;
import com.baijiahulian.livecore.models.imodels.IMessageModel;
import com.baijiahulian.livecore.utils.DisplayUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.direct.bean.HideSoftInputEvent;
import com.huatu.teacheronline.utils.StringUtils;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.functions.Action1;

import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;

public class ChatFragment extends Fragment {


    @BindView(R.id.rcv_chat)
    RecyclerView mRcvChat;
    @BindView(R.id.img_chat_send)
    ImageView mImgChatSend;
    @BindView(R.id.img_chat_emoji)
    ImageView mImgChatEmoji;
    @BindView(R.id.edt_chat_msg)
    EditText mEdtChatMsg;
    @BindView(R.id.vp_chat_emoji)
    ViewPager mVpChatEmoji;
    Unbinder unbinder;
    private View mRoot_view;

    private EmojiPagerAdapter pagerAdapter;
    private int gridPadding;
    private int spanCount = 8;
    private int rouCount = 5;

    private int PAGE_SIZE = spanCount * rouCount;
    private List<IExpressionModel> emojiList;
    private List<IMessageModel> chatMsgList = new ArrayList<>();
    private int currentPageFirstItem;
    private BaseQuickAdapter<IMessageModel, BaseViewHolder> mChatMsgAdapter;
    private LiveRoom mLiveRoom;
    private int emojiSize;

    public ChatFragment() {
    }

    public static ChatFragment newInstance(LiveRoom liveRoom) {
        ChatFragment fragment = new ChatFragment();
        fragment.setLiveRoom(liveRoom);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void setLiveRoom(LiveRoom liveRoom) {
        mLiveRoom = liveRoom;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRoot_view = inflater.inflate(R.layout.fragment_chat, container, false);
        unbinder = ButterKnife.bind(this, mRoot_view);
        initUI();
        initChat();
        return mRoot_view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public boolean back() {
        boolean use = false;
        if (mVpChatEmoji != null && mVpChatEmoji.getVisibility() == View.VISIBLE) {
            mVpChatEmoji.setVisibility(View.GONE);
            use = true;
        }

        return use;
    }

    public void initUI() {
        emojiSize = (int) (DisplayUtils.getScreenDensity(getContext()) * 32);


        mEdtChatMsg.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {

                if (null != keyEvent && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode()) {
                    switch (keyEvent.getAction()) {
                        case KeyEvent.ACTION_UP:
                            String msg = mEdtChatMsg.getText().toString().trim();

                            if (!StringUtils.isEmpty(msg)) {
                                mLiveRoom.getChatVM().sendMessage(msg);
                            }
                            mEdtChatMsg.setText("");
                            HideSoftInputEvent event = new HideSoftInputEvent();
                            event.setClose(true);

                            EventBus.getDefault().post(event);


                            return true;
                        default:
                            return true;
                    }
                }

                return false;
            }
        });


        mEdtChatMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (StringUtils.isEmpty(charSequence.toString())) {
                    mImgChatSend.setEnabled(false);
                } else {
                    mImgChatSend.setEnabled(true);
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        mRcvChat.setHasFixedSize(true);
        mRcvChat.setLayoutManager(new LinearLayoutManager(getActivity()));
        mChatMsgAdapter = new BaseQuickAdapter<IMessageModel, BaseViewHolder>(R.layout.item_chatmsglist_layout, chatMsgList) {

            @Override
            protected void convert(BaseViewHolder helper, IMessageModel item) {
//                Logger.e("liveRoom chat:" + GsonUtils.toJson(item));
                boolean isTeacher = item.getFrom().getType() == LPConstants.LPUserType.Teacher;
                String time = formatData("HH:mm:ss", item.getTime().getTime());
                helper
                        .setText(R.id.tv_name, item.getFrom().getName().equals(mLiveRoom.getCurrentUser().getName()) ? "我" : item.getFrom().getName())
                        .setTextColor(R.id.tv_name, isTeacher ? getResources().getColor(R.color.blue005) : getResources().getColor(R.color.black013))
                        .setText(R.id.tv_time, time);
//                        .setText(R.id.tv_record, item.getContent());
                ImageView img = (ImageView) helper.getView(R.id.img_content);
                if (item.getMessageType() == LPConstants.MessageType.Emoji) {
                    img.setVisibility(View.VISIBLE);
                    Picasso.with(getContext()).load(item.getUrl())
                            .placeholder(R.drawable.live_ic_emoji_holder)
                            .error(R.drawable.live_ic_emoji_holder)
                            .resize(emojiSize, emojiSize)
                            .into(img);
                    helper.setGone(R.id.tv_record, false);

                } else {
                    helper.setGone(R.id.tv_record, true);
                    img.setVisibility(View.GONE);
                    helper.setText(R.id.tv_record, item.getContent());
                }


            }
        };
        mRcvChat.setAdapter(mChatMsgAdapter);


    }

    public void initChat() {
        emojiList = mLiveRoom.getChatVM().getExpressions();
        mLiveRoom.getChatVM().getObservableOfReceiveMessage().subscribe(new Action1<IMessageModel>() {
            @Override
            public void call(IMessageModel iMessageModel) {
                if (mChatMsgAdapter != null) {
                    mChatMsgAdapter.addData(iMessageModel);
                    mRcvChat.smoothScrollToPosition(mChatMsgAdapter.getData().size() - 1);
                }


            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Logger.e("getObservableOfReceiveMessage" + throwable.getMessage());
            }
        });
        EmojiPagerAdapter emojiPagerAdapter = new EmojiPagerAdapter();
        mVpChatEmoji.setAdapter(emojiPagerAdapter);
        mVpChatEmoji.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == SCROLL_STATE_IDLE) {
                    currentPageFirstItem = mVpChatEmoji.getCurrentItem() * PAGE_SIZE + 1;
                }
            }
        });

    }


    @OnClick({R.id.img_chat_send, R.id.img_chat_emoji})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_chat_send:
                String msg = mEdtChatMsg.getText().toString().trim();
                mLiveRoom.getChatVM().sendMessage(msg);
                mEdtChatMsg.setText("");
                HideSoftInputEvent event = new HideSoftInputEvent();
                event.setClose(true);

                EventBus.getDefault().post(event);

                break;
            case R.id.img_chat_emoji:
                if (mVpChatEmoji.getVisibility() == View.GONE) {
                    mVpChatEmoji.setVisibility(View.VISIBLE);
                } else {
                    mVpChatEmoji.setVisibility(View.GONE);
                }
                break;
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


    /********emoji Adapter************/


    private class EmojiPagerAdapter extends PagerAdapter {

        private View[] viewList;

        EmojiPagerAdapter() {
            viewList = new View[getCount()];
        }

        @Override
        public int getCount() {
            return emojiList.size() % PAGE_SIZE == 0 ? emojiList.size() / PAGE_SIZE : emojiList.size() / PAGE_SIZE + 1;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (viewList.length <= position || viewList[position] == null) {
                GridView gridView = new GridView(getContext());
                gridView.setPadding(gridPadding, 0, gridPadding, 0);
                gridView.setNumColumns(spanCount);
                gridView.setAdapter(new EmojiAdapter(position));
                viewList[position] = gridView;
            }
            container.addView(viewList[position]);
            return viewList[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList[position]);
        }

        @Override
        public void startUpdate(ViewGroup container) {
            super.startUpdate(container);
        }
    }


    private class EmojiAdapter extends BaseAdapter {

        private int page;

        EmojiAdapter(int page) {
            this.page = page;
        }

        @Override
        public int getCount() {
            return emojiList.size() < PAGE_SIZE * (page + 1) ? emojiList.size() % PAGE_SIZE : PAGE_SIZE;
        }

        @Override
        public IExpressionModel getItem(int position) {
            return emojiList.get(page * PAGE_SIZE + position);
        }

        @Override
        public long getItemId(int position) {
            return page * spanCount * rouCount + position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(R.layout.item_emoji, parent, false);
                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.item_emoji_iv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final IExpressionModel expressionModel = getItem(position);
            Picasso.with(getContext()).load(expressionModel.getUrl()).into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLiveRoom.getChatVM().sendEmojiMessage("[" + expressionModel.getKey() + "]");
                    mVpChatEmoji.setVisibility(View.GONE);
                }
            });
            return convertView;
        }
    }

    private class ViewHolder {
        ImageView imageView;
    }

}
