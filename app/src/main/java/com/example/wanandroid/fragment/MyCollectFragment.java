package com.example.wanandroid.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.wanandroid.R;
import com.example.wanandroid.adapter.CollectAdapter;
import com.example.wanandroid.api.Api;
import com.example.wanandroid.bean.Collect;
import com.example.wanandroid.interceptor.AddCookieInterceptor;
import com.example.wanandroid.listener.OnItemClickListener;
import com.example.wanandroid.view.LoadingView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

public class MyCollectFragment extends BaseBackFragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout srl_collect;
    private RecyclerView rv_collect;
    private Retrofit retrofit;
    private final String TAG="Debug";
    private ArrayList<Collect.DataBean.DatasBean> list;
    private int page=0;
    private MyDecoration myDecoration;
    private CollectAdapter collectAdapter;
    private LoadingView loadingView;
    private TextView tv_att;

    public static MyCollectFragment newInstance(){
        Bundle args=new Bundle();
        MyCollectFragment myCollectFragment=new MyCollectFragment();
        myCollectFragment.setArguments(args);
        return myCollectFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_my_collect,container,false);
        loadingView=view.findViewById(R.id.loading_view);
        srl_collect=view.findViewById(R.id.srl_collect);
        rv_collect=view.findViewById(R.id.rv_collect);
        Toolbar toolbar = view.findViewById(R.id.toolbar_detail);
        tv_att=view.findViewById(R.id.tv_att);
        _mActivity.setSupportActionBar(toolbar);
        setStatusBarColor();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _mActivity.onBackPressed();
            }
        });
        return attachToSwipeBack(view);
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        _mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        SharedPreferences sharedPreferences = _mActivity.getSharedPreferences("login_state", MODE_PRIVATE);
        boolean isLoginFlag = sharedPreferences.getBoolean("state", false);

        if (isLoginFlag){
            OkHttpClient client=new OkHttpClient.Builder()
                    .addInterceptor(new AddCookieInterceptor(_mActivity.getApplicationContext()))
                    .build();
            retrofit=new Retrofit.Builder()
                    .baseUrl("https://www.wanandroid.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();
            Log.i(TAG,"登录成功");
            getData();
            List<Collect.DataBean.DatasBean> listData=new ArrayList<>();
            LinearLayoutManager manager=new LinearLayoutManager(_mActivity.getApplicationContext(),LinearLayoutManager.VERTICAL,false);
            rv_collect.setLayoutManager(manager);
            rv_collect.setHasFixedSize(true);
            collectAdapter=new CollectAdapter(_mActivity,listData);
            collectAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position, View view, BaseViewHolder baseViewHolder) {
                    String url=collectAdapter.getCollect(position).getLink();
                    Bundle bundle=new Bundle();
                    bundle.putString("url",url);
                    DetailFragment detailFragment=DetailFragment.newInstance();
                    detailFragment.setArguments(bundle);
                    start(detailFragment,SquareFragment.SINGLETASK);
                }
            });
            if (myDecoration!=null){
                rv_collect.removeItemDecoration(myDecoration);
            }
            myDecoration=new MyDecoration(_mActivity.getApplicationContext(),LinearLayoutManager.VERTICAL);
            rv_collect.addItemDecoration(myDecoration);

            rv_collect.setAdapter(collectAdapter);
            srl_collect.setColorSchemeResources(R.color.colorPrimary);
            srl_collect.setOnRefreshListener(this);
        }else {
            //隐藏界面,显示
            loadingView.setVisibility(View.INVISIBLE);
            tv_att.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        Log.i(TAG,"执行 OnRefresh");
        List<Collect.DataBean.DatasBean> list1=getData();
        //注:使用setNewData()方法将会造成上拉加载时循环执行onLoadMore方法
        //原因:setNewData()方法会将数据指针重新替换引用
        //refresh()方法会造成下拉刷新不改变已经加载过的列表长度,也有可能根本没有进行刷新，暂不确定
        collectAdapter.replaceData(list1);
        Objects.requireNonNull(collectAdapter.getLoadMoreModule()).loadMoreComplete();
        srl_collect.setRefreshing(false);
    }

    private List<Collect.DataBean.DatasBean> getData(){
        list=new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                retrofit.create(Api.class)
                        .selectCollect(page)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Collect>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Collect collect) {
                                for (Collect.DataBean.DatasBean datasBean:collect.getData().getDatas()){
                                    list.add(datasBean);
                                    Log.i(TAG,"Article Title:"+datasBean.getTitle());
                                }

                                collectAdapter.refresh(list);
                                loadingView.setVisibility(View.INVISIBLE);
                                srl_collect.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i(TAG,"-->"+e.getMessage());
                                Toast.makeText(_mActivity.getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
//                        loadMoreCount=2;
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        }).start();
        Log.i(TAG,"list Size:"+list.size());
        return list;
    }

    public static class MyDecoration extends RecyclerView.ItemDecoration{
        private int orientation = LinearLayoutManager.VERTICAL;
        private Drawable divider;

        MyDecoration(Context context, int orientation) {
            int[] attrs = new int[]{android.R.attr.listDivider};
            TypedArray typedArray = context.obtainStyledAttributes(attrs);
            divider = typedArray.getDrawable(0);
            typedArray.recycle();
            setOrientation(orientation);
        }

        public void setOrientation(int orientation) {
            if (orientation != LinearLayoutManager.VERTICAL
                    && orientation != LinearLayoutManager.HORIZONTAL) {
                throw new IllegalArgumentException("unsupport type");
            }
            this.orientation = orientation;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (orientation == LinearLayoutManager.VERTICAL) {
                outRect.set(0, 0, 0, divider.getIntrinsicHeight());
            } else {
                outRect.set(0, 0, divider.getIntrinsicWidth(), 0);
            }
        }

        //RecyclerView回调绘制方法
        @Override
        public void onDraw(@NonNull Canvas c, RecyclerView parent, RecyclerView.State state) {
            if (orientation == LinearLayoutManager.VERTICAL) {
                drawVertical(c, parent);
            } else {
                drawHorizontal(c, parent);
            }
            super.onDraw(c, parent, state);
        }

        private void drawHorizontal(Canvas c, RecyclerView parent) {
            int top = parent.getPaddingTop();
            int bottom = parent.getHeight() - parent.getPaddingBottom();
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int left = child.getRight() + params.rightMargin + Math.round(ViewCompat.getTranslationX(child));
                int right = left + divider.getIntrinsicHeight();
                divider.setBounds(left, top, right, bottom);
                divider.draw(c);
            }
        }

        private void drawVertical(Canvas c, RecyclerView parent) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int top = child.getBottom() + params.bottomMargin + Math.round(ViewCompat.getTranslationY(child));
                int bottom = top + divider.getIntrinsicHeight();
                divider.setBounds(left, top, right, bottom);
                divider.draw(c);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        hideSoftInput();
    }

    @Override
    public boolean onBackPressedSupport() {
        return super.onBackPressedSupport();
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
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                layoutParams.flags |= flagTranslucentStatus;
                window.setAttributes(layoutParams);
            }
        }
    }
}
