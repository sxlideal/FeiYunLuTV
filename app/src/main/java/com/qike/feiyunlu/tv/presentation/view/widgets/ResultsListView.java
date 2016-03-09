package com.qike.feiyunlu.tv.presentation.view.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qike.feiyunlu.tv.R;

import java.lang.reflect.Field;



/**
 * <p>
 * 列表UI公共组件
 * </p>
 * <br/>
 * <p>
 * 实现下拉刷新上拉更多
 * </p>
 * 
 * @since 2.6
 * @author sunxh
 */
public class ResultsListView extends ListView implements OnScrollListener {
	private final static int RELEASE_To_REFRESH = 0;
	private final static int PULL_To_REFRESH = 1;
	// 正在刷新
	private final static int REFRESHING = 2;
	// 刷新完成
	private final static int DONE = 3;
	private final static int LOADING = 4;
	// 处理反复触发
	private Boolean mRepeate = true;
	/*
	 * 比率, 比例 这个值是个很有用的值，要设定一个合适的值 现在设置为3,是为了： 1.当手指在屏幕上拉动的距离是headView的3倍的时候才会刷新
	 * 2.每当手指滑动三个单位的距离，headView刷新一次 如果是1.那么整个listview会被拉到屏幕的最底部，效果不好
	 */
	private final static int RATIO = 3;
	/*** 显示底部加载 */
	public static final int FOOTER_SHOW = 0;
	/*** 显示底部无更多文案 */
	public static final int FOOTER_NOT_SHOW = 1;
	/*** 不显示底部加载栏 */
	public static final int FOOTER_NOT_DATA = 2;
	/*** 添加按钮 */
	public static final int FOOTER_BUTTON = 3;
	private LayoutInflater mInflater;
	private LinearLayout mHeadViewLinearLayout;// headerView
	private TextView mResultListViewMoreTextView;
	private TextView tipsTextview;// 提示文字
	private ImageView mArrowImageView;// 箭头
	private ProgressBar mProgressBar;// 菊花
	private String mTipsString = "加载中...";
	private RotateAnimation mAnimation;// 逆时针
	private RotateAnimation mReverseAnimation;// 顺时针
	private boolean mIsRecored;
	// private int mHeadContentWidth;// head宽
	private int mHeadContentHeight;// head高，这个属性比较重要
	private int mStartY;
	private int mFirstItemIndex;
	private int mState;
	private boolean mIsBack;
	private OnRefreshListener mRefreshListener;
	private boolean mIsRefreshable;// 是否可下拉刷新，只有设置了下拉刷新listener之后才可以下来
	private boolean mIsRefreshableNohead = true;// 如果删除headview就将此标志更改为false，取消onTouch事件，否则轻轻滑动会使列表回滚到第一行
	private int mScrollState;
	private BaseAdapter mAdapter;
	private Context mContext;
	private int pageLastIndex;
	private int when2LoadNext;
	private final int pagesize = 12;
	private boolean isUp;
	private View mView;
	private View mLayout;
	private TextView mText;
	private Button mItemClick;
	private RelativeLayout mItemLoadingLayout;
	private OnScrollListener onScrollListener;

	public ResultsListView(Context context) {
		super(context);
		init(context);
	}

