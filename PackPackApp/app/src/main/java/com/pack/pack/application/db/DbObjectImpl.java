package com.pack.pack.application.db;

/**
 * Created by Saurav on 16-09-2018.
 */
public abstract class DbObjectImpl implements DbObject {

    @Override
    public String updateRowWhereClause() {
        return getEntityIdColumnName() + " = '" + getEntityId() + "'";
    }

    @Override
    public String[] updateRowWhereClauseArguments() {
        /*String[] args = new String[1];
        args[0] = getEntityId();
        return args;*/
        return null;
    }

    @Override
    public String deleteRowWhereClause() {
        return updateRowWhereClause();
    }

    @Override
    public String[] deleteRowWhereClauseArguments() {
        return updateRowWhereClauseArguments();
    }

    protected abstract String getEntityIdColumnName();
}
