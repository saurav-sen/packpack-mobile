package com.pack.pack.application.data.cache;

import android.graphics.Bitmap;

import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.PackAttachmentType;

import org.apache.http.entity.mime.content.ContentBody;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Saurav on 28-02-2017.
 */
public class PackAttachmentsCache {

    public static final PackAttachmentsCache INSTANCE = new PackAttachmentsCache();

    private Map<String, ContentBody> attachmentIdVsVideoMap = new HashMap<String, ContentBody>();

    private Map<String, Bitmap> attachmentIdVsBitmap = new HashMap<String, Bitmap>();

    private Map<String, List<JPackAttachment>> successfullyUploadedAttachmentsMap = new HashMap<String, List<JPackAttachment>>();

    private Map<String, List<JPackAttachment>> uploadInProgressAttachmentsMap = new HashMap<String, List<JPackAttachment>>();

    private Map<String, String> inProgressVssuccessfulUploadAttachmentsMap = new HashMap<String, String>();

    private PackAttachmentsCache() {

    }

    private void load() {

    }

    private void save() {

    }

    public void addUploadInProgressAttachment(JPackAttachment attachment, String packId) {
        List<JPackAttachment> list = uploadInProgressAttachmentsMap.get(packId);
        if(list == null) {
            list = new LinkedList<JPackAttachment>();
            uploadInProgressAttachmentsMap.put(packId, list);
        }
        list.add(attachment);
        save();
    }

    public List<JPackAttachment> getUploadInProgressAttachments(String packId) {
        return uploadInProgressAttachmentsMap.get(packId);
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
        save();
    }

    public void removeSelectedAttachmentPhoto(String attachmentId) {
        attachmentIdVsBitmap.remove(attachmentId);
        save();
    }

    public List<JPackAttachment> getSuccessfullyUploadedAttachments(String packId) {
        return successfullyUploadedAttachmentsMap.get(packId);
    }

    public Map<String, String> getSuccessfulUploadVsInProgressAttachmentsMap() {
        return Collections.unmodifiableMap(inProgressVssuccessfulUploadAttachmentsMap);
    }

    public void successfullyUploadedAttachment(JPackAttachment attachment, String packId, String inProgressAttachmentId) {
        List<JPackAttachment> list = successfullyUploadedAttachmentsMap.get(packId);
        if(list == null) {
            list = new LinkedList<JPackAttachment>();
            successfullyUploadedAttachmentsMap.put(packId, list);
        }
        list.add(attachment);
        list = uploadInProgressAttachmentsMap.get(packId);
        if(list == null) {
            return;
        }
        inProgressVssuccessfulUploadAttachmentsMap.put(inProgressAttachmentId, attachment.getId());
        Iterator<JPackAttachment> itr = list.iterator();
        while(itr.hasNext()) {
            JPackAttachment inProgressAttachment = itr.next();
            if(inProgressAttachment.getId().equals(inProgressAttachmentId)) {
                itr.remove();;
            }
        }
        if(attachment.getMimeType().equals(PackAttachmentType.IMAGE.name())) {
            removeSelectedAttachmentPhoto(inProgressAttachmentId);
        } else if(attachment.getMimeType().equals(PackAttachmentType.VIDEO.name())) {
            removeSelectedAttachmentVideo(inProgressAttachmentId);
        }
        save();
    }

    public void removeFromCacheOfSuccessfullyUploadedAttachment(String packId, JPackAttachment attachment) {
        List<JPackAttachment> list = successfullyUploadedAttachmentsMap.get(packId);
        if(list == null) {
            return;
        }
        Iterator<JPackAttachment> itr = list.iterator();
        while(itr.hasNext()) {
            JPackAttachment item = itr.next();
            if(item.getId().equals(attachment.getId())) {
                itr.remove();
            }
        }
        save();
    }
}
