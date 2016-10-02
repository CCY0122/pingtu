package com.example.pingtu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView[][] smallImage;
    private Bitmap bigBm;
    private GridLayout gridLayout;
    private int screenX;
    private int screenY;//屏幕宽高，dp
    private ImageView emptyImage; //空图保存位
    private int state; //点击小图与空图相对位置
    private Boolean isStart = false; //是否开始标志位，在随机打乱时使用
    private int degree =30; //难度，即随机打乱次数
    private int count=0; //计步
    private TextView countView;
    private TextView bestView;
    private int best;

    private Intent intent;
    private int type=3;
    private int type_x=3;
    private int type_y=5;//默认type为3以及对应3*5  //2016-9-9 已更改， 默认值没有意义
    private int ImageResId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_main);
        initData();
        countView = (TextView) findViewById(R.id.count);
        bestView = (TextView) findViewById(R.id.best);
        SharedPreferences share = getSharedPreferences(""+type,0);
        best =share.getInt("best",999);
        bestView.setText(""+best);

        smallImage =new ImageView[type_x][type_y];
        Point point = ScreenSizeUtil.ScreenWandH(this);//point.x和point.y返回的单位是px
        screenX = ScreenSizeUtil.px2dip(this,point.x);
        screenY = ScreenSizeUtil.px2dip(this,point.y);
//        Log.d("ccy","px2dip---"+String.valueOf(screenX)+"---"+String.valueOf(screenY));

        int smallWidth = bigBm.getWidth() / type_y;
        int smallHeight = bigBm.getHeight() / type_x;   //小方块三行五列

        //将大图切成对应数量小图 存在smallImage里
        for (int i = 0; i <smallImage.length ; i++) {
            for(int j =0; j <smallImage[0].length ; j++){
                smallImage[i][j] = new ImageView(this); //可以不加这句么？ 答 不可以，会报空指针异常
                Bitmap bm = Bitmap.createBitmap(bigBm,j*smallWidth , i*smallHeight , smallWidth ,smallHeight);
                smallImage[i][j].setImageBitmap(bm);
                smallImage[i][j].setPadding(1,1,1,1);

                smallImage[i][j].setTag(new ImageBean(i,j,bm));
                smallImage[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       Boolean flag= isNearEmpty((ImageView) v);
//                        Toast.makeText(MainActivity.this,""+flag,Toast.LENGTH_SHORT).show();
//                        Log.d("cc","is"+flag+state);
                        if(flag && isStart){
                            count++;
                            countView.setText(""+count);
                            changeImageWithEmpty((ImageView)v);
                        }
                    }
                });

            }
        }
        //将小图放置到gridlayout上
        gridLayout = (GridLayout) findViewById(R.id.grid_view_pingtu);
        gridLayout.setColumnCount(type_y);
        gridLayout.setRowCount(type_x);
