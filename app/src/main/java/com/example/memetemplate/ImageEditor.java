package com.example.memetemplate;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;

public class ImageEditor extends AppCompatActivity {
    ImageView imView;
    EditText captionText;
    SeekBar sbar;
    Switch swPos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editor);
        imView = findViewById(R.id.imageView);
        captionText = findViewById(R.id.captionText);
        sbar = findViewById(R.id.seekBar);
        swPos = findViewById(R.id.switchPos);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Edit");
        //set back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        byte[] bytes = getIntent().getByteArrayExtra("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imView.setImageBitmap(bmp);

        sbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                byte[] bytes = getIntent().getByteArrayExtra("image");
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Paint p = new Paint();
                p.setAntiAlias(true);
                p.setStyle(Paint.Style.FILL);
                p.setColor(Color.WHITE);
                progress *= 2;
                Bitmap temp = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight()+progress, Bitmap.Config.RGB_565);
                Canvas tCanvas = new Canvas(temp);
                tCanvas.drawBitmap(bmp, 0, progress,null);
                tCanvas.drawRoundRect(new RectF(0, 0, bmp.getWidth(), progress),2,2,p);
                imView.setImageBitmap(temp);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

//        Bitmap mutableBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
//        Canvas canvas = new Canvas(mutableBitmap);
//        imView.setImageBitmap(bmp);
    }

//    public Bitmap ResizeTextBox(Bitmap bmp, float ratio, String text){
//        int h = bmp.getHeight();
//        int rectH = (int)(h * ratio);
//        Canvas canvas = new Canvas(bmp);
//
//        return canvas;
//    }
}
