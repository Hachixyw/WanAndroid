package com.example.wanandroid.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.wanandroid.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;
import me.yokeyword.fragmentation.SupportFragment;

public class MainFragment extends SupportFragment {
    private static final int HOME = 0;
    private static final int SQUARE = 1;
    private SupportFragment[] supportFragments = new SupportFragment[2];

    public static MainFragment newInstance(){
        Bundle args=new Bundle();
        MainFragment mainFragment=new MainFragment();
        mainFragment.setArguments(args);
        return mainFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view=inflater.inflate(R.layout.fragment_main,container,false);
    initView(view);
    return view;
    }

    private void initView(View view) {
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.nav_bottom_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.item_home);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.item_home:
                    showHideFragment(supportFragments[HOME], supportFragments[SQUARE]);
                    return true;
                case R.id.item_square:
                    showHideFragment(supportFragments[SQUARE], supportFragments[HOME]);
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SupportFragment homeFragment = findFragment(HomeFragment.class);
        if (homeFragment == null) {
            supportFragments[HOME] = HomeFragment.newInstance();
            supportFragments[SQUARE] = SquareFragment.newInstance();

            loadMultipleRootFragment(R.id.tab_container, HOME,
                    supportFragments[HOME], supportFragments[SQUARE]);

        } else {
            supportFragments[HOME] = findFragment(HomeFragment.class);
            supportFragments[SQUARE] = findFragment(SquareFragment.class);
        }
    }

    void startBrotherFragment(SupportFragment targetFragment){
        start(targetFragment,SupportFragment.SINGLETASK);
    }
}
