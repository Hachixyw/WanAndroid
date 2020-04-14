package com.example.wanandroid.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.example.wanandroid.R;

public class LoadingView extends LinearLayout {

    private ShapeView shapeView;
    private View shadowView;
    private int translationDistance=0;
    private final long ANIMATION_DURATION=500;
    private boolean isStopAnimator=false;


    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        translationDistance=dip2px(80);
        initLayout();
    }

    public int dip2px(int dip){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dip,getResources().getDisplayMetrics());
    }

    private void initLayout(){
        inflate(getContext(), R.layout.layout_loadding,this);

        shapeView=findViewById(R.id.shape_view);
        shadowView=findViewById(R.id.shadow_view);

        post(new Runnable() {
            @Override
            public void run() {
                startFallAnimator();
            }
        });
    }

    private void startFallAnimator() {
        if (isStopAnimator){
            return;
        }
        ObjectAnimator translationAnimator=ObjectAnimator.ofFloat(shapeView,"translationY",0,translationDistance);
        ObjectAnimator shadowAnimator=ObjectAnimator.ofFloat(shadowView,"scaleX",1.0f,0.5f);

        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.setDuration(ANIMATION_DURATION);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.playTogether(translationAnimator,shadowAnimator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                shapeView.exchangeShape();
                startUpShape();
            }
        });
        animatorSet.start();


    }

    private void startUpShape() {
        if (isStopAnimator){
            return;
        }

        ObjectAnimator shapeAnimator=ObjectAnimator.ofFloat(shapeView,"translationY",translationDistance,0);
        ObjectAnimator shadowAnimator=ObjectAnimator.ofFloat(shadowView,"scaleX",0.5f,1.0f);

        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.setDuration(ANIMATION_DURATION);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.playTogether(shapeAnimator,shadowAnimator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startFallAnimator();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                startRotationAnimator();
            }
        });
        animatorSet.start();
    }

    private void startRotationAnimator() {
        if (isStopAnimator){
            return;
        }

        ObjectAnimator rotationAnimator=null;
        switch (shapeView.getCurrentShape()){
            case Circle:
            case Square:
                rotationAnimator=ObjectAnimator.ofFloat(shapeView,"rotation",0,180);
                break;
            case Triangle:
                rotationAnimator=ObjectAnimator.ofFloat(shapeView,"rotation",0,-120);
                break;
        }
        rotationAnimator.setDuration(ANIMATION_DURATION);
        rotationAnimator.setInterpolator(new DecelerateInterpolator());
        rotationAnimator.start();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(INVISIBLE);

        shapeView.clearAnimation();
        shadowView.clearAnimation();

        ViewGroup root= (ViewGroup) getParent();
        if (root!=null){
            root.removeView(this);
            removeAllViews();
        }

        isStopAnimator=true;
    }
}
