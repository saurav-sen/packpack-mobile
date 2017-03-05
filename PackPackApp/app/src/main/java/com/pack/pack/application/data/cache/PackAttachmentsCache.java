package com.pack.pack.application.data.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;

import com.pack.pack.application.cz.fhucho.android.util.SimpleDiskCache;
import com.pack.pack.application.cz.fhucho.android.util.SimpleDiskCacheInitializer;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JPackAttachments;
import com.pack.pack.model.web.PackAttachmentType;
import com.pack.pack.services.exception.PackPackException;

import org.apache.http.entity.mime.content.ContentBody;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Saurav on 28-02-2017.
 */
public class PackAttachmentsCache {

    private Map<String, ContentBody> attachmentIdVsVideoMap = new HashMap<String, ContentBody>();

    private Map<String, Bitmap> attachmentIdVsBitmap = new HashMap<String, Bitmap>();

    //private Map<String, List<JPackAttachment>> successfullyUploadedAttachmentsMap = new HashMap<String, List<JPackAttachment>>();

    //private Map<String, List<JPackAttachment>> uploadInProgressAttachmentsMap = new HashMap<String, List<JPackAttachment>>();


    private Map<String, String> inProgressVsSuccessfulUploadAttachmentsMap;

    private SimpleDiskCache diskCache;

    private static final String LOG_TAG = "PackAttachmentsCache";

    private static final String IN_PROGRESS = "InProgress";

    private static final String InProgressVsSuccessfulUploadAttachmentsMap_KEY = "InProgressVsSuccessfulUploadAttachmentsMap";

    private static final String OLD = "OLD";

    private static PackAttachmentsCache instance;

    private static final Object lock = new Object();

    private PackAttachmentsCache() {

    }

