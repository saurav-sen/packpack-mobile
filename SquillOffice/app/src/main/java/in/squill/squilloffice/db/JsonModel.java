package in.squill.squilloffice.db;

import android.content.ContentValues;

import java.util.Collections;
import java.util.List;

/**
 * Created by Saurav on 25-06-2016.
 */
public class JsonModel extends DbObjectImpl {

    public static final String TABLE_NAME = "JSON_MODEL";

    public static final String ENTITY_ID = "entity_id";
    public static final String CONTENT = "content";

    public static final String FEED_TYPE = "feedType";
    public static final String PAGE_NO = "pageNo";

    private String content;

    private int pageNo;

    private String feedType;

    public String getFeedType() {
        return feedType;
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public String getEntityId() {
        return feedType + "_" + pageNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ENTITY_ID, getEntityId());
        contentValues.put(CONTENT, content);
        contentValues.put(FEED_TYPE, feedType);
        contentValues.put(PAGE_NO, pageNo);
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

    @Override
    protected String getEntityIdColumnName() {
        return ENTITY_ID;
    }

    @Override
    public String updateRowWhereClause() {
        return FEED_TYPE + "='" + feedType + "' AND " + PAGE_NO + "=" + pageNo;
    }

    @Override
    public String[] updateRowWhereClauseArguments() {
        return null;
    }

    @Override
    public String deleteRowWhereClause() {
        return updateRowWhereClause();
    }

    @Override
    public String[] deleteRowWhereClauseArguments() {
        return null;
    }
}
