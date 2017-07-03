package com.pack.pack.application.data.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;

import com.pack.pack.application.AppController;
import com.pack.pack.application.adapters.TopicDetailAdapter;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.db.PaginationInfo;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.Pagination;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadPackTask extends AbstractNetworkTask<ParcelableTopic, Integer, Pagination<JPack>> {

    private Activity activity;

    private TopicDetailAdapter adapter;

    private ProgressDialog progressDialog;

    public LoadPackTask(Activity activity, TopicDetailAdapter adapter) {
        super(true, true, activity, false);
        this.activity = activity;
        this.adapter = adapter;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showProgressDialog();
    }

    @Override
    protected Map<String, Object> prepareApiParams(ParcelableTopic inputObject) {
        Map<String, Object> apiParams = new HashMap<String, Object>();
        String userId = AppController.getInstance().getUserId();
        apiParams.put(APIConstants.User.ID, userId);
        apiParams.put(APIConstants.Topic.ID, inputObject.getTopicId());
        apiParams.put(APIConstants.Topic.CATEGORY, inputObject.getTopicCategory());
        return apiParams;
    }

    @Override
    protected Pagination<JPack> executeApi(API api) throws Exception {
        return (Pagination<JPack>) api.execute();
    }

    @Override
    protected COMMAND command() {
        return COMMAND.GET_ALL_PACKS_IN_TOPIC;
    }

    @Override
    protected String getFailureMessage() {
        return "Failed to load details";
    }

    @Override
    protected String getContainerIdForObjectStore() {
        return getInputObject().getTopicId();
    }

    @Override
    protected String getPaginationContainerId() {
        return getInputObject().getTopicId() + "::Packs";
    }

    @Override
    protected String getPaginationContainerClassName() {
        return JTopic.class.getName();
    }

    @Override
    protected Pagination<JPack> doRetrieveFromDB(SQLiteDatabase readable, ParcelableTopic inputObject) {
        Pagination<JPack> page = null;
        List<JPack> packs = DBUtil.loadAllJsonModelByContainerId(readable, inputObject.getTopicId(), JPack.class);
        if(packs != null && !packs.isEmpty()) {
            PaginationInfo paginationInfo = DBUtil.loadPaginationInfo(readable, inputObject.getTopicId());
            page = new Pagination<JPack>();
            page.setResult(packs);
            if(paginationInfo != null) {
                page.setNextLink(paginationInfo.getNextLink());
                page.setPreviousLink(paginationInfo.getPreviousLink());
            }
        }
        return page;
    }

    @Override
    protected void onPostExecute(Pagination<JPack> jPackPagination) {
        super.onPostExecute(jPackPagination);
        if(jPackPagination != null) {
            List<JPack> packs = jPackPagination.getResult();
            if(packs != null && !packs.isEmpty()) {
                adapter.setPacks(packs);
                adapter.notifyDataSetChanged();
            }
        }
        hideProgressDialog();
    }

    private void showProgressDialog() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }
        });
    }

    private void hideProgressDialog() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        });
    }
}