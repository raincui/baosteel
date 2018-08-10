package com.android.baosteel.lan.news.comment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.baosteel.lan.basebusiness.business.BusinessCallback;
import com.android.baosteel.lan.basebusiness.business.NetApi;
import com.android.baosteel.lan.basebusiness.business.ProtocolUrl;
import com.android.baosteel.lan.basebusiness.entity.ColumnInfo;
import com.android.baosteel.lan.baseui.customview.DragSortListView;
import com.android.baosteel.lan.baseui.ui.BaseFragment;
import com.android.baosteel.lan.baseui.ui.SimpleBaseAdapter;
import com.android.baosteel.lan.news.ColumnInfoHelper;
import com.baosight.lan.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yulong.cui
 * @Package com.android.baosteel.lan.news
 * @Title: LookActivity
 * @Description: 维护关注列表
 * Create DateTime: 2017/3/6
 */
public class LookFragment extends BaseFragment {

    private View viewMain;
    private DragSortListView dragList;

    private DragAdapter mAdapter;
    private boolean isChangeSort;
    private boolean isFirstInit = true;//是否第一次加载
    @Override
    protected void initView() {
        super.initView();
        initTitle();
        dragList = findView(viewMain,R.id.list);

    }

    @Override
    protected void initData() {
        super.initData();
        if(!isFirstInit)return;
        isFirstInit = false;
        View head = View.inflate(getContext(), R.layout.view_look_head, null);
        dragList.addHeaderView(head);
        mAdapter = new DragAdapter(getContext(), ColumnInfoHelper.getInstance().getEditableColumns());
        dragList.setAdapter(mAdapter);
    }

    /**
     * 保存顺序
     */
    private void saveDataSort() {
        if (!isChangeSort) return;
        ColumnInfoHelper.getInstance().saveSortColumns();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(!isVisibleToUser){
            saveDataSort();
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        dragList.setDragListener(new DragSortListView.DragListener() {
            @Override
            public void drag(int from, int to) {
                if (from == to) return;
                isChangeSort = true;
                mAdapter.move(from, to);
                ColumnInfoHelper.getInstance().changeSortColumns(from, to);
                getActivity().setResult(Activity.RESULT_OK);
            }

            @Override
            public void dragEnd() {
                if(isChangeSort)
                    mAdapter.notifyDataSetChanged();
            }
        });
        dragList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position -= dragList.getHeaderViewsCount();
                if(position<0)return;
                ColumnInfo info = mAdapter.getItem(position);
                changeLook(info);
            }
        });
    }

    /**
     * 改变订阅状态
     *
     * @param info
     */
    private void changeLook(final ColumnInfo info) {
        info.changeLook();
        mAdapter.notifyDataSetChanged();
        Map<String,Object> param = new HashMap<>();
        param.put("groupId",info.getGroupId());
        param.put("type",info.getIsDy());
        NetApi.call(NetApi.getJsonParam(ProtocolUrl.changeLook, param), new BusinessCallback(getContext()) {
            @Override
            public void subCallback(boolean flag,String json) {
                if (getActivity().isFinishing()||!isAdded()) return;
                if(!flag){
                    info.changeLook();
                    mAdapter.notifyDataSetChanged();
                    return;
                }
                getActivity().setResult(Activity.RESULT_OK);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (viewMain == null)
            viewMain = inflater.inflate(R.layout.activity_look, null);
        else {
            ViewGroup parent = (ViewGroup) viewMain.getParent();
            if (parent != null) parent.removeView(viewMain);
        }
        return viewMain;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initListener();
        initData();
    }

    class DragAdapter extends SimpleBaseAdapter<ColumnInfo> {

        public DragAdapter(Context context, List<ColumnInfo> data) {
            super(context, data);
        }

        @Override
        public int getItemResource(int position) {
            return R.layout.item_look;
        }

        @Override
        public View getItemView(int position, View convertView, ViewHolder holder) {
            ColumnInfo info = getItem(position);

            TextView name = holder.getView(R.id.txt_name);
            name.setText(info.getGroupName());

            TextView content = holder.getView(R.id.txt_content);
            content.setText(info.getDescription());

            ImageView check = holder.getView(R.id.img_checkbox);
            check.setImageResource(info.isLook() ? R.drawable.dingyue : R.drawable.weidingyue);
            return convertView;
        }
    }

}