	public ResultsListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		// super.setOnScrollListener(l);
		this.onScrollListener = l;
	}

	private void init(Context context) {
		this.mContext = context;
		setCacheColorHint(context.getResources().getColor(R.color.transparent));
		mInflater = LayoutInflater.from(context);
		mHeadViewLinearLayout = (LinearLayout) mInflater.inflate(
				R.layout.mzw_resultslistview_head, null);
		mArrowImageView = (ImageView) mHeadViewLinearLayout
				.findViewById(R.id.head_arrowImageView);
		mArrowImageView.setMinimumWidth(70);
		mArrowImageView.setMinimumHeight(50);
		mProgressBar = (ProgressBar) mHeadViewLinearLayout
				.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) mHeadViewLinearLayout
				.findViewById(R.id.head_tipsTextView);
		measureView(mHeadViewLinearLayout);// 确定headView的宽高,如果不调用这个方法，之后得到的宽高值都为0
		mHeadContentHeight = mHeadViewLinearLayout.getMeasuredHeight();
		mHeadViewLinearLayout.setPadding(0, -1 * mHeadContentHeight, 0, 0);// 设置paddingTop为负数(-head_height)，这个是把head放到看不见的地方的代码
		mHeadViewLinearLayout.invalidate();
		addHeaderView(mHeadViewLinearLayout);// 和上边方法区别的，这个添加分割线
		super.setOnScrollListener(this);// 设置滚动监听
		// 逆时针动画
		mAnimation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mAnimation.setInterpolator(new LinearInterpolator());
		mAnimation.setDuration(200);
		mAnimation.setFillAfter(true);// 设置在动画结束之后图片就呈现的是动画结束之后的样子
		mReverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mReverseAnimation.setInterpolator(new LinearInterpolator());
		mReverseAnimation.setDuration(200);
		mReverseAnimation.setFillAfter(true);
		mState = LOADING;// 初始化state
		mIsRefreshable = false;// 默认不可下拉刷新

