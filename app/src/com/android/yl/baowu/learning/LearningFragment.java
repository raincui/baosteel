package com.android.yl.baowu.learning;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.yl.baowu.baseui.ui.BaseFragment;
import com.android.yl.baowu.news.NewsListFragment;
import com.android.yl.baowu.R;

/**
 * @author yulong.cui
 * @Package com.android.baosteel.lan.learning
 * @Title: LearningFragment
 * Create DateTime: 2017/2/27
 */
public class LearningFragment extends BaseFragment {

    private View viewMain;
    private boolean isFirstInit;
    private View headView;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        isFirstInit = true;
    }

    public void setHead(View headView) {
        this.headView = headView;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (viewMain == null)
            viewMain = inflater.inflate(R.layout.fragment_learning, null);
        else {
            ViewGroup parent = (ViewGroup) viewMain.getParent();
            if (parent != null) parent.removeView(viewMain);
        }
        return viewMain;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isFirstInit) {
            final NewsListFragment nFragment = NewsListFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putString("type", "5");
            nFragment.setArguments(bundle);
            getChildFragmentManager().beginTransaction().replace(R.id.fragment, nFragment).commitAllowingStateLoss();
            if (headView != null) {
                viewMain.post(new Runnable() {
                    @Override
                    public void run() {
                        nFragment.setHeadView(headView);
                    }
                });
            }

        }
    }
}