    public static final PackAttachmentsCache open(Context context) {
        try {
            synchronized (lock) {
                if (instance == null) {
                    instance = new PackAttachmentsCache();
                }
                if (instance.diskCache == null) {
                    SimpleDiskCacheInitializer.prepare(context);
                    instance.diskCache = SimpleDiskCache.getInstance();
                }
            }
            return instance;
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private Map<String, String> getInProgressVsSuccessfulUploadAttachmentsMap() {
        if(diskCache == null) {
            throw new RuntimeException("[PackAttachmentsCache] Cache NOT loaded yet");
        }

        try {
            inProgressVsSuccessfulUploadAttachmentsMap = new HashMap<String, String>();
            SimpleDiskCache.StringEntry stringEntry = diskCache.getString(InProgressVsSuccessfulUploadAttachmentsMap_KEY);
            if(stringEntry != null) {
                String json = stringEntry.getString();
                if (json == null) {
                    return inProgressVsSuccessfulUploadAttachmentsMap;
                }
                Entries e = JSONUtil.deserialize(json, Entries.class, true);
                if (e == null) {
                    return inProgressVsSuccessfulUploadAttachmentsMap;
                }
                List<Entry> entries = e.getEntries();
                if (entries == null || entries.isEmpty()) {
                    return inProgressVsSuccessfulUploadAttachmentsMap;
                }
                Iterator<Entry> itr = entries.iterator();
                while (itr.hasNext()) {
                    Entry entry = itr.next();
                    if (entry == null) {
                        continue;
                    }
                    inProgressVsSuccessfulUploadAttachmentsMap.put(entry.getKey(), entry.getValue());
                }
                return inProgressVsSuccessfulUploadAttachmentsMap;
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        }
        return null;
    }

    private List<JPackAttachment> getAttachments(String packId, String keySuffix) {
        if(diskCache == null) {
            throw new RuntimeException("[PackAttachmentsCache] Cache NOT loaded yet");
        }

        try {
            String json = null;
            if(keySuffix != null) {
                SimpleDiskCache.StringEntry stringEntry = diskCache.getString(packId + keySuffix);
                if(stringEntry != null) {
                    json = stringEntry.getString();
                }
            } else {
                SimpleDiskCache.StringEntry stringEntry = diskCache.getString(packId);
                if(stringEntry != null) {
                    json = stringEntry.getString();
                }
            }
            if(json != null) {
                JPackAttachments c = JSONUtil.deserialize(json, JPackAttachments.class, true);
                if(c == null) {
                    return new LinkedList<JPackAttachment>();
                }
                return c.getAttachments();
            }
        } catch (IOException e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        } catch (PackPackException e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    private void removeAttachment(String packId, String keySuffix, String attachmentId) {
        List<JPackAttachment> list = getAttachments(packId, keySuffix);
        if(list == null || list.isEmpty()) {
            return;
        }
        Iterator<JPackAttachment> itr = list.iterator();
        while(itr.hasNext()) {
            JPackAttachment attachment = itr.next();
            if(attachment.getId().equals(attachmentId)) {
                itr.remove();
            }
        }
        JPackAttachments c = new JPackAttachments();
        c.setAttachments(list);
        try {
            String json = JSONUtil.serialize(c);
            if(keySuffix != null) {
                diskCache.put(packId + keySuffix, json);
            } else {
                diskCache.put(packId, json);
            }
        } catch (PackPackException e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        }
    }

    private void putAttachment(String packId, String keySuffix, JPackAttachment attachment) {
        if(diskCache == null) {
            throw new RuntimeException("[PackAttachmentsCache] Cache NOT loaded yet");
        }

        try {
            List<JPackAttachment> list = getAttachments(packId, keySuffix);
            if(list == null || list.isEmpty()) {
                list = new LinkedList<JPackAttachment>();
            }
            list.add(attachment);
            JPackAttachments c = new JPackAttachments();
            c.setAttachments(list);
            String json = JSONUtil.serialize(c);

            if(keySuffix != null) {
                diskCache.put(packId + keySuffix, json);
            } else {
                diskCache.put(packId, json);
            }
        } catch (PackPackException e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        }
    }

    public void addAttachments(String packId, List<JPackAttachment> attachments) {
        if(diskCache == null) {
            throw new RuntimeException("[PackAttachmentsCache] Cache NOT loaded yet");
        }

        if(attachments == null || attachments.isEmpty()) {
            return;
        }

        try {
            List<JPackAttachment> list = getAttachments(packId, null);
            if(list == null || list.isEmpty()) {
                list = new LinkedList<JPackAttachment>();
            }
            list.addAll(attachments);
            JPackAttachments c = new JPackAttachments();
            c.setAttachments(list);
            String json = JSONUtil.serialize(c);

            diskCache.put(packId, json);
        } catch (PackPackException e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        }
    }

    public void addUploadInProgressAttachment(JPackAttachment attachment, String packId) {
        putAttachment(packId, IN_PROGRESS, attachment);
    }

    public List<JPackAttachment> getUploadInProgressAttachments(String packId) {
        List<JPackAttachment> list = getAttachments(packId, IN_PROGRESS);
        if(list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(list);
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

    public List<JPackAttachment> getSuccessfullyUploadedAttachments(String packId) {
        List<JPackAttachment> list = getAttachments(packId, null);
        if(list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(list);
    }

    public Map<String, String> getSuccessfulUploadVsInProgressAttachmentsMap() {
        Map<String, String> map = getInProgressVsSuccessfulUploadAttachmentsMap();
        if(map == null || map.isEmpty()) {
            Collections.emptyMap();
        }
        return Collections.unmodifiableMap(map);
    }

    public void successfullyUploadedAttachment(JPackAttachment attachment, String packId, String inProgressAttachmentId) {
        putAttachment(packId, null, attachment);
        getInProgressVsSuccessfulUploadAttachmentsMap().put(inProgressAttachmentId, attachment.getId());
        removeAttachment(packId, IN_PROGRESS, inProgressAttachmentId);

        if(attachment.getMimeType().equals(PackAttachmentType.IMAGE.name())) {
            removeSelectedAttachmentPhoto(inProgressAttachmentId);
        } else if(attachment.getMimeType().equals(PackAttachmentType.VIDEO.name())) {
            removeSelectedAttachmentVideo(inProgressAttachmentId);
        }
    }

    public void removeFromCacheOfSuccessfullyUploadedAttachment(String packId, JPackAttachment attachment) {
        /*List<JPackAttachment> list = successfullyUploadedAttachmentsMap.get(packId);
        if(list == null) {
            return;
        }
        Iterator<JPackAttachment> itr = list.iterator();
        while(itr.hasNext()) {
            JPackAttachment item = itr.next();
            if(item.getId().equals(attachment.getId())) {
                itr.remove();
            }
        }*/
    }

    private class Entry {

        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    private class Entries {
        private List<Entry> entries;

        public List<Entry> getEntries() {
            if(entries == null) {
                entries = new LinkedList<Entry>();
            }
            return entries;
        }

        public void setEntries(List<Entry> entries) {
            this.entries = entries;
        }
    }
}
