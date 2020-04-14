package com.example.wanandroid.fragment;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wanandroid.R;
import com.example.wanandroid.api.Api;
import com.example.wanandroid.bean.ResponseBase;
import com.example.wanandroid.interceptor.AddCookieInterceptor;
import com.example.wanandroid.interceptor.SaveCookieInterceptor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.yokeyword.fragmentation.SupportFragment;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class EditShareFragment extends BaseBackFragment {

    private Toast mToast;

    static EditShareFragment newInstance(){
        Bundle bundle=new Bundle();
        EditShareFragment editShareFragment=new EditShareFragment();
        editShareFragment.setArguments(bundle);
        return editShareFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_edit_share,container,false);
        initView(view);
        return attachToSwipeBack(view);
    }

    private void initView(View view) {
        Toolbar toolbar=view.findViewById(R.id.toolbar_detail);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _mActivity.onBackPressed();
            }
        });
        final EditText et_title=view.findViewById(R.id.et_add_share_title);
        final EditText et_link=view.findViewById(R.id.et_add_share_link);
        FloatingActionButton floatingActionButton=view.findViewById(R.id.fab_add_share_post);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title=et_title.getText().toString();
                final String link=et_link.getText().toString();
                Log.d("TAG","title:"+title);
                OkHttpClient okHttpClient=new OkHttpClient.Builder()
                        .addInterceptor(new AddCookieInterceptor(_mActivity.getApplicationContext()))
                        .build();
                final Retrofit retrofit=new Retrofit.Builder()
                        .baseUrl("https://www.wanandroid.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .client(okHttpClient)
                        .build();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        retrofit.create(Api.class)
                        .addShareArticle(title,link)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<ResponseBase>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(ResponseBase responseBase) {
                                String errorMessage=responseBase.getErrorMsg().toString();
                                Log.d("TAG","error:"+errorMessage);
                                if (errorMessage.equals("")){
                                    //跳转分享页面
                                    start(MyShareFragment.newInstance(), SupportFragment.SINGLETASK);
                                }else {
                                    showToast(errorMessage);
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

            }
        });

    }


    private void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(_mActivity, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        hideSoftInput();
    }
}
