package com.example.pingtu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by Administrator on 2016/8/16.
 */
public class GridAdapter extends BaseAdapter {
    private List<Bitmap> bitmapList;
    private Context mContext;

    public GridAdapter(Context context ,List<Bitmap> list ) {
        this.bitmapList = list;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return bitmapList.size();
    }

    @Override
    public Object getItem(int position) {
        return bitmapList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView item = null;
        if(convertView ==null){
            item = new ImageView(mContext);
            item.setLayoutParams(new GridView.LayoutParams(330,320));
//            item.setScaleType(ImageView.ScaleType.FIT_XY);
        }else {
            item = (ImageView) convertView;
        }
        item.setBackgroundColor(Color.BLACK);
        item.setImageBitmap(bitmapList.get(position));
        return item;
    }
}
