package com.example.memetemplate;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

public class ImageEditor extends AppCompatActivity implements BotttomSheet.BottomSheetListener{
    ImageView imView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editor);
        imView = findViewById(R.id.imageView);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Edit");
        //set back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        byte[] bytes = getIntent().getByteArrayExtra("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imView.setImageBitmap(bmp);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.add_border:
            BotttomSheet botttomSheet = new BotttomSheet();
            botttomSheet.show(getSupportFragmentManager(), "Bottom Sheet");
            return(true);
    }
        return(super.onOptionsItemSelected(item));
    }
    @Override
    public void onTopSeekbarChanged(int progress) {
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
    public void onBottomSeekbarChanged(int progress) {
        byte[] bytes = getIntent().getByteArrayExtra("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.WHITE);
        progress *= 2;
        Bitmap temp = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight()+progress, Bitmap.Config.RGB_565);
        Canvas tCanvas = new Canvas(temp);
        tCanvas.drawBitmap(bmp, 0 ,bmp.getHeight(),null);
        tCanvas.drawRoundRect(new RectF(0, bmp.getHeight(), bmp.getWidth(), progress),2,2,p);
        imView.setImageBitmap(temp);
    }
}
