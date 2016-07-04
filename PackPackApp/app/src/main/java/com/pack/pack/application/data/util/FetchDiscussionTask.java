package com.pack.pack.application.data.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.pack.pack.application.AppController;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.db.PaginationInfo;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.EntityType;
import com.pack.pack.model.web.JDiscussion;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.Pagination;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Saurav on 04-07-2016.
 */
public class FetchDiscussionTask extends AbstractNetworkTask<ScrollableDiscussion, Integer, Pagination<JDiscussion>> {

    private String errorMsg;
    private Activity context;

    private ProgressDialog progressDialog;

    public FetchDiscussionTask(Activity context, IAsyncTaskStatusListener listener) {
        super(true, true, context);
        this.context = context;
        addListener(listener);
    }

    @Override
    protected COMMAND command() {
        if(EntityType.TOPIC.name().equalsIgnoreCase(getInputObject().entityType)) {
            return COMMAND.GET_ALL_DISCUSSIONS_FOR_TOPIC;
        } else if(EntityType.PACK.name().equalsIgnoreCase(getInputObject().entityType)) {
            return  COMMAND.GET_ALL_DISCUSSIONS_FOR_PACK;
        }
        return null;
    }

    @Override
    protected Map<String, Object> prepareApiParams(ScrollableDiscussion inputObject) {
        Map<String, Object> apiParams = new HashMap<String, Object>();
        String userId = AppController.getInstance().getUserId();
        apiParams.put(APIConstants.User.ID, userId);
        String key = null;
        if(EntityType.TOPIC.name().equalsIgnoreCase(getInputObject().entityType)) {
            key = APIConstants.Topic.ID;
        } else if(EntityType.PACK.name().equalsIgnoreCase(getInputObject().entityType)) {
            key = APIConstants.Pack.ID;
        }
        if(key != null) {
            apiParams.put(key, getInputObject().entityId);
        }
        apiParams.put(APIConstants.PageInfo.PAGE_LINK,
                inputObject.scrollUp ? inputObject.previousLink
                        : inputObject.nextLink);
        return apiParams;
    }

    @Override
    protected Pagination<JDiscussion> doRetrieveFromDB(SQLiteDatabase readable, ScrollableDiscussion inputObject) {
        Pagination<JDiscussion> page = null;
        List<JDiscussion> discussions = DBUtil.getDiscussionInfosBasedUponContainerId(readable,
                getContainerIdForObjectStore(), inputObject.entityType);
        if(discussions != null && !discussions.isEmpty()) {
            PaginationInfo paginationInfo = DBUtil.loadPaginationInfo(readable, getPaginationContainerId());
            page = new Pagination<JDiscussion>();
            page.setResult(discussions);
            if(paginationInfo != null) {
                page.setNextLink(paginationInfo.getNextLink());
                page.setPreviousLink(paginationInfo.getPreviousLink());
            }
        }
        return page;
    }

    @Override
    protected String getContainerIdForObjectStore() {
        return getInputObject().entityId;
    }

    @Override
    protected String getPaginationContainerId() {
        return getInputObject() + "::" + getInputObject().entityType + "::Discussion";
    }

    @Override
    protected String getPaginationContainerClassName() {
        if(EntityType.TOPIC.name().equalsIgnoreCase(getInputObject().entityType)) {
            return JTopic.class.getName();
        } else if(EntityType.PACK.name().equalsIgnoreCase(getInputObject().entityType)) {
            return JPack.class.getName();
        }
        return null;
    }

    @Override
    protected Pagination<JDiscussion> executeApi(API api) throws Exception {
        Pagination<JDiscussion> page = null;
        try {
            page = (Pagination<JDiscussion>) api.execute();
        } catch (Exception e) {
            errorMsg = "Failed fetching discussions from Server";
        }
        return page;
    }

    @Override
    protected String getFailureMessage() {
        return errorMsg;
    }
}
