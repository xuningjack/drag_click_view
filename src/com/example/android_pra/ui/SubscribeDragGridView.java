package com.example.android_pra.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android_pra.R;
import com.example.android_pra.ui.adapter.SubScribeDragViewAdapter;


/**
 * 支持可拖动、点击item的GridView
 * @author Jack
 */
public class SubscribeDragGridView extends NoScrollGridView {
	
	/** 点击时候的X位置 */
	public int downX;
	/**点击时候的Y位置 */
	public int downY;
	/** 点击时候对应整个界面的X位置 */
	public int windowX;
	/** 点击时候对应整个界面的Y位置 */
	public int windowY;
	/** 屏幕上的X */
	private int win_view_x;
	/** 屏幕上的Y */
	private int win_view_y;
	/** 拖动的里x的距离 */
	int dragOffsetX;
	/** 拖动的里Y的距离 */
	int dragOffsetY;
	/** 长按时候对应postion */
	public int dragPosition;
	/** Up后对应的ITEM的Position */
	private int dropPosition;
	/** 开始拖动的ITEM的Position */
	private int startPosition;
	/**  item高 */
	private int itemHeight;
	/** item宽  */
	private int itemWidth;
	/** 拖动的时候对应ITEM的VIEW */
	private View dragImageView;
	/** 长按的时候ITEM的VIEW */
	private ViewGroup dragItemView;
	/** WindowManager管理器 */
	private WindowManager windowManager;
	private WindowManager.LayoutParams windowParams;
	/** item总量 */
	private int itemTotalCount;
	/** 一行的ITEM数量 */
	private int nColumns = 4;
	/** 行数 */
	private int nRows;
	/** 剩余部分  */
	private int Remainder;
	/** 是否在移动 */
	private boolean isMoving = false;
	/** */   
	private int holdPosition;
	/** 拖动的时候放大的倍数 */
	private double dragScale = 1;
	/** 每个ITEM之间的水平间距 */
	private int mHorizontalSpacing = 15;
	/** 每个ITEM之间的竖直间距 */
	private int mVerticalSpacing = 15;
	private String mLastAnimationID;

	
	public SubscribeDragGridView(Context context) {
		super(context);
		init(context);
	}

