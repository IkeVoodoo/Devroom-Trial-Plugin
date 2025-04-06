package me.ikevoodoo.devroomtrial.db;

import me.ikevoodoo.devroomtrial.api.flags.events.FlagAddEvent;
import me.ikevoodoo.devroomtrial.api.flags.events.FlagPostUpdateEvent;
import me.ikevoodoo.devroomtrial.api.regions.Region;
import me.ikevoodoo.devroomtrial.api.regions.events.RegionCreateEvent;
import me.ikevoodoo.devroomtrial.api.regions.events.RegionRenameEvent;
import me.ikevoodoo.devroomtrial.api.regions.events.RegionResizeEvent;
import me.ikevoodoo.devroomtrial.api.whitelist.events.WhitelistAddEvent;
import me.ikevoodoo.devroomtrial.api.whitelist.events.WhitelistRemoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseSaveListener implements Listener {

    private final Logger logger;
    private final DatabaseHandler databaseHandler;

    public DatabaseSaveListener(Logger logger, DatabaseHandler databaseHandler) {
        this.logger = logger;
        this.databaseHandler = databaseHandler;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onRegionCreate(RegionCreateEvent event) {
        this.async(event.getRegion(), (connection, region) -> {
            this.logger.log(Level.INFO, () -> "Uploading region " + region.name());

            final var createRegionStatement = connection.prepareStatement(DatabaseQueries.WRITE_REGION_DATA);

            DatabaseHelper.setUuid(createRegionStatement, 1, region.uniqueId());
            createRegionStatement.setString(2, region.name());
            DatabaseHelper.setUuid(createRegionStatement, 3, region.worldId());
            final var flagSetIndex = DatabaseHelper.setBoundingBox(createRegionStatement, 4, region.boundingBox());
            DatabaseHelper.setFlagSet(createRegionStatement, flagSetIndex, region.flags());

            createRegionStatement.execute();

            final var createWhitelistStatement = connection.prepareStatement(DatabaseQueries.WRITE_WHITELIST_ENTRY);
            for (final var whitelisted : region.whitelist()) {
                DatabaseHelper.setUuid(createWhitelistStatement, 1, region.uniqueId());
                DatabaseHelper.setUuid(createWhitelistStatement, 2, whitelisted);

                createWhitelistStatement.addBatch();
            }

            createWhitelistStatement.execute();
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onRegionResize(RegionResizeEvent event) {
        final var bb = event.getCurrent().clone();

        this.async(event.getRegion(), (connection, region) -> {
            this.logger.log(Level.INFO, () -> "Resizing region " + region.name());

            final var statement = connection.prepareStatement(DatabaseQueries.WRITE_REGION_BOUNDING_BOX);

            final var uuidIndex = DatabaseHelper.setBoundingBox(statement, 1, bb);
            DatabaseHelper.setUuid(statement, uuidIndex, region.uniqueId());

            statement.execute();
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onRegionRename(RegionRenameEvent event) {
        this.async(event.getRegion(), (connection, region) -> {
            this.logger.log(Level.INFO, () -> "Updating region name to " + region.name());

            final var statement = connection.prepareStatement(DatabaseQueries.WRITE_REGION_NAME);

            statement.setString(1, region.name());
            DatabaseHelper.setUuid(statement, 2, region.uniqueId());

            statement.execute();
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onWhitelistAdd(WhitelistAddEvent event) {
        final var entityId = event.getEntityId();

        this.async(event.getRegion(), (connection, region) -> {
            this.logger.log(Level.INFO, () -> "Adding entity " + entityId + " to whitelist of " + region.name());

            final var statement = connection.prepareStatement(DatabaseQueries.WRITE_WHITELIST_ENTRY);

            DatabaseHelper.setUuid(statement, 1, region.uniqueId());
            DatabaseHelper.setUuid(statement, 2, entityId);

            statement.execute();
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onWhitelistRemove(WhitelistRemoveEvent event) {
        final var entityId = event.getEntityId();

        this.async(event.getRegion(), (connection, region) -> {
            this.logger.log(Level.INFO, () -> "Removing entity " + entityId + " from whitelist of " + region.name());

            final var statement = connection.prepareStatement(DatabaseQueries.DELETE_WHITELIST_ENTRY);

            DatabaseHelper.setUuid(statement, 1, region.uniqueId());
            DatabaseHelper.setUuid(statement, 2, entityId);

            statement.execute();
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFlagAdd(FlagAddEvent event) {
        final var flag = event.getFlag();

        this.async(event.getRegion(), (connection, region) -> {
            this.logger.log(Level.INFO, () -> "Adding flag " + flag.id() + " to " + region.name());

            final var statement = connection.prepareStatement(DatabaseQueries.WRITE_REGION_FLAG_SET);

            DatabaseHelper.setFlagSet(statement, 1, region.flags(), flag);
            DatabaseHelper.setUuid(statement, 2, region.uniqueId());

            statement.execute();
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFlagPostUpdate(FlagPostUpdateEvent event) {
        this.async(event.getRegion(), (connection, region) -> {
            this.logger.log(Level.INFO, () -> "Updating flags for " + region.name());

            final var statement = connection.prepareStatement(DatabaseQueries.WRITE_REGION_FLAG_SET);

            DatabaseHelper.setFlagSet(statement, 1, region.flags());
            DatabaseHelper.setUuid(statement, 2, region.uniqueId());

            statement.execute();
        });
    }

    private void async(Region region, AsyncTask<Region> consumer) {
        final var plugin = JavaPlugin.getProvidingPlugin(this.getClass());
        Bukkit.getScheduler().runTaskAsynchronously(
                plugin,
                () -> {
                    try(final var connection = this.databaseHandler.getConnection()) {
                        consumer.run(connection, region);
                    } catch (Throwable e) {
                        plugin.getLogger().log(Level.SEVERE, "Exception when running async task", e);
                    }
                }
        );
    }

    private interface AsyncTask<T> {
        void run(Connection connection, T value) throws Throwable;
    }

}
