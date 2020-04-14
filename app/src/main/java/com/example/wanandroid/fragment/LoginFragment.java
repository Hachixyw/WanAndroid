package com.example.wanandroid.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.wanandroid.R;
import com.example.wanandroid.api.Api;
import com.example.wanandroid.bean.User;
import com.example.wanandroid.interceptor.SaveCookieInterceptor;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginFragment extends BaseBackFragment {



    private EditText et_username;
    private EditText et_password;

    private static final String TAG="Debug";
    private static final String LOGIN_PREF="login_state";
    private OnLoginListener onLoginListener;


    private View.OnClickListener mOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //获取输入到的账号密码
            String username = et_username.getText().toString();
            String passwrod = et_password.getText().toString();
            if (username.equals("")&& passwrod.equals("")){
                Toast.makeText(_mActivity,"用户名密码不能为空",
                        Toast.LENGTH_SHORT).show();
            }else if (username.equals("")){
                Toast.makeText(_mActivity,"用户名不能为空",
                        Toast.LENGTH_SHORT).show();
            }else if (passwrod.equals("")){
                Toast.makeText(_mActivity,"密码不能为空",
                        Toast.LENGTH_SHORT).show();
            }else {
                sendRequest(username, passwrod);
            }
        }
    };

    public static LoginFragment newInstance(){
        Bundle bundle=new Bundle();
        LoginFragment loginFragment=new LoginFragment();
        loginFragment.setArguments(bundle);
        return loginFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_login,container,false);
        setTranslucent(_mActivity);
        et_username=view.findViewById(R.id.et_username);
        et_password=view.findViewById(R.id.et_password);
        Button btn_login=view.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(mOnClickListener);
        return attachToSwipeBack(view);
    }

    private void sendRequest(final String username, final String passwrod){


        new Thread(new Runnable() {
            @Override
            public void run() {
                //持久化响应Cookie
                OkHttpClient okHttpClient=new OkHttpClient.Builder()
                        .addInterceptor(new SaveCookieInterceptor(_mActivity.getApplicationContext()))
                        .build();

                Retrofit retrofit=new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .baseUrl("https://www.wanandroid.com/")
                        .client(okHttpClient)
                        .build();
                retrofit.create(Api.class)
                        .userLogin(username,passwrod)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<User>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(User user) {
                                String msg=user.getErrorMsg();
                                if (msg.equals("")){
                                    //跳转回首页面,在首页面中需要重新绘制
                                    SharedPreferences sharedPreferences=_mActivity.getSharedPreferences(LOGIN_PREF
                                            , Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor=sharedPreferences.edit();
                                    editor.putBoolean("state",true);
                                    editor.putString("username",username);
                                    editor.apply();
                                    onLoginListener.onLoginSuccess(username);
                                    start(MainFragment.newInstance());
                                }else if (msg.equals("账号密码不匹配！")){
                                    Toast.makeText(_mActivity,"账号密码不匹配!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i(TAG,"-->"+e.getMessage());
                                Toast.makeText(_mActivity, "网络错误", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        }).start();
    }

    /**
     * 设置状态栏透明
     *
     */
    private static void setTranslucent(Activity activity){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup rootView=(ViewGroup) ((ViewGroup)activity.findViewById(android.R.id.content))
                    .getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginListener){
            onLoginListener= (OnLoginListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onLoginListener=null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        hideSoftInput();
    }

    public interface OnLoginListener{
        void onLoginSuccess(String account);
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        _mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        hideSoftInput();
    }


}
