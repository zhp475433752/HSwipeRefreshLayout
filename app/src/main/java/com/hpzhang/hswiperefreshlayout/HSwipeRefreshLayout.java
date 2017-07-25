package com.hpzhang.hswiperefreshlayout;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by hpzhang on 2017/6/15.
 * 刷新和加载更多
 *
 */

public class HSwipeRefreshLayout extends SwipeRefreshLayout {

    private Context mContext;
    /**
     * ListView 底部加载更多视图
     */
    private View mViewFooter;
    /**
     * ListView
     */
    private ListView mListView;
    /**
     * 刷新监听 必须设置
     */
    private OnLoadMoreListener onLoadMoreListener;
    /**
     * 按下时Y坐标
     */
    private float mDownY;
    /**
     * 抬起时Y坐标
     */
    private float mUpY;
    /**
     * 上拉加载更多出现的滑动距离
     */
    private float mScaledTouchSlop = 100;
    /**
     * 是否正在加载数据...
     */
    private  boolean isLoading;

    public HSwipeRefreshLayout(Context context) {
        super(context);
    }

    public HSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mViewFooter = LayoutInflater.from(context).inflate(R.layout.listview_footer, null, false);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mListView == null) {
            setListView();
            // 设置刷新颜色
            setColorSchemeResources(android.R.color.holo_red_dark);
        }
    }

    /**
     * 加载更多监听 必须设置
     * @param onLoadMoreListener
     */
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    /**
     * 获取 ListView 控件
     */
    private void setListView() {
        int childCount = getChildCount();
        if (childCount > 0) {
            View childAt = getChildAt(0);
            if (childAt instanceof ListView) {
                mListView = (ListView) childAt;
                // 设置ListView的滑动监听
                setListViewOnScroll();
            }
        }
    }

    /**
     * 设置 ListView 的滑动监听
     */
    private void setListViewOnScroll() {
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 是否可以加载更多数据
                if (canLoadMore()) {
                    loadMore();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    /**
     * 加载更多
     */
    private void loadMore() {
        if (onLoadMoreListener != null) {
            setLoading(true);
            onLoadMoreListener.onLoadMore();
            setRefreshing(false);
        } else {
            Toast.makeText(mContext, "未设置加载更多监听...", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 判断是否可以加载更多数据
     * @return
     */
    private boolean canLoadMore() {

        // 1 只有上拉状态才加载更多数据
        boolean stata_pull = (mDownY - mUpY)>= mScaledTouchSlop;

        // 2 超过一屏幕数据 && 最后一个item可见
        boolean stata_visible = false;
        if (mListView != null && mListView.getAdapter() != null) {
            int firstVisiblePosition = mListView.getFirstVisiblePosition();
            int lastVisiblePosition = mListView.getLastVisiblePosition();
            int childCount = mListView.getAdapter().getCount();
            // 超过一屏幕数据 && 最后一个item可见
            if ((lastVisiblePosition - firstVisiblePosition + 1 < childCount) && (lastVisiblePosition == childCount - 1)) {
                stata_visible = true;
            }
        }

        // 3 非加载状态
        boolean stata_load = !isLoading;

        return stata_pull && stata_visible && stata_load;
    }

    /**
     * 设置加载状态
     * @param loading
     */
    private void setLoading(boolean loading) {
        isLoading = loading;
        if (isLoading) {
            mListView.addFooterView(mViewFooter);
        } else {
            mListView.removeFooterView(mViewFooter);
        }
        mDownY = 0;
        mUpY = 0;
    }

    /**
     * 事件分发处理
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (canLoadMore()) {
                    loadMore();
                }
                break;
            case MotionEvent.ACTION_UP:
                mUpY = ev.getY();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 刷新完成
     */
    public void setRefreshComplete() {
        // 下拉停止刷新
        setRefreshing(false);
        // 加载更多停止刷新
        setLoading(false);
    }
}
