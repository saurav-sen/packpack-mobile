package com.pack.pack.application.data.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pack.pack.application.db.DbObject;
import com.pack.pack.application.db.JsonModel;
import com.pack.pack.application.db.PaginationInfo;
import com.pack.pack.application.db.UserInfo;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JUser;
import com.pack.pack.services.exception.PackPackException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saurav on 11-06-2016.
 */
public class DBUtil {

    private static final String LOG_TAG = "DBUtil";

    private DBUtil() {
    }

    public static UserInfo loadLastLoggedInUserInfo(SQLiteDatabase readable) {
        Cursor cursor = null;
        try {
            String[] projection = new String[] {UserInfo._ID, UserInfo.ENTITY_ID,
                    UserInfo.USER_NAME, UserInfo.PASSWORD, UserInfo.ACCESS_TOKEN,
                    UserInfo.ACCESS_TOKEN_SECRET};
            try {
                cursor = readable.query(UserInfo.TABLE_NAME, projection, null, null,
                        null, null, null);

                if(cursor.moveToFirst()) {
                    do {
                        long id = cursor.getLong(cursor.getColumnIndexOrThrow(UserInfo._ID));
                        String userId = cursor.getString(cursor.getColumnIndexOrThrow(UserInfo.ENTITY_ID));
                        String userName = cursor.getString(cursor.getColumnIndexOrThrow(UserInfo.USER_NAME));
                        String password = cursor.getString(cursor.getColumnIndexOrThrow(UserInfo.PASSWORD));
                        String accessToken = cursor.getString(cursor.getColumnIndexOrThrow(UserInfo.ACCESS_TOKEN));
                        String accessTokenSecret = cursor.getString(cursor.getColumnIndexOrThrow(UserInfo.ACCESS_TOKEN_SECRET));
                        return new UserInfo(userName, password, userId, accessToken, accessTokenSecret);
                    } while(cursor.moveToNext());
                }
            } finally {
                if(cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
        }
        //return new UserInfo("sourabhnits@gmail.com", "P@ckp@K#123");
        return null;
    }

    public static DbObject convert(Object object, String containerId) {
        return null;
    }

    public static JUser convertUserInfo(UserInfo userInfo) {
        JUser user = new JUser();
        user.setId(userInfo.getEntityId());
        user.setUsername(userInfo.getUsername());
        return user;
    }

    public static <T> T loadJsonModelByEntityId(SQLiteDatabase writable, String entityId, Class<T> classType) {
        String type = classType.getName();
        T result = null;
        Cursor cursor = null;
        try {
            String __SQL = "SELECT " + JsonModel.CONTENT + " FROM " + JsonModel.TABLE_NAME
                    + " WHERE " + JsonModel.ENTITY_ID + "='" + entityId + "' AND "
                    + JsonModel.CLASS_TYPE + "='" + type + "'";
            cursor = writable.rawQuery(__SQL, null);
            if(cursor.moveToFirst()) {
                String json = cursor.getString(cursor.getColumnIndexOrThrow(JsonModel.CONTENT));
                result = convertFromJson(json, classType);
            }
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }

    public static <T> List<T> loadAllJsonModelByContainerId(
            SQLiteDatabase readable, String entityContainerId,
            Class<T> classType) {
        Cursor cursor =  null;
        List<T> result = new ArrayList<T>();
        String type = classType.getName();
        try {
            String __SQL = "SELECT " + JsonModel.CONTENT + " FROM " + JsonModel.TABLE_NAME
                    + " WHERE " + JsonModel.ENTITY_CONTAINER_ID + "='" + entityContainerId
                    + "' AND " + JsonModel.CLASS_TYPE + "='" + type + "'";
            cursor = readable.rawQuery(__SQL, null);
            if(cursor.moveToFirst()) {
                do {
                    String json = cursor.getString(cursor.getColumnIndexOrThrow(JsonModel.CONTENT));
                    T r = convertFromJson(json, classType);
                    if(r != null) {
                        result.add(r);
                    }
                } while(cursor.moveToNext());
            }
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }

    private static <T> T convertFromJson(String json, Class<T> classType) {
        T result = null;
        try {
            result = JSONUtil.deserialize(json, classType);
        } catch (PackPackException e) {
            Log.i(LOG_TAG, e.getMessage());
        }
        return result;
    }

    public static PaginationInfo loadPaginationInfo(SQLiteDatabase readable, String entityId) {
        return loadPaginationInfo(readable, entityId, null);
    }

    public static PaginationInfo loadPaginationInfo(SQLiteDatabase readable, String entityId, String type) {
        Cursor cursor = null;
        PaginationInfo paginationInfo = null;
        try {
            String __SQL = "SELECT " + PaginationInfo._ID + ", " + PaginationInfo.ENTITY_ID
                    + ", " + PaginationInfo.NEXT_LINK + ", " + PaginationInfo.PREVIOUS_LINK
                    + "  FROM " + PaginationInfo.TABLE_NAME  + " WHERE " + PaginationInfo.ENTITY_ID
                    + "='" + entityId + "'";
            if(type != null && !type.trim().isEmpty()) {
                __SQL = __SQL + " AND " + PaginationInfo.CLASS_TYPE + "='" + type + "'";
            }
            cursor = readable.rawQuery(__SQL, null);
            if(cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(PaginationInfo._ID));
                String objId = cursor.getString(cursor.getColumnIndexOrThrow(PaginationInfo.ENTITY_ID));
                String nextLink = cursor.getString(cursor.getColumnIndexOrThrow(PaginationInfo.NEXT_LINK));
                String previousLink = cursor.getString(cursor.getColumnIndexOrThrow(PaginationInfo.PREVIOUS_LINK));
                paginationInfo = new PaginationInfo();
                paginationInfo.setEntityId(entityId);
                paginationInfo.setNextLink(nextLink);
                paginationInfo.setPreviousLink(previousLink);
            }
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return paginationInfo;
    }
}
