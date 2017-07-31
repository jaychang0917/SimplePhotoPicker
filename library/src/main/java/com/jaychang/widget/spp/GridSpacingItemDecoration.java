package com.jaychang.widget.spp;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

  private boolean includeEdge;
  private int spacing;

  private GridSpacingItemDecoration(Builder builder) {
    includeEdge = builder.includeEdge;
    spacing = builder.spacing;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
    int spacingDp = Utils.dpToPx(view.getContext(), spacing);
    int spanCount = gridLayoutManager.getSpanCount();

    int position = parent.getChildAdapterPosition(view); // item position
    int column = position % spanCount; // item column

    if (includeEdge) {
      outRect.left = spacingDp - column * spacingDp / spanCount; // spacingDp - column * ((1f / spanCount) * spacingDp)
      outRect.right = (column + 1) * spacingDp / spanCount; // (column + 1) * ((1f / spanCount) * spacingDp)

      if (position < spanCount) { // top edge
        outRect.top = spacingDp;
      }
      outRect.bottom = spacingDp; // item bottom
    } else {
      outRect.left = column * spacingDp / spanCount; // column * ((1f / spanCount) * spacingDp)
      outRect.right = spacingDp - (column + 1) * spacingDp / spanCount; // spacingDp - (column + 1) * ((1f /    spanCount) * spacingDp)
      if (position >= spanCount) {
        outRect.top = spacingDp; // item top
      }
    }
  }

  public static final class Builder {
    private boolean includeEdge;
    private int spacing;

    private Builder() {
    }

    public Builder includeEdge() {
      this.includeEdge = true;
      return this;
    }

    public Builder spacing(int dp) {
      spacing = dp;
      return this;
    }

    public GridSpacingItemDecoration build() {
      return new GridSpacingItemDecoration(this);
    }
  }
}