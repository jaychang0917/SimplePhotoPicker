package com.jaychang.widget.spp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.StringRes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;


public class SimplePhotoPicker {

  static final String EXTRA_TOOLBAR_COLOR = "EXTRA_TOOLBAR_COLOR";
  static final String EXTRA_TOOLBAR_TITLE_COLOR = "EXTRA_TOOLBAR_TITLE_COLOR";
  static final String EXTRA_STATUS_BAR_COLOR = "EXTRA_STATUS_BAR_COLOR";
  static final String EXTRA_SELECTED_BORDER_COLOR = "EXTRA_SELECTED_BORDER_COLOR";
  static final String EXTRA_SELECTED_ICON = "EXTRA_SELECTED_ICON";
  static final String EXTRA_ACTION_TEXT = "EXTRA_ACTION_TEXT";
  static final String EXTRA_LIMIT = "EXTRA_LIMIT";
  static final String EXTRA_COL_COUNT = "EXTRA_COL_COUNT";
  static final String EXTRA_IS_SINGLE_MODE = "EXTRA_IS_SINGLE_MODE";
  static final String EXTRA_IS_TITLE_CENTER = "EXTRA_IS_TITLE_CENTER";
  static final String EXTRA_TITLE = "EXTRA_TITLE";

  private int toolbarColor;
  private int toolbarTitleTextColor;
  private int statusBarColor;
  private int selectedBorderColor;
  private int selectedIcon;
  private int actionText;
  private int limit;
  private int columnCount;
  private boolean isSingleMode;
  private boolean isTitleCenter;
  private String title;

  @SuppressLint("StaticFieldLeak")
  private static SimplePhotoPicker instance;
  private Context appContext;
  private PublishSubject photoEmitter;

  private SimplePhotoPicker(Context context) {
    appContext = context;

    int primaryColor = android.R.color.background_dark;
    toolbarColor = primaryColor;
    toolbarTitleTextColor = android.R.color.white;
    statusBarColor = primaryColor;
    selectedBorderColor = R.color.npp_border;
    selectedIcon = R.drawable.ic_photo_selected;
    actionText = R.string.spp_done;
    limit = -1;
    columnCount = 3;
    isSingleMode = false;
    isTitleCenter = false;
    title = appContext.getString(R.string.spp_all_photos);
  }

  public static synchronized SimplePhotoPicker with(Context context) {
    if (instance == null) {
      instance = new SimplePhotoPicker(context.getApplicationContext());
    }
    return instance;
  }

  static SimplePhotoPicker getInstance() {
    return instance;
  }

  public SimplePhotoPicker toolbarColor(@ColorRes int toolbarColor) {
    this.toolbarColor = toolbarColor;
    return this;
  }

  public SimplePhotoPicker toolbarTitleTextColor(@ColorRes int toolbarTitleTextColor) {
    this.toolbarTitleTextColor = toolbarTitleTextColor;
    return this;
  }

  public SimplePhotoPicker statusBarColor(@ColorRes int statusBarColor) {
    this.statusBarColor = statusBarColor;
    return this;
  }

  public SimplePhotoPicker selectedBorderColor(@ColorRes int selectedBorderColor) {
    this.selectedBorderColor = selectedBorderColor;
    return this;
  }

  public SimplePhotoPicker selectedIcon(@DrawableRes int selectedIcon) {
    this.selectedIcon = selectedIcon;
    return this;
  }

  public SimplePhotoPicker actionText(@StringRes int actionText) {
    this.actionText = actionText;
    return this;
  }

  public SimplePhotoPicker title(@StringRes int title) {
    this.title = appContext.getString(title);
    return this;
  }

  public SimplePhotoPicker title(String title) {
    this.title = title;
    return this;
  }

  public SimplePhotoPicker limit(@IntRange(from = 1) int limit) {
    this.limit = limit;
    return this;
  }

  public SimplePhotoPicker columnCount(@IntRange(from = 2) int columnCount) {
    this.columnCount = columnCount;
    return this;
  }

  public SimplePhotoPicker isTitleCenter(boolean isTitleCenter) {
    this.isTitleCenter = isTitleCenter;
    return this;
  }

  public Observable<Uri> pickSinglePhotoFromAlbum() {
    isSingleMode = true;
    photoEmitter = PublishSubject.create();
    startGalleryActivity();
    return photoEmitter;
  }

  public Observable<List<Uri>> pickMultiPhotosFromAlbum() {
    isSingleMode = false;
    photoEmitter = PublishSubject.create();
    startGalleryActivity();
    return photoEmitter;
  }

  private void startGalleryActivity() {
    Intent intent = new Intent(appContext, GalleryActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.putExtra(EXTRA_TOOLBAR_COLOR, toolbarColor);
    intent.putExtra(EXTRA_TOOLBAR_TITLE_COLOR, toolbarTitleTextColor);
    intent.putExtra(EXTRA_STATUS_BAR_COLOR, statusBarColor);
    intent.putExtra(EXTRA_SELECTED_BORDER_COLOR, selectedBorderColor);
    intent.putExtra(EXTRA_ACTION_TEXT, actionText);
    intent.putExtra(EXTRA_SELECTED_ICON, selectedIcon);
    intent.putExtra(EXTRA_COL_COUNT, columnCount);
    intent.putExtra(EXTRA_IS_SINGLE_MODE, isSingleMode);
    intent.putExtra(EXTRA_IS_TITLE_CENTER, isTitleCenter);
    intent.putExtra(EXTRA_TITLE, title);
    intent.putExtra(EXTRA_LIMIT, limit);
    appContext.startActivity(intent);
  }

  public Observable<Uri> takePhotoFromCamera() {
    isSingleMode = false;
    photoEmitter = PublishSubject.create();
    Intent intent = new Intent(appContext, CameraHiddenActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    appContext.startActivity(intent);
    return photoEmitter;
  }

  void onPhotoPicked(Uri uri) {
    if (photoEmitter != null) {
      Uri copy = Uri.fromFile(new File(uri.getPath()));
      photoEmitter.onNext(copy);
      photoEmitter.onComplete();
    }
  }

  void onPhotosPicked(List<Uri> uris) {
    if (photoEmitter != null) {
      List<Uri> copy = new ArrayList<>(uris);
      photoEmitter.onNext(copy);
      photoEmitter.onComplete();
    }
  }

  void onError(Throwable throwable) {
    if (photoEmitter != null) {
      Throwable copy = new Throwable(throwable);
      photoEmitter.onError(copy);
    }
  }

}
