package com.pack.pack.application.adapters;

import android.content.Context;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pack.pack.application.R;

/**
 * Created by Saurav on 01-07-2017.
 */
public class LandingPageGridAdapter extends BaseAdapter {

    private Context mContext;
    private final String[] web;
    private final int[] Imageid;

    public LandingPageGridAdapter(Context c,String[] web,int[] Imageid ) {
        mContext = c;
        this.Imageid = Imageid;
        this.web = web;
    }

    @Override
    public int getCount() {
        return web.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            grid = new View(mContext);
            grid = inflater.inflate(R.layout.landing_page_grid, null);
            //TextView landing_grid_text = (TextView) grid.findViewById(R.id.landing_grid_text);
            ImageView landing_grid_image = (ImageView)grid.findViewById(R.id.landing_grid_image);
            //landing_grid_text.setText(web[position]);
            landing_grid_image.setImageResource(Imageid[position]);

            /*landing_grid_image.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
                        float X = event.getX(0) - event.getX(1);
                        float Y = event.getY(0) - event.getY(1);

                        float newDistance = (float) Math.sqrt(X*X + Y*Y);
                    }
                    return false;
                }
            });*/
        } else {
            grid = (View) convertView;
        }

        return grid;
    }
}
