package lu.crghost.myex.dao;

import android.content.ContentValues;
import lu.crghost.myex.MyExApp;
import lu.crghost.myex.models.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Dump all models implementing BaseModelInterface
 * Created by CR on 11/05/2015.
 */
public final class DbSqlDump {

    public static String dump(MyExApp app, BaseModelInterface table, boolean addDrop) {
        String[] fieldNames = table.getFieldNames();
        StringBuilder s = new StringBuilder();
        s.append("-- creating " + table.getTableName());
        s.append(System.getProperty("line.separator"));
        if (addDrop) {
            s.append("DROP TABLE IF EXISTS " + table.getTableName() + ";");
            s.append(System.getProperty("line.separator"));
        }
        s.append(table.getTableSqlCre());
        s.append(System.getProperty("line.separator"));

        List<BaseModelInterface> data = null;
        if (table instanceof Account) {
            data = (List<BaseModelInterface>) (List<?>) app.getDataManager().getAccounts(null,null);
        }
        if (table instanceof Costcenter) {
            data = (List<BaseModelInterface>) (List<?>) app.getDataManager().getCostcenters(null, null);
        }
        if (table instanceof Debtor) {
            data = (List<BaseModelInterface>) (List<?>) app.getDataManager().getDebtors(null, null);
        }
        if (table instanceof Geotrack) {
            data = (List<BaseModelInterface>) (List<?>) app.getDataManager().getGeotracks(null,null);
        }
        if (table instanceof Measure) {
            data = (List<BaseModelInterface>) (List<?>) app.getDataManager().getMeasures(null,null);
        }
        if (table instanceof Transaction) {
            data = (List<BaseModelInterface>) (List<?>) app.getDataManager().getTransactions(null,null);
        }

        if (data == null) {
            s.append("-- No data for table " + table.getTableName());
        } else {
            for (BaseModelInterface rec : data) {
                ContentValues c = rec.getContentValues(true);
                s.append("INSERT INTO " + table.getTableName() + " (");
                for (int i=0; i < fieldNames.length; i++) {
                    s.append(fieldNames[i]);
                    if (i < (fieldNames.length-1)) s.append(",");
                }
                s.append(") VALUES(");
                for (int i=0; i < fieldNames.length; i++) {
                    s.append(toSqlString(fieldNames[i],c.get(fieldNames[i])));
                    if (i < (fieldNames.length-1)) s.append(",");
                }
                s.append(");");
                s.append(System.getProperty("line.separator"));
            }
        }

        return s.toString();
    }

    private static String toSqlString(String fieldName, Object o) {
        String s = "null";
        if (o != null) {
            if (o instanceof String) {
                s = "'" + (String) o + "'";
            } else if (o instanceof BigDecimal) {
                s = ((BigDecimal) o).toString();
            } else if (o instanceof Integer) {
                Integer i = (Integer) o;
                if (i.intValue() != 0) s = i.toString();
            } else if (o instanceof Double) {
                s = ((Double) o).toString();
            } else if (o instanceof Float) {
                s = ((Float) o).toString();
            } else if (o instanceof Short) {
                s = ((Short) o).toString();
            } else if (o instanceof Long) {
                Long l = (Long) o;
                if (l.longValue() != 0) s = l.toString();
            } else if (o instanceof Boolean) {
                s = ((Boolean) o).toString();
            }
        // 0 not null for numeric fields
        } else {
            if (o instanceof Number) {
                s = "0";
            }
        }
        return s;
    }

}
