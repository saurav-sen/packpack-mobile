package com.pack.pack.application.data.util;

import android.os.AsyncTask;
import android.util.Log;

import com.pack.pack.application.AppController;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JAttachmentStoryID;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Saurav on 30-04-2017.
 */
public class EditAttachmentStoryTask extends AsyncTask<EditAttachmentStoryTask.EditInfo, Integer, String> {

    private String taskID;

    private static final String LOG_TAG = "EditAttachmentStoryTask";

    private List<IAsyncTaskStatusListener> listeners = new LinkedList<IAsyncTaskStatusListener>();

    public EditAttachmentStoryTask() {
        this(null);
    }

    public EditAttachmentStoryTask(IAsyncTaskStatusListener listener) {
        addListener(listener);
        taskID = UUID.randomUUID().toString();
    }

    public void addListener(IAsyncTaskStatusListener listener) {
        if(listener != null) {
            listeners.add(listener);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        for(IAsyncTaskStatusListener listener : listeners) {
            listener.onPreStart(taskID);
        }
    }

    @Override
    protected String doInBackground(EditInfo... editInfos) {
        if (editInfos == null || editInfos.length == 0)
            return null;
        String storyId = null;
        try {
            EditInfo editInfo = editInfos[0];
            String oAuthToken = AppController.getInstance().getoAuthToken();
            // String userId = AppController.getInstance().getUserId();
            API api = APIBuilder.create(ApiConstants.BASE_URL)
                    .setOauthToken(oAuthToken)
                    .setAction(COMMAND.ADD_STORY_TO_ATTACHMENT)
                    .addApiParam(APIConstants.PackAttachment.ID, editInfo.getAttachmentId())
                    .addApiParam(APIConstants.AttachmentStory.STORY, editInfo.getContent())
                    .build();
            JAttachmentStoryID attachmentStoryID = (JAttachmentStoryID) api.execute();
            if(attachmentStoryID != null) {
                storyId = attachmentStoryID.getStoryId();
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        }
        return storyId;
    }

    @Override
    protected void onPostExecute(String story) {
        super.onPostExecute(story);
        for(IAsyncTaskStatusListener listener : listeners) {
            listener.onSuccess(taskID, story);
        }
        for(IAsyncTaskStatusListener listener : listeners) {
            listener.onPostComplete(taskID);
            listener.onSuccess(taskID, story);
        }
        listeners.clear();
    }

    public static class EditInfo {

        private String attachmentId;

        private String content;

        public EditInfo(String attachmentId, String content) {
            this.attachmentId = attachmentId;
            this.content = content;
        }

        public String getAttachmentId() {
            return attachmentId;
        }

        public void setAttachmentId(String attachmentId) {
            this.attachmentId = attachmentId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