	public SubscribeDragGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public SubscribeDragGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void init(Context context) {
		//	mHorizontalSpacing = ViewUtil.dip2px(context, mHorizontalSpacing);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			downX = (int) ev.getX();
			downY = (int) ev.getY();
			windowX = (int) ev.getX();
			windowY = (int) ev.getY();
			setOnItemClickListener(ev);
		}
		return super.onInterceptTouchEvent(ev);
	}


	/**
	 * 长按点击监听
	 * @param ev
	 */
	public void setOnItemClickListener(final MotionEvent ev) {
		setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				int x = (int) ev.getX();	// 长安事件的X位置
				int y = (int) ev.getY();	// 长安事件的y位置
				Log.e("Jack", "setOnItemClickListener-startPosition=" + startPosition);
				startPosition = position;	// 第一次点击的postion
				dragPosition = position;
				if (startPosition < 0) {
					return false;
				}
				ViewGroup dragViewGroup = (ViewGroup) getChildAt(dragPosition - getFirstVisiblePosition());
				TextView dragTextView = (TextView) dragViewGroup.findViewById(R.id.text_item);
				dragTextView.setSelected(true);
				dragTextView.setEnabled(false);
				itemHeight = dragViewGroup.getHeight();
				itemWidth = dragViewGroup.getWidth();
				itemTotalCount = SubscribeDragGridView.this.getCount();
				int row = itemTotalCount / nColumns;	// 算出行数
				Remainder = (itemTotalCount % nColumns);	// 算出最后一行多余的数量
				if (Remainder != 0) {
					nRows = row + 1;
				} else {
					nRows = row;
				}
				// 如果特殊的这个不等于拖动的那个,并且不等于-1
				if (dragPosition != AdapterView.INVALID_POSITION) {
					// 释放的资源使用的绘图缓存。如果你调用buildDrawingCache()手动没有调用setDrawingCacheEnabled(真正的),你应该清理缓存使用这种方法。
					win_view_x = windowX - dragViewGroup.getLeft();		//VIEW相对自己的X，半斤
					win_view_y = windowY - dragViewGroup.getTop();		//VIEW相对自己的y，半斤
					dragOffsetX = (int) (ev.getRawX() - x);		//手指在屏幕的上X位置-手指在控件中的位置就是距离最左边的距离
					dragOffsetY = (int) (ev.getRawY() - y);		//手指在屏幕的上y位置-手指在控件中的位置就是距离最上边的距离
					dragItemView = dragViewGroup;
					dragViewGroup.destroyDrawingCache();
					dragViewGroup.setDrawingCacheEnabled(true);
					Bitmap dragBitmap = Bitmap.createBitmap(dragViewGroup.getDrawingCache());
					startDrag(dragBitmap, (int) ev.getRawX(), (int) ev.getRawY());
					hideDropItem();
					dragViewGroup.setVisibility(View.INVISIBLE);
					isMoving = false;
					requestDisallowInterceptTouchEvent(true);
					return true;
				}
				return false;
			}
		});
	}

	public void startDrag(Bitmap dragBitmap, int x, int y) {
		stopDrag();
		windowParams = new WindowManager.LayoutParams();		// 获取WINDOW界面的
		windowParams.gravity = Gravity.TOP | Gravity.LEFT;
		//得到preview左上角相对于屏幕的坐标
		windowParams.x = x - win_view_x;
		windowParams.y = y - win_view_y;
		//设置拖拽item的宽和高
		windowParams.width = (int) (dragScale * dragBitmap.getWidth());		// 放大dragScale倍，可以设置拖动后的倍数
		windowParams.height = (int) (dragScale * dragBitmap.getHeight());		// 放大dragScale倍，可以设置拖动后的倍数
		this.windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		this.windowParams.format = PixelFormat.TRANSLUCENT;
		this.windowParams.windowAnimations = 0;
		ImageView iv = new ImageView(getContext());
		iv.setImageBitmap(dragBitmap);
		windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);	// "window"
		windowManager.addView(iv, windowParams);
		dragImageView = iv;
	}

	private void stopDrag() {
		if (dragImageView != null) {
			windowManager.removeView(dragImageView);
			dragImageView = null;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		boolean bool = true;
		if (dragImageView != null && dragPosition != AdapterView.INVALID_POSITION) {
			bool = super.onTouchEvent(ev);
			int x = (int) ev.getX();
			int y = (int) ev.getY();
			switch (ev.getAction()) {
				case MotionEvent.ACTION_DOWN:
					downX = (int) ev.getX();
					windowX = (int) ev.getX();
					downY = (int) ev.getY();
					windowY = (int) ev.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					onDrag(x, y, (int) ev.getRawX(), (int) ev.getRawY());
					if (!isMoving) {
						OnMove(x, y);
					}
					if (pointToPosition(x, y) != AdapterView.INVALID_POSITION) {
						break;
					}
					break;
				case MotionEvent.ACTION_UP:
					stopDrag();
					onDrop(x, y);
					requestDisallowInterceptTouchEvent(false);
					break;
				default:
					break;
			}
		}
		return super.onTouchEvent(ev);
	}

	private void onDrop(int x, int y) {
		int tempPostion = pointToPosition(x, y);
		dropPosition = tempPostion;
		SubScribeDragViewAdapter mDragAdapter = (SubScribeDragViewAdapter) getAdapter();
		mDragAdapter.setShowDropItem(true);   //显示刚拖动的ITEM
		mDragAdapter.notifyDataSetChanged();   //刷新适配器，让对应的ITEM显示
	}

	private void onDrag(int x, int y, int rawx, int rawy) {
		if (dragImageView != null) {
			windowParams.alpha = 0.6f;
			windowParams.x = rawx - win_view_x;
			windowParams.y = rawy - win_view_y;
			windowManager.updateViewLayout(dragImageView, windowParams);
		}
	}

	public void OnMove(int x, int y) {
		int dPosition = pointToPosition(x, y);   // 拖动的VIEW下方的POSTION
		if (dPosition >= 0) {   // 判断下方的POSTION是否是最开始2个不能拖动的
			if ((dPosition == -1) || (dPosition == dragPosition)) {
				return;
			}
			dropPosition = dPosition;
			if (dragPosition != startPosition) {
				dragPosition = startPosition;
			}
			int movecount;
			if ((dragPosition == startPosition) || (dragPosition != dropPosition)) {  //拖动的=开始拖的，并且 拖动的 不等于放下的
				movecount = dropPosition - dragPosition;   //移需要移动的动ITEM数量
			} else {
				movecount = 0;  //移需要移动的动ITEM数量为0
			}
			if (movecount == 0) {
				return;
			}

			int movecount_abs = Math.abs(movecount);

			if (dPosition != dragPosition) {		//dragGroup设置为不可见
				ViewGroup dragGroup = (ViewGroup) getChildAt(dragPosition);
				dragGroup.setVisibility(View.INVISIBLE);
				float to_x = 1;	// 当前下方positon
				float to_y;			// 当前下方右边positon
				//x_vlaue移动的距离百分比（相对于自己长度的百分比）
				float x_vlaue = ((float) mHorizontalSpacing / (float) itemWidth) + 1.0f;
				//y_vlaue移动的距离百分比（相对于自己宽度的百分比）
				float y_vlaue = ((float) mVerticalSpacing / (float) itemHeight) + 1.0f;
				for (int i = 0; i < movecount_abs; i++) {
					to_x = x_vlaue;
					to_y = y_vlaue;
					if (movecount > 0) {  //像左
						holdPosition = dragPosition + i + 1;   // 判断是不是同一行的
						if (dragPosition / nColumns == holdPosition / nColumns) {
							to_x = -x_vlaue;
							to_y = 0;
						} else if (holdPosition % 4 == 0) {
							to_x = 3 * x_vlaue;
							to_y = -y_vlaue;
						} else {
							to_x = -x_vlaue;
							to_y = 0;
						}
					} else {  //向右,下移到上，右移到左
						holdPosition = dragPosition - i - 1;
						if (dragPosition / nColumns == holdPosition / nColumns) {
							to_x = x_vlaue;
							to_y = 0;
						} else if ((holdPosition + 1) % 4 == 0) {
							to_x = -3 * x_vlaue;
							to_y = y_vlaue;
						} else {
							to_x = x_vlaue;
							to_y = 0;
						}
					}
					ViewGroup moveViewGroup = (ViewGroup) getChildAt(holdPosition);
					Animation moveAnimation = getMoveAnimation(to_x, to_y);
					moveViewGroup.startAnimation(moveAnimation);
					if (holdPosition == dropPosition) {
						mLastAnimationID = moveAnimation.toString();
					}
					moveAnimation.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
							isMoving = true;
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}

						@Override
						public void onAnimationEnd(Animation animation) {  // 如果为最后个动画结束，那执行下面的方法
							if (animation.toString().equalsIgnoreCase(mLastAnimationID)) {
								SubScribeDragViewAdapter mDragAdapter = (SubScribeDragViewAdapter) getAdapter();
								mDragAdapter.exchange(startPosition, dropPosition);
								startPosition = dropPosition;
								dragPosition = dropPosition;
								isMoving = false;
							}
						}
					});
				}
			}
		}
	}

	/**
	 * 获取移动动画
	 */
	public Animation getMoveAnimation(float toXValue, float toYValue) {
		TranslateAnimation mTranslateAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0F,
				Animation.RELATIVE_TO_SELF, toXValue,
				Animation.RELATIVE_TO_SELF, 0.0F,
				Animation.RELATIVE_TO_SELF, toYValue);		// 当前位置移动到指定位置
		mTranslateAnimation.setFillAfter(true);		// 设置一个动画效果执行完毕后，View对象保留在终止的位置。
		mTranslateAnimation.setDuration(300L);
		return mTranslateAnimation;
	}

	/**
	 * 隐藏 放下 的ITEM
	 */
	private void hideDropItem() {
		((SubScribeDragViewAdapter) getAdapter()).setShowDropItem(false);
	}
}