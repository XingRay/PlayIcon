package com.leixing.playicon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.Arrays;

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

    /**
     * attributes
     */
    private int[][] mFrames;
    private int mRectWidth;
    private long mFrameIntervalTime = 100;
    private boolean mPlaying;
    private int mFrameColor = 0xffffffff;

    private int mWidth;
    private int mHeight;
    private int mFrameIndex;
    private Paint mPaint;
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
        mPaint.setColor(mFrameColor);

        mHandler = new InnerHandler();
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
        mFrameColor = color;
        mPaint.setColor(mFrameColor);
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

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

    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.mFrames = mFrames;
        savedState.mFrameColor = mFrameColor;
        savedState.mFrameIntervalTime = mFrameIntervalTime;
        savedState.mPlaying = mPlaying;
        savedState.mRectWidth = mRectWidth;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        mFrames = savedState.mFrames;
        mFrameColor = savedState.mFrameColor;
        mFrameIntervalTime = savedState.mFrameIntervalTime;
        mPlaying = savedState.mPlaying;
        mRectWidth = savedState.mRectWidth;
        Log.i(TAG, "onRestoreInstanceState: mFrames" + Arrays.toString(mFrames));
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
        if (!mPlaying) {
            return;
        }
        mFrameIndex = (mFrameIndex + 1) % mFrames.length;
        invalidate();
        sendFrameMessage();
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

    private static class SavedState extends BaseSavedState {

        private int[][] mFrames;
        private int mRectWidth;
        private long mFrameIntervalTime;
        private boolean mPlaying;
        private int mFrameColor;

        SavedState(Parcelable superState) {
            super(superState);
        }

        SavedState(Parcel in) {
            super(in);
            int length = in.readInt();
            mFrames = new int[length][];
            int[] lengths = new int[length];
            in.readIntArray(lengths);
            for (int i = 0; i < length; i++) {
                int len = lengths[i];
                if (len == 0) {
                    mFrames[i] = new int[0];
                    continue;
                }
                int[] frame = new int[len];
                in.readIntArray(frame);
                mFrames[i] = frame;
            }

            this.mRectWidth = in.readInt();
            this.mFrameIntervalTime = in.readLong();
            this.mPlaying = in.readByte() != 0;
            this.mFrameColor = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            if (mFrames == null || mFrames.length == 0) {
                dest.writeInt(0);
            } else {
                dest.writeInt(mFrames.length);
                int[] lengths = new int[mFrames.length];
                for (int i = 0; i < mFrames.length; i++) {
                    int[] mFrame = mFrames[i];
                    lengths[i] = mFrame == null ? 0 : mFrame.length;
                }
                dest.writeIntArray(lengths);
                for (int[] mFrame : mFrames) {
                    if (mFrame == null || mFrame.length == 0) {
                        continue;
                    }
                    dest.writeIntArray(mFrame);
                }
            }
            dest.writeInt(this.mRectWidth);
            dest.writeLong(this.mFrameIntervalTime);
            dest.writeByte(this.mPlaying ? (byte) 1 : (byte) 0);
            dest.writeInt(this.mFrameColor);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}