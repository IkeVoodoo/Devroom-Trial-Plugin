package me.ikevoodoo.devroomtrial.regions;

import me.ikevoodoo.devroomtrial.api.RegionService;
import me.ikevoodoo.devroomtrial.api.flags.FlagManager;
import me.ikevoodoo.devroomtrial.api.regions.SelectionManager;
import me.ikevoodoo.devroomtrial.regions.flags.TrialFlagManager;
import org.jetbrains.annotations.NotNull;

public class TrialRegionService implements RegionService {

    private final TrialRegionManager regionManager;
    private final SelectionManager selectionManager;
    private final TrialFlagManager flagRegistry;

    public TrialRegionService(TrialRegionManager regionManager, SelectionManager selectionManager, TrialFlagManager flagRegistry) {
        this.regionManager = regionManager;
        this.selectionManager = selectionManager;
        this.flagRegistry = flagRegistry;
    }

    @Override
    public @NotNull FlagManager getFlagRegistry() {
        return this.flagRegistry;
    }

    @Override
    public @NotNull SelectionManager getSelectionManager() {
        return this.selectionManager;
    }

    @Override
    public @NotNull TrialRegionManager getRegionManager() {
        return this.regionManager;
    }
}
