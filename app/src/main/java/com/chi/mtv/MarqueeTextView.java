package com.chi.mtv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 跑马灯
 * Created by askilledhand on 2020/12/30
 */
@SuppressLint("AppCompatCustomView")
public class MarqueeTextView extends TextView implements Runnable {

    private static final String TAG = MarqueeTextView.class.getSimpleName();

    /** 第一次滚动默认延迟 */
    private static final int FIRST_SCROLL_DELAY_DEFAULT = 1000;
    /** 初次滚动延时 */
    private int mFirstScrollDelay;

    /** 默认滚动速度 */
    private static final int SPEED_DEFAULT = 3;
    /** 滚动速度 */
    private int mSpeed;

    /** 默认滚动次数 */
    private static final int TIMES_DEFAULT = Integer.MAX_VALUE;
    /** 滚动次数 */
    private int mTimes;

    /** 默认起始滚动位置 */
    private static final int LOCATION_DEFAULT = 1;
    /** 起始滚动位置（最左/最右） */
    private int mScrollLocation;


    /** 记录文字最新位置 */
    private int currentScrollX;
    private boolean isStop = false;

    /** 文本宽度 */
    private int textWidth;
    /** 轮播次数计数器 */
    private int counter = 0;
    private boolean isMeasure = false;
    private int color;
    private float textBaseY;
    private int scrollY;
    private Callback callback;

    public MarqueeTextView(Context context) {
        this(context, null);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs, defStyle);
    }

    private void initView(Context context, AttributeSet attrs, int defStyle) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MarqueeTextView);
        mFirstScrollDelay = typedArray.getInt(R.styleable.MarqueeTextView_scroll_first_delay, FIRST_SCROLL_DELAY_DEFAULT);
        mSpeed = typedArray.getInt(R.styleable.MarqueeTextView_scroll_speed, SPEED_DEFAULT);
        mTimes = typedArray.getInt(R.styleable.MarqueeTextView_scroll_times, TIMES_DEFAULT);
        mScrollLocation = typedArray.getInt(R.styleable.MarqueeTextView_scroll_start_location, LOCATION_DEFAULT);
        typedArray.recycle();
        setSingleLine();
        setEllipsize(null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isMeasure) { // 文字宽度只需获取一次就可以了
            Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
            //计算文字高度
            float fontHeight = fontMetrics.bottom - fontMetrics.top;
            //计算文字baseline
            scrollY = -(int) (getHeight() - fontHeight) / 2 + getPaddingTop();
            textBaseY = getHeight() - (getHeight() - fontHeight) / 2 - fontMetrics.bottom;
            Paint paint = getPaint();
            paint.setColor(color);
            canvas.drawText(getText().toString(), -getWidth(), textBaseY, paint);
            getTextWidth();
            isMeasure = true;
        } else {
            super.onDraw(canvas);
        }
    }

    /**
     * 获取文字宽度
     */
    private void getTextWidth() {
        Paint paint = this.getPaint();
        String str = this.getText().toString();
        textWidth = (int) paint.measureText(str);
        if (mScrollLocation == 1) {
            currentScrollX = -getWidth();
        }
        scrollTo(currentScrollX, scrollY);
    }

    @Override
    public void run() {
        if (textWidth > 0) {
            currentScrollX += mSpeed; // 滚动速度
            scrollTo(currentScrollX, scrollY);
            if (isStop) {
                callback.finish();
                return;
            }
            if (getScrollX() >= textWidth) {
                counter++;
                if (counter == mTimes) {
                    scrollTo(0, scrollY);
                    callback.finish();
                    return;
                }
                scrollTo(-getWidth(), scrollY);
                currentScrollX = -getWidth();
            }
        }
        postDelayed(this, 20);
    }

    public void startScroll(Callback callback) {
        this.callback = callback;
        isStop = false;
        this.removeCallbacks(this);
        postDelayed(this, mFirstScrollDelay);
    }

    public void stopScroll() {
        isStop = true;
    }

    /**
     * 设置开始滚动延时
     * @param delay 延时ms
     */
    public void setFirstScrollDelay(int delay) {
        this.mFirstScrollDelay = delay;
    }

    /**
     * 设置滚动速度
     * @param speed 速度
     */
    public void setSpeed(int speed) {
        this.mSpeed = speed;
    }

    /**
     * 设置滚动次数
     * @param count 次数
     */
    public void setRepeatTime(int count) {
        this.mTimes = count;
    }

    /**
     * 设置文字开始滚动位置
     * @param location 0 for left && 1 for right
     */
    public void setScrollLocation(int location) {
        this.mScrollLocation = location;
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        this.color = color;
    }

    public interface Callback {
        void finish();
    }
}