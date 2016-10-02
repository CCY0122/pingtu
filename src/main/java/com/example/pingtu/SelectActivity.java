package com.example.pingtu;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/16.
 */
public class SelectActivity extends AppCompatActivity {

    public static int RESULT_IMAGE =100;
    public static int RESULT_CAMERA =200;
    public static String TEMP_IMAGE_PATH;

    private int[] imageIdList =  {R.drawable.a,R.drawable.big,R.drawable.small,R.drawable.v,R.drawable.w,R.drawable.x,R.drawable.z,R.drawable.kk
                                    ,R.drawable.jj,R.drawable.gg,R.drawable.wq,R.drawable.qw};
    private int type = 2;
    private List<Bitmap> bitmapList = new ArrayList<Bitmap>();
    private GridView gridView;
    private Button typeButton;
    private TextView typeText;
    private View contentView;//popupwindow布局
    private TextView popupType_1;
    private TextView popupType_2;
    private TextView popupType_3;
    private TextView popupType_4;
    private PopupWindow popup;
    private Button chooseFromAlbum;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.select_main);
        for (int i = 0; i <imageIdList.length ; i++) {
            Bitmap bm =decodeBitmap(imageIdList[i],120,120);
            bitmapList.add(bm);
        }
        chooseFromAlbum = (Button) findViewById(R.id.choose_from_album);
        chooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogCustom();
            }
        });
        gridView = (GridView) findViewById(R.id.picture_list);
        gridView.setAdapter(new GridAdapter(this,bitmapList));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(SelectActivity.this , MainActivity.class);
                    intent.putExtra("imageId",imageIdList[position]);
                    intent.putExtra("type",type);
                    startActivity(intent);
            }
        });
        contentView = LayoutInflater.from(this).inflate(R.layout.popup_layout,null);
        typeButton = (Button) findViewById(R.id.type_button);
        typeText = (TextView) findViewById(R.id.type_);
        popupInit();
        typeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });
    }

    private void showDialogCustom() {
        TEMP_IMAGE_PATH = Environment.getExternalStorageDirectory().getPath()+"/t.jpg";
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectActivity.this);
        builder.setTitle("选择");
        String [] choose = new String[]{"本地相册","拍照"};
        builder.setItems(choose, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    Intent intent = new Intent(Intent.ACTION_PICK);
//                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    intent.setType("image/*");
                    startActivityForResult(intent, RESULT_IMAGE);
                }else  if(which ==1){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//"android.media.action.IMAGE_CAPTURE"
                    Uri photoUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath(),"t.jpg"));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, RESULT_CAMERA);
                }
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode ==RESULT_OK){
            if(requestCode == RESULT_IMAGE && data!=null){
//                Cursor cursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,null,null,null);
//                cursor.moveToFirst();//有必要么？
//                String imagePath = cursor.getString(cursor.getColumnIndex("_data"));
                String imagePath = data.getData().getPath();
                Log.d("ccy1",""+imagePath);
                Log.d("ccy1",TEMP_IMAGE_PATH);
                Intent intent = new Intent(SelectActivity.this,MainActivity.class);
                intent.putExtra("Id",1);
                intent.putExtra("picPath",imagePath);
                intent.putExtra("type",type);
                startActivity(intent);
            }else if (requestCode == RESULT_CAMERA) {
                Intent intent = new Intent(SelectActivity.this, MainActivity.class);
                intent.putExtra("Id", 1);
                intent.putExtra("picPath", TEMP_IMAGE_PATH);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        }
    }

    private void popupInit() {
        popupType_1= (TextView) contentView.findViewById(R.id.type_1);
        popupType_2= (TextView) contentView.findViewById(R.id.type_2);
        popupType_3 = (TextView) contentView.findViewById(R.id.type_3);
        popupType_4 = (TextView) contentView.findViewById(R.id.type_4);
        popupType_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeText.setText("2*2");
                type=1;
                popup.dismiss();
            }
        });
        popupType_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeText.setText("3*3");
                type=2;
                popup.dismiss();
            }
        });
        popupType_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeText.setText("3*4");
                type=3;
                popup.dismiss();
            }
        });
        popupType_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeText.setText("4*4");
                type=4;
                popup.dismiss();
            }
        });
    }

    private void showPopup(View v) {
        popup = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setFocusable(true);
        popup.setOutsideTouchable(true);
        Drawable transpent = new ColorDrawable(Color.TRANSPARENT);
        popup.setBackgroundDrawable(transpent);
//        popup.showAsDropDown(v);
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        int height = v.getPaddingTop()+v.getHeight();
        popup.showAtLocation(v, Gravity.NO_GRAVITY,location[0],location[1]+height);

    }

    public Bitmap decodeBitmap(int imageId ,int requestW , int requestH){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(this.getResources() ,imageId , options );
        int width = options.outWidth;
        int height = options.outHeight;
//        Log.d("ccy","option"+String.valueOf(width)+"---"+String.valueOf(height)); //单位为dip
        options.inSampleSize = 1;
        if(width > requestW || height > requestW){
            Math.ceil((double)((float)width/(float)requestW));
            int xScale =Math.round((float)width/(float)requestW);
            int yScale =Math.round((float)width/(float)requestW); //用float除更准确，例10/7=1(用int除的结果，等于没缩放
            options.inSampleSize =xScale>yScale ? xScale:yScale ;
//            Log.d("ccy",String.valueOf(options.inSampleSize));
        }
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(getResources(),imageId , options);
    }
}
