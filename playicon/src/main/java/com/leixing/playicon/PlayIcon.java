package com.leixing.playicon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * description : xxx
 *
 * @author : leixing
 * email : leixing@baidu.com
 * @date : 2018/12/20 17:24
 */
public class PlayIcon extends View {

    private static final String TAG = PlayIcon.class.getSimpleName();
    private static final int MSG_FRAME = 100;

    private int mWidth;
    private int mHeight;
    private int[][] mFrames;
    private int mRectWidth;
    private long mFrameIntervalTime = 100;

    private int mFrameIndex;
    private Paint mPaint;
    private boolean mPlaying;
    private InnerHandler mHandler;

    public PlayIcon(Context context) {
        this(context, null);
    }

    public PlayIcon(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayIcon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        mHandler = new InnerHandler();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i(TAG, "onMeasure: ");
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.i(TAG, "onLayout: ");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i(TAG, "onSizeChanged "
                + "\nw:" + w
                + "\nh:" + h
                + "\noldw:" + oldw
                + "\noldh:" + oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i(TAG, "onDraw");

        if (mFrames == null) {
            return;
        }
        if (mFrames.length <= mFrameIndex) {
            return;
        }
        int[] rectHeights = mFrames[mFrameIndex];
        if (rectHeights == null) {
            return;
        }
        int distance = (mWidth - mRectWidth * rectHeights.length) / rectHeights.length + 1;

        for (int i = 0; i < rectHeights.length; i++) {
            float top = mHeight - rectHeights[i];
            float left = i * mRectWidth + distance * (i + 1);
            canvas.drawRect(left, top, left + mRectWidth, mHeight, mPaint);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mPlaying) {
            sendFrameMessage();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeFrameMessage();
    }

    public void setFrames(int rectWidth, int[][] frames) {
        mRectWidth = rectWidth;
        mFrames = frames;
        invalidate();
    }

    public void setFrameIntervalTime(long timeMills) {
        mFrameIntervalTime = timeMills;
    }

    public void setFrameColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    public void setPlaying(boolean playing) {
        mPlaying = playing;
        if (mPlaying) {
            sendFrameMessage();
        } else {
            removeFrameMessage();
        }
    }

    private void removeFrameMessage() {
        mHandler.removeMessages(MSG_FRAME);
    }

    private void sendFrameMessage() {
        Message message = mHandler.obtainMessage(MSG_FRAME);
        message.obj = new WeakReference<>(this);
        mHandler.sendMessageDelayed(message, mFrameIntervalTime);
    }

    private void onNewFrame() {
        mFrameIndex = (mFrameIndex + 1) % mFrames.length;
        invalidate();
        if (mPlaying) {
            sendFrameMessage();
        }
    }

    private static class InnerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_FRAME:
                    handleMsgFrame(msg.obj);
                    break;
                default:
            }
        }

        private void handleMsgFrame(Object obj) {
            if (!(obj instanceof WeakReference)) {
                return;
            }
            WeakReference weakReference = (WeakReference) obj;
            Object o = weakReference.get();
            if (!(o instanceof PlayIcon)) {
                return;
            }
            PlayIcon playIcon = (PlayIcon) o;
            playIcon.onNewFrame();
        }
    }
}