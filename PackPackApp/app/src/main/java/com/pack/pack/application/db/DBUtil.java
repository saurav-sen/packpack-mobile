package com.pack.pack.application.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JUser;
import com.pack.pack.services.exception.PackPackException;

import java.util.ArrayList;
import java.util.LinkedList;
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
                    UserInfo.USER_NAME, UserInfo.ACCESS_TOKEN,
                    UserInfo.ACCESS_TOKEN_SECRET, UserInfo.FOLLWED_CATEGORIES};
            try {
                cursor = readable.query(UserInfo.TABLE_NAME, projection, null, null,
                        null, null, null);

                if(cursor.moveToFirst()) {
                    do {
                        long id = cursor.getLong(cursor.getColumnIndexOrThrow(UserInfo._ID));
                        String userId = cursor.getString(cursor.getColumnIndexOrThrow(UserInfo.ENTITY_ID));
                        String userName = cursor.getString(cursor.getColumnIndexOrThrow(UserInfo.USER_NAME));
                        String accessToken = cursor.getString(cursor.getColumnIndexOrThrow(UserInfo.ACCESS_TOKEN));
                        String accessTokenSecret = cursor.getString(cursor.getColumnIndexOrThrow(UserInfo.ACCESS_TOKEN_SECRET));
                        //String followedCategories = cursor.getString(cursor.getColumnIndexOrThrow(UserInfo.FOLLWED_CATEGORIES));
                        UserInfo userInfo = new UserInfo(userName, userId, accessToken, accessTokenSecret);//, followedCategories);
                        return userInfo;
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
        return null;
    }

    public static DbObject convert(Object object, String containerId) {
       /* if(object == null)
            return null;
        if(object instanceof JTopic) {
            return convertToJsonModel((JTopic) object, containerId);
        } else if(object instanceof JPack) {
            return convertToJsonModel((JPack)object, containerId);
        } else if(object instanceof JPackAttachment) {
            return convertJPackAttachment((JPackAttachment) object, containerId);
        }*/
        return null;
    }

   /* private static JsonModel convertToJsonModel(JTopic topic, String containerId) {
        JsonModel jsonModel = new JsonModel();
        try {
            jsonModel.setEntityId(topic.getId());
            jsonModel.setClassType(JTopic.class.getName());
            jsonModel.setContent(JSONUtil.serialize(topic));
            jsonModel.setEntityContainerId(containerId);
        } catch (PackPackException e) {
            Log.d(LOG_TAG, e.getMessage());
        }
        return jsonModel;
    }*/

    /*private static JsonModel convertToJsonModel(JPack pack, String containerId) {
        JsonModel jsonModel = new JsonModel();
        try {
            jsonModel.setEntityId(pack.getId());
            jsonModel.setClassType(JPack.class.getName());
            jsonModel.setContent(JSONUtil.serialize(pack));
            jsonModel.setEntityContainerId(containerId);
        } catch (PackPackException e) {
            Log.d(LOG_TAG, e.getMessage());
        }
        return jsonModel;
    }*/

   /* private static AttachmentInfo convertJPackAttachment(JPackAttachment attachment, String containerId) {
        AttachmentInfo attachmentInfo = null;
        try {
            attachmentInfo = new AttachmentInfo();
            attachmentInfo.setUrl(attachment.getAttachmentUrl());
            attachmentInfo.setType(attachment.getAttachmentType());
            attachmentInfo.setContainerId(containerId);
            attachmentInfo.setEntityId(attachment.getId());
            attachmentInfo.setJsonBody(JSONUtil.serialize(attachment));
            return attachmentInfo;
        } catch (PackPackException e) {
            Log.d(LOG_TAG, e.getMessage());
        }
        return attachmentInfo;
    }*/

    public static JUser convertUserInfo(UserInfo userInfo) {
        JUser user = new JUser();
        user.setId(userInfo.getEntityId());
        user.setUsername(userInfo.getUsername());
        return user;
    }

   /* public static List<JTopic> convertUserOwnedTopicInfo(List<UserOwnedTopicInfo> userOwnedTopicInfos) {
        List<JTopic> result = new ArrayList<JTopic>();
        if(userOwnedTopicInfos != null && !userOwnedTopicInfos.isEmpty()) {
            for(UserOwnedTopicInfo userOwnedTopicInfo : userOwnedTopicInfos) {
                result.add(convertUserOwnedTopicInfo(userOwnedTopicInfo));
            }
        }
        return result;
    }

    private static JTopic convertUserOwnedTopicInfo(UserOwnedTopicInfo userOwnedTopicInfo) {
        JTopic topic = new JTopic();
        topic.setId(userOwnedTopicInfo.getId());
        topic.setName(userOwnedTopicInfo.getName());
        topic.setDescription(userOwnedTopicInfo.getDescription());
        topic.setCategory(userOwnedTopicInfo.getCategory());
        topic.setWallpaperUrl(userOwnedTopicInfo.getWallpaperUrl());
        return topic;
    }*/

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
        return result != null && !result.isEmpty() ? result : null;
    }

    private static <T> T convertFromJson(String json, Class<T> classType) {
        T result = null;
        try {
            result = JSONUtil.deserialize(json, classType, true);
        } catch (PackPackException e) {
            Log.i(LOG_TAG, e.getMessage());
        }
        return result;
    }

    public static PaginationInfo loadPaginationInfo(SQLiteDatabase readable, String entityId) {
        return loadPaginationInfo(readable, entityId, null);
    }

    /*public static List<JPackAttachment> loadAllAttachmentInfo(SQLiteDatabase readable, String containerId) {
        Cursor cursor = null;
        List<JPackAttachment> attachments = new LinkedList<JPackAttachment>();
        try {
            String __SQL = "SELECT " + AttachmentInfo.JSON_BODY + " FROM " + AttachmentInfo.TABLE_NAME
                    + " WHERE " + AttachmentInfo.CONTAINER_ID + "='" + containerId + "'";
            cursor = readable.rawQuery(__SQL, null);
            if(cursor.moveToFirst()) {
                do {
                    String json = cursor.getString(cursor.getColumnIndexOrThrow(AttachmentInfo.JSON_BODY));
                    JPackAttachment attachment = JSONUtil.deserialize(json, JPackAttachment.class, true);
                    if(attachment != null) {
                        attachments.add(attachment);
                    }
                } while (cursor.moveToNext());
            }
        } catch (PackPackException e) {
            Log.i(LOG_TAG, e.getMessage());
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return attachments != null && !attachments.isEmpty() ? attachments : null;
    }*/

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

    /*public static List<JDiscussion> getDiscussionInfosBasedUponContainerId(SQLiteDatabase readable, String containerId, String containerType) {
        Cursor cursor = null;
        List<JDiscussion> infos = null;
        try {
            String __SQL = "SELECT " + DiscussionInfo._ID + ", " + DiscussionInfo.ENTITY_ID + ", "
                    + DiscussionInfo.DATE_TIME + ", " + DiscussionInfo.CONTAINER_TYPE + ", "
                    + DiscussionInfo.CONTAINER_ID + ", " + DiscussionInfo.CONTENT + ", "
                    + DiscussionInfo.FROM_USERNAME + ", " + DiscussionInfo.FROM_USER_FULL_NAME
                    + " FROM " + DiscussionInfo.TABLE_NAME + " WHERE " + DiscussionInfo.CONTAINER_ID
                    + "='" + containerId + "' AND " + DiscussionInfo.CONTAINER_TYPE + "='"
                    + containerType + "'";
            cursor = readable.rawQuery(__SQL, null);
            if(cursor.moveToFirst()) {
                infos = new ArrayList<JDiscussion>();
                do {
                    String eId = cursor.getString(cursor.getColumnIndexOrThrow(DiscussionInfo.ENTITY_ID));
                    String cId = cursor.getString(cursor.getColumnIndexOrThrow(DiscussionInfo.CONTAINER_ID));
                    String cType = cursor.getString(cursor.getColumnIndexOrThrow(DiscussionInfo.CONTAINER_TYPE));
                    String content = cursor.getString(cursor.getColumnIndexOrThrow(DiscussionInfo.CONTENT));
                    String userName = cursor.getString(cursor.getColumnIndexOrThrow(DiscussionInfo.FROM_USERNAME));
                    String userFullName = cursor.getString(cursor.getColumnIndexOrThrow(DiscussionInfo.FROM_USER_FULL_NAME));
                    long dateTime = cursor.getLong(cursor.getColumnIndexOrThrow(DiscussionInfo.DATE_TIME));
                    JDiscussion info = new JDiscussion();
                    info.setId(eId);
                    info.setContent(content);
                    info.setDateTime(dateTime);
                    JUser user = new JUser();
                    user.setUsername(userName);
                    user.setName(userFullName);
                    info.setFromUser(user);
                    infos.add(info);
                } while (cursor.moveToNext());
            }
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return infos;
    }*/
}
