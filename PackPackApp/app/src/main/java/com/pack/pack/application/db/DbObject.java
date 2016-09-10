package com.pack.pack.application.db;

import android.content.ContentValues;
import android.provider.BaseColumns;

import java.util.List;

/**
 * Created by Saurav on 25-06-2016.
 */
public interface DbObject extends BaseColumns {

    public ContentValues toContentValues();

    public String getTableName();

    public String getEntityId();

    public List<? extends DbObject> getChildrenObjects();
}
