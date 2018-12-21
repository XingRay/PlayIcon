package com.leixing.playicon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

/**
 * description : xxx
 *
 * @author : leixing
 * email : leixing@baidu.com
 * @date : 2018/12/20 17:55
 */
public class PlayIconTestActivity extends Activity {
    private static final String TAG = PlayIconTestActivity.class.getSimpleName();

    private PlayIcon piIcon;
    private Context mContext;

    public static void start(Context context) {
        Intent intent = new Intent(context, PlayIconTestActivity.class);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_play_icon_test);
        initView();
    }

    private void initView() {
        piIcon = findViewById(R.id.pi_icon);
        piIcon.setFrames(10, new int[][]{
                {20, 80, 40, 90},
                {30, 90, 30, 80},
                {40, 80, 20, 70},
                {50, 70, 10, 60},
                {60, 60, 20, 50},
                {70, 50, 30, 40},
                {80, 40, 40, 30},
                {90, 30, 50, 20},
                {80, 20, 60, 30},
                {70, 30, 70, 40},
                {60, 40, 80, 50},
                {50, 50, 70, 60},
                {40, 60, 60, 70},
                {30, 70, 50, 80},
        });
        piIcon.setFrameColor(0xffff0000);
        piIcon.setPlaying(true);

        piIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestActivity.start(mContext);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState:  保存状态");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState: 恢复状态");
    }
}
