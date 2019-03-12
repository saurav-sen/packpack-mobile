package in.squill.squilloffice.db;

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

    public String updateRowWhereClause();

    public String[] updateRowWhereClauseArguments();

    public String deleteRowWhereClause();

    public String[] deleteRowWhereClauseArguments();
}
