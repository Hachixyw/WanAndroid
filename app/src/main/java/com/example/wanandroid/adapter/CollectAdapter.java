package com.example.wanandroid.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.util.Log;
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
import com.example.wanandroid.bean.Collect;
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

public class CollectAdapter extends BaseQuickAdapter<Collect.DataBean.DatasBean, CollectAdapter.ViewHolder> implements LoadMoreModule {

    private Context mContext;
    private List<Collect.DataBean.DatasBean> list;
    private boolean flag;
    private Toast mToast;
    private OnItemClickListener onItemClickListener;

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
            articleTitle=itemView.findViewById(R.id.tv_articleTitle);
            author=itemView.findViewById(R.id.tv_author);
            shareUser=itemView.findViewById(R.id.tv_shareUser);
            ll_author=itemView.findViewById(R.id.ll_author);
            ll_share=itemView.findViewById(R.id.ll_share);
            iv_collect=itemView.findViewById(R.id.iv_collect);
            ll_article=itemView.findViewById(R.id.ll_article);
            tv_time=itemView.findViewById(R.id.tv_time);
        }
    }

    public CollectAdapter(Context mContext, List<Collect.DataBean.DatasBean> data) {
        super(R.layout.item_article,data);
        this.mContext = mContext;
        this.list = data;
    }

    private void showToast(String text){
        if (mToast==null){
            mToast=Toast.makeText(mContext,text,Toast.LENGTH_SHORT);
        }else {
            mToast.setText(text);
        }
        mToast.show();
    }

    public Collect.DataBean.DatasBean getCollect(int position){
        return list.get(position);
    }

    @Override
    protected void convert(@NonNull final CollectAdapter.ViewHolder viewHolder, final Collect.DataBean.DatasBean dataBean) {
        //注：源数据是Html格式的，需要进行格式转换
        viewHolder.articleTitle.setText(Html.fromHtml(dataBean.getTitle(),Html.FROM_HTML_MODE_LEGACY));
        Log.i("Debug","Title:"+dataBean.getTitle()+" Author:"+dataBean.getAuthor()
                +" id:"+dataBean.getId());
        viewHolder.iv_collect.setImageResource(R.mipmap.ic_collect);
        Log.i("Debug","isCollect:"+flag);


        if (!dataBean.getAuthor().equals("")){
            viewHolder.ll_author.setVisibility(View.VISIBLE);
            viewHolder.author.setText(dataBean.getAuthor());
        }else {
            viewHolder.ll_author.setVisibility(View.GONE);
        }


        viewHolder.tv_time.setText(""+dataBean.getNiceDate());

        viewHolder.iv_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //获取缓存
                SharedPreferences sharedPreferences=mContext.
                        getSharedPreferences("login_state",Context.MODE_PRIVATE);
                if (sharedPreferences.getBoolean("state",false)){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //请求头响应Cookie
                            OkHttpClient okHttpClient=new OkHttpClient.Builder()
                                    .addInterceptor(new AddCookieInterceptor(mContext))
                                    .build();
                            Retrofit retrofit=new Retrofit.Builder()
                                    .baseUrl("https://www.wanandroid.com/")
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                    .client(okHttpClient)
                                    .build();
                            Log.i("Debug","isCollect:"+flag);
                            //收藏过则取消收藏
                            retrofit.create(Api.class)
                                    .cancelCollectionInMyCollect(dataBean.getId(),dataBean.getOriginId())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<ResponseBase>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onNext(ResponseBase responseBase) {
                                            if (responseBase.getErrorMsg().equals("")){
                                                showToast("取消成功");
                                                viewHolder.iv_collect.setImageResource(R.mipmap.ic_no_collect);
                                                //刷新数据
                                                list.remove(dataBean);
                                                notifyDataSetChanged();
                                            }else {
                                                showToast("取消失败");
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Log.i("Debug","-->"+e.getMessage());
                                            showToast("网络错误");
                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });
                        }
                    }).start();
                }else {
                    //没有提醒登录
                    Toast.makeText(mContext,"请先登录",Toast.LENGTH_SHORT).show();
                }

            }
        });

        viewHolder.ll_article.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(viewHolder.getAdapterPosition(),v,viewHolder);
            }
        });
        flag=false;
    }

    public void refresh(List<Collect.DataBean.DatasBean> list){
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }
}
