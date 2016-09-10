package com.pack.pack.application.db;

import android.content.ContentValues;
import android.provider.BaseColumns;

import java.util.Collections;
import java.util.List;

/**
 * Created by Saurav on 25-06-2016.
 */
public class JsonModel implements DbObject {

    public static final String TABLE_NAME = "JSON_MODEL";

    public static final String ENTITY_ID = "entity_id";
    public static final String ENTITY_CONTAINER_ID = "entity_container_id";
    public static final String CONTENT = "content";
    public static final String CLASS_TYPE = "type";
    //public static final String HAS_ATTACHMENT = "has_attachment";
    //public static final String COMMAND = "command";

    private String entityId;

    private String content;

    private String classType;

    //private boolean hasAttachment;

    //private String command;

    private String entityContainerId;

    public String getEntityContainerId() {
        return entityContainerId;
    }

    public void setEntityContainerId(String entityContainerId) {
        this.entityContainerId = entityContainerId;
    }

    /*public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }*/

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    /*public boolean isHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }*/

    @Override
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ENTITY_ID, entityId);
        contentValues.put(ENTITY_CONTAINER_ID, entityContainerId);
        contentValues.put(CONTENT, content);
        contentValues.put(CLASS_TYPE, classType);
        return contentValues;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public List<? extends DbObject> getChildrenObjects() {
        return Collections.emptyList();
    }
}
