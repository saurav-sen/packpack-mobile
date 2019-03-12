package in.squill.squilloffice.data.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import in.squill.squilloffice.db.SquillDbHelper;

/**
 * Created by Saurav on 26-08-2018.
 */
public abstract class AbstractDbLoadTask<X, Y, Z> extends AsyncTask<X, Y, Z> {

    private Context context;

    private boolean showProgressDialog;

    private List<IAsyncTaskStatusListener> listeners = new LinkedList<IAsyncTaskStatusListener>();

    private ProgressDialog progressDialog = null;

    private String taskID;

    private X x;

    private Z successResult;

    private boolean isSuccess = false;

    private static final String LOG_TAG = "DbLoadTask";

    public AbstractDbLoadTask(Context context, boolean showProgressDialog) {
        super();
        this.context = context;
        this.showProgressDialog = showProgressDialog;
        this.taskID = UUID.randomUUID().toString();
    }

    public AsyncTask<X, Y, Z> addListener(IAsyncTaskStatusListener listener) {
        if(listener != null) {
            listeners.add(listener);
        }
        return this;
    }

    public String getTaskID() {
        return taskID;
    }

    @Override
    protected void onPreExecute() {
        try {
            if(showProgressDialog) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Please Wait...");
                progressDialog.setIndeterminate(true);
                progressDialog.show();
            }
            fireOnPreStart();
            super.onPreExecute();
        } catch (Throwable e) {
            if(progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            throw e;
        }
    }

    protected void fireOnPreStart() {
        for(IAsyncTaskStatusListener listener : listeners) {
            listener.onPreStart(getTaskID());
        }
    }

    protected void fireOnSuccess(Object data) {
        for(IAsyncTaskStatusListener listener : listeners) {
            listener.onSuccess(getTaskID(), data);
        }
    }

    protected void fireOnFailure(String errorMsg) {
        for(IAsyncTaskStatusListener listener : listeners) {
            listener.onFailure(getTaskID(), errorMsg);
        }
    }

    protected void fireOnPostComplete() {
        for(IAsyncTaskStatusListener listener : listeners) {
            listener.onPostComplete(getTaskID());
        }
    }

    protected X getInputObject() {
        return x;
    }

    private void setInputObject(X x) {
        this.x = x;
    }

    @Override
    protected Z doInBackground(X... xes) {
        Z z = null;
        try {
            if(xes == null || xes.length == 0)
                return null;
            X x = xes[0];
            setInputObject(x);
            z = doExecuteInBackground(x);
        } finally {
            if(progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
        return z;
    }

    protected final Z doExecuteInBackground(X x) {
        SQLiteDatabase readable = null;
        try {
            readable = new SquillDbHelper(context).getReadableDatabase();
            successResult = doFetchFromDb(readable, getInputObject());
            isSuccess = true;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } finally {
            if(readable != null && readable.isOpen()) {
                readable.close();
            }
        }
        return successResult;
    }

    protected abstract Z doFetchFromDb(SQLiteDatabase readable, X inputObject);

    protected Object getSuccessResult(Z result) {
        return successResult;
    }

    protected boolean isSuccess(Z result) {
        return isSuccess;
    }

    protected abstract String getFailureMessage();

    @Override
    protected void onPostExecute(Z z) {
        super.onPostExecute(z);
        if(isSuccess(z)) {
            Object data = getSuccessResult(z);
            fireOnSuccess(data);
        }
        else {
            fireOnFailure(getFailureMessage());
        }
        fireOnPostComplete();
    }
}
