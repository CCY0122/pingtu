package com.example.pingtu;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by Administrator on 2016/8/14.
 * Point返回的x、y单位是px   屏幕宽高1080*1920
 */
public class ScreenSizeUtil {
    public static Point ScreenWandH(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        Log.d("ccy","screen"+String.valueOf(point.x)+"---"+String.valueOf(point.y));
        return point;
    }

    //px转dip、dp
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
