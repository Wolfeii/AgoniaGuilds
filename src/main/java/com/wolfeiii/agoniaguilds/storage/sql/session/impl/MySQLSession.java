package com.wolfeiii.agoniaguilds.storage.sql.session.impl;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import com.wolfeiii.agoniaguilds.storage.sql.session.RemoteSQLSession;
import com.wolfeiii.agoniaguilds.utilities.logging.Log;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class MySQLSession extends RemoteSQLSession {

    public MySQLSession(AgoniaGuilds plugin, boolean logging) {
        super(plugin, logging);
    }

    @Override
    public boolean createConnection() {
        if (logging) Log.info("Försöker att ansluta till avlägsen databas (MySQL)...");

        try {
            HikariConfig config = new HikariConfig();
            config.setConnectionTestQuery("SELECT 1");
            config.setPoolName("AgoniaGuilds Pool");

            config.setDriverClassName("com.mysql.jdbc.Driver");

            String address = plugin.getConfiguration().getDatabaseSection().getAddress();
            String dbName = plugin.getConfiguration().getDatabaseSection().getDBName();
            String userName = plugin.getConfiguration().getDatabaseSection().getUsername();
            String password = plugin.getConfiguration().getDatabaseSection().getPassword();
            int port = plugin.getConfiguration().getDatabaseSection().getPort();

            boolean useSSL = plugin.getConfiguration().getDatabaseSection().hasSSL();
            boolean publicKeyRetrieval = plugin.getConfiguration().getDatabaseSection().hasPublicKeyRetrieval();

            config.setJdbcUrl("jdbc:mysql://" + address + ":" + port + "/" + dbName + "?useSSL=" + useSSL);
            config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s?useSSL=%b&allowPublicKeyRetrieval=%b",
                    address, port, dbName, useSSL, publicKeyRetrieval));
            config.setUsername(userName);
            config.setPassword(password);
            config.setMinimumIdle(5);
            config.setMaximumPoolSize(50);
            config.setConnectionTimeout(10000);
            config.setIdleTimeout(plugin.getConfiguration().getDatabaseSection().getWaitTimeout());
            config.setMaxLifetime(plugin.getConfiguration().getDatabaseSection().getMaxLifetime());
            config.addDataSourceProperty("characterEncoding", "utf8");
            config.addDataSourceProperty("useUnicode", "true");

            dataSource = new HikariDataSource(config);

            if (logging) Log.info("Lyckades skapa en anslutning till en avlägsen databas!");

            ready.complete(null);

            return true;
        } catch (Throwable error) {
            Log.error(error, "Ett oförväntat fel uppstod vid anslutning till en MySQL databas:");
        }

        return false;
    }

}
