package me.ikevoodoo.devroomtrial.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.ikevoodoo.devroomtrial.api.regions.RegionManager;
import me.ikevoodoo.devroomtrial.regions.TrialRegion;
import me.ikevoodoo.devroomtrial.regions.TrialRegionManager;
import me.ikevoodoo.devroomtrial.regions.flags.TrialFlagSet;
import me.ikevoodoo.devroomtrial.regions.whitelist.TrialWhitelist;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class DatabaseHandler {

    private final HikariDataSource dataSource;

    public DatabaseHandler(String host, int port, String db, String username, String password) {
        final var conf = new HikariConfig();

        conf.setJdbcUrl("jdbc:mysql://%s:%s/%s?allowPublicKeyRetrieval=true".formatted(host, port, db));
        conf.setUsername(username);
        conf.setPassword(password);

        conf.setMaximumPoolSize(32);
        conf.setMinimumIdle(2);
        conf.setMinimumIdle(30_000);
        conf.setMaxLifetime(600_000);
        conf.setConnectionTimeout(30_000);

        this.dataSource = new HikariDataSource(conf);
    }

    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    public void loadRegions() throws SQLException {
        try(final var connection = this.getConnection()) {
            final var readRegions = connection.prepareStatement(DatabaseQueries.READ_REGION_TABLE);

            final var plugin = JavaPlugin.getProvidingPlugin(this.getClass());

            final var regions = readRegions.executeQuery();
            while (regions.next()) {
                final var regionId = DatabaseHelper.getUuid(regions, 1);
                final var name = regions.getString(2);
                final var worldId = DatabaseHelper.getUuid(regions, 3);
                final var boundingBox = DatabaseHelper.getBoundingBox(regions, 4);

                plugin.getLogger().log(Level.INFO, () -> "Loading region named: " + name);

                final var region = new TrialRegion(
                        regionId,
                        name,
                        worldId,
                        boundingBox,
                        new TrialFlagSet(),
                        new TrialWhitelist()
                );

                DatabaseHelper.getFlagSet(regions, 10, region);

                final var readWhitelist = connection.prepareStatement(DatabaseQueries.READ_WHITELIST_TABLE);
                DatabaseHelper.setUuid(readWhitelist, 1, regionId);

                final var whitelistData = readWhitelist.executeQuery();
                while (whitelistData.next()) {
                    final var uuid = DatabaseHelper.getUuid(whitelistData, 1);

                    region.whitelist().add(uuid);
                }

                final var added = ((TrialRegionManager) RegionManager.instance()).addRegion(region);
                if (!added) {
                    plugin.getLogger().log(Level.INFO, () -> "Failed adding region: " + name);
                }
            }
        }
    }

    public void setupDatabase() throws SQLException {
        try(final var connection = this.getConnection()) {
            final var setupRegionTable = connection.prepareStatement(DatabaseQueries.SETUP_REGION_TABLE);
            setupRegionTable.execute();

            final var setupWhitelistTable = connection.prepareStatement(DatabaseQueries.SETUP_WHITELIST_TABLE);
            setupWhitelistTable.execute();
        }
    }

    public void stop() {
        this.dataSource.close();
    }

}
