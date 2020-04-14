package com.example.wanandroid.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.wanandroid.R;

public class ShapeView extends View {

    private Shape mCurrentShape=Shape.Circle;
    private Paint paint;
    private Path path;

    public ShapeView(Context context) {
        this(context,null);
    }

    public ShapeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ShapeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint=new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width=MeasureSpec.getSize(widthMeasureSpec);
        int height=MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(Math.min(width,height),Math.min(width,height));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (mCurrentShape){
            case Circle:
                int center=getWidth()/2;
                paint.setColor(ContextCompat.getColor(getContext(), R.color.circle));
                canvas.drawCircle(center,center,center,paint);
                break;
            case Square:
                paint.setColor(ContextCompat.getColor(getContext(),R.color.square));
                canvas.drawRect(0,0,getWidth(),getHeight(),paint);
                break;
            case Triangle:
                paint.setColor(ContextCompat.getColor(getContext(),R.color.triangle));
                if (path==null){
                    path=new Path();
                    path.moveTo(getWidth()/2,0);
                    path.lineTo(0, (float) (getWidth()/2*Math.sqrt(3)));
                    path.lineTo(getWidth(),(float)(getWidth()/2*Math.sqrt(3)));
                    path.close();
                }
                canvas.drawPath(path,paint);
                break;
        }
    }

    public void exchangeShape(){
        switch (mCurrentShape){
            case Circle:
                mCurrentShape=Shape.Square;
                break;
            case Square:
                mCurrentShape=Shape.Triangle;
                break;
            case Triangle:
                mCurrentShape=Shape.Circle;
                break;
        }
        invalidate();
    }

    public enum Shape{
        Circle,Square,Triangle;
    }

    public Shape getCurrentShape(){
        return mCurrentShape;
    }
}


