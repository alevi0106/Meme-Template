package com.teenengineer.memetemplate;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import java.io.ByteArrayOutputStream;

public class HomeActivity extends AppCompatActivity {
    LinearLayoutManager mLayoutManager; //for sorting
    SharedPreferences mSharedPref; //for saving sort settings
    RecyclerView mRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    AdView mAdView;
    ImageButton add_image_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("01A52AC1C81DB7346FE6FCABE563A6B0").build();
        mAdView.loadAd(adRequest);
        //set title
        mSharedPref = getSharedPreferences("SortSettings", MODE_PRIVATE);
        String mSorting = mSharedPref.getString("Sort", "newest"); //where if no settingsis selected newest will be default

        if (mSorting.equals("newest")) {
            mLayoutManager = new LinearLayoutManager(this);
            //this will load the items from bottom means newest first
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
        } else if (mSorting.equals("oldest")) {
            mLayoutManager = new LinearLayoutManager(this);
            //this will load the items from bottom means oldest first
            mLayoutManager.setReverseLayout(false);
            mLayoutManager.setStackFromEnd(false);
        }

        if ( ConnectionUtils.isConnected(this)){
            //RecyclerView
            mRecyclerView = findViewById(R.id.recyclerView);
            mRecyclerView.setHasFixedSize(true);

            //set layout as LinearLayout
            mRecyclerView.setLayoutManager(mLayoutManager);

            //send Query to FirebaseDatabase
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mRef = mFirebaseDatabase.getReference("Data");
            add_image_btn = findViewById(R.id.add_image);
            add_image_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectImage(HomeActivity.this);
                }
            });
        }
        else{
            Intent intent = new Intent(HomeActivity.this, InternetActivity.class);
            startActivity(intent);
            finish();
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

    private void firebaseSearch(String searchText) {

        //convert string entered in SearchView to lowercase
        String query = searchText.toUpperCase();
//        Query firebaseSearchQuery = mRef.orderByChild("search").startAt("%${query}%").endAt(query + "\uf8ff");
        Query firebaseSearchQuery = mRef.orderByChild("search").startAt(query).endAt(query + "\uf8ff");

        FirebaseRecyclerAdapter<Model, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Model, ViewHolder>(
                        Model.class,
                        R.layout.row,
                        ViewHolder.class,
                        firebaseSearchQuery
                ) {
                @Override
                    protected void populateViewHolder(ViewHolder viewHolder, Model model, int position) {
                        viewHolder.setDetails(getApplicationContext(), model.getTitle(), model.getDescription(), model.getImage());
                    }

                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                        ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);

                        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                try {
                                    //Views
                                    TextView mTitleTv = view.findViewById(R.id.rTitleTv);
                                    //TextView mDescTv = view.findViewById(R.id.rDescriptionTv);
                                    ImageView mImageView = view.findViewById(R.id.rImageView);
                                    //get data from views
                                    String mTitle = mTitleTv.getText().toString();
                                    //String mDesc = mDescTv.getText().toString();
                                    Drawable mDrawable = mImageView.getDrawable();

                                    Bitmap mBit = ((BitmapDrawable) mDrawable).getBitmap();
                                    Bitmap mBitmap = getResizedBitmap(mBit, 500);
                                    //pass this data to new activity
                                    Intent intent = new Intent(view.getContext(), ImageEditor.class);
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    byte[] bytes = stream.toByteArray();
                                    intent.putExtra("image", bytes); //put bitmap image as array of bytes
                                    intent.putExtra("title", mTitle); // put title
                                    //intent.putExtra("description", mDesc); //put description
                                    startActivity(intent); //start activity
                                }
                                catch(Exception e){
                                    Toast.makeText(HomeActivity.this, "Wait for image to load!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {
                                //TODO do your own implementaion on long item click
                            }
                        });

                        return viewHolder;
                    }


                };

        //set adapter to recyclerview
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }


    //load data into recycler view onStart
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Model, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Model, ViewHolder>(
                        Model.class,
                        R.layout.row,
                        ViewHolder.class,
                        mRef
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, Model model, int position) {
                        viewHolder.setDetails(getApplicationContext(), model.getTitle(), model.getDescription(), model.getImage());
                    }

                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                        ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);

                        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                try{
                                    //Views
                                    TextView mTitleTv = view.findViewById(R.id.rTitleTv);
                                    //TextView mDescTv = view.findViewById(R.id.rDescriptionTv);
                                     ImageView mImageView = view.findViewById(R.id.rImageView);
                                    //get data from views
                                    String mTitle = mTitleTv.getText().toString();
                                    //String mDesc = mDescTv.getText().toString();
                                    Drawable mDrawable = mImageView.getDrawable();
                                    Bitmap mBit = ((BitmapDrawable) mDrawable).getBitmap();
                                    Bitmap mBitmap = getResizedBitmap(mBit, 500);
                                    //pass this data to new activity
                                    Intent intent = new Intent(view.getContext(), ImageEditor.class);
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    byte[] bytes = stream.toByteArray();
                                    intent.putExtra("image", bytes); //put bitmap image as array of bytes
                                    intent.putExtra("title", mTitle); // put title
                                    //intent.putExtra("description", mDesc); //put description
                                    startActivity(intent); //start activity
                                }
                                catch(Exception e){
                                    Toast.makeText(HomeActivity.this, "Wait for image to load!", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onItemLongClick(View view, int position) {
                                //TODO do your own implementaion on long item click
                            }
                        });

                        return viewHolder;
                    }

                };

        //set adapter to recyclerview
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the menu; this adds items to the action bar if it present
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Filter as you type
                firebaseSearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //handle other action bar item clicks here
        if (id == R.id.action_sort) {
            //display alert dialog to choose sorting
            showSortDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSortDialog() {
        //options to display in dialog
        String[] sortOptions = {" Newest", " Oldest"};
        //create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort by") //set title
                .setIcon(R.drawable.ic_action_sort) //set icon
                .setItems(sortOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position of the selected item
                        // 0 means "Newest" and 1 means "oldest"
                        if (which == 0) {
                            //sort by newest
                            //Edit our shared preferences
                            SharedPreferences.Editor editor = mSharedPref.edit();
                            editor.putString("Sort", "newest"); //where 'Sort' is key & 'newest' is value
                            editor.apply(); // apply/save the value in our shared preferences
                            recreate(); //restart activity to take effect
                        } else if (which == 1) {
                            {
                                //sort by oldest
                                //Edit our shared preferences
                                SharedPreferences.Editor editor = mSharedPref.edit();
                                editor.putString("Sort", "oldest"); //where 'Sort' is key & 'oldest' is value
                                editor.apply(); // apply/save the value in our shared preferences
                                recreate(); //restart activity to take effect
                            }
                        }
                    }
                });
        builder.show();
    }

    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
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
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        Bitmap mBitmap = getResizedBitmap(selectedImage, 720);
//                        imageView.setImageBitmap(selectedImage);
                        Intent intent = new Intent(HomeActivity.this, ImageEditor.class);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] bytes = stream.toByteArray();
                        intent.putExtra("image", bytes); //put bitmap image as array of bytes
                        intent.putExtra("title", "Custom Meme"); // put title
                        //intent.putExtra("description", mDesc); //put description
                        startActivity(intent); //start activity
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
//                                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                Bitmap bmpImage = BitmapFactory.decodeFile(picturePath);
                                Bitmap mBitmap = getResizedBitmap(bmpImage, 720);
                                Intent intent = new Intent(HomeActivity.this, ImageEditor.class);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] bytes = stream.toByteArray();
                                intent.putExtra("image", bytes); //put bitmap image as array of bytes
                                intent.putExtra("title", "Custom Meme"); // put title
                                //intent.putExtra("description", mDesc); //put description
                                startActivity(intent); //start activity
                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you sure want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                HomeActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.show();
    }
}
