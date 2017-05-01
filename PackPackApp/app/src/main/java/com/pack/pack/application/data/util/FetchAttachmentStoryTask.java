package com.pack.pack.application.data.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.pack.pack.application.AppController;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Saurav on 30-04-2017.
 */
public class FetchAttachmentStoryTask extends AbstractNetworkTask<String, Integer, String> {

    private String errorMsg;
    private Activity context;

    private ProgressDialog progressDialog;

    private static final String LOG_TAG = "FetchAttachmentStory";

    public FetchAttachmentStoryTask(Activity context, IAsyncTaskStatusListener listener) {
        super(false, false, false, context, false, true);
        this.context = context;
        addListener(listener);
    }

    @Override
    protected COMMAND command() {
        return COMMAND.GET_STORY_FROM_ATTACHMENT;
    }

    @Override
    protected Map<String, Object> prepareApiParams(String inputObject) {
        Map<String, Object> apiParams = new HashMap<String, Object>();
        String userId = AppController.getInstance().getUserId();
        apiParams.put(APIConstants.PackAttachment.ID, inputObject);
        apiParams.put(APIConstants.User.ID, userId);
        return apiParams;
    }

    @Override
    protected String executeApi(API api) throws Exception {
        String story = null;
        try {
            story = (String) api.execute();
        } catch (Exception e) {
            errorMsg = "Failed fetching story from Server";
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        return story;
    }

    @Override
    protected String getContainerIdForObjectStore() {
        return null;
    }

    @Override
    protected String getFailureMessage() {
        return errorMsg;
    }
}
