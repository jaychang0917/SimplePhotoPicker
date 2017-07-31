package com.jaychang.widget.spp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class CameraHiddenActivity extends AppCompatActivity {

  private static final int RC_CAMERA = 1001;
  private static final int RC_CAMERA_PERMISSION = 1002;
  private Uri photoUri;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    checkPermissions();
  }

  private void checkPermissions() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
      == PackageManager.PERMISSION_GRANTED) {
      takePhoto();
    } else {
      ActivityCompat.requestPermissions(this,
        new String[]{Manifest.permission.CAMERA},
        RC_CAMERA_PERMISSION);
    }
  }

  public Uri takePhotoFromCamera(Activity activity, int requestCode) {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (intent.resolveActivity(activity.getPackageManager()) != null) {
      File photoFile = null;
      try {
        photoFile = createImageFile();
      } catch (IOException ex) {
        ex.printStackTrace();
      }

      if (photoFile != null) {
        try {
          Uri uri = Uri.fromFile(photoFile);
          intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
          activity.startActivityForResult(intent, requestCode);
          return uri;
        } catch (Exception e) {
          // Prevent FileUriExposedException from Android N
          e.printStackTrace();
        }
      }
    } else {
      Log.d(activity.getClass().getSimpleName(), "No camera app.");
    }

    finish();

    return null;
  }

  private File createImageFile() throws IOException {
    String imageFileName = "Image_" + UUID.randomUUID() + ".jpg";
    return new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), imageFileName);
  }

  private void takePhoto() {
    photoUri = takePhotoFromCamera(this, RC_CAMERA);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[],
                                         int[] grantResults) {
    if (requestCode == RC_CAMERA_PERMISSION &&
      grantResults.length > 0 &&
      grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      takePhoto();
    } else {
      finish();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RC_CAMERA && resultCode == RESULT_OK && photoUri != null) {
      SimplePhotoPicker.getInstance().onPhotoPicked(photoUri);
    }
    finish();
  }
}
