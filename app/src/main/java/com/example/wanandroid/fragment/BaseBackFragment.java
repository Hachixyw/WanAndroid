package com.example.wanandroid.fragment;


import android.os.Bundle;

import androidx.annotation.Nullable;

import me.yokeyword.fragmentation_swipeback.SwipeBackFragment;

public class BaseBackFragment extends SwipeBackFragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //设置视差滑动的偏移
        setParallaxOffset(0.5f);
    }

}