//        Log.d("ccy",String.valueOf(params.width)+"---"+String.valueOf(params.height));
        float scale = (float)(point.x/type_y)/smallWidth;
        for (int i = 0; i <smallImage.length ; i++) {
            for (int j = 0; j < smallImage[0].length; j++) {
               RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(point.x/type_y,(int)(smallHeight*scale));
               smallImage[i][j].setScaleType(ImageView.ScaleType.FIT_XY);
                gridLayout.addView(smallImage[i][j],params);

            }
        }
        setEmptyImage(smallImage[type_x-1][type_y-1]);
        randomImage();
    }

    private void initData() {
        intent = getIntent();
        int id = intent.getIntExtra("Id",0);
        if(id==1){
            String path = intent.getStringExtra("picPath");
            bigBm =decodeBitmapByFile(path,400,400);
        }else {
            ImageResId = intent.getIntExtra("imageId", R.drawable.a);
            bigBm = decodeBitmap(ImageResId, 400, 600);
        }

        type = intent.getIntExtra("type",2);
        switch (type){
            case 1: type_x=2;type_y=2;degree=30;break;
            case 2: type_x=3;type_y=3;degree=50;break;
            case 3: type_x=4;type_y=3;degree=70;break;
            case 4: type_x=4;type_y=4;degree=90;break;
        }
    }

    private Bitmap decodeBitmapByFile(String path, int requestW, int requestH) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);
        int width = options.outWidth;
        int height = options.outHeight;
        Log.d("ccy","optionBy相册or拍照"+String.valueOf(width)+"---"+String.valueOf(height)); //单位为dip
        options.inSampleSize = 1;
        if(width > requestW || height > requestH){
            Math.ceil((double)((float)width/(float)requestW));
            int xScale =Math.round((float)width/(float)requestW);
            int yScale =Math.round((float)width/(float)requestW); //用float除更准确，例10/7=1(用int除的结果，等于没缩放
            options.inSampleSize =xScale>yScale ? xScale:yScale ;
            Log.d("ccy",String.valueOf(options.inSampleSize));
        }
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path,options);
    }

    public Bitmap decodeBitmap(int imageId ,int requestW , int requestH){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(),imageId , options );
        int width = options.outWidth;
        int height = options.outHeight;
        Log.d("ccy","option"+String.valueOf(width)+"---"+String.valueOf(height)); //单位为dip
        options.inSampleSize = 1;
        if(width > requestW || height > requestH){
            Math.ceil((double)((float)width/(float)requestW));
            int xScale =Math.round((float)width/(float)requestW);
            int yScale =Math.round((float)width/(float)requestW); //用float除更准确，例10/7=1(用int除的结果，等于没缩放
            options.inSampleSize =xScale>yScale ? xScale:yScale ;
            Log.d("ccy",String.valueOf(options.inSampleSize));
        }
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(getResources(),imageId , options);
    }



    public void setEmptyImage(ImageView i){
        i.setImageBitmap(null);
        emptyImage = i;
    }

    /**
     * state  1左 2右 3上 4下
     * @param i
     * @return
     */
    public boolean isNearEmpty(ImageView i){
        ImageBean empty = (ImageBean) emptyImage.getTag();
        ImageBean current = (ImageBean) i.getTag();
        if(empty.x == current.x && empty.y == current.y+1){ //点击的图在空图的左边
            state = 1;
            return true;
        }else if(empty.x == current.x && empty.y == current.y-1){ //右
            state=2;
            return true;
        }else if(empty.x == current.x+1 && empty.y == current.y){//上
            state=3;
            return true;
        }else if(empty.x == current.x-1 && empty.y == current.y){//下
            state=4;
            return true;
        }
        return false;
    }

    public void changeImageWithEmpty(ImageView i){
            ImageBean empty = (ImageBean) emptyImage.getTag();
            ImageBean current = (ImageBean) i.getTag();
            ImageBean z = new ImageBean();
            z.mBitmap = empty.mBitmap;
            z.px = empty.px;
            z.py = empty.py;
            empty.mBitmap = current.mBitmap;
            empty.px = current.px;
            empty.py = current.py;
            emptyImage.setImageBitmap(current.mBitmap);
            current.mBitmap = z.mBitmap;
            current.px = z.px;
            current.py = z.py;
            setEmptyImage(i);
       if(isFinished() && isStart){
           Toast.makeText(MainActivity.this,"用了"+count+"步完成",Toast.LENGTH_SHORT).show();
           if(best>count){
               SharedPreferences.Editor editor = getSharedPreferences(""+type,0).edit();
               editor.putInt("best",count);
               editor.commit();
               bestView.setText(""+count);
           }
           count=0;
           isStart=false;
       }
    }

    public void randomImage(){
        isStart = false;
        int x,y,random;
        ImageView image;
        for (int i = 0; i <degree ; i++) {
            ImageBean empty = (ImageBean)emptyImage.getTag();
            x = empty.x;  y = empty.y;
            int new_x = x, new_y = y;
            random =(int)(4*Math.random())+1;
//            Log.d("cc",""+random);
            switch (random){
                case 1:
                    new_y = y-1;break;
                case 2:
                    new_y = y+1;break;
                case 3:
                    new_x = x-1;break;
                case 4:
                    new_x = x+1;break;
            }
            if(new_x>=0 && new_x<smallImage.length && new_y>=0 && new_y<smallImage[0].length){
                changeImageWithEmpty(smallImage[new_x][new_y]);
            }
        }
        isStart = true;
    }
    public Boolean isFinished(){
        for (int i = 0; i <smallImage.length ; i++) {
            for (int j =0 ; j < smallImage[0].length ; j++){
                ImageBean z = (ImageBean) smallImage[i][j].getTag();
//                Log.d("cc",""+z.isCorrect());
                if(!z.isCorrect()){
                    return false;
                }
            }
        }
        ImageBean empty = (ImageBean) emptyImage.getTag();
        smallImage[type_x-1][type_y-1].setImageBitmap(empty.mBitmap);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.restart:
                randomImage();
                count=0;break;
            case R.id.easy:
                degree=25;
                randomImage();
                count=0;break;
            case R.id.hard:
//                degree=75;
//                randomImage();
                Toast.makeText(this,"谢谢夸奖",Toast.LENGTH_SHORT).show();
                 break;
            case R.id.normal_:
                degree=50;
                randomImage();
                count=0;break;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
