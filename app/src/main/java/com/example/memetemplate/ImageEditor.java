package com.example.memetemplate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ImageEditor extends AppCompatActivity implements BotttomSheet.BottomSheetListener, BottomText.BottomTextListener {
    ImageView imView;
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;
    Bitmap final_image;
    TextView[] tv = new TextView[100];
    static int tv_id = 0;
    RelativeLayout layout;
    RelativeLayout.LayoutParams params;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editor);
        layout = findViewById(R.id.image_editor_layout);
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        imView = findViewById(R.id.imageView);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Edit");
        byte[] bytes = getIntent().getByteArrayExtra("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imView.setImageBitmap(bmp);
        final_image = ((BitmapDrawable)imView.getDrawable()).getBitmap();

        ImageButton add_text_btn = findViewById(R.id.add_text);
        add_text_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomText bottomText = new BottomText();
                bottomText.show(getSupportFragmentManager(), "Bottom Text");
            }
        });

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Discard Changes?");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){
                        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        //show popup to grant permission
                        requestPermissions(permission, WRITE_EXTERNAL_STORAGE_CODE);
                    }
                    else {
                        //permission already granted, save image
                        saveImage();
                    }
                }
                else {
                    //System os is < marshmallow, save image
                    saveImage();
                }
                BotttomSheet.set_pixel_value();
                ImageEditor.super.onBackPressed();
            }
        });
        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                BotttomSheet.set_pixel_value();
                ImageEditor.super.onBackPressed();
            }
        });
        builder.show();
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
            return true;
        case R.id.save_image:
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Download?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                            String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permission, WRITE_EXTERNAL_STORAGE_CODE);
                        }
                        else {
                            saveImage();
                        }
                    }
                    else {
                        saveImage();
                    }
                    ImageEditor.super.onBackPressed();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            builder.show();
            return true;
        case R.id.share_image:
            shareImage();
            return true;
    }
        return(super.onOptionsItemSelected(item));
    }

    private void shareImage() {
        try {
            //get title and description and save in string s
            String s = getIntent().getStringExtra("title");

            File file = new File(getExternalCacheDir(), "sample.png");
            FileOutputStream fOut = new FileOutputStream(file);
            final_image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true,false);
            //intent to share image and text
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_TEXT, s); // put the text
            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this,"com.example.memetemplate.provider", file));
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent, "Share via"));
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImage() {
        //time stamp, for image name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(System.currentTimeMillis());
        //path to external storage
        File path = Environment.getExternalStorageDirectory();
        //create folder named "Firebase"
        File dir = new File(path+"/MemeTemplate/");
        dir.mkdirs();
        //image name
        String imageName = timeStamp + ".PNG";
        File file = new File(dir, imageName);
        OutputStream out;
        try {
            out = new FileOutputStream(file);
            final_image.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Toast.makeText(this, imageName+" saved to"+ dir, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            //failed saving image
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case WRITE_EXTERNAL_STORAGE_CODE:{
                //if request code is cancelled the result arrays are empty
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    //permission is granted, save image
                    saveImage();
                }
                else {
                    //permission denied
                    Toast.makeText(this, "enable permission to save image", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @Override
    public void onSeekbarChanged(int top_progress, int bottom_progress) {
        byte[] bytes = getIntent().getByteArrayExtra("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//        BitmapDrawable drawable = (BitmapDrawable)imView.getDrawable();
//        Bitmap bmp = drawable.getBitmap();
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.WHITE);
        top_progress *= 10;
        bottom_progress *= 10;
        Bitmap temp = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight()+top_progress+bottom_progress, Bitmap.Config.RGB_565);
        Canvas tCanvas = new Canvas(temp);
        tCanvas.drawBitmap(bmp, 0, top_progress,null);
        tCanvas.drawRoundRect(new RectF(0, 0, bmp.getWidth(), top_progress),2,2,p);
//        tCanvas.drawBitmap(bmp,0,0,null);
        tCanvas.drawRoundRect(new RectF(0, bmp.getHeight()+top_progress, bmp.getWidth(), bmp.getHeight()+top_progress+bottom_progress),2,2,p);
        imView.setImageBitmap(temp);
        final_image = temp;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void onTextAdded(String textString){
        tv_id += 1;
        tv[tv_id] = new TextView(ImageEditor.this);
        tv[tv_id].setText(textString);
        tv[tv_id].setTextSize((float) 20);
        tv[tv_id].setLayoutParams(params);
        tv[tv_id].setTypeface(Typeface.DEFAULT_BOLD);
//        tv[tv_id].setPadding(20, 50, 20, 50);
        //tv[tv_id].setGravity(Gravity.Left);
        tv[tv_id].setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        layout.addView(tv[tv_id]);
        tv[tv_id].setOnTouchListener(new View.OnTouchListener() {
            float lastX = 0, lastY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case (MotionEvent.ACTION_DOWN):
                        lastX = event.getX();
                        lastY = event.getY();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getX() - lastX;
                        float dy = event.getY() - lastY;
                        float finalX = v.getX() + dx;
                        float finalY = v.getY() + dy + v.getHeight();
                        v.setX(finalX);
                        v.setY(finalY);
                        break;
                }
                return true;
            }
        });
    }
}
