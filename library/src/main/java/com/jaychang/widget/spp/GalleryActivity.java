package com.jaychang.widget.spp;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.ColorRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jaychang.widget.stb.SimpleToolbar;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;


public class GalleryActivity extends AppCompatActivity {

  private static final int CELL_SPACING = 2;
  private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 5001;

  private static final String[] GALLERY_PROJECTION = new String[]{
    MediaStore.Images.Media._ID,
    MediaStore.Images.Media.BUCKET_ID,
    MediaStore.Images.Media.DATE_TAKEN
  };

  private int toolbarColor;
  private int toolbarTitleTextColor;
  private int statusBarColor;
  private int selectedBorderColor;
  private GradientDrawable selectedBorderDrawable;
  private int selectedIcon;
  private int actionText;
  private int limit;
  private int columnCount;
  private boolean isSingleMode;
  private boolean isTitleCenter;
  private String title;

  private final ArrayList<Photo> selectedPhotos = new ArrayList<>();
  private GalleryCursorAdapter galleryCursorAdapter;
  private RecyclerView recyclerView;
  private SimpleToolbar toolbar;
  private TextView doneActionView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    checkPermissions();
  }

  private void checkPermissions() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
      == PackageManager.PERMISSION_GRANTED) {
      setupAfterGrantPermission();
    } else {
      ActivityCompat.requestPermissions(this,
        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
        REQUEST_WRITE_EXTERNAL_STORAGE);
    }
  }

  private void setupAfterGrantPermission() {
    setContentView(R.layout.activity_gallery);
    init();
    getLoaderManager().initLoader(0, null, new GalleryLoaderCallback());
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[],
                                         int[] grantResults) {
    if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE &&
      grantResults.length > 0 &&
      grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      setupAfterGrantPermission();
    } else {
      finish();
    }
  }

  private void init() {
    toolbarColor = getIntent().getIntExtra(SimplePhotoPicker.EXTRA_TOOLBAR_COLOR, 0);
    toolbarTitleTextColor = getIntent().getIntExtra(SimplePhotoPicker.EXTRA_TOOLBAR_TITLE_COLOR, 0);
    statusBarColor = getIntent().getIntExtra(SimplePhotoPicker.EXTRA_STATUS_BAR_COLOR, 0);
    selectedBorderColor = getIntent().getIntExtra(SimplePhotoPicker.EXTRA_SELECTED_BORDER_COLOR, 0);
    selectedIcon = getIntent().getIntExtra(SimplePhotoPicker.EXTRA_SELECTED_ICON, 0);
    actionText = getIntent().getIntExtra(SimplePhotoPicker.EXTRA_ACTION_TEXT, 0);
    limit = getIntent().getIntExtra(SimplePhotoPicker.EXTRA_LIMIT, -1);
    columnCount = getIntent().getIntExtra(SimplePhotoPicker.EXTRA_COL_COUNT, 3);
    isSingleMode = getIntent().getBooleanExtra(SimplePhotoPicker.EXTRA_IS_SINGLE_MODE, false);
    isTitleCenter = getIntent().getBooleanExtra(SimplePhotoPicker.EXTRA_IS_TITLE_CENTER, false);
    title = getIntent().getStringExtra(SimplePhotoPicker.EXTRA_TITLE);

    selectedBorderDrawable = new GradientDrawable();
    selectedBorderDrawable.setStroke(8, ContextCompat.getColor(this, selectedBorderColor));

    toolbar = (SimpleToolbar) findViewById(R.id.toolbar);
    toolbar.setTitle(title);
    toolbar.setRightText(actionText);
    if (isTitleCenter) {
      toolbar.alightTitleCenter();
    } else {
      toolbar.alightTitleLeft();
    }
    toolbar.setBackgroundColor(ContextCompat.getColor(this, toolbarColor));
    toolbar.setTitleTextColor(ContextCompat.getColor(this, toolbarTitleTextColor));
    toolbar.getLeftIconView().setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });
    doneActionView = toolbar.getRightTextView();
    doneActionView.setVisibility(isSingleMode ? View.INVISIBLE : View.VISIBLE);
    doneActionView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        notifySelectedPhotos();
      }
    });
    updateToolbar();

    if (Build.VERSION.SDK_INT >= 21) {
      getWindow().setStatusBarColor(ContextCompat.getColor(this, statusBarColor));
    }

    recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    recyclerView.setLayoutManager(new GridLayoutManager(this, columnCount));
    recyclerView.addItemDecoration(GridSpacingItemDecoration.newBuilder().spacing(CELL_SPACING).build());
    galleryCursorAdapter = new GalleryCursorAdapter(this, null);
    recyclerView.setAdapter(galleryCursorAdapter);
  }

  private void notifySelectedPhotos() {
    List<Uri> uris = Observable.fromIterable(selectedPhotos)
      .map(new Function<Photo, Uri>() {
        @Override
        public Uri apply(@NonNull Photo photo) throws Exception {
          return photo.getUri(GalleryActivity.this);
        }
      })
      .toList().blockingGet();

    SimplePhotoPicker.getInstance().onPhotosPicked(uris);

    finish();
  }

  private void notifySelectedPhoto() {
    SimplePhotoPicker.getInstance().onPhotoPicked(selectedPhotos.get(0).getUri(this));

    finish();
  }

  private class GalleryLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
      Uri baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
      String sort = MediaStore.Images.Media.DATE_TAKEN + " DESC";
      return new CursorLoader(GalleryActivity.this, baseUri, GALLERY_PROJECTION, null, null, sort);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
      galleryCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
      galleryCursorAdapter.swapCursor(null);
    }
  }

  private class GalleryCursorAdapter extends CursorRecyclerViewAdapter<PhotoViewHolder> {

    GalleryCursorAdapter(Context context, Cursor cursor) {
      super(context, cursor);
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      Context context = parent.getContext();
      View view = LayoutInflater.from(context).inflate(R.layout.cell_photo, parent, false);
      return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder viewHolder, Cursor cursor) {
      final int photoId = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
      final Uri imageUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(photoId));
      final Photo photo = new Photo(imageUri, photoId);

      Glide.with(viewHolder.itemView.getContext())
        .load(imageUri)
        .centerCrop()
        .dontAnimate()
        .into(viewHolder.photoView);

      if (isSelected(photo)) {
        viewHolder.layerView.setSelected(true);
        viewHolder.selectedIconView.setImageResource(selectedIcon);
        viewHolder.borderView.setVisibility(View.VISIBLE);
        viewHolder.borderView.setBackgroundDrawable(selectedBorderDrawable);
      } else {
        viewHolder.layerView.setSelected(false);
        viewHolder.selectedIconView.setImageResource(0);
        viewHolder.borderView.setVisibility(View.INVISIBLE);
      }

      if (isOverLimit() && !isSelected(photo)) {
        viewHolder.layerView.setEnabled(false);
      } else {
        viewHolder.layerView.setEnabled(true);
      }

      viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          if (isOverLimit() && !isSelected(photo)) {
            return;
          }

          ObjectAnimator cellAnim = AnimUtils.getReboundAnimation(viewHolder.itemView);

          if (isSelected(photo)) {
            viewHolder.layerView.setSelected(false);
            selectedPhotos.remove(photo);
            viewHolder.selectedIconView.setImageResource(0);
            viewHolder.borderView.setVisibility(View.INVISIBLE);
            cellAnim.addListener(new AnimatorListenerAdapter() {
              @Override
              public void onAnimationEnd(Animator animation) {
                if (!isOverLimit()) {
                  notifyDataSetChanged();
                }
              }
            });
            cellAnim.start();
          } else {
            viewHolder.layerView.setSelected(true);
            selectedPhotos.add(photo);
            viewHolder.selectedIconView.setImageResource(selectedIcon);
            AnimUtils.scaleIn(viewHolder.selectedIconView);
            viewHolder.borderView.setVisibility(View.VISIBLE);
            viewHolder.borderView.setBackgroundDrawable(selectedBorderDrawable);
            cellAnim.addListener(new AnimatorListenerAdapter() {
              @Override
              public void onAnimationEnd(Animator animation) {
                if (isOverLimit()) {
                  notifyDataSetChanged();
                }
              }
            });
            cellAnim.start();
          }

          if (isSingleMode) {
            notifySelectedPhoto();
            return;
          }

          updateToolbar();
        }
      });
    }

    private boolean isOverLimit() {
      return limit != -1 && !isSingleMode && selectedPhotos.size() >= limit;
    }

    private boolean isSelected(Photo photo) {
      return selectedPhotos.contains(photo);
    }
  }

  private void updateToolbar() {
    if (selectedPhotos.size() <= 0) {
      doneActionView.setEnabled(false);
      updateActionTextColor(R.color.npp_disable);
      toolbar.setTitle(title);
    } else {
      doneActionView.setEnabled(true);
      updateActionTextColor(android.R.color.white);
      if (limit != -1) {
        toolbar.setTitle(selectedPhotos.size() + " / " + limit);
      }
    }
  }

  private void updateActionTextColor(@ColorRes int color) {
    SpannableString span = new SpannableString(getString(actionText));
    int aColor = ContextCompat.getColor(this, color);
    span.setSpan(new ForegroundColorSpan(aColor), 0, span.length(), 0);
    doneActionView.setText(span);
  }

  private static class PhotoViewHolder extends RecyclerView.ViewHolder {
    ImageView photoView;
    ImageView selectedIconView;
    View borderView;
    View layerView;

    PhotoViewHolder(View itemView) {
      super(itemView);
      photoView = (ImageView) itemView.findViewById(R.id.photoView);
      selectedIconView = (ImageView) itemView.findViewById(R.id.selectedIconView);
      borderView = itemView.findViewById(R.id.borderView);
      layerView = itemView.findViewById(R.id.layerView);
    }
  }

}
