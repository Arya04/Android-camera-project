package com.example.solution_color;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.library.bitmap_utilities.BitMap_Helpers;
import com.library.bitmap_utilities.BlurBuilder;
import com.library.bitmap_utilities.ManipBitmap;

import java.io.File;

public class MainActivity extends AppCompatActivity  {
    private ImageView myView;
    private Bitmap bm;
    private Bitmap bm2;
    private Bitmap bm3;
    private Button button;
    private int pixHeight;
    private int pixWidth;
    private File imageFile;
    private Uri temp;
    private String image_path;
    private static final int CAM_REQUEST = 1313;
    private File mediaStorageDir;
    private int sketchiness = 50;
    private int saturation = 150;
    private String subject = "Hello";
    private String shareText = "This is my Picture";
    private Bitmap takenImage;
    private Bitmap takenImage2;
    private Uri bmpUri;
    private Uri bmpUri2;
    private Uri bmpUri3;
    private Bitmap currentBm;
    private SharedPreferences myPreference;


    public final String APP_TAG = "MyCustomApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myView = (ImageView)findViewById(R.id.imageView);
        button = (Button)findViewById(R.id.button);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        pixHeight = metrics.heightPixels;
        pixWidth = metrics.widthPixels;



        button.setOnClickListener(new buttonTakePicture());

        getMyPrefs();


    }

    private void getMyPrefs(){
        myPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sub = myPreference.getString("Share Subject",subject);
        String text = myPreference.getString("Share Text",shareText);
      //  int ske = myPreference.getInt("Sketchiness",sketchiness);
       // int sat = myPreference.getInt("Saturation",saturation);

    }

    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private String mySubString;
    private String myTextString;
    private int newSketch;
    private int newSat;
    public void doPrefChangeListener(View view){
        listener = new SharedPreferences.OnSharedPreferenceChangeListener(){

            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Toast.makeText(MainActivity.this, "key=" + key,Toast.LENGTH_SHORT).show();
                if(key.equals("Share Subject")){
                     mySubString = myPreference.getString("Share Subject","Nothing Found");
                    Toast.makeText(MainActivity.this, "from listener share subject=" + mySubString,Toast.LENGTH_SHORT).show();
                    subject = mySubString;

                }
                else if(key.equals("Share Text")){
                    myTextString = myPreference.getString("Share Text","Nothing Found)");
                    shareText = myTextString;

                }
                else if(key.equals("Sketchiness")){
                    newSketch = myPreference.getInt("Sketchiness",50);
                    sketchiness = newSketch;
                }
                else if(key.equals("Saturation")){
                    newSat = myPreference.getInt("Saturation",150);
                    saturation = newSat;
                }
            }
        };
        myPreference.registerOnSharedPreferenceChangeListener(listener);

    }




    protected void onActivityResult(int requestCode, int resultCode, Intent data){
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {

                     takenImage = BitmapFactory.decodeFile(temp.getPath());
                     takenImage2 = BitmapFactory.decodeFile(temp.getPath());
                    // Load the taken image into a preview

                    //rotate Image because otherwise it rotates on it's own.
                   // bm = rotateImage(takenImage, 90);
                   // bm2 = rotateImage(takenImage2,90);
                    bm = takenImage;
                    bm2 = takenImage2;

                    myView.setImageBitmap(bm);

                } else { // Result was a failure
                    Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                }
            }

        }
    public Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }
    public Uri getPhotoFileUri(String fileName){
             mediaStorageDir = new File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(APP_TAG, "failed to create directory");
            }
            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
        }

    class buttonTakePicture implements Button.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent camIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

            temp = getPhotoFileUri(photoFileName);


            camIntent.putExtra(MediaStore.EXTRA_OUTPUT,temp);


            if (camIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(camIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void sendShare(){

        Intent emailIntent1 = new Intent(     android.content.Intent.ACTION_SEND);
        emailIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        currentBm = ((BitmapDrawable)myView.getDrawable()).getBitmap();

        if(currentBm == bm){
            String pathofBmp = MediaStore.Images.Media.insertImage(getContentResolver(), bm,"title", null);
            bmpUri = Uri.parse(pathofBmp);
            emailIntent1.putExtra(Intent.EXTRA_STREAM, bmpUri);
        }
        else if(currentBm == bm2){
            String pathofBmp2 = MediaStore.Images.Media.insertImage(getContentResolver(), bm2,"title2", null);
            bmpUri2 = Uri.parse(pathofBmp2);
            emailIntent1.putExtra(Intent.EXTRA_STREAM, bmpUri2);
        }
        else{
            String pathofBmp3 = MediaStore.Images.Media.insertImage(getContentResolver(), bm3,"title3", null);
            bmpUri3 = Uri.parse(pathofBmp3);
            emailIntent1.putExtra(Intent.EXTRA_STREAM, bmpUri3);
        }

        // emailIntent1.putExtra(Intent.EXTRA_STREAM, bmpUri);      //regular
       // emailIntent1.putExtra(Intent.EXTRA_STREAM, bmpUri2);     //Color
       // emailIntent1.putExtra(Intent.EXTRA_STREAM, bmpUri3);     //BW

        emailIntent1.putExtra(Intent.EXTRA_SUBJECT,subject);
        emailIntent1.putExtra(Intent.EXTRA_TEXT, shareText);
        emailIntent1.setType("image/png");

        startActivity(emailIntent1);




    }


    public void doSettings(){
        Intent intentSettings = new Intent(this,SettingsActivity.class);


        startActivity(intentSettings);


    }

    public void makeBW(){
        bm3 = BitMap_Helpers.thresholdBmp(bm,sketchiness);
        myView.setImageBitmap(bm3);
    }
    public void makeColor(){
        bm3 = BitMap_Helpers.thresholdBmp(bm, sketchiness);
        bm2 = BitMap_Helpers.colorBmp(bm2, saturation);
        BitMap_Helpers.merge(bm2, bm3);
        myView.setImageBitmap(bm2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.settings:
                doSettings();
                doPrefChangeListener(myView);
                return true;
            case R.id.reset:
               // Toast.makeText(this,"reset goes here", Toast.LENGTH_SHORT).show();
                myView.setImageResource(R.drawable.gutters);
                return true;
            case R.id.share:
                if(bm != null){
                    sendShare();
                }
                else{
                    Toast.makeText(this,"nothing to share", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.make_Color:
                if(bm != null){
                    makeColor();
                }
                else{
                    Toast.makeText(this,"need picture to colorize", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.b_w:
                if(bm != null){
                    makeBW();
                }
                else{
                    Toast.makeText(this,"need picture to make black & white", Toast.LENGTH_SHORT).show();
                }
                return true;
        }


        //all else fails let super handle it
        return super.onOptionsItemSelected(item);

    }


}

