package com.pack.pack.application.data.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import com.pack.pack.application.AppController;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.db.DbObject;
import com.pack.pack.application.db.PaginationInfo;
import com.pack.pack.application.db.SquillDbHelper;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.Pagination;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Saurav on 12-06-2016.
 */
public abstract class AbstractNetworkTask<X, Y, Z> extends AsyncTask<X, Y, Z> {

    private List<IAsyncTaskStatusListener> listeners = new LinkedList<IAsyncTaskStatusListener>();

    private boolean isSuccess = false;

    private Z successResult;

    private Context context;

    private SquillDbHelper squillDbHelper;

    private boolean storeResultsInDB;

    private boolean tryRetrievingFromDB;

    private boolean updateExistingObjectInDB;

    private X x;

    public AbstractNetworkTask(boolean tryRetrievingFromDB, boolean storeResultsInDB) {
        this(tryRetrievingFromDB, storeResultsInDB, false);
    }

    public AbstractNetworkTask(boolean tryRetrievingFromDB, boolean storeResultsInDB, boolean updateExistingObjectInDB) {
        this.tryRetrievingFromDB = tryRetrievingFromDB;
        this.storeResultsInDB = storeResultsInDB;
        this.updateExistingObjectInDB = updateExistingObjectInDB;
    }

    public AbstractNetworkTask(Context context) {
        this.context = context;
        squillDbHelper = new SquillDbHelper(context);
    }

    protected Context getContext() {
        return context;
    }

    public void addListener(IAsyncTaskStatusListener listener) {
        if(listener == null)
            return;
        listeners.add(listener);
    }

    @Override
    protected void onPreExecute() {
        fireOnPreStart();
        super.onPreExecute();
    }

    protected void fireOnPreStart() {
        for(IAsyncTaskStatusListener listener : listeners) {
            listener.onPreStart();
        }
    }

    protected void fireOnSuccess(Object data) {
        for(IAsyncTaskStatusListener listener : listeners) {
            listener.onSuccess(data);
        }
    }

    protected void fireOnFailure(String errorMsg) {
        for(IAsyncTaskStatusListener listener : listeners) {
            listener.onFailure(errorMsg);
        }
    }

    protected void fireOnPostComplete() {
        for(IAsyncTaskStatusListener listener : listeners) {
            listener.onPostComplete();
        }
    }

    protected X getInputObject() {
        return x;
    }

    private void setInputObject(X x) {
        this.x = x;
    }

    @Override
    protected final Z doInBackground(X... xes) {
        if(xes == null || xes.length == 0)
            return null;
        X x = xes[0];
        setInputObject(x);
        Z z = null;
        if(tryRetrievingFromDB) {
            z = doRetrieveFromDB(squillDbHelper.getReadableDatabase(), getInputObject());
        }
        if(z == null) {
            z = doExecuteInBackground(x);
        }
        return z;
    }

    protected SQLiteOpenHelper getSquillDbHelper() {
        return squillDbHelper;
    }

    protected Z doRetrieveFromDB(SQLiteDatabase readable, X inputObject) {
        return null;
    }

    protected void doUpdateExistingInDB(SQLiteDatabase writable, X inputObject, Z outputObject) {

    }

    protected void storeResultsInDb(Object data) {
        storeResultsInDb(data, false);
    }

    protected abstract String getContainerIdForObjectStore();

