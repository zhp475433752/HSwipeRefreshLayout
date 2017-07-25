package com.hpzhang.hswiperefreshlayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hpzhang
 *
 * 自定义刷新控件Demo
 */
public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, OnLoadMoreListener {

    /**
     * 自定义刷新控件
     */
    private HSwipeRefreshLayout refreshLayout;
    /**
     * 适配器
     */
    private HAdapter adapter;
    /**
     * 数据集合
     */
    private List<String> data;
    /**
     * 每次加载的数据条数
     */
    private int size = 20;
    /**
     * HSwipeRefreshLayout 控件中的 ListView
     */
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refreshLayout = (HSwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        listView = (ListView) findViewById(R.id.listView);
        // 下拉刷新
        refreshLayout.setOnRefreshListener(this);
        // 加载更多
        refreshLayout.setOnLoadMoreListener(this);

        data = new ArrayList<>();
        for (int k=0; k<size; k++) {
            data.add("这是测试数据："+k);
        }

        adapter = new HAdapter(data, this);
        listView.setAdapter(adapter);
    }

    /**
     * 发送和接收消息
     */
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg != null) {
                if (msg.what == 1) {
                    refresh();
                } else if (msg.what == 2) {
                    loadMore();
                }
            }
            return false;
        }
    });

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        handler.sendEmptyMessageDelayed(1, 1000);
    }

    /**
     * 上拉加载更多
     */
    @Override
    public void onLoadMore() {
        handler.sendEmptyMessageDelayed(2, 1000);
    }

    private void loadMore() {
        int length = data.size();
        for(int k=length; k<length + size; k++) {
            data.add("这是测试数据："+k);
        }
        stopRefresh();
    }

    private void refresh() {
        data.clear();
        for (int k=0; k<size; k++) {
            data.add("这是测试数据："+k);
        }
        stopRefresh();
    }

    private void stopRefresh() {
        adapter.notifyDataSetChanged();
        // 数据加载完成后记得调用停止刷新！！！
        refreshLayout.setRefreshComplete();
    }
}
