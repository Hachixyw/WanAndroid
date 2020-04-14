package com.example.wanandroid.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
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
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;
import com.youth.banner.transformer.CubeOutTransformer;

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

public class HomeFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.rv_home)
    RecyclerView recyclerView;
    private static final String TAG = "Debug";
    private List<Article> list;
    private ArticleAdapter articleAdapter;
    private static final String LOGIN_PREF = "login_state";
    private boolean loginFlag = false;
    private Retrofit retrofit;
    private int loadMoreCount;
    private List<String> imageTitle;
    private List<String> imagePath;
    private List<String> imageLink;
    private Banner banner;
    private int page = 0;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyDecoration myDecoration;
    private LoadingView loadingView;

    static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(args);
        return homeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("Hachi", "Home 执行了 onCreateView");
        View view = inflater.inflate(R.layout.fragment_home, null);
        recyclerView = view.findViewById(R.id.rv_home);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        loadingView = view.findViewById(R.id.loading_view);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("首页");
        initToolBar(toolbar);
        return view;
    }

    private void request() {
        //获取登录状态缓存
        if (loginFlag) {
            File cacheFile = new File(Objects.requireNonNull(getActivity()).getApplication().getCacheDir(), "cacheData");
            Cache cache = new Cache(cacheFile, 1024 * 1024 * 14);
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
        Log.i(TAG, "执行 HomeFragment Resume");
        request();

        getImageData();
        List<Article> listData = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        articleAdapter = new ArticleAdapter(getActivity(), listData);
        View view1 = getActivity().getLayoutInflater().inflate(R.layout.item_header_banner, null);
        banner = view1.findViewById(R.id.banner);
        banner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 700));
        articleAdapter.addHeaderView(banner);
        articleAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, BaseViewHolder baseViewHolder) {
                String url=articleAdapter.getArticle(position).getLink();
                Bundle bundle = new Bundle();
                bundle.putString("url", url);
                DetailFragment detailFragment1 = DetailFragment.newInstance();
                detailFragment1.setArguments(bundle);
                ((MainFragment) Objects.requireNonNull(getParentFragment())).startBrotherFragment(detailFragment1);
            }
        });
        final BaseLoadMoreModule loadMoreModule = articleAdapter.getLoadMoreModule();
        Objects.requireNonNull(loadMoreModule).setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "执行HomeFragment OnLoadMore");
                        List<Article> list1 = getData(false);
                        if (loadMoreCount == 1) {
                            articleAdapter.addData(list1);
                            Log.i(TAG, "loadMoreCount:" + loadMoreCount);
                            loadMoreModule.loadMoreComplete();
                        } else if (loadMoreCount == 2) {
                            Log.i(TAG, "loadMoreCount:" + loadMoreCount);
                            loadMoreModule.loadMoreFail();
                        } else if (loadMoreCount == 3) {
                            Log.i(TAG, "loadMoreCount:" + loadMoreCount);
                            loadMoreModule.loadMoreEnd();
                        }
                    }
                }, 300);
            }
        });
        if (myDecoration != null) {
            recyclerView.removeItemDecoration(myDecoration);
        }
        myDecoration = new MyDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(myDecoration);
        recyclerView.setAdapter(articleAdapter);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        page = 0;
        Log.i("Hachi", "Home 执行了 onPause");
    }

    private void getImageData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                retrofit.create(Api.class)
                        .getHomeBanner()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<com.example.wanandroid.bean.Banner>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(com.example.wanandroid.bean.Banner banner) {
                                imageTitle = new ArrayList<>();
                                imagePath = new ArrayList<>();
                                imageLink = new ArrayList<>();
                                for (com.example.wanandroid.bean.Banner.DataBean dataBean : banner.getData()) {
                                    imageTitle.add(dataBean.getTitle());
                                    imagePath.add(dataBean.getImagePath());
                                    imageLink.add(dataBean.getUrl());
                                }
                                getData(false);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i(TAG, "-->" + e.getMessage());
                                Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        }).start();
    }


    private List<Article> getData(boolean isRefresh) {
        if (isRefresh) {
            page = 0;
        }
        Log.i(TAG, "运行 HomeFragment getData");
        Log.i(TAG, "查询第" + page + "页");
        list = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                retrofit.create(Api.class)
                        .getHomeArticleList(page)
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

                                banner.setImages(imagePath)
                                        .setBannerAnimation(CubeOutTransformer.class)
                                        .setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE)
                                        .setBannerTitles(imageTitle)
                                        .setImageLoader(new MyImageLoader())
                                        .setIndicatorGravity(BannerConfig.RIGHT)
                                        .setDelayTime(3000)
                                        .start();
                                banner.setOnBannerListener(new OnBannerListener() {
                                    @Override
                                    public void OnBannerClick(int position) {
                                        String url = imageLink.get(position);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("url", url);
                                            DetailFragment detailFragment1 = DetailFragment.newInstance();
                                            detailFragment1.setArguments(bundle);
                                        ((MainFragment) Objects.requireNonNull(getParentFragment())).startBrotherFragment(detailFragment1);
                                    }
                                });
                                articleAdapter.refresh(list);
                                ++page;
                                if (page < articleList.getData().getPageCount()) {
                                    loadMoreCount = 1;
                                } else {
                                    loadMoreCount = 3;
                                }

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
                Log.i(TAG, "list Size:" + list.size());

            }
        }).start();
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
        Objects.requireNonNull(articleAdapter.getLoadMoreModule()).loadMoreComplete();
        swipeRefreshLayout.setRefreshing(false);
    }

    static class MyImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context)
                    .load(path)
                    .into(imageView);
        }
    }

    public static class MyDecoration extends RecyclerView.ItemDecoration {
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
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (orientation == LinearLayoutManager.VERTICAL) {
                outRect.set(0, 0, 0, divider.getIntrinsicHeight());
            } else {
                outRect.set(0, 0, divider.getIntrinsicWidth(), 0);
            }
        }

        //RecyclerView回调绘制方法
        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
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
        EventBusActivityScope.getDefault(_mActivity).unregister(this);
    }
}
