package com.pack.pack.application.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;

/**
 * Created by Saurav on 03-09-2016.
 */
public class ProfilePicturePreference extends Preference {

    //private Drawable image;

    public ProfilePicturePreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProfilePicturePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutResource(R.layout.profile_picture_preference);

        /*TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProfilePicturePreference, defStyle, 0);
        image = typedArray.getDrawable(R.styleable.ProfilePicturePreference_image);*/
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        ImageView profile_picture_pref = (ImageView) view.findViewById(R.id.profile_picture_pref);
        /*if(profile_picture_pref != null && image != null) {
            profile_picture_pref.setImageDrawable(image);
        }*/
    }

    public void setImage(Drawable image) {
        //this.image = image;
        notifyChanged();
    }
}
