package com.huatu.teacheronline.widget.bjywidget;

import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.baijiahulian.player.bean.VideoItem;
import com.baijiahulian.player.playerview.CenterViewStatus;
import com.baijiahulian.player.playerview.IPlayerCenterContact;
import com.baijiahulian.player.utils.Query;
import com.baijiahulian.player.utils.Utils;
import com.huatu.teacheronline.R;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by kinndann on 2018/9/14.
 * description:
 */
public class CenterViewPresenter implements IPlayerCenterContact.CenterView {
    private static final int CENTER_PAGE_INIT = 0;
    private static final int CENTER_PAGE_FRAME = 1;
    private static final int CENTER_PAGE_RATE = 2;
    private static final int CENTER_PAGE_SEGMENTS = 4;
    private CenterViewStatus centerViewStatus;
    private int mCenterPageState;
    private Query $;
    private IPlayerCenterContact.IPlayer mPlayer;
    private a mHandler;
    private boolean isDialogShowing;
    private List<VideoItem.DefinitionItem> definitionItemList;
    private boolean isRightMenuHidden;
    private View centerView;

    public void setRightMenuHidden(boolean var1) {
        this.isRightMenuHidden = var1;
    }

    public CenterViewPresenter(View var1) {
        this.centerViewStatus = CenterViewStatus.NONE;
        this.mCenterPageState = 0;
        this.isDialogShowing = false;
        this.isRightMenuHidden = false;
        this.centerView = var1;
        this.$ = Query.with(var1);
        this.mHandler = new a(this);
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_functions_rate_tv).clicked(new View.OnClickListener() {
            public void onClick(View var1) {
                if (CenterViewPresenter.this.mPlayer.getOrientation() == 1) {
                    CenterViewPresenter.this.mCenterPageState = 2;
                    CenterViewPresenter.this.setPageView();
                }
            }
        });
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_functions_segments_tv).clicked(new View.OnClickListener() {
            public void onClick(View var1) {
                if (CenterViewPresenter.this.mPlayer.getOrientation() == 1) {
                    CenterViewPresenter.this.mCenterPageState = 4;
                    CenterViewPresenter.this.setPageView();
                }
            }
        });
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_functions_frame_tv).clicked(new View.OnClickListener() {
            public void onClick(View var1) {
                if (CenterViewPresenter.this.mPlayer.getOrientation() == 1) {
                    CenterViewPresenter.this.mCenterPageState = 1;
                    CenterViewPresenter.this.setPageView();
                }
            }
        });
