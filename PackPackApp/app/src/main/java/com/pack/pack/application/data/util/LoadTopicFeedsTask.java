package com.pack.pack.application.data.util;

import android.content.Context;
import android.util.Log;

import com.pack.pack.application.AppController;
import com.pack.pack.application.data.cache.RssFeedCache;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.Pagination;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Saurav on 23-08-2017.
 */
public class LoadTopicFeedsTask extends AbstractNetworkTask<String, Integer, Pagination<JPackAttachment>> {

    private String pageLink;

    private String errorMsg;

    private static final String LOG_TAG = "LoadTopicFeedsTask";

    private String topicId;

    public LoadTopicFeedsTask(Context context, String topicId) {
        super(false, false, context, false);
        this.topicId = topicId;
    }

    @Override
    protected String getFailureMessage() {
        return errorMsg;
    }

    @Override
    protected Pagination<JPackAttachment> executeApi(API api) throws Exception {
        if(pageLink == null) {
            pageLink = "FIRST_PAGE";
        }
        fireOnPreStart();
        Pagination<JPackAttachment> page = null;
        try {
            page = (Pagination<JPackAttachment>)api.execute();
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
            errorMsg = "Oops! Something went wrong";
            fireOnFailure(errorMsg);
        }
        return page;
    }

    @Override
    protected String getContainerIdForObjectStore() {
        return null;
    }

    @Override
    protected COMMAND command() {
        return COMMAND.GET_ALL_SHARED_FEEDS_TO_TOPIC;
    }

    @Override
    protected Map<String, Object> prepareApiParams(String inputObject) {
        Map<String, Object> apiParams = new HashMap<String, Object>();
        pageLink = inputObject;
        String userId = AppController.getInstance().getUserId();
        apiParams.put(APIConstants.User.ID, userId);
        apiParams.put(APIConstants.Topic.ID, topicId);
        apiParams.put(APIConstants.PageInfo.PAGE_LINK, pageLink);
        return apiParams;
    }
}
