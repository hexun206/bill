package com.huatu.teacheronline.direct;

import android.media.Ringtone;
import android.os.Bundle;
import android.view.View;

import cn.xiaoneng.activity.ChatActivity;
import cn.xiaoneng.uiapi.EPlusFunctionType;
import cn.xiaoneng.uiapi.Ntalker;
import cn.xiaoneng.uiapi.OnCustomMsgListener;
import cn.xiaoneng.uiapi.OnPlusFunctionClickListener;
import cn.xiaoneng.uiapi.XNSendGoodsBtnListener;

public class TestChatActivity extends ChatActivity implements OnPlusFunctionClickListener, OnCustomMsgListener,XNSendGoodsBtnListener {

	Ringtone ringtonenotification;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setExtraFunc();
		super.onCreate(savedInstanceState);

	}

	private void setExtraFunc() {
		// 设置头像
//		Bitmap  bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.accept_btn);
//		Ntalker.getExtendInstance().settings().setUsersHeadIcon(bitmap);

		// 设置是否显示连接名片
//		Ntalker.getExtendInstance().settings().setShowCard(false);
//		Ntalker.getExtendInstance().settings().setShowVideo(true);
		
		
		
		// //设置商品Bar
//		Ntalker.getExtendInstance().chatHeadBar().setOnViewClickListener(this);
//		 Ntalker.getExtendInstance().chatHeadBar().setOnViewInflatedListener(R.layout.list_item2, this);
//		Ntalker.getExtendInstance().chatHeadBar().setOnViewClickListener(this);
		Ntalker.getExtendInstance().chatHeadBar().setOnSendGoodsListener(this);
//		Ntalker.getExtendInstance().message().setOnCustomMsgListener(1, R.layout.customorder_demo, this);
//		Ntalker.getExtendInstance().message().setOnCustomMsgListener(2, R.layout.customtitlebar_demo, this);
//		Ntalker.getExtendInstance().message().setOnCustomMsgListener(3, R.layout.customgoods_demo, this);
		
		//
		// //设置+号 功能
		// 
		// //Ntalker.getExtendInstance().extensionArea().removeAll();
		Ntalker.getExtendInstance().extensionArea().setOnPlusFunctionClickListener(this);
		Ntalker.getExtendInstance().extensionArea().addPlusFunction(EPlusFunctionType.DEFAULT_VIDEO);
//		Ntalker.getExtendInstance().extensionArea().addPlusFunction(EPlusFunctionType.SELFDEFINE, "最近订单", R.drawable.chat_order_style);
//		Ntalker.getExtendInstance().extensionArea().addPlusFunction(EPlusFunctionType.SELFDEFINE, "最近商品", R.drawable.chat_goods_style);
	}



	@Override
	public void onPlusFunctionClick(String functionName) {
		// TODO Auto-generated method stub
		String funname1 = "最近订单";
		String funname2 = "最近商品";
		if (functionName.equals(funname1)) {
//			Toast.makeText(getApplicationContext(), "点击自定义功能:" + functionName, Toast.LENGTH_SHORT).show();
//			Intent intent = new Intent(this, OrderListActivity.class);
//			startActivity(intent);
		}

		if (functionName.equals(funname2)) {
//			Toast.makeText(getApplicationContext(), "点击自定义功能:" + functionName, Toast.LENGTH_SHORT).show();
//			Intent intent = new Intent(this, GoodsListActivity.class);
//			startActivity(intent);
		}
	}



	/**
	 * 1为订单格式的消息；2为商品详情格式的消息；3为最近商品格式的消息
	 */
	@Override
	public void setCustomViewFromDB(View view, int msgType, String[] msg) {
//		if (msgType == 1) {
//			TextView tv_orderid = (TextView) view.findViewById(R.id.tv_orderid_demo);
//			TextView tv_ordernum = (TextView) view.findViewById(R.id.tv_ordernum_demo);
//			TextView tv_orderprice = (TextView) view.findViewById(R.id.tv_orderprice_demo);
//			TextView tv_ordertime = (TextView) view.findViewById(R.id.tv_ordertime_demo);
//			ImageView pic = (ImageView) view.findViewById(R.id.iv_icon_demo);
//			RelativeLayout rl_custom = (RelativeLayout) view.findViewById(R.id.rl_custom);
//			NtLog.i_logic("custom  接收 " + msg[4]);
//			tv_orderid.setText(msg[0]);
//			tv_ordernum.setText(msg[1]);
//			tv_ordertime.setText(msg[2]);
//			tv_orderprice.setText(msg[3]);
//			ImageShow.getInstance(view.getContext()).DisplayImage(ImageShow.IMAGE_NORMAL, null, msg[4], pic, null, R.drawable.watch, R.drawable.watch, null);
//
//			rl_custom.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					Intent intent = new Intent(TestChatActivity.this, XNExplorerActivity.class);
//					intent.putExtra("urlintextmsg", "https://www.wbiao.cn/epos-g53474.html");// 实际打开的链接
//					startActivity(intent);
////					Toast.makeText(v.getContext(), "点击跳转到订单", Toast.LENGTH_SHORT).show();
//				}
//			});
//		} else if (msgType == 3) {
//			TextView tv_goodstitle = (TextView) view.findViewById(R.id.tv_goodstitle);
//			TextView tv_orderprice_demo = (TextView) view.findViewById(R.id.tv_orderprice_demo);
//			ImageView iv_goodsicon = (ImageView) view.findViewById(R.id.iv_goodsicon_demo);
//			RelativeLayout rl_goodscustom = (RelativeLayout) view.findViewById(R.id.rl_goodscustom);
//			NtLog.i_logic("自定义消息，msg.length =" + msg.length + ",msg[2]=" + msg[2]);
//			tv_goodstitle.setText(msg[0]);
//			tv_orderprice_demo.setText(msg[1]);
//			ImageShow.getInstance(view.getContext()).DisplayImage(ImageShow.IMAGE_NORMAL, null, msg[2], iv_goodsicon, null, R.drawable.watch2, R.drawable.watch2, null);
//
//			rl_goodscustom.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//
//					Intent intent = new Intent(TestChatActivity.this, XNExplorerActivity.class);
//					intent.putExtra("urlintextmsg", "http://www.wbiao.cn/davosa-g57727.html");// 实际打开的链接
//					startActivity(intent);
////					Toast.makeText(v.getContext(), "点击跳转到最近商品", Toast.LENGTH_SHORT).show();
//				}
//			});
//		} else if (msgType == 2) {
//			final String url = "http://zhenimg.com/upload/20/08/small_2008188bb2f3af717bb5848854b11d41.JPG";
//			TextView tv_titlebar_price = (TextView) view.findViewById(R.id.tv_titlebar_price);
//			TextView tv_titlebar_title = (TextView) view.findViewById(R.id.tv_titlebar_title);
//			ImageView iv_titlebar_icon = (ImageView) view.findViewById(R.id.iv_titlebar_icon);
//			RelativeLayout rl_titlebarcustom = (RelativeLayout) view.findViewById(R.id.rl_titlebarcustom);
//			tv_titlebar_title.setText(msg[0]);
//			tv_titlebar_price.setText(msg[1]);
//
//			rl_titlebarcustom.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//
//					Intent intent = new Intent(TestChatActivity.this, XNExplorerActivity.class);
//					intent.putExtra("urlintextmsg", "www.baidu.com");// 实际打开的链接
//					startActivity(intent);
////					Toast.makeText(v.getContext(), "点击跳转到台头url", Toast.LENGTH_SHORT).show();
//				}
//			});
//		}
	}

	@Override
	public void setSendGoodsBtnListener(View view, final String title, final String price,
			final String pic) {
		
//		view.findViewById(R.id.tv_sendgoods).setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Ntalker.getExtendInstance().message().sendCustomMsg(2, new String[] { title, price, pic });

			}
//		});
		
//	}


}
