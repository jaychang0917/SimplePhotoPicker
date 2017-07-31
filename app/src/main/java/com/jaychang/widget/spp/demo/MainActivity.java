package com.jaychang.widget.spp.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jaychang.widget.spp.SimplePhotoPicker;

public class MainActivity extends AppCompatActivity {

  private String TAG = getClass().getSimpleName();
  private ImageView imageView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Button pickPhotoButton = (Button) findViewById(R.id.pickPhotoButton);
    pickPhotoButton.setOnClickListener(view -> pickPhotosFromAlbum());

    Button takePhotoButton = (Button) findViewById(R.id.takePhotoButton);
    takePhotoButton.setOnClickListener(view -> takePhotoFromCamera());

    imageView = (ImageView) findViewById(R.id.imageView);
  }

  private void pickPhotosFromAlbum() {
    SimplePhotoPicker.with(this)
      .toolbarColor(R.color.colorPrimary)
      .statusBarColor(R.color.colorPrimary)
      .selectedBorderColor(R.color.colorPrimary)
      .selectedIcon(R.drawable.ic_add)
      .isTitleCenter(true)
      .title("Title")
      .actionText(R.string.add)
      .columnCount(3)
      .limit(6)
      .pickMultiPhotosFromAlbum()
      .subscribe(uris -> {
        Log.d(TAG, "uri size: " + uris.size());
        Log.d(TAG, "uri: " + uris.get(0));
        Glide.with(MainActivity.this).load(uris.get(0)).into(imageView);
      });
  }

  private void takePhotoFromCamera() {
    SimplePhotoPicker.with(this)
      .takePhotoFromCamera()
      .subscribe(uri -> {
        Log.d(TAG, "uri: " + uri);
        Glide.with(MainActivity.this).load(uri).into(imageView);
      });
  }

}
