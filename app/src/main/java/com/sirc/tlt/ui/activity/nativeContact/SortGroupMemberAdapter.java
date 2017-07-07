package com.sirc.tlt.ui.activity.nativeContact;

import java.io.InputStream;
import java.util.List;


import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sirc.tlt.R;
import com.sirc.tlt.feiyucloud.InCallActivity;
import com.sirc.tlt.ui.activity.LoginActivity;
import com.sirc.tlt.utils.CommonUtil;

public class SortGroupMemberAdapter extends BaseAdapter implements SectionIndexer {
	private List<ContactBean> list = null;
	private Context mContext;
	private Activity activity;

	public SortGroupMemberAdapter(Context mContext, List<ContactBean> list,Activity activity) {
		this.mContext = mContext;
		this.list = list;
		this.activity = activity;
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<ContactBean> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final ContactBean mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.activity_nativecontact_member_item, null);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.name);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
			viewHolder.tvPhone = (TextView) view.findViewById(R.id.number);
			viewHolder.qcb = (ImageView) view.findViewById(R.id.qcb);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);

		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getSortLetters());
		} else {
			viewHolder.tvLetter.setVisibility(View.GONE);
		}
		
		viewHolder.tvTitle.setText(this.list.get(position).getDesplayName());
		viewHolder.tvPhone.setText(this.list.get(position).getPhoneNum());
		if (0 == this.list.get(position).getPhotoId()) {
			Glide.with(mContext).load(R.drawable.img_logo).into(viewHolder.qcb);
//			viewHolder.qcb.setImageResource(R.drawable.img_logo);
		} else {
			Uri uri = ContentUris.withAppendedId(
					ContactsContract.Contacts.CONTENT_URI,
					this.list.get(position).getContactId());
			InputStream input = ContactsContract.Contacts
					.openContactPhotoInputStream(mContext
							.getContentResolver(), uri);
			Bitmap contactPhoto = BitmapFactory.decodeStream(input);
//			Glide.with(mContext).load(input).into(viewHolder.qcb);
			viewHolder.qcb.setImageBitmap(contactPhoto);
		}

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CommonUtil.getIsLogin(activity)){
					int callType = 1;
					Intent intent = new Intent();
					intent.putExtra("Flag_Incoming", false);
					intent.putExtra("CallType", callType);
					intent.putExtra("CallNumber", list.get(position)
							.getPhoneNum());
					intent.setClass(activity,InCallActivity.class);
					activity.startActivity(intent);
					activity.finish();
					Log.i("拨打list号码",position+"");
				}else {
					Intent intent = new Intent(activity,
							LoginActivity.class);
					activity.startActivity(intent);
				}
			}
		});

		return view;

	}

	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
		TextView tvPhone;
		ImageView qcb;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}