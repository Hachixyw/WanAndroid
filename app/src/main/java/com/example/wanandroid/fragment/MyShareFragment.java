package com.example.wanandroid.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.wanandroid.R;
import com.example.wanandroid.adapter.SharedAdapter;
import com.example.wanandroid.api.Api;
import com.example.wanandroid.bean.Article;
import com.example.wanandroid.bean.ResponseBase;
import com.example.wanandroid.bean.SharedArticle;
import com.example.wanandroid.interceptor.AddCookieInterceptor;
import com.example.wanandroid.listener.OnItemClickListener;
import com.example.wanandroid.view.LoadingView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.yokeyword.fragmentation.SupportFragment;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

public class MyShareFragment extends BaseBackFragment implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout srl_share;
    private SwipeRecyclerView rv_share;
    private final String TAG = "Debug";
    private Retrofit retrofit;
    private List<Article> list;
    private SharedAdapter sharedAdapter;
    private LoadingView loadingView;
    private TextView tv_att;
    private FloatingActionButton floatingActionButton;
    private boolean isLoginFlag=false;
    private MyDecoration myDecoration;
    private Toast toast;

    public static MyShareFragment newInstance(){
        Bundle args=new Bundle();
        MyShareFragment myShareFragment=new MyShareFragment();
        myShareFragment.setArguments(args);
        return myShareFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("TAG","onCreateView 方法");
        View view=inflater.inflate(R.layout.fragment_my_share,container,false);
        init();
        SharedPreferences sharedPreferences = _mActivity.getSharedPreferences("login_state", MODE_PRIVATE);
        isLoginFlag = sharedPreferences.getBoolean("state", false);
        Toolbar toolbar=view.findViewById(R.id.toolbar_detail);
        _mActivity.setSupportActionBar(toolbar);
        setStatusBarColor();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _mActivity.onBackPressed();
            }
        });
        loadingView=view.findViewById(R.id.loading_view);
        srl_share=view.findViewById(R.id.srl_share);
        rv_share=view.findViewById(R.id.rv_share);
        tv_att=view.findViewById(R.id.tv_att);
        floatingActionButton=view.findViewById(R.id.fab_add_share);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到编辑界面
                if (isLoginFlag){
                    start(EditShareFragment.newInstance(), SupportFragment.SINGLETASK);
                }else {
                    Toast.makeText(_mActivity,"请先登录",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return attachToSwipeBack(view);
    }

    private void init(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                Window window=_mActivity.getWindow();
                View decorView=window.getDecorView();

                int option=View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                decorView.setSystemUiVisibility(option);
                window.setStatusBarColor(getResources().getColor(R.color.white));
            }else {
                Window window=_mActivity.getWindow();
                WindowManager.LayoutParams layoutParams=window.getAttributes();
                int flagTranslucentStatus=WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                layoutParams.flags|=flagTranslucentStatus;
                window.setAttributes(layoutParams);
            }
        }
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        Log.d("TAG","onEnterAnimationEnd 方法");
        // 入场动画结束后执行  优化,防动画卡顿

        _mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        if (isLoginFlag){
            OkHttpClient client=new OkHttpClient.Builder()
                    .addInterceptor(new AddCookieInterceptor(getActivity()))
                    .build();
            retrofit=new Retrofit.Builder()
                    .baseUrl("https://www.wanandroid.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();
            getData();
            List<Article> list1=new ArrayList<>();
            LinearLayoutManager manager=new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
            rv_share.setLayoutManager(manager);
            rv_share.setHasFixedSize(true);

            //创建左滑菜单
            rv_share.setSwipeMenuCreator(swipeMenuCreator);
            //设置点击事件
            rv_share.setOnItemMenuClickListener(onItemMenuClickListener);

            sharedAdapter=new SharedAdapter(_mActivity,list1);
            sharedAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position, View view, BaseViewHolder baseViewHolder) {
                    String url=sharedAdapter.getArticle(position).getLink();
                    Bundle bundle=new Bundle();
                    bundle.putString("url",url);
                    DetailFragment detailFragment=DetailFragment.newInstance();
                    detailFragment.setArguments(bundle);
                    start(detailFragment,SquareFragment.SINGLETASK);
                }
            });

//            if (myDecoration!=null){
//                rv_share.removeItemDecoration(myDecoration);
//            }
//            myDecoration=new MyDecoration(_mActivity.getApplicationContext(),LinearLayoutManager.VERTICAL);
//            rv_share.addItemDecoration(myDecoration);
            rv_share.setAdapter(sharedAdapter);

            srl_share.setColorSchemeResources(R.color.colorPrimary);
            srl_share.setOnRefreshListener(this);
        }else {
            loadingView.setVisibility(View.INVISIBLE);
            tv_att.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        Log.i(TAG,"执行 OnRefresh");
        List<Article> list1=getData();
        //注:使用setNewData()方法将会造成上拉加载时循环执行onLoadMore方法
        //原因:setNewData()方法会将数据指针重新替换引用
        //refresh()方法会造成下拉刷新不改变已经加载过的列表长度,也有可能根本没有进行刷新，暂不确定
        sharedAdapter.replaceData(list1);
        Objects.requireNonNull(sharedAdapter.getLoadMoreModule()).loadMoreComplete();
        srl_share.setRefreshing(false);
    }

    private List<Article> getData() {
        list=new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                retrofit.create(Api.class)
                        .getSharedArticle(1 )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<SharedArticle>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(SharedArticle sharedArticle) {
                                for (Article article:sharedArticle.getData().getShareArticles().getDatas()){
                                    list.add(article);
                                    Log.i(TAG,"ShareDetail Article Title:"+article.getTitle());
                                }
                                sharedAdapter.refresh(list);
                                loadingView.setVisibility(View.INVISIBLE);
                                srl_share.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i(TAG,"-->"+e.getMessage());
                                Toast.makeText(_mActivity.getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        }).start();
        return list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        hideSoftInput();
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

    //自定义RecyclerView的下划线
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

    private SwipeMenuCreator swipeMenuCreator=new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
            int width=getResources().getDimensionPixelSize(R.dimen.dp_42);
            int height=ViewGroup.LayoutParams.MATCH_PARENT;

            SwipeMenuItem rightSwipeMenuItem=new SwipeMenuItem(getContext())
                    .setImage(R.mipmap.ic_delete)
                    .setText("删除")
                    .setWidth(width)
                    .setHeight(height);
            rightMenu.addMenuItem(rightSwipeMenuItem);

        }
    };

    private OnItemMenuClickListener onItemMenuClickListener=new OnItemMenuClickListener() {
        @Override
        public void onItemClick(final SwipeMenuBridge menuBridge, final int adapterPosition) {
            //
            int id=sharedAdapter.getArticle(adapterPosition).getId();
            String a=String.valueOf(id);
            Log.d("TAG","id :"+a);

            //发起删除请求
            retrofit.create(Api.class)
                    .deleteShareArticle(a)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBase>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ResponseBase responseBase) {
                            //关闭菜单
                            menuBridge.closeMenu();
                            //重新获取数据，并刷新视图
                            sharedAdapter.removePosition(adapterPosition);
                        }

                        @Override
                        public void onError(Throwable e) {
                            showToast("网络错误");
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    };

    private void showToast(String str){
        if (toast==null){
            toast=Toast.makeText(getContext(),str,Toast.LENGTH_SHORT);
        }else {
            toast.setText(str);
        }
        toast.show();
    }
}