    protected final void storeResultsInDb(Object data, boolean ignorePagination) {
        if(successResult == null)
            return;
        if(data instanceof Collection) {
            Collection<?> c = (Collection<?>)successResult;
            Iterator<?> itr = c.iterator();
            while(itr.hasNext()) {
                Object __obj = itr.next();
                if(__obj == null)
                    continue;
                DbObject __dbObject = DBUtil.convert(__obj, getContainerIdForObjectStore());
                if(__dbObject == null)
                    continue;
                storeResultsInDb_0(__dbObject);
            }
        } else if(data instanceof Pagination<?>) {
            Pagination<?> page = (Pagination<?>) data;
            List<?> list = page.getResult();
            if(list != null && !list.isEmpty()) {
                storeResultsInDb(list, true);
            }
            String entityId = getPaginationContainerId();
            if(entityId != null && !ignorePagination) {
                String className = getPaginationContainerClassName();
                PaginationInfo paginationInfo = new PaginationInfo();
                paginationInfo.setEntityId(entityId);
                paginationInfo.setType(className);
                paginationInfo.setNextLink(page.getNextLink());
                paginationInfo.setPreviousLink(page.getPreviousLink());
                if(checkExistence_0(paginationInfo)) {
                    boolean success = deleteExisting_0(paginationInfo);
                    if(!success)
                        return;
                }
                ContentValues contentValues = paginationInfo.toContentValues();
                String tableName = paginationInfo.getTableName();
                SQLiteDatabase wDB = squillDbHelper.getWritableDatabase();
                long newRowID = wDB.insert(tableName, null, contentValues);
            }
        } else {
            //DbObject __dbObject = DBUtil.convert(successResult, getContainerIdForObjectStore());
            DbObject __dbObject = convertObjectForStore(successResult, getContainerIdForObjectStore());
            if(__dbObject == null)
                return;
            storeResultsInDb_0(__dbObject);
        }
    }

    protected DbObject convertObjectForStore(Z successResult, String containerIdForObjectStore) {
        return DBUtil.convert(successResult, getContainerIdForObjectStore());
    }

    protected String getPaginationContainerId() {
        return null;
    }

    protected String getPaginationContainerClassName() {
        return null;
    }

    private void storeResultsInDb_0(DbObject __dbObject) {
        if(checkExistence_0(__dbObject)) {
            boolean success = deleteExisting_0(__dbObject);
            if(!success)
                return;
        }
        ContentValues values = __dbObject.toContentValues();
        String table_name = __dbObject.getTableName();
        SQLiteDatabase wDB = squillDbHelper.getWritableDatabase();
        long newRowID = wDB.insert(table_name, null, values);
    }

    private boolean checkExistence_0(DbObject __dbObject) {
        Cursor cursor = null;
        boolean exists = false;
        try {
            String table_name = __dbObject.getTableName();
            String[] projection = new String[] {(String)(__dbObject.getClass().getField("_ID").get(null))};
            String selection = (String)(__dbObject.getClass().getField("ENTITY_ID").get(null));
            SQLiteDatabase rDB = squillDbHelper.getReadableDatabase();
            cursor = rDB.query(table_name, projection, selection,
                    new String[]{__dbObject.getEntityId()}, null, null,
                    null);
            exists = !(!cursor.moveToFirst() || cursor.getCount() == 0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return exists;
    }

    private boolean deleteExisting_0(DbObject __dbObject) {
        try {
            SQLiteDatabase wDB = squillDbHelper.getWritableDatabase();
            String table_name = __dbObject.getTableName();
            String KEY_NAME = (String)(__dbObject.getClass().getField("ENTITY_ID").get(null));
            String KEY_VALUE = __dbObject.getEntityId();
            return wDB.delete(table_name, KEY_NAME + "=" + KEY_VALUE, null) > 0;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected final Z doExecuteInBackground(X x) {
        try {
            String oAuthToken = AppController.getInstance().getoAuthToken();
            APIBuilder builder = APIBuilder.create().setAction(command())
                    .setOauthToken(oAuthToken);
            Map<String, Object> apiParams = prepareApiParams(x);
            if(apiParams != null && !apiParams.isEmpty()) {
                Iterator<String> itr = apiParams.keySet().iterator();
                while (itr.hasNext()) {
                    String paramName = itr.next();
                    Object paramValue = apiParams.get(paramName);
                    builder.addApiParam(paramName, paramValue);
                }
            }
            API api = builder.build();
            successResult = executeApi(api);
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();;
        }
        return successResult;
    }

    protected abstract Z executeApi(API api) throws Exception;

    protected abstract COMMAND command();

    protected abstract Map<String, Object> prepareApiParams(X inputObject);

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
            if(storeResultsInDB) {
                storeResultsInDb(data);
            }
            if(updateExistingObjectInDB) {
                doUpdateExistingInDB(squillDbHelper.getWritableDatabase(), getInputObject(), z);
            }
            fireOnSuccess(data);
        }
        else {
            fireOnFailure(getFailureMessage());
        }
        fireOnPostComplete();
    }
}