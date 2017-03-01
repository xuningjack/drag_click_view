package com.example.android_pra;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android_pra.ui.NoScrollGridView;
import com.example.android_pra.ui.SubscribeDragGridView;
import com.example.android_pra.ui.adapter.SubScribeDragViewAdapter;
import com.example.android_pra.ui.adapter.UnSubScribeAdapter;
import com.example.android_pra.ui.bean.SubScribeBean;


/**
 * 主界面
 * @author Jack
 */
public class MainActivity extends Activity implements OnItemClickListener{

	private TextView mEmpty;
	
	/**已订阅View*/
	private SubscribeDragGridView mSubscribeGridView;
	/**未订阅View*/
	private NoScrollGridView mUnSubScribeGridView;
	/**已订阅的adapter*/
	private SubScribeDragViewAdapter mAdapter;
	/**未订阅的adpater*/
	private UnSubScribeAdapter mUnSubScribeAdapter;
	
	/**已订阅频道列表*/
	private ArrayList<SubScribeBean> mSubScribeList = new ArrayList<SubScribeBean>();
	/**未订阅频道列表*/
	private ArrayList<SubScribeBean> mUnSubScribeList = new ArrayList<SubScribeBean>();
	/**所有的频道列表*/
	private ArrayList<SubScribeBean> mAllSubScribeList = new ArrayList<SubScribeBean>();
	
