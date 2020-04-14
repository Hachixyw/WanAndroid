package com.example.wanandroid.fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.widget.Toolbar;


import android.view.LayoutInflater;


import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.wanandroid.R;

import java.util.Objects;

import me.yokeyword.eventbusactivityscope.EventBusActivityScope;

public class DetailFragment extends BaseBackFragment {

    static DetailFragment newInstance(){
        Bundle bundle=new Bundle();
        DetailFragment detailFragment=new DetailFragment();
        detailFragment.setArguments(bundle);
        return detailFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar_detail);
        _mActivity.setSupportActionBar(toolbar);
        setStatusBarColor();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _mActivity.onBackPressed();
            }
        });

        Bundle bundle=getArguments();
        String url = Objects.requireNonNull(bundle).getString("url");
        WebView wv_detail = view.findViewById(R.id.wv_detail);
        wv_detail.getSettings().setJavaScriptEnabled(true);
        wv_detail.setWebViewClient(new WebViewClient());
        wv_detail.loadUrl(url);
        return attachToSwipeBack(view);
    }


    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = _mActivity.getWindow();
                View decorView = window.getDecorView();

                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                decorView.setSystemUiVisibility(option);
                window.setStatusBarColor(getResources().getColor(R.color.white));
            } else {
                Window window = _mActivity.getWindow();
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                layoutParams.flags |= flagTranslucentStatus;
                window.setAttributes(layoutParams);
            }
        }
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        _mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBusActivityScope.getDefault(_mActivity).unregister(this);
        _mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        hideSoftInput();
    }

}
