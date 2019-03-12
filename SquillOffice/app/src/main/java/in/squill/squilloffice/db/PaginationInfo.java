package in.squill.squilloffice.db;

import android.content.ContentValues;

import java.util.Collections;
import java.util.List;

/**
 * Created by Saurav on 27-06-2016.
 */
public class PaginationInfo extends DbObjectImpl {

    public static final String TABLE_NAME = "PAGE_INFO";

    public static final String ENTITY_ID = "entity_id";
    public static final String CLASS_TYPE = "type";
    public static final String NEXT_PAGE_NO = "nextPageNo";

    private String entityId;

    private String type;

    public int getNextPageNo() {
        return nextPageNo;
    }

    public void setNextPageNo(int nextPageNo) {
        this.nextPageNo = nextPageNo;
    }

    private int nextPageNo;

    @Override
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ENTITY_ID, entityId);
        contentValues.put(CLASS_TYPE, type);
        contentValues.put(NEXT_PAGE_NO, nextPageNo);
        return contentValues;
    }

    @Override
    public List<? extends DbObject> getChildrenObjects() {
        return Collections.emptyList();
    }

    @Override
    protected String getEntityIdColumnName() {
        return ENTITY_ID;
    }
}