//        this.initFunctions();
    }

    public void onBind(IPlayerCenterContact.IPlayer var1) {
        this.mPlayer = var1;
        this.setPageView();
    }

    public boolean onBackTouch() {
        if (this.mCenterPageState > 0) {
            this.mCenterPageState = 0;
            this.setPageView();
            return true;
        } else {
            return false;
        }
    }

    public void setOrientation(int var1) {
        if (var1 == 0) {
            this.onHide();
            this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_functions_ll).gone();
        }

    }

    public void showProgressSlide(int var1) {
        this.centerView.setVisibility(View.VISIBLE);
//        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_ll).visible();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_loading_pb).gone();
        this.$.id(R.id.tv_center_progress).visible();
//        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_title_iv).visible();
//        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_message_tv).visible();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_buttons_ll).gone();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_controller_volume_dialog_ll).gone();
        String var2 = Utils.formatDuration(this.mPlayer.getDuration());
        String var3 = Utils.formatDuration(this.mPlayer.getCurrentPosition() + var1, this.mPlayer.getDuration() >= 3600);
//        this.$.id(R.id.tv_center_progress).text(String.format("%s/%s", var3, var2));
        this.$.id(R.id.tv_center_progress).text(String.format("%s", var3));
//        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_message_tv).text(String.format("%s/%s", var3, var2));
//        if (var1 > 0) {
//            this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_title_iv).image(com.baijiahulian.player.R.drawable.bjplayer_ic_kuaijin);
//        } else {
//            this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_title_iv).image(com.baijiahulian.player.R.drawable.bjplayer_ic_huitui);
//        }

        this.centerViewStatus = CenterViewStatus.FUNCTION;
    }

    public void showLoading(String var1) {
        this.centerView.setVisibility(View.VISIBLE);
        this.$.id(R.id.tv_center_progress).gone();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_ll).visible();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_loading_pb).visible();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_title_iv).gone();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_message_tv).visible();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_message_tv).text(var1);
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_buttons_ll).gone();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_controller_volume_dialog_ll).gone();
        this.isDialogShowing = true;
        this.centerViewStatus = CenterViewStatus.LOADING;
    }

    public void dismissLoading() {
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_ll).gone();
        this.$.id(R.id.tv_center_progress).gone();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_controller_volume_dialog_ll).gone();
        this.isDialogShowing = false;
        this.centerViewStatus = CenterViewStatus.NONE;
    }

    public void showVolumeSlide(int var1, int var2) {
        this.centerView.setVisibility(View.VISIBLE);
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_ll).gone();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_controller_volume_dialog_ll).visible();
        int var3 = var1 * 100 / var2;
        if (var3 == 0) {
            this.$.id(com.baijiahulian.player.R.id.bjplayer_center_controller_volume_ic_iv).image(com.baijiahulian.player.R.drawable.bjplayer_ic_volume_off_white);
            this.$.id(com.baijiahulian.player.R.id.bjplayer_center_controller_volume_tv).text("off");
        } else {
            this.$.id(com.baijiahulian.player.R.id.bjplayer_center_controller_volume_ic_iv).image(com.baijiahulian.player.R.drawable.bjplayer_ic_volume_up_white);
            this.$.id(com.baijiahulian.player.R.id.bjplayer_center_controller_volume_tv).text(var3 + "%");
        }

        this.mHandler.y();
        this.centerViewStatus = CenterViewStatus.FUNCTION;
    }

    public void showBrightnessSlide(int var1) {
        this.centerView.setVisibility(View.VISIBLE);
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_ll).gone();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_controller_volume_dialog_ll).visible();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_controller_volume_ic_iv).image(com.baijiahulian.player.R.drawable.bjplayer_ic_brightness);
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_controller_volume_tv).text(var1 + "%");
        this.mHandler.y();
        this.centerViewStatus = CenterViewStatus.FUNCTION;
    }

    public void showError(int var1, int var2) {
        String[] var3 = this.$.contentView().getContext().getResources().getStringArray(com.baijiahulian.player.R.array.bjplayer_error_tips);
        int var4 = var1 - 1;
        String var5;
        if (var1 >= 0 && var1 < var3.length) {
            var5 = var3[var4];
        } else {
            var5 = this.$.contentView().getContext().getString(com.baijiahulian.player.R.string.bjplayer_error_unknow);
        }

        if (var1 == 500) {
            var5 = this.$.contentView().getContext().getString(com.baijiahulian.player.R.string.bjplayer_network_error);
        }

        this.showError(var1, var5);
    }

    public void showError(final int var1, String var2) {
        this.mHandler.removeCallbacksAndMessages((Object) null);
        this.centerView.setVisibility(View.VISIBLE);
        this.onHide();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_controller_volume_dialog_ll).gone();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_ll).visible();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_loading_pb).gone();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_title_iv).gone();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_message_tv).text(var2 + "\n[" + var1 + "]\n");
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_message_tv).visible();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_buttons_ll).visible();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_button2_tv).gone();
        if (var1 != -3) {
            this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_button1_tv).visible();
            this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_button1_tv).text(this.$.contentView().getContext().getString(com.baijiahulian.player.R.string.bjplayer_video_reload));
            this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_button1_tv).clicked(new View.OnClickListener() {
                public void onClick(View var1x) {
                    CenterViewPresenter.this.dismissLoading();
                    if (var1 == 500) {
                        CenterViewPresenter.this.mPlayer.ijkInternalError();
                    } else {
                        CenterViewPresenter.this.mPlayer.playVideo();
                    }

                }
            });
        }

        this.isDialogShowing = true;
        this.centerViewStatus = CenterViewStatus.ERROR;
    }

    public void showWarning(String var1) {
        this.mHandler.removeCallbacksAndMessages((Object) null);
        this.centerView.setVisibility(View.VISIBLE);
        this.onHide();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_controller_volume_dialog_ll).gone();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_ll).visible();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_loading_pb).gone();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_title_iv).gone();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_message_tv).text(var1 + "\n");
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_message_tv).visible();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_buttons_ll).visible();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_button1_tv).visible();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_button2_tv).gone();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_button1_tv).text(this.$.contentView().getContext().getString(com.baijiahulian.player.R.string.bjplayer_video_goon));
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_progress_dialog_button1_tv).clicked(new View.OnClickListener() {
            public void onClick(View var1) {
                CenterViewPresenter.this.dismissLoading();
                CenterViewPresenter.this.mPlayer.setEnableNetWatcher(false);
                CenterViewPresenter.this.mPlayer.playVideo();
            }
        });
        this.isDialogShowing = true;
        this.centerViewStatus = CenterViewStatus.WARNING;
    }

    public void onShow() {
        if (this.mPlayer != null && this.mPlayer.getOrientation() == 1 && !this.isRightMenuHidden) {
            this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_functions_ll).visible();
        } else {
            this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_functions_ll).gone();
        }

    }

    public void onHide() {
        this.$.id(com.baijiahulian.player.R.id.bjplayer_layout_center_video_functions_segments_ll).gone();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_layout_center_video_functions_rate_ll).gone();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_layout_center_video_functions_frame_ll).gone();
        this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_functions_ll).gone();
        this.mCenterPageState = 0;
    }

    public void onVideoInfoLoaded(VideoItem var1) {
//        if (var1 != null) {
//            if (this.courseAdapter != null) {
//                this.courseAdapter.notifyDataSetChanged();
//            }
//
//            if (var1.definition != null) {
//                this.definitionItemList = var1.definition;
//                this.definitionAdapter.notifyDataSetChanged();
//            }
//
//            if (this.mPlayer.isPlayLocalVideo()) {
//                this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_functions_frame_tv).gone();
//            } else {
//                this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_functions_frame_tv).visible();
//            }
//
//            this.updateDefinition();
//        }
    }

    public boolean isDialogShowing() {
        return this.isDialogShowing;
    }

    private void setPageView() {
        switch (this.mCenterPageState) {
            case 0:
                this.$.id(com.baijiahulian.player.R.id.bjplayer_layout_center_video_functions_segments_ll).gone();
                this.$.id(com.baijiahulian.player.R.id.bjplayer_layout_center_video_functions_rate_ll).gone();
                this.$.id(com.baijiahulian.player.R.id.bjplayer_layout_center_video_functions_frame_ll).gone();
                if (this.mPlayer == null) {
                    this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_functions_ll).gone();
                }

                if (this.mPlayer.getOrientation() == 1 && !this.isRightMenuHidden) {
                    this.setAnimationVisible(com.baijiahulian.player.R.id.bjplayer_center_video_functions_ll);
                } else {
                    this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_functions_ll).gone();
                }

                this.mPlayer.showTopAndBottom();
                break;
            case 1:
                this.$.id(com.baijiahulian.player.R.id.bjplayer_layout_center_video_functions_segments_ll).gone();
                this.$.id(com.baijiahulian.player.R.id.bjplayer_layout_center_video_functions_rate_ll).gone();
                this.setAnimationVisible(com.baijiahulian.player.R.id.bjplayer_layout_center_video_functions_frame_ll);
                this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_functions_ll).gone();
                this.mPlayer.hideTopAndBottom();
                break;
            case 2:
                this.$.id(com.baijiahulian.player.R.id.bjplayer_layout_center_video_functions_segments_ll).gone();
                this.setAnimationVisible(com.baijiahulian.player.R.id.bjplayer_layout_center_video_functions_rate_ll);
                this.$.id(com.baijiahulian.player.R.id.bjplayer_layout_center_video_functions_frame_ll).gone();
                this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_functions_ll).gone();
                this.setFocusRate();
                this.mPlayer.hideTopAndBottom();
            case 3:
            default:
                break;
            case 4:
                this.setAnimationVisible(com.baijiahulian.player.R.id.bjplayer_layout_center_video_functions_segments_ll);
                this.$.id(com.baijiahulian.player.R.id.bjplayer_layout_center_video_functions_rate_ll).gone();
                this.$.id(com.baijiahulian.player.R.id.bjplayer_layout_center_video_functions_frame_ll).gone();
                this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_functions_ll).gone();
                this.mPlayer.hideTopAndBottom();
        }

        this.updateDefinition();
    }

    public void updateDefinition() {
        if (this.mPlayer != null) {
            switch (this.mPlayer.getVideoDefinition()) {
                case 0:
                default:
                    this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_functions_frame_tv).text(this.$.contentView().getContext().getString(com.baijiahulian.player.R.string.bjplayer_video_frame_low));
                    break;
                case 1:
                    this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_functions_frame_tv).text(this.$.contentView().getContext().getString(com.baijiahulian.player.R.string.bjplayer_video_frame_high));
                    break;
                case 2:
                    this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_functions_frame_tv).text(this.$.contentView().getContext().getString(com.baijiahulian.player.R.string.bjplayer_video_frame_super));
                    break;
                case 3:
                    this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_functions_frame_tv).text(this.$.contentView().getContext().getString(com.baijiahulian.player.R.string.bjplayer_video_frame_720p));
                    break;
                case 4:
                    this.$.id(com.baijiahulian.player.R.id.bjplayer_center_video_functions_frame_tv).text(this.$.contentView().getContext().getString(com.baijiahulian.player.R.string.bjplayer_video_frame_1080p));
            }
        }

    }

    public CenterViewStatus getStatus() {
        return this.centerViewStatus;
    }

    private void setAnimationVisible(final int var1) {
        this.$.id(var1).visible();
        TranslateAnimation var2 = new TranslateAnimation((float) this.$.id(var1).view().getWidth(), 0.0F, 0.0F, 0.0F);
        var2.setDuration(500L);
        var2.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation var1x) {
            }

            public void onAnimationEnd(Animation var1x) {
                CenterViewPresenter.this.$.id(var1).view().clearAnimation();
            }

            public void onAnimationRepeat(Animation var1x) {
            }
        });
        this.$.id(var1).view().setAnimation(var2);
    }

    private void setAnimationGone(final int var1) {
        TranslateAnimation var2 = new TranslateAnimation(0.0F, (float) this.$.id(var1).view().getWidth(), 0.0F, 0.0F);
        var2.setDuration(400L);
        var2.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation var1x) {
            }

            public void onAnimationEnd(Animation var1x) {
                CenterViewPresenter.this.$.id(var1).view().clearAnimation();
                CenterViewPresenter.this.$.id(var1).gone();
            }

            public void onAnimationRepeat(Animation var1x) {
            }
        });
        this.$.id(var1).view().startAnimation(var2);
    }

    private void initFunctions() {
        this.$.id(com.baijiahulian.player.R.id.bjplayer_layout_center_video_functions_rate_0_7_btn).clicked(new View.OnClickListener() {
            public void onClick(View var1) {
                CenterViewPresenter.this.mPlayer.setVideoRate(0.7F);
                CenterViewPresenter.this.onBackTouch();
            }
        });
        this.$.id(com.baijiahulian.player.R.id.bjplayer_layout_center_video_functions_rate_1_btn).clicked(new View.OnClickListener() {
            public void onClick(View var1) {
                CenterViewPresenter.this.mPlayer.setVideoRate(1.0F);
                CenterViewPresenter.this.onBackTouch();
            }
        });
        this.$.id(com.baijiahulian.player.R.id.bjplayer_layout_center_video_functions_rate_1_2_btn).clicked(new View.OnClickListener() {
            public void onClick(View var1) {
                CenterViewPresenter.this.mPlayer.setVideoRate(1.2F);
                CenterViewPresenter.this.onBackTouch();
            }
        });
        this.$.id(com.baijiahulian.player.R.id.bjplayer_layout_center_video_functions_rate_1_5_btn).clicked(new View.OnClickListener() {
            public void onClick(View var1) {
                CenterViewPresenter.this.mPlayer.setVideoRate(1.5F);
                CenterViewPresenter.this.onBackTouch();
            }
        });
        this.$.id(com.baijiahulian.player.R.id.bjplayer_layout_center_video_functions_rate_2_btn).clicked(new View.OnClickListener() {
            public void onClick(View var1) {
                CenterViewPresenter.this.mPlayer.setVideoRate(2.0F);
                CenterViewPresenter.this.onBackTouch();
            }
        });
    }

    private void setFocusRate() {
        TextView var1 = (TextView) this.$.id(com.baijiahulian.player.R.id.bjplayer_layout_center_video_functions_rate_0_7_btn).view();
        TextView var2 = (TextView) this.$.id(com.baijiahulian.player.R.id.bjplayer_layout_center_video_functions_rate_1_btn).view();
        TextView var3 = (TextView) this.$.id(com.baijiahulian.player.R.id.bjplayer_layout_center_video_functions_rate_1_2_btn).view();
        TextView var4 = (TextView) this.$.id(com.baijiahulian.player.R.id.bjplayer_layout_center_video_functions_rate_1_5_btn).view();
        TextView var5 = (TextView) this.$.id(com.baijiahulian.player.R.id.bjplayer_layout_center_video_functions_rate_2_btn).view();
        TextView[] var6 = new TextView[]{var1, var2, var3, var4, var5};
        TextView[] var7 = var6;
        int var8 = var6.length;

        for (int var9 = 0; var9 < var8; ++var9) {
            TextView var10 = var7[var9];
            var10.setTextColor(ContextCompat.getColor(this.$.contentView().getContext(), R.color.white));
            var10.setBackgroundResource(com.baijiahulian.player.R.drawable.bjplayer_bg_radius_12);
        }

        if (this.mPlayer.getVideoRateInFloat() == 0.7F) {
            var1.setTextColor(ContextCompat.getColor(this.$.contentView().getContext(), com.baijiahulian.player.R.color.bjplayer_color_primary));
            var1.setBackgroundResource(com.baijiahulian.player.R.drawable.bjplayer_bg_primary_radius_12);
        } else if (this.mPlayer.getVideoRateInFloat() == 1.0F) {
            var2.setTextColor(ContextCompat.getColor(this.$.contentView().getContext(), com.baijiahulian.player.R.color.bjplayer_color_primary));
            var2.setBackgroundResource(com.baijiahulian.player.R.drawable.bjplayer_bg_primary_radius_12);
        } else if (this.mPlayer.getVideoRateInFloat() == 1.2F) {
            var3.setTextColor(ContextCompat.getColor(this.$.contentView().getContext(), com.baijiahulian.player.R.color.bjplayer_color_primary));
            var3.setBackgroundResource(com.baijiahulian.player.R.drawable.bjplayer_bg_primary_radius_12);
        } else if (this.mPlayer.getVideoRateInFloat() == 1.5F) {
            var4.setTextColor(ContextCompat.getColor(this.$.contentView().getContext(), com.baijiahulian.player.R.color.bjplayer_color_primary));
            var4.setBackgroundResource(com.baijiahulian.player.R.drawable.bjplayer_bg_primary_radius_12);
        } else if (this.mPlayer.getVideoRateInFloat() == 2.0F) {
            var5.setTextColor(ContextCompat.getColor(this.$.contentView().getContext(), com.baijiahulian.player.R.color.bjplayer_color_primary));
            var5.setBackgroundResource(com.baijiahulian.player.R.drawable.bjplayer_bg_primary_radius_12);
        }

    }

    public CenterViewStatus getCenterViewStatus() {
        return this.centerViewStatus;
    }


    interface d {
        void a(View var1, int var2);
    }

    private static class a extends Handler {
        private WeakReference<CenterViewPresenter> aw;

        private a(CenterViewPresenter var1) {
            this.aw = new WeakReference(var1);
        }

        private void y() {
            this.removeMessages(0);
            Message var1 = this.obtainMessage(0);
            this.sendMessageDelayed(var1, 2000L);
        }

        public void handleMessage(Message var1) {
            if (this.aw.get() != null) {
                switch (var1.what) {
                    case 0:
                        ((CenterViewPresenter) this.aw.get()).dismissLoading();
                    default:
                }
            }
        }
    }
}
