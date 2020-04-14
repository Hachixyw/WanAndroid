package com.example.wanandroid.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.wanandroid.R;
import com.example.wanandroid.adapter.ArticleAdapter;
import com.example.wanandroid.api.Api;
import com.example.wanandroid.bean.Article;
import com.example.wanandroid.bean.ArticleList;
import com.example.wanandroid.interceptor.AddCookieInterceptor;
import com.example.wanandroid.interceptor.NetWorkInterceptor;
import com.example.wanandroid.listener.OnItemClickListener;
import com.example.wanandroid.view.LoadingView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.yokeyword.eventbusactivityscope.EventBusActivityScope;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SquareFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.rv_square)
    RecyclerView recyclerView;
    private static final String TAG = "Debug";
    private List<Article> list;
    private ArticleAdapter articleAdapter;
    private static final String LOGIN_PREF = "login_state";
    private boolean loginFlag = false;
    private Retrofit retrofit;
    private int loadMoreCount;
    private int page = 0;
    private SwipeRefreshLayout swipeRefreshLayout;
    private HomeFragment.MyDecoration myDecoration;
    private LoadingView loadingView;

    static SquareFragment newInstance() {
        Bundle args = new Bundle();
        SquareFragment squareFragment = new SquareFragment();
        squareFragment.setArguments(args);
        return squareFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_square, null);
        recyclerView = view.findViewById(R.id.rv_square);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_square);
        loadingView = view.findViewById(R.id.loading_view);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("广场");
        initToolBar(toolbar);
        return view;
    }


    private void request() {
        File cacheFile = new File(Objects.requireNonNull(getActivity()).getApplication().getCacheDir(), "cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 5);
        //获取登录状态缓存
        if (loginFlag) {
            //请求头响应Cookie
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AddCookieInterceptor(getActivity()))
                    .addNetworkInterceptor(new NetWorkInterceptor())
                    .cache(cache)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://www.wanandroid.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build();
            Log.i(TAG, "登录成功");
        } else {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://www.wanandroid.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            Log.i(TAG, "登录失败");
        }

    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(LOGIN_PREF,
                Context.MODE_PRIVATE);
        loginFlag = sharedPreferences.getBoolean("state", false);
        Log.i(TAG, "执行 Resume");
        request();
        List<Article> listData = getData(false);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        articleAdapter = new ArticleAdapter(getActivity(), listData);
        articleAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, BaseViewHolder baseViewHolder) {
                String url = articleAdapter.getArticle(position).getLink();
                Bundle bundle = new Bundle();
                bundle.putString("url", url);
                DetailFragment detailFragment = DetailFragment.newInstance();
                detailFragment.setArguments(bundle);
                ((MainFragment) getParentFragment()).startBrotherFragment(detailFragment);
            }
        });
        final BaseLoadMoreModule loadMoreModule = articleAdapter.getLoadMoreModule();
        loadMoreModule.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "执行OnLoadMore");
                        List<Article> list1 = getData(false);
                        if (loadMoreCount == 1) {
                            articleAdapter.addData(list1);
                            loadMoreModule.loadMoreComplete();
                        } else if (loadMoreCount == 2) {
                            loadMoreModule.loadMoreFail();
                        } else if (loadMoreCount == 3) {
                            loadMoreModule.loadMoreEnd();
                        }
                    }
                }, 500);
            }
        });

        if (myDecoration != null) {
            recyclerView.removeItemDecoration(myDecoration);
        }
        myDecoration = new HomeFragment.MyDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(myDecoration);

        recyclerView.setAdapter(articleAdapter);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this);
    }


    private List<Article> getData(boolean isRefresh) {
        if (isRefresh) {
            page = 0;
        }
        Log.i(TAG, "运行getData");
        list = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                retrofit.create(Api.class)
                        .getSquareList(page)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<ArticleList>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(ArticleList articleList) {

                                for (Article article : articleList.getData().getDatas()) {
                                    list.add(article);
                                    Log.i(TAG, "Article Title:" + article.getTitle());
                                }
                                ++page;
                                if (page < articleList.getData().getPageCount()) {

                                    loadMoreCount = 1;
                                } else {
                                    loadMoreCount = 3;
                                }
                                articleAdapter.refresh(list);
                                loadingView.setVisibility(View.INVISIBLE);
                                swipeRefreshLayout.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i(TAG, "-->" + e.getMessage());
                                Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
                                loadMoreCount = 2;
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        }).start();

        Log.i(TAG, "list Size:" + list.size());
        return list;
    }

    @Override
    public void onRefresh() {
        Log.i(TAG, "执行 OnRefresh");
        List<Article> list1 = getData(true);
        //注:使用setNewData()方法将会造成上拉加载时循环执行onLoadMore方法
        //原因:setNewData()方法会将数据指针重新替换引用
        //refresh()方法会造成下拉刷新不改变已经加载过的列表长度,也有可能根本没有进行刷新，暂不确定
        articleAdapter.replaceData(list1);
        articleAdapter.getLoadMoreModule().loadMoreComplete();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        page = 0;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBusActivityScope.getDefault(_mActivity).unregister(this);
    }
}
