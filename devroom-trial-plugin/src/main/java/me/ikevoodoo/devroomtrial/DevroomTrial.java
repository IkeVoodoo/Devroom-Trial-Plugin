package me.ikevoodoo.devroomtrial;

import me.ikevoodoo.devroomtrial.api.RegionService;
import me.ikevoodoo.devroomtrial.commands.RegionCommand;
import me.ikevoodoo.devroomtrial.db.DatabaseHandler;
import me.ikevoodoo.devroomtrial.db.DatabaseSaveListener;
import me.ikevoodoo.devroomtrial.flags.BlockBreakFlag;
import me.ikevoodoo.devroomtrial.flags.BlockPlaceFlag;
import me.ikevoodoo.devroomtrial.flags.EntityDamageFlag;
import me.ikevoodoo.devroomtrial.flags.InteractFlag;
import me.ikevoodoo.devroomtrial.listeners.PlayerMenuListener;
import me.ikevoodoo.devroomtrial.listeners.PlayerWandListener;
import me.ikevoodoo.devroomtrial.listeners.RegionListener;
import me.ikevoodoo.devroomtrial.menus.Menu;
import me.ikevoodoo.devroomtrial.particles.ParticleRendererManager;
import me.ikevoodoo.devroomtrial.regions.TrialRegionManager;
import me.ikevoodoo.devroomtrial.regions.TrialRegionService;
import me.ikevoodoo.devroomtrial.regions.TrialSelectionManager;
import me.ikevoodoo.devroomtrial.regions.flags.TrialFlagManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class DevroomTrial extends JavaPlugin {
;
    private final ParticleRendererManager particleManager = new ParticleRendererManager(this);
    private final Menu regionMenu = new Menu(
            new NamespacedKey(this, "paginator_left"),
            new NamespacedKey(this, "paginator_right")
    );
    private DatabaseHandler databaseHandler;

    @Override
    public void onEnable() {
        this.registerRegionService();

        this.saveDefaultConfig();

        final var config = getConfig();

        getServer().getPluginManager().registerEvents(new PlayerWandListener(this.particleManager), this);
        getServer().getPluginManager().registerEvents(new PlayerMenuListener(this.regionMenu), this);
        getServer().getPluginManager().registerEvents(new RegionListener(this.regionMenu), this);

        try {
            this.databaseHandler = new DatabaseHandler(
                    config.getString("database.host", "localhost"),
                    config.getInt("database.port", 3306),
                    config.getString("database.name", "region_db"),
                    config.getString("database.username", "root"),
                    config.getString("database.password", "root")
            );

            this.databaseHandler.setupDatabase();
            this.databaseHandler.loadRegions();

            getServer().getPluginManager().registerEvents(new DatabaseSaveListener(this.getLogger(), this.databaseHandler), this);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Unable to setup database, exiting.", e);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.setupCommand("region", new RegionCommand(this.regionMenu));
    }

    @Override
    public void onDisable() {
        this.databaseHandler.stop();
    }

    private void registerRegionService() {
        final var flagRegistry = new TrialFlagManager();
        final var regionManager = new TrialRegionManager();
        final var selectionManager = new TrialSelectionManager(
                new NamespacedKey(this, "wand")
        );

        flagRegistry.registerFlag("block-breaking", BlockBreakFlag::new);
        flagRegistry.registerFlag("block-placing", BlockPlaceFlag::new);
        flagRegistry.registerFlag("interact", InteractFlag::new);
        flagRegistry.registerFlag("entity-damage", EntityDamageFlag::new);

        Bukkit.getServicesManager().register(RegionService.class, new TrialRegionService(regionManager, selectionManager, flagRegistry), this, ServicePriority.Normal);
    }

    private void setupCommand(String name, TabExecutor executor) {
        final var pluginCommand = this.getCommand(name);
        if (pluginCommand == null) {
            this.getLogger().severe(() -> "Unable to register command %s".formatted(name));
            return;
        }

        pluginCommand.setExecutor(executor);
        pluginCommand.setTabCompleter(executor);
    }

}
