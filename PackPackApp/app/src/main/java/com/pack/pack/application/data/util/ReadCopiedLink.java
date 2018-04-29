package com.pack.pack.application.data.util;

import android.content.Context;

import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.squill.feed.web.model.JRssFeed;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Saurav on 31-08-2017.
 */
public class ReadCopiedLink extends AbstractNetworkTask<String, Integer, JRssFeed> {

    private String errorMsg;

    public ReadCopiedLink(Context context, IAsyncTaskStatusListener listener) {
        super(false, false, false,context, false, true);
        addListener(listener);
    }

    @Override
    protected COMMAND command() {
        return COMMAND.CRAWL_FEED;
    }

    @Override
    protected String getFailureMessage() {
        return errorMsg;
    }

    @Override
    protected JRssFeed executeApi(API api) throws Exception {
        JRssFeed feed = null;
        try {
            feed = (JRssFeed) api.execute();
        } catch (Exception e) {
            errorMsg = "Failed reading from external link";
        }
        return feed;
    }

    @Override
    protected String getContainerIdForObjectStore() {
        return null;
    }

    @Override
    protected Map<String, Object> prepareApiParams(String inputObject) {
        Map<String, Object> apiParams = new HashMap<String, Object>();
        apiParams.put(APIConstants.ExternalResource.RESOURCE_URL, inputObject);
        return apiParams;
    }
}
