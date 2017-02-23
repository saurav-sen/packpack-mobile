package com.pack.pack.application.data.cache;

import android.graphics.Bitmap;

import com.pack.pack.application.topic.activity.model.UploadAttachmentData;
import com.pack.pack.model.web.JPackAttachment;

import org.apache.http.entity.mime.content.ContentBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Saurav on 23-02-2017.
 */
public class AppCache {

    public static final AppCache INSTANCE = new AppCache();

    private Map<String, ContentBody> attachmentIdVsVideoMap = new HashMap<String, ContentBody>();

    private Map<String, Bitmap> attachmentIdVsBitmap = new HashMap<String, Bitmap>();

    private Map<String, JPackAttachment> uploadedAttachmentsMap = new HashMap<String, JPackAttachment>();

    private AppCache() {

    }

    public ContentBody getSelectedAttachmentVideo(String attachmentId) {
        return attachmentIdVsVideoMap.get(attachmentId);
    }

    public void addSelectedAttachmentVideo(String attachmentId, ContentBody selectedGalleryVideo) {
        attachmentIdVsVideoMap.put(attachmentId, selectedGalleryVideo);
    }

    public void removeSelectedAttachmentVideo(String attachmentId) {
        attachmentIdVsVideoMap.remove(attachmentId);
    }

    public Bitmap getSelectedAttachmentPhoto(String attachmentId) {
        return attachmentIdVsBitmap.get(attachmentId);
    }

    public void addSelectedAttachmentPhoto(String attachmentId, Bitmap selectedBitmapPhoto) {
        attachmentIdVsBitmap.put(attachmentId, selectedBitmapPhoto);
    }

    public void removeSelectedAttachmentPhoto(String attachmentId) {
        attachmentIdVsBitmap.remove(attachmentId);
    }

    public JPackAttachment getSuccessfullyUploadedAttachment(String attachmentId) {
        return uploadedAttachmentsMap.get(attachmentId);
    }

    public void successfullyUploadedAttachment(JPackAttachment attachment) {
        uploadedAttachmentsMap.put(attachment.getId(), attachment);
    }

    public void removeFromCacheOfSuccessfullyUploadedAttachment(JPackAttachment attachment) {
        uploadedAttachmentsMap.remove(attachment.getId());
    }
}
