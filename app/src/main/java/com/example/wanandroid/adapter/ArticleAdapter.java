package com.example.wanandroid.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.wanandroid.R;
import com.example.wanandroid.api.Api;
import com.example.wanandroid.bean.Article;
import com.example.wanandroid.bean.ResponseBase;
import com.example.wanandroid.interceptor.AddCookieInterceptor;
import com.example.wanandroid.listener.OnItemClickListener;

import java.util.List;


import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArticleAdapter extends BaseQuickAdapter<Article, ArticleAdapter.ViewHolder> implements LoadMoreModule {

    private Context mContext;
    private List<Article> list;
    private boolean flag;
    private Toast mToast;

    private OnItemClickListener mClickListener;

    static class ViewHolder extends BaseViewHolder {
        TextView articleTitle;
        TextView author;
        TextView shareUser;
        LinearLayout ll_author;
        LinearLayout ll_share;
        LinearLayout ll_article;
        ImageView iv_collect;
        TextView tv_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            articleTitle = itemView.findViewById(R.id.tv_articleTitle);
            author = itemView.findViewById(R.id.tv_author);
            shareUser = itemView.findViewById(R.id.tv_shareUser);
            ll_author = itemView.findViewById(R.id.ll_author);
            ll_share = itemView.findViewById(R.id.ll_share);
            iv_collect = itemView.findViewById(R.id.iv_collect);
            ll_article = itemView.findViewById(R.id.ll_article);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }


    public ArticleAdapter(Context mContext, List<Article> list) {
        super(R.layout.item_article, list);
        this.mContext = mContext;
        this.list = list;
    }


    private void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }

    public Article getArticle(int position) {
        return list.get(position);
    }


    @Override
    protected void convert(@NonNull final ViewHolder helper, final Article item) {
        //注：源数据是Html格式的，需要进行格式转换
        helper.articleTitle.setText(Html.fromHtml(item.getTitle(), Html.FROM_HTML_MODE_LEGACY));
        Log.i("Debug", "Title:" + item.getTitle() + " Author:" + item.getAuthor()
                + " Collect:" + item.isCollect() + " id:" + item.getId());
        flag = item.isCollect();
        if (flag) {
            helper.iv_collect.setImageResource(R.mipmap.ic_collect);
            Log.i("Debug", "isCollect:" + flag);
        } else {
            helper.iv_collect.setImageResource(R.mipmap.ic_no_collect);
            Log.i("Debug", "isCollect:" + flag);
        }

        if (!item.getAuthor().equals("")) {
            helper.ll_author.setVisibility(View.VISIBLE);
            helper.author.setText(item.getAuthor());
        } else {
            helper.ll_author.setVisibility(View.GONE);
        }

        if (!item.getShareUser().equals("")) {
            helper.ll_share.setVisibility(View.VISIBLE);
            helper.shareUser.setText(item.getShareUser());
        } else {
            helper.ll_share.setVisibility(View.GONE);
        }

        helper.tv_time.setText("" + item.getNiceDate());

        helper.iv_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取缓存
                SharedPreferences sharedPreferences = mContext.
                        getSharedPreferences("login_state", Context.MODE_PRIVATE);
                if (sharedPreferences.getBoolean("state", false)) {


                    Log.i("Debug", "isCollect:" + flag);
                    //收藏过则取消收藏
                    if (flag) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //请求头响应Cookie
                                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                        .addInterceptor(new AddCookieInterceptor(mContext))
                                        .build();
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("https://www.wanandroid.com/")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                        .client(okHttpClient)
                                        .build();
                                retrofit.create(Api.class)
                                        .cancelCollectionInArticle(item.getId())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Observer<ResponseBase>() {
                                            @Override
                                            public void onSubscribe(Disposable d) {

                                            }

                                            @Override
                                            public void onNext(ResponseBase responseBase) {
                                                if (responseBase.getErrorMsg().equals("")) {
                                                    showToast("取消成功");
                                                    helper.iv_collect.setImageResource(R.mipmap.ic_no_collect);
                                                    flag = false;
                                                } else {
                                                    showToast("取消失败");
                                                }
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Log.i("Debug", "-->" + e.getMessage());
                                                showToast("网络错误");
                                            }

                                            @Override
                                            public void onComplete() {

                                            }
                                        });
                            }
                        }).start();

                    } else {
                        //请求头响应Cookie
                        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                .addInterceptor(new AddCookieInterceptor(mContext))
                                .build();
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("https://www.wanandroid.com/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                .client(okHttpClient)
                                .build();
                        //未收藏过则收藏
                        retrofit.create(Api.class)
                                .collectArticle(item.getId())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<ResponseBase>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(ResponseBase responseBase) {
                                        if (responseBase.getErrorMsg().equals("")) {
                                            showToast("收藏成功");
                                            helper.iv_collect.setImageResource(R.mipmap.ic_collect);
                                            flag = true;
                                        } else {
                                            showToast("收藏失败");
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.i("Debug", "-->" + e.getMessage());
                                        showToast("网络错误");
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    }


                } else {
                    //没有提醒登录
                    showToast("请先登录");
                }

            }
        });

        helper.ll_article.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(helper.getAdapterPosition(), v, helper);
                }
            }
        });


        flag = false;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void refresh(List<Article> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

}
