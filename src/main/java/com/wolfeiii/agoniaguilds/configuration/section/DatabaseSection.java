package com.wolfeiii.agoniaguilds.configuration.section;

import com.wolfeiii.agoniaguilds.AgoniaGuilds;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Locale;

public class DatabaseSection {

    public final String databaseType;
    public final boolean databaseBackup;
    public final String databaseMySQLAddress;
    public final int databaseMySQLPort;
    public final String databaseMySQLDBName;
    public final String databaseMySQLUsername;
    public final String databaseMySQLPassword;
    public final String databaseMySQLPrefix;
    public final boolean databaseMySQLSSL;
    public final boolean databaseMySQLPublicKeyRetrieval;
    public final long databaseMySQLWaitTimeout;
    public final long databaseMySQLMaxLifetime;

    public DatabaseSection(AgoniaGuilds plugin, YamlConfiguration configuration) {
        databaseType = configuration.getString("database.type", "MySQL").toUpperCase(Locale.ENGLISH);
        databaseBackup = configuration.getBoolean("database.backup");
        databaseMySQLAddress = configuration.getString("database.address");
        databaseMySQLPort = configuration.getInt("database.port");
        databaseMySQLDBName = configuration.getString("database.db-name");
        databaseMySQLUsername = configuration.getString("database.user-name");
        databaseMySQLPassword = configuration.getString("database.password");
        databaseMySQLPrefix = configuration.getString("database.prefix");
        databaseMySQLSSL = configuration.getBoolean("database.useSSL");
        databaseMySQLPublicKeyRetrieval = configuration.getBoolean("database.allowPublicKeyRetrieval");
        databaseMySQLWaitTimeout = configuration.getLong("database.waitTimeout");
        databaseMySQLMaxLifetime = configuration.getLong("database.maxLifetime");
    }

    public String getAddress() {
        return databaseMySQLAddress;
    }

    public String getDBName() {
        return databaseMySQLDBName;
    }

    public String getUsername() {
        return databaseMySQLUsername;
    }

    public String getPassword() {
        return databaseMySQLPassword;
    }

    public int getPort() {
        return databaseMySQLPort;
    }

    public String getType() {
        return databaseType;
    }

    public String getPrefix() {
        return databaseMySQLPrefix;
    }

    public boolean hasSSL() {
        return databaseMySQLSSL;
    }

    public boolean hasPublicKeyRetrieval() {
        return databaseMySQLPublicKeyRetrieval;
    }

    public long getMaxLifetime() {
        return databaseMySQLMaxLifetime;
    }

    public long getWaitTimeout() {
        return databaseMySQLWaitTimeout;
    }
}
