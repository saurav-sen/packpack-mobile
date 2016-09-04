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
import com.pack.pack.application.image.loader.DownloadImageTask;
import com.pack.pack.model.web.JUser;

/**
 * Created by Saurav on 03-09-2016.
 */
public class ProfilePicturePreference extends Preference {

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
        JUser user = AppController.getInstance().getUser();
        if(user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()) {
            new DownloadImageTask(profile_picture_pref, getContext()).execute(user.getProfilePictureUrl());
        }
       else {
            profile_picture_pref.setImageResource(R.drawable.default_profile_picture);
        }
    }
}