	/**item是否移动*/
	private boolean mIsMove = false;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findView();
		initViewAndDatas();
	}
	
	/**
	 * 刷新头部分类的adapter
	 */
	private void notifyPopDataSetChanged() {
		if(mAdapter != null){
			mAdapter.haveChanged = false;
			mAdapter.notifyDataSetChanged();
		}
		if(mUnSubScribeAdapter != null){
			mUnSubScribeAdapter.haveChanged = false;
			mUnSubScribeAdapter.notifyDataSetChanged();
		}
	}
	
	
	private void findView(){
		mEmpty = (TextView)findViewById(R.id.subscribe_empty_textview);
		mSubscribeGridView = (SubscribeDragGridView)findViewById(R.id.subcribe_gridview);
		mUnSubScribeGridView = (NoScrollGridView)findViewById(R.id.unsubscribe_gridview);
		mSubscribeGridView.setOnItemClickListener(this);
		mUnSubScribeGridView.setOnItemClickListener(this);
	}
	
	
	private void initViewAndDatas() {
		SubScribeBean bean1 = new SubScribeBean("1", "car", false);
		SubScribeBean bean2 = new SubScribeBean("2", "train", false);
		SubScribeBean bean3 = new SubScribeBean("3", "boat", true);
		mAllSubScribeList.add(bean1);
		mAllSubScribeList.add(bean2);
		mAllSubScribeList.add(bean3);
		
		mSubScribeList.addAll(mAllSubScribeList);
		/*mSubScribeList.add(bean1);
		mSubScribeList.add(bean2);
		mUnSubScribeList.add(bean3);*/
		
		mAdapter = new SubScribeDragViewAdapter(MainActivity.this, mSubScribeList);
		mSubscribeGridView.setAdapter(mAdapter);

		mUnSubScribeAdapter = new UnSubScribeAdapter(MainActivity.this, mUnSubScribeList);
		mUnSubScribeGridView.setAdapter(mUnSubScribeAdapter);
	}
	
	
	/**
	 * 获得一个ImageView
	 * @param view
	 * @return
	 */
	private ImageView getImageView(View view) {
		view.destroyDrawingCache();
		view.setDrawingCacheEnabled(true);
		Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
		view.setDrawingCacheEnabled(false);
		ImageView iv = new ImageView(MainActivity.this);
		iv.setImageBitmap(cache);
		return iv;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
		if (mIsMove) {
			return;
		}
		mEmpty.setVisibility(View.GONE);
		switch (parent.getId()) {
			case R.id.subcribe_gridview:
				final ImageView moveImageView = getImageView(view);
				if (moveImageView != null) {
					TextView newTextView = (TextView) view.findViewById(R.id.text_item);
					final int[] startLocation = new int[2];
					newTextView.getLocationInWindow(startLocation);
					final SubScribeBean subscribeBean = ((SubScribeDragViewAdapter) parent.getAdapter()).getItem(position);		// 获取点击的频道内容
					mUnSubScribeAdapter.setVisible(false);
					mUnSubScribeAdapter.addItem(subscribeBean);
					new Handler().postDelayed(new Runnable() {
						public void run() {
							try {
								int[] endLocation = new int[2];
								mUnSubScribeGridView.getChildAt(mUnSubScribeGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
								moveAnim(moveImageView, startLocation, endLocation, subscribeBean, mSubscribeGridView);
								mAdapter.setRemove(position);
							} catch (Exception localException) {
							}
						}
					}, 50L);
				}
				break;
			case R.id.unsubscribe_gridview:
				final ImageView moveImageViews = getImageView(view);
				if (moveImageViews != null) {
					TextView newTextView = (TextView) view.findViewById(R.id.text_item);
					final int[] startLocation = new int[2];
					newTextView.getLocationInWindow(startLocation);
					final SubScribeBean subscribeBean = ((UnSubScribeAdapter) parent.getAdapter()).getItem(position);
					mAdapter.setVisible(false);
					// 添加到最后一个
					mAdapter.addItem(subscribeBean);
					new Handler().postDelayed(new Runnable() {
						public void run() {
							try {
								int[] endLocation = new int[2];   // 获取终点的坐标
								mSubscribeGridView.getChildAt(mSubscribeGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
								moveAnim(moveImageViews, startLocation, endLocation, subscribeBean, mUnSubScribeGridView);
								mUnSubScribeAdapter.setRemove(position);
							} catch (Exception localException) {
							}
						}
					}, 50L);
				}
				break;
			default:
				break;
		}
	}
	
	/**
	 * 清空数据源
	 */
	public void resetListDatas() {
		mSubScribeList.clear();
		mAllSubScribeList.clear();
		mUnSubScribeList.clear();
	}

	/**
	 * 获得移动的布局
	 * @return
	 */
	private ViewGroup getMoveViewGroup() {
		ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
		LinearLayout moveLinearLayout = new LinearLayout(MainActivity.this);
		moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		moveViewGroup.addView(moveLinearLayout);
		return moveLinearLayout;
	}
	
	/**
	 * 获得移动的View
	 * @param viewGroup
	 * @param view
	 * @param initLocation
	 * @return
	 */
	private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
		int x = initLocation[0];
		int y = initLocation[1];
		viewGroup.addView(view);
		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		mLayoutParams.leftMargin = x;
		mLayoutParams.topMargin = y;
		view.setLayoutParams(mLayoutParams);
		return view;
	}
	
	
	/**
	 * 移动item增加动画效果
	 * @param moveView
	 * @param startLocation
	 * @param endLocation
	 * @param moveSub
	 * @param clickGridView
	 */
	private void moveAnim(View view, int[] startLocation, int[] endLocation, final SubScribeBean moveSub, final GridView clickGridView) {
		int[] initLocation = new int[2];
		view.getLocationInWindow(initLocation);
		final ViewGroup moveViewGroup = getMoveViewGroup();
		final View moveView = getMoveView(moveViewGroup, view, initLocation);
		TranslateAnimation moveAnimation = new TranslateAnimation(startLocation[0], endLocation[0], startLocation[1], endLocation[1]);
		moveAnimation.setDuration(300L);	// 动画时间
		AnimationSet moveAnimationSet = new AnimationSet(true);
		moveAnimationSet.setFillAfter(false);
		moveAnimationSet.addAnimation(moveAnimation);
		moveView.startAnimation(moveAnimationSet);
		moveAnimationSet.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				mIsMove = true;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				moveViewGroup.removeView(moveView);
				if (clickGridView instanceof SubscribeDragGridView) {
					mUnSubScribeAdapter.setVisible(true);
					mUnSubScribeAdapter.notifyDataSetChanged();
					mAdapter.remove();
				} else {
					mAdapter.setVisible(true);
					mAdapter.notifyDataSetChanged();
					mUnSubScribeAdapter.removeItem();
				}
				mIsMove = false;
			}
		});
	}
	
	
	/**
	 * 重新设置item的位置
	 * @param subScribeList
	 */
	private void reSetPopDatas(ArrayList<SubScribeBean> subScribeList) {
		resetListDatas();
		mSubScribeList.addAll(subScribeList);
		mAllSubScribeList.removeAll(mSubScribeList);
		mUnSubScribeList.addAll(mAllSubScribeList);
		notifyPopDataSetChanged();
	}
}