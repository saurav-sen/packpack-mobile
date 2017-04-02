package com.pack.pack.application.view.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.image.loader.DownloadImageTask;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JUser;

import java.io.ByteArrayOutputStream;

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

    public void setProfilePicture(Bitmap profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void downloadUserProfilePicutre(ImageView imageView, Context context, JUser user) {
        DownloadImageTask downloadImageTask = new DownloadImageTask(imageView, 300, 300, context);
        downloadImageTask.addListener(new DownloadProfilePictureTaskListener(imageView));
        downloadImageTask.execute(user.getProfilePictureUrl());
    }

    public void uploadUserProfilePicture(Context context, Bitmap profilePicture) {
        this.profilePicture = profilePicture;
        new UploadDPTask().execute(profilePicture);
    }

    private class UploadDPTask extends AsyncTask<Bitmap, Void, JUser> {

        private static final String LOG_TAG = "UploadDPTask";
        @Override
        protected JUser doInBackground(Bitmap... bitmaps) {
            JUser user = null;
            if(bitmaps == null || bitmaps.length == 0)
                return user;
            Bitmap bitmap = bitmaps[0];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            if(data == null || data.length == 0)
                return user;
            try {
                API api = APIBuilder.create(ApiConstants.BASE_URL)
                        .setAction(COMMAND.UPLOAD_USER_PROFILE_PICTURE)
                        .setOauthToken(AppController.getInstance().getoAuthToken())
                        .addApiParam(APIConstants.User.ID, AppController.getInstance().getUserId())
                        .addApiParam(APIConstants.User.PROFILE_PICTURE, data)
                        .build();
                user = (JUser) api.execute();
            } catch (Exception e) {
                Log.i(LOG_TAG, e.getMessage(), e);
            }
            return user;
        }

        @Override
        protected void onPostExecute(JUser jUser) {
            if(jUser != null) {
                AppController.getInstance().setUser(jUser);
            }
            super.onPostExecute(jUser);
        }
    }

    private class DownloadProfilePictureTaskListener implements IAsyncTaskStatusListener {

        ImageView profilePictureImageView;

        DownloadProfilePictureTaskListener(ImageView profilePictureImageView) {
            this.profilePictureImageView = profilePictureImageView;
        }

        @Override
        public void onPreStart(String taskID) {

        }

        @Override
        public void onSuccess(String taskID, Object data) {
            if(data != null && (data instanceof Bitmap)) {
                profilePicture = (Bitmap)data;
                profilePictureImageView.setImageBitmap(profilePicture);
            }
        }

        @Override
        public void onFailure(String taskID, String errorMsg) {
            profilePictureImageView.setImageResource(R.drawable.default_profile_picture_big);
        }

        @Override
        public void onPostComplete(String taskID) {

        }
    }
}
