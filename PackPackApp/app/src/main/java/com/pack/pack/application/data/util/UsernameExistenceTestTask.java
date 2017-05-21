package com.pack.pack.application.data.util;

import android.content.Context;
import android.util.Log;

import com.pack.pack.application.activity.SignupActivity;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JStatus;
import com.pack.pack.model.web.StatusType;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Saurav
 *
 */
public class UsernameExistenceTestTask extends AbstractNetworkTask<String, Integer, JStatus> {

    private static final String LOG_TAG = "UsernameExistenceTest";

    private boolean validationComplete;

    private String errorMsg;

    public boolean isValidationComplete() {
        return validationComplete;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public boolean isValidUserName() {
        return isValidUserName;
    }

    private boolean isValidUserName;

    public UsernameExistenceTestTask(Context context) {
        super(false, false, false, context, false, true);
    }

    @Override
    protected String getContainerIdForObjectStore() {
        return null;
    }

    @Override
    protected String getFailureMessage() {
        return errorMsg;
    }

    @Override
    protected COMMAND command() {
        return COMMAND.VALIDATE_USER_NAME;
    }

    @Override
    protected Map<String, Object> prepareApiParams(String inputObject) {
        Map<String, Object> apiParams = new HashMap<String, Object>();
        if(inputObject == null) {
            return apiParams;
        }
        apiParams.put(APIConstants.User.USERNAME, inputObject);
        return apiParams;
    }

    @Override
    protected JStatus executeApi(API api) throws Exception {
        JStatus status = null;
        try {
            status = (JStatus) api.execute();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            errorMsg = "Failed while validating EMail existence check.";
        } finally {
            validationComplete = true;
        }
        if(errorMsg == null) {
            isValidUserName = (status.getStatus() == StatusType.OK);
            if (!isValidUserName) {
                errorMsg = status.getInfo();
            }
        } else {
            isValidUserName = false;
        }
        return status;
    }
}