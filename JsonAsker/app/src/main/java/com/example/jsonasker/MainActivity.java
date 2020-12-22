package com.example.jsonasker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static java.lang.Thread.currentThread;

import static java.lang.Thread.currentThread;

public class MainActivity extends AppCompatActivity {
    Context context = this;
    TextView textView;
    CheckBox checkBox;
    private final String url = "http://date.jsontest.com/";
    ImageView imageView;
    Button startButton;
    Button stopButton;


    private static final int MY_PERMISSIONS_REQUEST_READ_IMAGES = 0;
//    private ImageView imageView;
//    private Button startButton;
//    private Button cancelButton;
    private String[] imagePaths;
    private Subscription subscription = null;



//    public interface JsontestApi {
////        @GET("/api/get")
////        Call<List<PostModel>> getData(@Query ("site") String siteName, @Query("name") String resourceName, @Query("num") int count);
//        @GET("/?service={choice}")
//        Call<List<PostModel>> getData(@Query("choice") String choice);     // "date" or "ip"
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_IMAGES);
        } else {
            setupButtons();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_IMAGES: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    setupButtons();
                    Log.d("MY_TAG", "PERMISSION_GRANTED");
                } else {
                    Log.d("MY_TAG", "PERMISSION_DENIED");
                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
                return;
            }
        }
        // other 'case' lines to check for other permissions this app might request
    }

    private void readImagesFromGallery() {
        Cursor imageCursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media.DATA}, null,
                null, null);
        if (imageCursor == null) {
            return;
        }
        imagePaths = new String[imageCursor.getCount()];
        for (int i = 0; i < imagePaths.length; i++) {
            imageCursor.moveToNext();
            imagePaths[i] = imageCursor.getString(0);
        }
        imageCursor.close();

    }

    private void startLoadImage() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
        Observable<String> observableImagePaths = Observable.from(imagePaths);
        subscription =
                observableImagePaths.subscribeOn(Schedulers.io()).doOnNext(s -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(imagePath -> {
                            Log.d("OBSERVER_SUCCESS", "onNext: " + imagePath);
                            loadImage(imagePath);
                        });
    }



    private void cancelLoadImages(){
        subscription.unsubscribe();
    }

    private void loadImage(String imagePath){
        try {
            Bitmap bit = BitmapFactory.decodeFile(imagePath);
            Log.d("BITMAP", "" + bit);
            Log.d("Image", "" + currentThread().getName());
            imageView.setImageBitmap(bit);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setupButtons(){
        readImagesFromGallery();
        startButton.setOnClickListener(v -> startLoadImage());
        stopButton.setOnClickListener(v -> cancelLoadImages());
    }

//    public void dateCall(View view) {
////        Response response
//        if (checkBox.isChecked()) {
//            System.out.println("NOPEEEEEEEEEEEEEEEEEE");
//
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl(url)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//
//            IpApi ipMessagesApi = retrofit.create(IpApi.class);
//            Call<IpQuery> ipMessage = ipMessagesApi.ipMessage();
//
//            ipMessage.enqueue(new Callback<IpQuery>() {
//                @Override
//                public void onResponse(Call<IpQuery> call, retrofit2.Response<IpQuery> response) {
//                    if (response.isSuccessful()) {
//                        Log.d("RESPONSE_SUCCESS", "response " + response.body());
//                        textView.setText("IP: " + response.body().getIp());
//                    } else {
//                        Log.d("FAIL", "response code " + response.code());
//                    }
//                }
//
//
//                @Override
//                public void onFailure(Call<IpQuery> call, Throwable t) {
//                    Log.d("FAIL", "Fail " + t);
//                }
//            });
//        } else {
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl(url)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//
//            DateApi dateMessageApi = retrofit.create(DateApi.class);
//            Call<DateQuery> dateMessage = dateMessageApi.dateMessage();
//
//            dateMessage.enqueue(new Callback<DateQuery>() {
//                @Override
//                public void onResponse(Call<DateQuery> call, retrofit2.Response<DateQuery> response) {
//                    if (response.isSuccessful()) {
//                        Log.d("SUCCESS", "Success " + response.body());
//                        textView.setText("Date: " + response.body().getDate());
//                    } else {
//                        Log.d("FAIL", "response code " + response.code());
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<DateQuery> call, Throwable t) {
//                    Log.d("FAIL", "Fail " + t);
//                }
//            });
//
//        }
//    }
}


