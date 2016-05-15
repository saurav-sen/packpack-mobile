package com.pack.pack.application.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.pack.pack.application.image.loader.DownloadImageTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CipherCloud on 12-05-2016.
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
            new DownloadImageTask(imageView, 90, 100).execute(imageUrl.trim());
        }
        return imageView;
    }
}
