package com.android.util.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.util.LContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 张全
 */
public class ModelDao {
    public static String TABLE = ModelDBHelper.TABLE;
    private static final String QUERY_ALL = "select * from " + TABLE;
    private static final String WHERE_CLAUSE = ModelColumn.uid + "=? and "
            + ModelColumn.key + "=?";

    private synchronized static SQLiteDatabase getDB() {
        return new ModelDBHelper(LContext.getContext()).getWritableDatabase();
    }

    public synchronized static <T> List<T> getByColumn(DBModel<T> model,
                                                       String column, String value) {
        String sql = QUERY_ALL + " where " + column + " ='" + value + "' ";
        return get(model, sql);
    }

    public synchronized static <T> List<T> getByColumns(DBModel<T> model,
                                                        String[] cloumns, String[] values) {
        final String and = " and ";
        StringBuffer sql = new StringBuffer(QUERY_ALL);
        sql.append(" where ");
        for (int i = 0; i < cloumns.length; i++) {
            sql.append(cloumns[i] + "='" + values[i] + "'" + and);
        }
        sql = sql.delete(sql.length() - and.length(), sql.length());
        return get(model, sql.toString());
    }

    public synchronized static <T> List<T> getUserModels(String uid,
                                                         DBModel<T> model) {
        String[] columns = {ModelColumn.uid, ModelColumn.model};
        String[] values = {uid, model.getModelName()};
        return getByColumns(model, columns, values);
    }

    public synchronized static <T> List<T> get(DBModel<T> model, String sql) {
        List<T> modelList = new ArrayList<T>();

        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = getDB();
            c = db.rawQuery(sql, null);
            while (c.moveToNext()) {
                String value = DBUtil.getString(c, ModelColumn.value);
                modelList.add(model.fromDBValue(value));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.closeDB(db, c);
        }
        return modelList;
    }

    public synchronized static <T> boolean save(DBModel<T> model) {
        if (null == model) {
            return false;
        }

        List<DBModel<T>> modelList = new ArrayList<DBModel<T>>();
        modelList.add(model);
        return save(modelList);
    }

    public synchronized static <T> boolean save(
            List<? extends DBModel<T>> modelList) {
        if (null == modelList || modelList.isEmpty()) {
            return false;
        }
        boolean result = true;
        SQLiteDatabase db = null;
        try {
            db = getDB();
            // 使用事务机制 提高插入速度
            db.beginTransaction();
            int size = modelList.size();
            for (int i = 0; i < size; i++) {
                DBModel<T> model = modelList.get(i);
                db.delete(
                        TABLE,
                        WHERE_CLAUSE,
                        new String[]{model.getUid(),
                                model.getKey()});

                db.insert(TABLE, null, getValue(model));
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            if (null != db)
                db.endTransaction();
            DBUtil.closeDB(db, null);
        }
        return result;
    }

    public synchronized static <T> boolean update(DBModel<T> model) {
        int result = 0;
        SQLiteDatabase db = null;
        try {
            db = getDB();
            result = db.update(
                    TABLE,
                    getValue(model),
                    WHERE_CLAUSE,
                    new String[]{model.getUid(),
                            model.getKey()});
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        } finally {
            DBUtil.closeDB(db, null);
        }
        return result == 1 ? true : false;
    }

    public synchronized static <T> boolean deleteUserModels(String uid,
                                                            String modelName) {
        String[] columns = {ModelColumn.uid, ModelColumn.model};
        String[] values = {uid, modelName};
        return deleteByColumns(columns, values);
    }

    public synchronized static <T> boolean delete(DBModel<T> model) {
        return deleteByColumns(
                new String[]{ModelColumn.uid, ModelColumn.key},
                new String[]{model.getUid(), model.getKey()});
    }

    public synchronized static <T> boolean deleteByColumn(String column,
                                                          String value) {
        return deleteByColumns(new String[]{column}, new String[]{value});
    }

    public synchronized static <T> boolean deleteByColumns(String[] columns,
                                                           String[] values) {
        boolean result = true;
        SQLiteDatabase db = null;
        try {
            db = getDB();
            StringBuffer whereClause = new StringBuffer();
            whereClause.append(columns[0]).append("=?");
            if (columns.length > 1) {
                for (int i = 1; i < columns.length; i++) {
                    whereClause.append(" and ").append(columns[i]).append("=?");
                }
            }
            db.delete(TABLE, whereClause.toString(), values);
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            DBUtil.closeDB(db, null);
        }
        return result;
    }

    private static synchronized <T> ContentValues getValue(DBModel<T> model) {
        ContentValues values = new ContentValues();
        values.put(ModelColumn.uid, model.getUid());
        values.put(ModelColumn.key, model.getKey());
        values.put(ModelColumn.value, model.toDBValue());
        values.put(ModelColumn.model, model.getModelName());
        return values;
    }
}
