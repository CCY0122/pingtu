package com.example.pingtu;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/8/14.
 */
public class ImageBean {
    public Bitmap mBitmap;
    public int x;
    public int y;
    public int px;
    public int py;

    public ImageBean(int x, int y, Bitmap bm) {
        this.x = x;
        this.y = y;
        this.px = x;
        this.py = y;
        this.mBitmap = bm;
    }
    public ImageBean(){

    }
    public Boolean isCorrect(){
        if(this.px == this.x && this.py == this.y){
            return true;
        }
        return  false;
    }
}