//		setDividerHeight(2);
	}

	public GetMoreDataListener mGetMoreDataListener;

	public void setmGetMoreDataListener(GetMoreDataListener mGetMoreDataListener) {
		this.mGetMoreDataListener = mGetMoreDataListener;
	}

	/**
	 * 
	 * 底部更多回调接口
	 * 
	 * @author sunxh
	 * 
	 */
	public interface GetMoreDataListener {
		void getMore();
	}

	public void daoClear() {
		// lastIndex = 0;
		pageLastIndex = 0;
		isUp = true;
		when2LoadNext = 0;
	}

	/**
	 * 记录滑动状态
	 */
	public void onScroll(AbsListView view, int firstVisiableItem,
			int visibleItemCount, int totalItemCount) {
		if (onScrollListener != null) {
			onScrollListener.onScroll(view, firstVisiableItem,
					visibleItemCount, totalItemCount);
		}
		mFirstItemIndex = firstVisiableItem;
		if (mAdapter != null) {
			// lastIndex = mAdapter.getCount();//总数据的�?���?��数据的索�?
			// 判断滑动动作
			if (pageLastIndex < firstVisiableItem + visibleItemCount) {
				pageLastIndex = firstVisiableItem + visibleItemCount;// 页面上显示的�?���?��数据的索�?
				isUp = true;
			} else {
				isUp = false;
			}
			if (!(when2LoadNext < pagesize) || !(when2LoadNext > 0)) {
				when2LoadNext = pagesize / 2;
			}
			if ((totalItemCount - (firstVisiableItem + visibleItemCount)) != 0) {
				if ((totalItemCount - (firstVisiableItem + visibleItemCount)) < when2LoadNext) {
					if (isUp) {
						if (!isFastScrollEnabled()) {

						//	setFastScrollEnabled(true);
						//	speedinessGlide(this);
						}

						if (mRefreshListener != null) {
							mRefreshListener.onUpload();
						}
					}
				}
			}
		}

	}

	/**
	 * <p>
	 * 底部加载栏控制
	 * </p>
	 * <br/>
	 * 
	 * @since 4.2
	 * @author sunxh
	 * @param option
	 */
	public void setFooterView(int option) {

		switch (option) {
		
		// 显示底部加载
		case FOOTER_SHOW:
			mLayout.setVisibility(View.VISIBLE);
			mText.setVisibility(View.GONE);
			mItemClick.setVisibility(View.GONE);
			Log.i("setFooterView", "setFooterView///"+"FOOTER_SHOW");
			break;
		// 显示底部无更多文�?
		case FOOTER_NOT_SHOW:
			mLayout.setVisibility(View.GONE);
			mText.setVisibility(View.VISIBLE);
			mItemClick.setVisibility(View.GONE);
			Log.i("setFooterView", "setFooterView///"+"FOOTER_NOT_SHOW");
			break;
		// 不显示底部加载栏
		case FOOTER_NOT_DATA:
			mLayout.setVisibility(View.GONE);
			mText.setVisibility(View.GONE);
			mItemClick.setVisibility(View.GONE);
			Log.i("setFooterView", "setFooterView///"+"FOOTER_NOT_DATA");
			break;
		default:
			break;
		}
	}

	/**
	 * <p>
	 * 底部加载栏控制
	 * </p>
	 * <br/>
	 * <p>
	 * footer添加按钮
	 * </p>
	 * 
	 * @since 4.2
	 * @author sunxh
	 * @param option
	 * @param click
	 * @param text
	 */
	public void setFooterView(int option, OnClickListener click, String text) {
		switch (option) {
		case FOOTER_BUTTON:
			mLayout.setVisibility(View.GONE);
			mText.setVisibility(View.GONE);
			mItemClick.setVisibility(View.VISIBLE);
			mItemClick.setText(text);
			mItemClick.setOnClickListener(click);
			break;

		default:
			break;
		}
	}

	/**
	 * 滑动动作
	 */
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		if (onScrollListener != null) {
			onScrollListener.onScrollStateChanged(arg0, arg1);
		}

		int first = arg0.getFirstVisiblePosition();// 判断当前listview第一条item显示第几条数�?
		this.mScrollState = arg1;
		if (mScrollState == SCROLL_STATE_IDLE) {
			if (((BaseAdapter) ((HeaderViewListAdapter) getAdapter())
					.getWrappedAdapter()) != null) {
//				// //某些手机在没有数据时候仍可以滑动
//				// ((BaseAdapter) ((HeaderViewListAdapter)
//				// getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
			}
		}
		if (arg1 == 2 && first == 0) {
			// 解决部分手机下拉刷新是listview状态错误
			this.mScrollState = SCROLL_STATE_IDLE;
		}
	}

	public int getScrollState() {
		return mScrollState;
	}

	/**
	 * <p>
	 * 自定义滑块样式
	 * </p>
	 * <br/>
	 * 
	 * @since 4.2
	 * @author sunxh
	 * @param list
	 */
	private void speedinessGlide(ListView list) {
		try {
			Field f = AbsListView.class.getDeclaredField("mFastScroller");
			f.setAccessible(true);
			Object o = f.get(list);
			f = f.getType().getDeclaredField("mThumbDrawable");
			f.setAccessible(true);
			Drawable drawable = (Drawable) f.get(o);
			drawable = getResources().getDrawable(
					R.mipmap.mzw_general_scroll_icon);
			f.set(o, drawable);
		} catch (Exception e) {
			// throw new RuntimeException(e);
		}
	}

	public boolean onTouchEvent(MotionEvent event) {

		if (mIsRefreshable && mIsRefreshableNohead) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:// 按下
				if (mFirstItemIndex == 0 && !mIsRecored) {
					mIsRecored = true;
					mStartY = (int) event.getY();
				}
				break;
			case MotionEvent.ACTION_UP:
				if (mState != REFRESHING && mState != LOADING) {
					if (mState == DONE) {
					}
					if (mState == PULL_To_REFRESH) {// 拉的距离不够
						mState = DONE;
						changeHeaderViewByState();
					}
					if (mState == RELEASE_To_REFRESH) {
						mState = REFRESHING;
						changeHeaderViewByState();
						onRefresh();
					}
				}
				mIsRecored = false;
				mIsBack = false;
				break;
			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();
				if (!mIsRecored && mFirstItemIndex == 0) {
					mIsRecored = true;
					mStartY = tempY;
				}
				if (mState != REFRESHING && mIsRecored && mState != LOADING) {

					if (mState == RELEASE_To_REFRESH) {
						setSelection(0);
						if (((tempY - mStartY) / RATIO < mHeadContentHeight)
								&& (tempY - mStartY) > 0) {
							mState = PULL_To_REFRESH;
							changeHeaderViewByState();
						} else if (tempY - mStartY <= 0) {
							mState = DONE;
							changeHeaderViewByState();
						}
					}
					if (mState == PULL_To_REFRESH) {
						setSelection(0);
						if ((tempY - mStartY) / RATIO >= mHeadContentHeight) {// 滑动的距离到达临近点
							mState = RELEASE_To_REFRESH;
							mIsBack = true;
							changeHeaderViewByState();
						} else if (tempY - mStartY <= 0) {
							mState = DONE;
							changeHeaderViewByState();

						}
					}
					if (mState == DONE) {
						if (tempY - mStartY > 0) {
							mState = PULL_To_REFRESH;
							changeHeaderViewByState();

						}
					}
					// 更新headView的size
					if (mState == PULL_To_REFRESH) {
						mHeadViewLinearLayout.setPadding(0, -1
								* mHeadContentHeight + (tempY - mStartY)
								/ RATIO, 0, 0);
					}
					// 更新headView的paddingTop
					if (mState == RELEASE_To_REFRESH) {
						mHeadViewLinearLayout.setPadding(0, (tempY - mStartY)
								/ RATIO - mHeadContentHeight, 0, 0);
					}
				}
				break;
			}
		}
		try {
			return super.onTouchEvent(event);
		} catch (IllegalStateException e) {
			return true;
		} catch (IndexOutOfBoundsException e) {
			return true;
		}
	}

	/**
	 * <p>
	 * 当状态改变时候，调用该方法，以更新界面
	 * </p>
	 * <br/>
	 * 
	 * @since 4.2
	 * @author sunxh
	 */
	private void changeHeaderViewByState() {
		switch (mState) {
		case RELEASE_To_REFRESH:
			releaeeToRefresh();
			break;
		case PULL_To_REFRESH:
			pullToRefresh();
			break;
		case REFRESHING:
			refreshing();
			break;
		case DONE:
			done();
			break;
		}
	}

	/**
	 * <p>
	 * 添加监听
	 * </p>
	 * <br/>
	 * 
	 * @since 4.2
	 * @author sunxh
	 * @param refreshListener
	 */
	public void setonRefreshListener(OnRefreshListener refreshListener) {
		this.mRefreshListener = refreshListener;
	}

	/**
	 * <p>
	 * 下拉回调接口
	 * </p>
	 * <br/>
	 * 
	 * @since 4.2
	 * @author sunxh
	 */
	public interface OnRefreshListener {
		public void onRefresh();

		public void onUpload();
	}

	/**
	 * <p>
	 * 加载完成
	 * </p>
	 * <br/>
	 * 
	 * @since 4.2
	 * @author sunxh
	 */
	public void onRefreshComplete() {
		mState = DONE;
		mRepeate = true;
		changeHeaderViewByState();
	}

	/**
	 * <p>
	 * 正在刷新方法
	 * </p>
	 * <br/>
	 * 
	 * @since 4.2
	 * @author sunxh
	 */
	private void onRefresh() {
		if (mRefreshListener != null) {
			setFastScrollEnabled(false);
			mRefreshListener.onRefresh();
		}
	}

	/**
	 * <p>
	 * 只有调用了这个方法以后，header的宽高才能被计算出来
	 * </p>
	 * <br/>
	 * 
	 * @since 4.2
	 * @author sunxh
	 * @param header
	 */
	private void measureView(View header) {
		ViewGroup.LayoutParams p = header.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int childHeightSpec;
		int lpHeight = p.height;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		header.measure(childWidthSpec, childHeightSpec);
	}

	/**
	 * <p>
	 * 列表初始化
	 * </p>
	 * <br/>
	 * 
	 * @since 4.2
	 * @author sunxh
	 * @param adapter
	 * @param context
	 */
	public void setAdapter(BaseAdapter adapter, Context context) {
		mAdapter = adapter;
		mContext = context;
		mView = View.inflate(mContext, R.layout.mzw_item_loading, null);
		mLayout = mView.findViewById(R.id.itemloading);
		mItemClick = (Button) mView.findViewById(R.id.item_click);
		mItemLoadingLayout = (RelativeLayout) mView
				.findViewById(R.id.itemloadinglayout);
		mItemLoadingLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		mText = (TextView) mView.findViewById(R.id.footer);
		addFooterView(mView);
		refreshing();
		onRefreshComplete();
		super.setAdapter(adapter);
	}

	/**
	 * 
	 * <p>
	 * 设置加载中背景
	 * </p>
	 * <br/>
	 * 
	 * @since 1.0.0
	 * @author xky
	 * @param resourceId
	 */
	public void setLoadingBg(int resourceId) {
		if (mItemLoadingLayout != null) {
			mItemLoadingLayout.setBackgroundResource(resourceId);
		}
	}

	/**
	 * <p>
	 * 松开即可刷新动作
	 * </p>
	 * <br/>
	 * 
	 * @since 4.2
	 * @author sunxh
	 */
	public void releaeeToRefresh() {
		mArrowImageView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
		tipsTextview.setVisibility(View.VISIBLE);
		mArrowImageView.clearAnimation();
		mArrowImageView.startAnimation(mAnimation);
		tipsTextview.setText(mContext.getString(R.string.mzw_let_go_refresh));
	}

	/**
	 * <p>
	 * 下拉刷新动作
	 * </p>
	 * <br/>
	 * 
	 * @since 4.2
	 * @author sunxh
	 */
	public void pullToRefresh() {
		mProgressBar.setVisibility(View.GONE);
		tipsTextview.setVisibility(View.VISIBLE);
		mArrowImageView.clearAnimation();
		mArrowImageView.setVisibility(View.VISIBLE);
		if (mIsBack) {
			mIsBack = false;
			mArrowImageView.clearAnimation();
			mArrowImageView.startAnimation(mReverseAnimation);
			tipsTextview.setText(mContext.getString(R.string.mzw_pull_refresh));
		} else {
			tipsTextview.setText(mContext.getString(R.string.mzw_pull_refresh));
		}
	}

	/**
	 * <p>
	 * 加载中
	 * </p>
	 * <br/>
	 * 
	 * @since 4.2
	 * @author sunxh
	 */
	public void refreshing() {
		mIsRefreshable = false;
		mHeadViewLinearLayout.setPadding(0, 0, 0, 0);
		mProgressBar.setVisibility(View.VISIBLE);
		mArrowImageView.clearAnimation();
		mArrowImageView.setVisibility(View.GONE);
		tipsTextview.setText(mTipsString);
	}

	/**
	 * <p>
	 * 加载结束
	 * </p>
	 * <br/>
	 * 
	 * @since 4.2
	 * @author sunxh
	 */
	public void done() {
		mIsRefreshable = true;
		mHeadViewLinearLayout.setPadding(0, -1 * mHeadContentHeight, 0, 0);
		mProgressBar.setVisibility(View.GONE);
		mArrowImageView.clearAnimation();
		mArrowImageView.setImageResource(R.mipmap.refresh);
		tipsTextview.setText(mContext.getString(R.string.mzw_already_finish));
	}

	/**
	 * <p>
	 * 移除上拉刷新
	 * </p>
	 * <br/>
	 * 
	 * @since 4.2
	 * @author sunxh
	 */
	public void removeHead() {
		mIsRefreshableNohead = false;
		removeHeaderView(mHeadViewLinearLayout);
	}

	/**
	 * <p>
	 * 添加上拉刷新
	 * </p>
	 * <br/>
	 * 
	 * @since 4.2
	 * @author sunxh
	 */
	public void addHead() {
		addHeaderView(mHeadViewLinearLayout, null, false);
	}

	/**
	 * <p>
	 * 修改头文字内容
	 * </p>
	 * <br/>
	 * 
	 * @since 4.2
	 * @author sunxh
	 * @param string
	 */
	public void setTipsString(String string) {
		mTipsString = string;
	}

	/**
	 * <p>
	 * 修改顶部文字内容
	 * </p>
	 * <br/>
	 * 
	 * @since 4.2
	 * @author sunxh
	 * @param string
	 */
	public void setBootomString(String string) {
		mResultListViewMoreTextView.setText(string);
	}
	
}
