package com.example.android_pra.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android_pra.R;
import com.example.android_pra.ui.bean.SubScribeBean;


/**
 * 未订阅的adapter
 * @author Jack
 */
public class UnSubScribeAdapter extends BaseAdapter {
	
	private Context context;
	public List<SubScribeBean> unSubScribeList;
	private TextView mItemText;
	/** 是否可见 */
	boolean isVisible = true;
	/** 要删除的position */
	public int mRemovePosition = -1;
	public boolean haveChanged = false;

	public UnSubScribeAdapter(Context context, List<SubScribeBean> unSubScribeList) {
		this.context = context;
		this.unSubScribeList = unSubScribeList;
	}

	@Override
	public int getCount() {
		return unSubScribeList == null ? 0 : unSubScribeList.size();
	}

	@Override
	public SubScribeBean getItem(int position) {
		if (unSubScribeList != null && unSubScribeList.size() > 0) {
			return unSubScribeList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(R.layout.subscribe_category_item, null);
		mItemText = (TextView) view.findViewById(R.id.text_item);
		SubScribeBean subscribe = getItem(position);
		mItemText.setText(subscribe.tag);
		if (!isVisible && (position == -1 + unSubScribeList.size())) {
			mItemText.setText("");
		}
		if (mRemovePosition == position) {
			mItemText.setText("");
		}
		return view;
	}

	/**
	 * 获取频道列表
	 */
	public List<SubScribeBean> getChannnelLst() {
		return unSubScribeList;
	}

	/**
	 * 添加item
	 * @param subscribeBean
	 */
	public void addItem(SubScribeBean subscribeBean) {
		unSubScribeList.add(subscribeBean);
		haveChanged = true;
		notifyDataSetChanged();
	}

	/**
	 * 设置remove的position
	 * @param position
	 */
	public void setRemove(int position) {
		mRemovePosition = position;
		notifyDataSetChanged();
	}

	/**
	 * 删除某个item
	 */
	public void removeItem() {
		unSubScribeList.remove(mRemovePosition);
		mRemovePosition = -1;
		haveChanged = true;
		notifyDataSetChanged();
	}

	public void setListDate(List<SubScribeBean> list) {
		unSubScribeList = list;
	}

	/**
	 * 获取是否可见
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * 设置是否可见
	 */
	public void setVisible(boolean visible) {
		isVisible = visible;
	}
}