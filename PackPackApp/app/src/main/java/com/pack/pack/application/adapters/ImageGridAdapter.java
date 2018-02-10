package com.pack.pack.application.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.pack.pack.application.image.loader.DownloadImageTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saurav on 12-05-2016.
 */
public class ImageGridAdapter extends BaseAdapter {

    private Context mContext;

    private List<String> imageUrls;

    public ImageGridAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    public List<String> getImageUrls() {
        if(imageUrls == null) {
            imageUrls = new ArrayList<String>();
        }
        return imageUrls;
    }

    @Override
    public Object getItem(int i) {
        if(i < getCount()) {
            return imageUrls.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView = new ImageView(mContext);
        imageView.setMinimumHeight(300);
        imageView.setMinimumWidth(300);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if(i < getCount()) {
            String imageUrl = imageUrls.get(i);
            DownloadImageTask.SamplingOption samplingOption = new DownloadImageTask.SamplingOption();
            samplingOption.considerSuppliedDimensionDuringSampling = false;
            samplingOption.minimumInSampleSize = 3;
            new ThumbnailImageDownloadTask(imageView, mContext).setSamplingOption(samplingOption).execute(imageUrl.trim());
        }
        return imageView;
    }

    private class ThumbnailImageDownloadTask extends DownloadImageTask {

        private static final int IMAGE_WIDTH = 90;
        private static final int IMAGE_HEIGHT = 100;

        public ThumbnailImageDownloadTask(ImageView imageView, Context context) {
            super(imageView, IMAGE_WIDTH, IMAGE_HEIGHT, context);
        }

        @Override
        protected void setImageBitmapToImageView(Bitmap bitmap) {
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT, true);
            super.setImageBitmapToImageView(scaledBitmap);
        }
    }
}
