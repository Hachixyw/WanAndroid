package com.example.wanandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.wanandroid.bean.Collect;
import com.example.wanandroid.bean.CollectWeb;
import com.example.wanandroid.fragment.BaseFragment;
import com.example.wanandroid.fragment.CollectWebFragment;
import com.example.wanandroid.fragment.HomeFragment;
import com.example.wanandroid.fragment.LoginFragment;
import com.example.wanandroid.fragment.MainFragment;
import com.example.wanandroid.fragment.MyCollectFragment;
import com.example.wanandroid.fragment.MyShareFragment;
import com.example.wanandroid.interceptor.SaveCookieInterceptor;
import com.google.android.material.navigation.NavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.SupportFragment;

public class MainActivity extends SupportActivity implements BaseFragment.OnFragmentOpenDrawerListener, LoginFragment.OnLoginListener {
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.footer_item_setting)
    Button footer_item_setting;
    @BindView(R.id.footer_item_exit)
    Button footer_item_exit;



    private static final String LOGIN_PREF = "login_state";
    private TextView tv_username;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Hachi", "Main 执行了 onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (findFragment(MainFragment.class)==null){
            loadRootFragment(R.id.framelayout,MainFragment.newInstance());
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                0, 0);
//        //替换侧滑栏图标
        toggle.setDrawerIndicatorEnabled(false);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        ColorStateList colorStateList = getResources().getColorStateList(R.color.nav_menu_text_color);
        navigationView.setItemTextColor(colorStateList);
        navigationView.setItemIconTintList(colorStateList);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                final ISupportFragment topFragment = getTopFragment();
                SupportFragment myHome = (SupportFragment) topFragment;
                switch (menuItem.getItemId()) {
                    case R.id.item_collection_article:
                        drawerLayout.closeDrawers();
                        MyCollectFragment fragment = findFragment(MyCollectFragment.class);
                        if (fragment == null) {
                            myHome.startWithPopTo(MyCollectFragment.newInstance(), HomeFragment.class, false);
                        } else {
                            myHome.start(MyCollectFragment.newInstance(), SupportFragment.SINGLETASK);
                        }
                        break;
                    case R.id.item_MyShare:
                        drawerLayout.closeDrawers();
                        MyShareFragment fragment1 = findFragment(MyShareFragment.class);
                        if (fragment1 == null) {
                            myHome.startWithPopTo(MyShareFragment.newInstance(), HomeFragment.class, false);
                        } else {
                            myHome.start(MyShareFragment.newInstance(), SupportFragment.SINGLETASK);
                        }
                        break;
                    case R.id.item_collection_web:
                        drawerLayout.closeDrawers();
                        CollectWebFragment fragment2=findFragment(CollectWebFragment.class);
                        if (fragment2==null){
                            myHome.startWithPopTo(CollectWebFragment.newInstance(),HomeFragment.class,false);
                        }else {
                            myHome.start(CollectWebFragment.newInstance(),SupportFragment.SINGLETASK);
                        }
                        break;
                }
                return false;
            }
        });
        View headView = navigationView.getHeaderView(0);
        tv_username = headView.findViewById(R.id.tv_username);
        init();
        tv_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sharedPreferences.getBoolean("state", false)) {
                    drawerLayout.closeDrawers();
                    start(LoginFragment.newInstance());
                }
            }
        });
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            View decorView = window.getDecorView();

            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {
            Window window = getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            layoutParams.flags |= flagTranslucentStatus;
            window.setAttributes(layoutParams);
        }
    }

    @Override
    protected void onResume() {
        Log.i("Hachi", "Main 执行了 onResume");
        super.onResume();
        sharedPreferences = getSharedPreferences(LOGIN_PREF, MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("state", false)) {
            tv_username.setText("登录");
        }else {

            tv_username.setText(sharedPreferences.getString("username",""));
        }

        footer_item_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveCookieInterceptor.clearCookies(getApplicationContext());
                start(MainFragment.newInstance());
                tv_username.setText("登录");
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
    }

    @Override
    public void onOpenDrawer() {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }


    @Override
    public void onLoginSuccess(String account) {
        tv_username.setText(account);
    }
}
