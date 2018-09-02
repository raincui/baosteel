package com.android.yl.baowu.baseui.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class LJRefreshListView extends ListView implements OnScrollListener {

    private Context mContext;

    public LJRefreshListView(Context context) {
        super(context);
        init(context);
    }

    public LJRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        ImageView imageView = null;
        if (getHeaderViewsCount() == 0 && getFooterViewsCount() == 0) {
            // 添加高度和宽度为0的空view，屏蔽addFooterView和addHeadView必须要在setAdapter前调用问题
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new LayoutParams(0, 0));
            addFooterView(imageView, null, false);
            //imageView.setVisibility(GONE);
        }
        super.setAdapter(adapter);
        if (imageView != null) {
            removeFooterView(imageView);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        try {
            super.dispatchDraw(canvas);
        } catch (IndexOutOfBoundsException e) {
            Log.e("LJRefreshListView", "Ignore list view error ->" + e.toString());
        }catch (Exception e){
            Log.e("LJRefreshListView", "DispatchDraw error ->" + e.toString());
        }
    }

}
