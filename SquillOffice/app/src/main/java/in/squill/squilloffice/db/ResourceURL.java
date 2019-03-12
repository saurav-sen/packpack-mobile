package in.squill.squilloffice.db;

import android.content.ContentValues;

import java.util.Collections;
import java.util.List;

/**
 * Created by Saurav on 28-06-2016.
 */
public class ResourceURL extends DbObjectImpl {

    public static final String TABLE_NAME = "RESOURCE_URL";

    public static final String URL = "url";
    public static final String BLOB_CONTENT = "CONTENT"; // Blob

    private String url;

    private byte[] bytes;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(URL, url);
        contentValues.put(BLOB_CONTENT, bytes);
        return contentValues;
    }

    @Override
    public String getEntityId() {
        return url;
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
        return URL;
    }
}
