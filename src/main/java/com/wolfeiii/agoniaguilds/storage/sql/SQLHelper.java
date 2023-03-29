package com.wolfeiii.agoniaguilds.storage.sql;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.storage.sql.session.QueryResult;
import com.wolfeiii.agoniaguilds.storage.sql.session.SQLSession;
import com.wolfeiii.agoniaguilds.storage.sql.session.impl.MariaDBSession;
import com.wolfeiii.agoniaguilds.storage.sql.session.impl.MySQLSession;
import com.wolfeiii.agoniaguilds.storage.sql.session.impl.SQLiteSession;
import com.wolfeiii.agoniaguilds.utilities.objects.Pair;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SQLHelper {

    private static SQLSession globalSession = null;

    private SQLHelper() {

    }

    public static boolean isReady() {
        return globalSession != null;
    }

    public static void waitForConnection() {
        if (isReady())
            globalSession.waitForConnection();
    }

    public static SQLSession createSession(AgoniaGuilds plugin, boolean logging) {
        return switch (plugin.getConfiguration().getDatabaseSection().getType()) {
            case "MYSQL" -> new MySQLSession(plugin, logging);
            case "MARIADB" -> new MariaDBSession(plugin, logging);
            default -> new SQLiteSession(plugin, logging);
        };
    }

    public static boolean createConnection(AgoniaGuilds plugin) {
        SQLSession session = createSession(plugin, true);

        if (session.createConnection()) {
            globalSession = session;
            return true;
        }

        return false;
    }

    public static void createTable(String tableName, Pair<String, String>... columns) {
        if (isReady())
            globalSession.createTable(tableName, columns, QueryResult.EMPTY_VOID_QUERY_RESULT);
    }

    public static void createIndex(String indexName, String tableName, String... columns) {
        if (isReady())
            globalSession.createIndex(indexName, tableName, columns, QueryResult.EMPTY_VOID_QUERY_RESULT);
    }

    public static void modifyColumnType(String tableName, String columnName, String newType) {
        if (isReady()) {
            globalSession.modifyColumnType(tableName, columnName, newType, QueryResult.EMPTY_VOID_QUERY_RESULT);
        }
    }

    public static void removePrimaryKey(String tableName, String columnName) {
        if (isReady())
            globalSession.removePrimaryKey(tableName, columnName, QueryResult.EMPTY_VOID_QUERY_RESULT);
    }

    public static void select(String tableName, String filters, QueryResult<ResultSet> queryResult) {
        if (isReady())
            globalSession.select(tableName, filters, queryResult);
    }

    public static void setJournalMode(String journalMode, QueryResult<ResultSet> queryResult) {
        if (isReady())
            globalSession.setJournalMode(journalMode, queryResult);
    }

    public static void customQuery(String query, QueryResult<PreparedStatement> queryResult) {
        if (isReady())
            globalSession.customQuery(query, queryResult);
    }

    public static void close() {
        if (isReady())
            globalSession.closeConnection();
    }

}