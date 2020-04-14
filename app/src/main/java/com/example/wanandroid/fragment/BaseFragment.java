package com.example.wanandroid.fragment;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.example.wanandroid.R;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * 主界面Fragment的抽象类
 */
public abstract class BaseFragment extends SupportFragment {
    private OnFragmentOpenDrawerListener onFragmentOpenDrawerListener;
    // 再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0;

    void initToolBar(Toolbar toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_head);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFragmentOpenDrawerListener != null) {
                    onFragmentOpenDrawerListener.onOpenDrawer();
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentOpenDrawerListener){
            onFragmentOpenDrawerListener= (OnFragmentOpenDrawerListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onFragmentOpenDrawerListener=null;
    }

    public interface OnFragmentOpenDrawerListener {
        void onOpenDrawer();
    }



    /**
     * 处理回退事件
     *
     *
     */
    @Override
    public boolean onBackPressedSupport() {
        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
            _mActivity.finish();
        } else {
            TOUCH_TIME = System.currentTimeMillis();
            Toast.makeText(_mActivity, "Press again to exit", Toast.LENGTH_SHORT).show();
        }
        return true;
    }


}
