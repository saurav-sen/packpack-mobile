package com.pack.pack.application.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.Preference;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.activity.SettingsActivity;
import com.pack.pack.application.data.util.ImageUtil;
import com.pack.pack.application.view.util.ProfilePicturePreferenceCache;
import com.pack.pack.model.web.JUser;

import static com.pack.pack.application.AppController.CAMERA_CAPTURE_PHOTO_REQUEST_CODE;
import static com.pack.pack.application.AppController.CROP_PHOTO_REQUEST_CODE;
import static com.pack.pack.application.AppController.MEDIA_TYPE_IMAGE;

/**
 * Created by Saurav on 03-09-2016.
 */
public class ProfilePicturePreference extends Preference implements SettingsActivity.SettingsChangeListener {

    private CircleImageView profile_picture_pref;

    public ProfilePicturePreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProfilePicturePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutResource(R.layout.profile_picture_preference);
    }

    @Override
    public void onChange(String preferenceKey, Object data) {
        if(SettingsActivity.PROFILE_PICTURE_CHANGE_KEY.equals(preferenceKey)) {
            Bitmap bitmap = (Bitmap) data;
            //bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
            ProfilePicturePreferenceCache.INSTANCE.uploadUserProfilePicture(getContext(), bitmap);
        } else if(SettingsActivity.PROFILE_PICTURE_CROP_KEY.equals(preferenceKey)) {
            cropPhoto();
        }
    }

    private Uri mediaFileUri;

    private void cropPhoto() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(mediaFileUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 256);
        intent.putExtra("outputY", 256);
        intent.putExtra("return-data", true);
        ((Activity) getContext()).startActivityForResult(intent, CROP_PHOTO_REQUEST_CODE);
    }

    private void capturePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mediaFileUri = ImageUtil.getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaFileUri);
        ((Activity)getContext()).startActivityForResult(intent, CAMERA_CAPTURE_PHOTO_REQUEST_CODE);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        profile_picture_pref = (CircleImageView) view.findViewById(R.id.profile_picture_pref);
        ImageButton profile_picture_pref_camera = (ImageButton) view.findViewById(R.id.profile_picture_pref_camera);
        profile_picture_pref_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capturePhoto();
            }
        });
        JUser user = AppController.getInstance().getUser();
        if(user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()) {
            ProfilePicturePreferenceCache.INSTANCE.downloadUserProfilePicutre(profile_picture_pref, getContext(), user);
        }

        if(ProfilePicturePreferenceCache.INSTANCE.getProfilePicture() != null){
            profile_picture_pref.setImageBitmap(ProfilePicturePreferenceCache.INSTANCE.getProfilePicture());
        } else {
            profile_picture_pref.setImageResource(R.drawable.default_profile_picture_big);
        }
    }
}
