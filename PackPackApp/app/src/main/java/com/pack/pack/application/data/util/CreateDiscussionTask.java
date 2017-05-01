package com.pack.pack.application.data.util;

import android.os.AsyncTask;
import android.util.Log;

import com.pack.pack.application.AppController;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.EntityType;
import com.pack.pack.model.web.JDiscussion;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class CreateDiscussionTask extends AsyncTask<CreateDiscussionTask.CreateInfo, Integer, JDiscussion> {

    private String taskID;

    private static final String LOG_TAG = "CreateDiscussionTask";

    public static class CreateInfo {
        public String entityId;
        public String entityType;
        public String title = "";
        public String content = "";
        public boolean isReply;
    }

    private List<IAsyncTaskStatusListener> listeners = new LinkedList<IAsyncTaskStatusListener>();

    public CreateDiscussionTask() {
        this(null);
    }

    public CreateDiscussionTask(IAsyncTaskStatusListener listener) {
        addListener(listener);
        taskID = UUID.randomUUID().toString();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        for(IAsyncTaskStatusListener listener : listeners) {
            listener.onPreStart(taskID);
        }
    }

    public void addListener(IAsyncTaskStatusListener listener) {
        if(listener != null) {
            listeners.add(listener);
        }
    }

    @Override
    protected JDiscussion doInBackground(CreateInfo... createInfos) {
        if (createInfos == null || createInfos.length == 0)
            return null;
        JDiscussion discussion = null;
        try {
            CreateInfo createInfo = createInfos[0];
            String oAuthToken = AppController.getInstance().getoAuthToken();
            String userId = AppController.getInstance().getUserId();
            COMMAND command = null;
            String parentIdKey = null;
            boolean isReply = false;
            if (EntityType.TOPIC.name().equalsIgnoreCase(createInfo.entityType)) {
                command = COMMAND.START_DISCUSSION_ON_TOPIC;
                parentIdKey = APIConstants.Topic.ID;
            } else if (EntityType.PACK.name().equalsIgnoreCase(createInfo.entityType)) {
                command = COMMAND.START_DISCUSSION_ON_PACK;
                parentIdKey = APIConstants.Pack.ID;
            } else if (EntityType.DISCUSSION.name().equalsIgnoreCase(createInfo.entityType) && createInfo.isReply) {
                command = COMMAND.ADD_REPLY_TO_DISCUSSION;
                parentIdKey = APIConstants.Discussion.ID;
                isReply = true;
            }

            APIBuilder apiBuilder = APIBuilder.create(ApiConstants.BASE_URL).setOauthToken(oAuthToken)
                    .setAction(command)
                    .addApiParam(APIConstants.User.ID, userId)
                    .addApiParam(parentIdKey, createInfo.entityId)
                    .addApiParam(APIConstants.Discussion.TITLE, createInfo.title)
                    .addApiParam(APIConstants.Discussion.CONTENT, createInfo.content);
            if (isReply) {
                apiBuilder = apiBuilder.addApiParam(APIConstants.Discussion.TYPE, EntityType.DISCUSSION.name());
            }

            API api = apiBuilder.build();
            discussion = (JDiscussion) api.execute();
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        }
        return discussion;
    }

    @Override
    protected void onPostExecute(JDiscussion jDiscussion) {
        super.onPostExecute(jDiscussion);
        for(IAsyncTaskStatusListener listener : listeners) {
            listener.onSuccess(taskID, jDiscussion);
        }
        for(IAsyncTaskStatusListener listener : listeners) {
            listener.onPostComplete(taskID);
            listener.onSuccess(taskID, jDiscussion);
        }
        listeners.clear();
        listeners = null;
    }
}