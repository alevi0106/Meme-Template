package com.teenengineer.memetemplate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class ImageEditor extends AppCompatActivity implements BotttomSheet.BottomSheetListener, BottomText.BottomTextListener {
    ImageView imView;
    AdView mAdView;
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;
    Bitmap final_image;
    FrameLayout image_editor;
    StickerTextView tv_sticker;
    StickerImageView image_sticker;
    RelativeLayout.LayoutParams params;
    ImageButton add_text_btn, add_image_btn;
    int btn_state = 0, img_btn_state = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editor);
        image_editor = findViewById(R.id.image_editor_layout);
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        imView = findViewById(R.id.imageView);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Edit");
        byte[] bytes = getIntent().getByteArrayExtra("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imView.setImageBitmap(bmp);
        final_image = ((BitmapDrawable)imView.getDrawable()).getBitmap();
        tv_sticker = new StickerTextView(ImageEditor.this);
        image_sticker = new StickerImageView(ImageEditor.this);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("01A52AC1C81DB7346FE6FCABE563A6B0").build();
        mAdView.loadAd(adRequest);
        add_text_btn = findViewById(R.id.add_text);
        add_text_btn.setOnClickListener(view -> {
            if(btn_state == 0) {
                BottomText bottomText = new BottomText();
                bottomText.show(getSupportFragmentManager(), "Bottom Text");
            }
            else if(btn_state == 1){
                btn_state = 0;
                add_text_btn.setVisibility(View.GONE);
                add_image_btn.setVisibility(View.GONE);
                tv_sticker.setControlItemsHidden(true);
                Bitmap image = Bitmap.createBitmap(image_editor.getWidth(),  image_editor.getHeight(), Bitmap.Config.RGB_565);
                image_editor.draw(new Canvas(image));
                int height = (final_image.getHeight()*imView.getWidth()/final_image.getWidth());
                int width = image.getWidth();
                int left = 0;
                if(height > imView.getHeight()){
                    height = imView.getHeight();
                }
                int top = (imView.getHeight()/2 - height/2);
                Bitmap temp = Bitmap.createBitmap(image, left, top, width, height);
                imView.setImageBitmap(temp);
                final_image = temp;
                image_editor.removeView(tv_sticker);
                add_text_btn.setVisibility(View.VISIBLE);
                add_image_btn.setVisibility(View.VISIBLE);
                add_text_btn.setImageResource(R.drawable.round_text);
            }
        });

        add_image_btn = findViewById(R.id.add_image);
        add_image_btn.setOnClickListener(view -> {
            if(img_btn_state == 0){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, WRITE_EXTERNAL_STORAGE_CODE);
                    }
                    else {
                        selectImage(ImageEditor.this);
                    }
                }
                else {
                    selectImage(ImageEditor.this);
                }
            }
            else if(img_btn_state == 1){
                img_btn_state = 0;
                add_text_btn.setVisibility(View.GONE);
                add_image_btn.setVisibility(View.GONE);
                image_sticker.setControlItemsHidden(true);
                Bitmap image = Bitmap.createBitmap(image_editor.getWidth(),  image_editor.getHeight(), Bitmap.Config.RGB_565);
                image_editor.draw(new Canvas(image));
                int height = (final_image.getHeight()*imView.getWidth()/final_image.getWidth());
                int width = image.getWidth();
                int left = 0;
                if(height > imView.getHeight()){
                    height = imView.getHeight();
                }
                int top = (imView.getHeight()/2 - height/2);
                Bitmap temp = Bitmap.createBitmap(image, left, top, width, height);
                imView.setImageBitmap(temp);
                final_image = temp;
                image_editor.removeView(image_sticker);
                add_text_btn.setVisibility(View.VISIBLE);
                add_image_btn.setVisibility(View.VISIBLE);
                add_image_btn.setImageResource(R.drawable.ic_add_image);
            }
        });

    }

    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle("Add Image");

        builder.setItems(options, (dialog, item) -> {

            if (options[item].equals("Take Photo")) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);

            } else if (options[item].equals("Choose from Gallery")) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);

            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                        assert selectedImage != null;
                        Bitmap mBitmap = getResizedBitmap(selectedImage, 600);
                        image_sticker.setControlItemsHidden(false);
                        image_sticker.setImageBitmap(mBitmap);
                        image_editor.addView(image_sticker);
                        img_btn_state = 1;
                        add_image_btn.setImageResource(R.drawable.round_ok);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                Bitmap bmpImage = BitmapFactory.decodeFile(picturePath);
                                Bitmap mBitmap = getResizedBitmap(bmpImage, 600);
                                image_sticker.setControlItemsHidden(false);
                                image_sticker.setImageBitmap(mBitmap);
                                image_editor.addView(image_sticker);
                                img_btn_state = 1;
                                add_image_btn.setImageResource(R.drawable.round_ok);
                            }
                        }

                    }
                    break;
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Discard Changes?");
        builder.setPositiveButton("Save", (dialog, id) -> {
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
        });
        builder.setNegativeButton("Discard", (dialog, id) -> {
            BotttomSheet.set_pixel_value();
            ImageEditor.super.onBackPressed();
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
                            ImageEditor.super.onBackPressed();
                        }
                    }
                    else {
                        saveImage();
                        ImageEditor.super.onBackPressed();
                    }
                    BotttomSheet.set_pixel_value();
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
            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this,"com.teenengineer.memetemplate.provider", file));
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
    public void onSeekbarChanged(int top_progress, int bottom_progress, int color) {
        byte[] bytes = getIntent().getByteArrayExtra("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.FILL);
        p.setColor(color);
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
    public void onTextAdded(String textString, int color){
        if(btn_state == 0 && !textString.matches("")){
            tv_sticker.setText(textString);
            tv_sticker.setTextColor(color);
            tv_sticker.setControlItemsHidden(false);
            String[] lines = textString.split("[\n|\r]");
            tv_sticker.setNumberOfLines(lines.length+1);
//            Toast.makeText(this,lines.toString(), Toast.LENGTH_SHORT).show();
            image_editor.addView(tv_sticker);
            btn_state = 1;
            add_text_btn.setImageResource(R.drawable.round_ok);
        }
    }
}
