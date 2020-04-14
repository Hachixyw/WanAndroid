package com.example.wanandroid.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wanandroid.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectWebFragment extends BaseBackFragment {

    public static CollectWebFragment newInstance(){
        Bundle bundle=new Bundle();
        CollectWebFragment collectWebFragment=new CollectWebFragment();
        collectWebFragment.setArguments(bundle);
        return collectWebFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collect_web, container, false);
    }
}
