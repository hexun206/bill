package com.huatu.teacheronline.direct.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.gensee.chat.gif.SpanResource;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.utils.DebugUtil;

import java.util.Map;

public class GridViewAvatarAdapter extends BaseAdapter{
	private Object[] resIds;

	private Map<String, Drawable> browMap;
	private SelectAvatarInterface selectAvatarInterface;

	public GridViewAvatarAdapter(Context context,
								 SelectAvatarInterface selectAvatarInterface) {
		browMap = SpanResource.getBrowMap(context);
		resIds = browMap.keySet().toArray();
		this.selectAvatarInterface = selectAvatarInterface;
	}

	
	@Override
	public int getCount() {
		return resIds.length / 2;
	}

	@Override
	public Object getItem(int position) {
		return resIds[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GridViewHolder viewHolder = null;
		if (null == convertView) {
		
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(
					R.layout.single_expression_layout, null);
			viewHolder = new GridViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (GridViewHolder) convertView.getTag();
		}
		viewHolder.init((String) getItem(position),
				browMap.get(getItem(position)), convertView);
		return convertView;
	}

	private class GridViewHolder {
		private ImageView ivAvatar;

		public GridViewHolder(View view) {
			ivAvatar = (ImageView) view.findViewById(R.id.image);
		}

		public void init(final String sAvatar,final Drawable resDrawable, View view) {
			ivAvatar.setBackgroundDrawable(resDrawable);
			view.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (null != selectAvatarInterface) {
						selectAvatarInterface.selectAvatar(sAvatar, resDrawable);
					}
				}
			});
		}
	}

	public interface SelectAvatarInterface {
		public void selectAvatar(String sAvatar, Drawable resId);
	}

}