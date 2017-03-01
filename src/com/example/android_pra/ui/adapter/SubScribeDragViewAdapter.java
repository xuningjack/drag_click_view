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
 * 支持可拖动item的GridView的已订阅的adapter
 * @author Jack
 */
public class SubScribeDragViewAdapter extends BaseAdapter {

	/** 是否显示底部的ITEM */
	private boolean isItemShow = false;
	private Context context;
	/** 控制的postion */
	private int holdPosition;
	/** 是否改变 */
	private boolean isChanged = false;
	/** 是否可见 */
	boolean isVisible = true;
	/** 可以拖动的列表（即用户选择的频道列表）*/
	public List<SubScribeBean> subscribeList;
	/** TextView 频道内容 */
	private TextView item_text;
	/** 要删除的position */
	public int mRemovePosition = -1;
	public boolean haveChanged = false;
	public SubScribeBean preSubScribeBean;

	
	
	public SubScribeDragViewAdapter(Context context, List<SubScribeBean> subscribeList) {
		this.context = context;
		this.subscribeList = subscribeList;
	}

	@Override
	public int getCount() {
		return subscribeList == null ? 0 : subscribeList.size();
	}

	@Override
	public SubScribeBean getItem(int position) {
		if (subscribeList != null && subscribeList.size() != 0) {
			return subscribeList.get(position);
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
		item_text = (TextView) view.findViewById(R.id.text_item);
		SubScribeBean subscribe = getItem(position);
		item_text.setText(subscribe.tag);
		if (isChanged && (position == holdPosition) && !isItemShow) {
			item_text.setText("");
//            item_text.setSelected(true);
			item_text.setEnabled(true);
			isChanged = false;
		}
		if (!isVisible && (position == -1 + subscribeList.size())) {
			item_text.setText("");
//            item_text.setSelected(true);
			item_text.setEnabled(true);
		}
		if (mRemovePosition == position) {
			item_text.setText("");
		}

		if (subscribe.isSelected) {
			item_text.setSelected(true);
			preSubScribeBean = subscribe;
		}
		return view;
	}

	/**
	 * 添加频道列表
	 */
	public void addItem(SubScribeBean subscribe) {
		subscribeList.add(subscribe);
		haveChanged = true;
		notifyDataSetChanged();
	}

	/**
	 * 拖动变更频道排序
	 */
	public void exchange(int dragPostion, int dropPostion) {
		holdPosition = dropPostion;
		SubScribeBean dragItem = getItem(dragPostion);
		if (dragPostion < dropPostion) {
			subscribeList.add(dropPostion + 1, dragItem);
			subscribeList.remove(dragPostion);
		} else {
			subscribeList.add(dropPostion, dragItem);
			subscribeList.remove(dragPostion + 1);
		}
		isChanged = true;
		haveChanged = true;
		notifyDataSetChanged();
	}

	/**
	 * 获取频道列表
	 */
	public List<SubScribeBean> getChannnelLst() {
		return subscribeList;
	}

	/**
	 * 设置删除的position
	 */
	public void setRemove(int position) {
		mRemovePosition = position;
		notifyDataSetChanged();
	}

	/**
	 * 删除频道列表
	 */
	public void remove() {
		subscribeList.remove(mRemovePosition);
		mRemovePosition = -1;
		haveChanged = true;
		notifyDataSetChanged();
	}

	/**
	 * 设置频道列表
	 */
	public void setListDate(List<SubScribeBean> list) {
		subscribeList = list;
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

	/**
	 * 显示放下的ITEM
	 */
	public void setShowDropItem(boolean show) {
		isItemShow = show;
	}

	public void setSelectedItem(String fid) {
		SubScribeBean temp = new SubScribeBean();
		temp.fid = fid;
		if (subscribeList != null) {
			int i = subscribeList.indexOf(temp);
			if (i != -1) {
				SubScribeBean sub = subscribeList.get(i);
				sub.isSelected = true;
				if (preSubScribeBean != null && preSubScribeBean != sub) {
					preSubScribeBean.isSelected = false;
				}
			}
		}
	}
}