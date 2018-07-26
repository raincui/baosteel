package com.android.baosteel.lan.baseui.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baosight.lan.R;


/**
 * 下拉刷新容器，后续会添加顶部这种样式
 */
@TargetApi(Build.VERSION_CODES.DONUT)
public class LJRefreshLayout extends SwipeRefreshLayout implements
        OnScrollListener {

    private OnViewRefreshListener mListener;
    private OnListViewScollListener mOnListViewScollListener;
    private OnListViewScollStateChanged mOnListViewScollStateChanged;
    /**
     * 滑动到最下面时的上拉操作
     */
    private int mTouchSlop;
    /**
     * listview实例
     */
    private LJRefreshListView mLJRefreshListView;

    /**
     * ListView的加载中footer
     */
    private View mFootContainerView;
    /**
     * ListView的已加载所有footer
     */
    private View mFootLoadEnd;
    private TextView mFootLoadEndText;


    /**
     * 按下时的y坐标
     */
    private int mYDown;
    /**
     * 抬起时的y坐标, 与mYDown一起用于滑动到底部时判断是上拉还是下拉
     */
    private int mLastY;
    /**
     * 是否在加载中 ( 上拉加载更多 )
     */
    private boolean mIsLoading = false;
    /**
     * 是否可以加载更多
     */
    private boolean mHasMore = false;

    // 上一次触摸时的X坐标
    private float mPrevX;

    public LJRefreshLayout(Context context) {
        super(context, null);
    }

    public LJRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mFootContainerView = (LinearLayout) LayoutInflater.from(context)
                .inflate(R.layout.view_refresh_bottom, null);
        mFootLoadEnd = (LinearLayout) LayoutInflater.from(context)
                .inflate(R.layout.view_refresh_bottom_end, null);
        mFootLoadEndText = (TextView) mFootLoadEnd.findViewById(R.id.view_load_end_text);
        // setColorSchemeColors(R.color.black, R.color.blue_pressed,
        // R.color.red,
        // R.color.goods_title);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 初始化ListView对象
        if (mLJRefreshListView == null) {
            if (getChildCount() > 0) {
                for (int i = 0; i < getChildCount(); i++) {
                    if (getChildAt(i) instanceof LJRefreshListView) {
                        mLJRefreshListView = (LJRefreshListView) getChildAt(i);
                        mLJRefreshListView.setOnScrollListener(this);
                        break;
                    }
                }
            }
        }
    }

    /*
     * 手动调用刷新事件，开始刷新和关闭刷新
     *
     * @see android.support.v4.widget.SwipeRefreshLayout#setRefreshing(boolean)
     */
    @Override
    public void setRefreshing(boolean refresh) {
        super.setRefreshing(refresh);
        if (refresh) {
            mIsLoading = true;
            if (mListener != null) {
                mListener.onRefresh(false);
            }
        } else {
            mIsLoading = false;
        }
    }

    /**
     * 设置加载监听
     *
     * @param listener
     */
    public void setOnRefreshListener(OnViewRefreshListener listener) {
        mListener = listener;
        setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (mListener != null) {
                    mListener.onRefresh(false);
                }
            }
        });
    }

    /**
     * 若有listview滚动时，可设置滚动监听
     *
     * @param listener
     */
    public void setOnListViewScollListener(OnListViewScollListener listener) {
        mOnListViewScollListener = listener;
    }

    /**
     * listview 滑动状态监听
     *
     * @param listener
     */
    public void setOnListViewScollStateChanged(OnListViewScollStateChanged listener) {
        mOnListViewScollStateChanged = listener;
    }

    /*
     * 为了对基础控件进行分装，外部不允许直接调用此方法，请使用{@link #setOnViewRefreshListener}
     *
     * @see
     * android.support.v4.widget.SwipeRefreshLayout#setOnRefreshListener(android
     * .support.v4.widget.SwipeRefreshLayout.OnRefreshListener)
     */
    @Override
    @Deprecated
    public void setOnRefreshListener(OnRefreshListener listener) {
        super.setOnRefreshListener(listener);
    }

    /**
     * 设置是否可以上拉加载更多
     *
     * @param enable
     */
    public void setLoadingMoreEnabled(boolean enable) {
        if (mLJRefreshListView != null) {
            mHasMore = enable;
            mLJRefreshListView.removeFooterView(mFootContainerView);
            mLJRefreshListView.removeFooterView(mFootLoadEnd);
        }
    }

    /**
     * 设置是否可以上拉加载更多
     *
     * @param enable
     */
    public void setLoadingMoreEnabled(boolean enable, String loadFinishMsg) {
        if (mLJRefreshListView != null) {
            mHasMore = enable;
            mLJRefreshListView.removeFooterView(mFootContainerView);
            mLJRefreshListView.removeFooterView(mFootLoadEnd);
            if (!mHasMore && !TextUtils.isEmpty(loadFinishMsg)) {
                mLJRefreshListView.addFooterView(mFootLoadEnd);
                mFootLoadEndText.setText(loadFinishMsg);
            }
        }
    }

    public interface OnViewRefreshListener {
        void onRefresh(boolean isLoadingMore);
    }

    public interface OnListViewScollListener {
        void onScroll(AbsListView view, int firstVisibleItem,
                      int visibleItemCount, int totalItemCount);
    }

    public interface OnListViewScollStateChanged {
        void onScrollStateChanged(AbsListView view, int scrollState);
    }

    /*
     * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mYDown = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                mLastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                if (canLoadMore()) {
                    loadMore();
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 是否可以加载更多, 条件是到了最底部, listview不在加载中, 且为上拉操作.
     *
     * @return
     */
    private boolean canLoadMore() {
        return isBottom() && !mIsLoading && isPullUp() && mHasMore;
    }

    /**
     * 判断是否到了最底部
     */
    private boolean isBottom() {
        if (mLJRefreshListView != null && mLJRefreshListView.getAdapter() != null) {
            return mLJRefreshListView.getLastVisiblePosition() == (mLJRefreshListView
                    .getAdapter().getCount() - 1);
        }
        return false;
    }

    /**
     * 是否是上拉操作
     *
     * @return
     */
    private boolean isPullUp() {
        return (mYDown - mLastY) >= mTouchSlop;
    }

    /**
     * 如果到了最底部,而且是上拉操作.那么执行onLoad方法
     */
    private void loadMore() {
        if (mListener != null) {
            // 设置状态
            setLoading(true);
            mListener.onRefresh(true);
        }
    }

    /**
     * @param loading
     */
    private void setLoading(boolean loading) {
        mIsLoading = loading;
        if (mIsLoading) {
            mLJRefreshListView.removeFooterView(mFootContainerView);
            mLJRefreshListView.addFooterView(mFootContainerView);
        } else {
            mYDown = 0;
            mLastY = 0;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mOnListViewScollStateChanged != null) {
            mOnListViewScollStateChanged.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (mOnListViewScollListener != null) {
            mOnListViewScollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
        // 滚动时到了最底部也可以加载更多
        if (canLoadMore()) {
            loadMore();
        }
    }

    @Override
    public void setVisibility(int visibility) {
        if (mLJRefreshListView != null) {
            mLJRefreshListView.setVisibility(visibility);
        }
        super.setVisibility(visibility);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPrevX = event.getX();
                break;

            case MotionEvent.ACTION_MOVE:
                final float eventX = event.getX();
                float xDiff = Math.abs(eventX - mPrevX);
                // Log.d("refresh" ,"move----" + eventX + "   " + mPrevX + "   " + mTouchSlop);
                // 增加60的容差，让下拉刷新在竖直滑动时就可以触发
                if (xDiff > mTouchSlop + 60) {
                    return false;
                }
        }

        return super.onInterceptTouchEvent(event);
    }
}
