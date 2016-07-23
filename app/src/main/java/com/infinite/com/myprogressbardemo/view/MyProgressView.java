package com.infinite.com.myprogressbardemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.infinite.com.myprogressbardemo.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-07-23.
 */
public class MyProgressView extends View {
    private static final String TAG = "MyProgressView";
    private List<Integer> mDots = new ArrayList<>();
    private Paint mProgressPaint;
    private Paint mDotsPaint;
    private int mSelectedColor = 0xFF00FF44;   //已完成颜色
    private int mDotColor = 0x55333333;         //点的颜色
    private int mUnselectedColor = 0xFF0000FF; //未完成的颜色
    private float mProgressHeight = (float) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 2.0f, getResources().getDisplayMetrics());
    private float mProgressWidth = (float) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 2.0f, getResources().getDisplayMetrics());
    private float mDotRadius = mProgressHeight * 1.0f / 2;  //点的半径
    private int mAllProgress = 0;  //总进度
    private int mCurrentDot = 0;   //当前点
    private float mCurrentProgress = 0.0f;   //当前点进度
    private float mScale = 1.0f;  //总进度与宽度的比例
    private Rect mProgressTextBound = new Rect();
    private Paint mProgressTextPaint;
    private DecimalFormat df;

    public MyProgressView(Context context) {
        this(context, null);
    }

    public MyProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.MyProgressView);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.MyProgressView_progress_heigth:
                    mProgressHeight = (int) a.getDimension(attr, TypedValue
                            .applyDimension(TypedValue.COMPLEX_UNIT_SP, 2.0f,
                                    getResources().getDisplayMetrics()));
                    break;
                case R.styleable.MyProgressView_progress_width:
                    mProgressWidth = (int) a.getDimension(attr, TypedValue
                            .applyDimension(TypedValue.COMPLEX_UNIT_SP, 2.0f,
                                    getResources().getDisplayMetrics()));
                    break;


            }
        }
        a.recycle();


        df = new DecimalFormat("#");
        mDots.add(1);
        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);

        mProgressPaint.setStyle(Paint.Style.STROKE); //设置填充
        mProgressPaint.setStrokeWidth(mProgressHeight);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);

        mProgressTextPaint = new Paint();
        mProgressTextPaint.setColor(Color.RED);
        mProgressTextPaint.setTextSize(40);
        mProgressTextPaint.setAntiAlias(true);
        mProgressTextPaint.setDither(true);


        mDotsPaint = new Paint();
        mDotsPaint.setAntiAlias(true);
        mDotsPaint.setColor(mDotColor);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mProgressWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        mProgressHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        mScale = mProgressWidth * 1.0f / mAllProgress;
        float line_start = getPaddingLeft();


        //绘制总进度
        mProgressPaint.setColor(mUnselectedColor);
        canvas.drawLine(line_start, getMeasuredHeight() * 1.0f / 2, line_start + mProgressWidth, getMeasuredHeight() * 1.0f / 2, mProgressPaint);

        //绘制当前进度
        mCurrentProgress = mCurrentProgress > 100.0f ? 100.0f : mCurrentProgress;
        float end = getPaddingLeft() + mCurrentProgress * 0.01f * mProgressWidth;
        mProgressPaint.setColor(mSelectedColor);
        canvas.drawLine(line_start, getMeasuredHeight() * 1.0f / 2, end, getMeasuredHeight() * 1.0f / 2, mProgressPaint);

        //绘制进度文字
        String t = df.format(mCurrentProgress);
        mProgressTextPaint.getTextBounds(t, 0, t.length(), mProgressTextBound);
        float width = mProgressTextPaint.measureText(t);
        canvas.drawText(t, end - width / 2, getPaddingTop()+mProgressHeight + mProgressTextBound.height() , mProgressTextPaint);


        //绘制点
        float dot_start = getPaddingLeft();
        float r = mProgressHeight / 2;
        for (int i = 0, n = mDots.size(); i < n; i++) {
            if (i == n - 1) {
                break;
            }
            if (n > 1) {
                dot_start += mDots.get(i) * mScale;
                canvas.drawCircle(dot_start, getMeasuredHeight() * 1.0f / 2, r, mDotsPaint);
            }
        }
    }


    public void setProgress(float progress) {
        mCurrentProgress = progress;
        invalidateView();
    }

    /**
     * @param index    从0开始
     * @param progress
     */
    public void setProgress(int index, float progress) {
        float _progress = 0.0f;
        float _all_progress = 0.0f;
        progress = progress > 100.0f ? 100.0f : progress;
        progress = progress < 0.0f ? 0.0f : progress;
        for (int i = 0, n = mDots.size(); i < n; i++) {
            if (n <= index || index < 0) {
                throw new IndexOutOfBoundsException("IndexOutOfBoundsException,the bound is:" + n + ",current index is：" + index);
            }
            if (i == index) {
                _progress = mDots.get(i) * progress * 0.01f;
                break;
            }
            _all_progress += mDots.get(i);
        }
        mCurrentProgress = ((_progress + _all_progress) / mAllProgress) * 100;
        invalidateView();
    }

    public void setProgressDots(List<Integer> dots) {
        if (dots != null && dots.size() > 0) {
            mDots = dots;
            for (Integer num : mDots) {
                mAllProgress += num;
            }
        }
    }

    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureDimension((int) mProgressWidth + getPaddingLeft() + getPaddingRight(), widthMeasureSpec);
        int height = measureDimension((int) mProgressHeight + getPaddingTop() + getPaddingBottom(), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    public int measureDimension(int defaultSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize;   //UNSPECIFIED
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

}
