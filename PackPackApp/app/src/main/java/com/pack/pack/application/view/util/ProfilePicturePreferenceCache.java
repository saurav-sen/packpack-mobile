package com.pack.pack.application.view.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.pack.pack.application.image.loader.DownloadImageTask;
import com.pack.pack.model.web.JUser;

/**
 * Created by Saurav on 16-09-2016.
 */
public class ProfilePicturePreferenceCache {

    private Bitmap profilePicture;

    public static final ProfilePicturePreferenceCache INSTANCE = new ProfilePicturePreferenceCache();

    private ProfilePicturePreferenceCache() {
    }

    public Bitmap getProfilePicture() {
        return profilePicture;
    }

    public void downloadUserProfilePicutre(Context context, JUser user) {
        new DownloadImageTask(null, context).execute(user.getProfilePictureUrl());
    }

    public void uploadUserProfilePicture(Context context, JUser user, Bitmap profilePicture) {
        this.profilePicture = profilePicture;
    }
}
