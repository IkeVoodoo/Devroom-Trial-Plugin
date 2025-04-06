package me.ikevoodoo.devroomtrial.api;

import me.ikevoodoo.devroomtrial.api.flags.FlagManager;
import me.ikevoodoo.devroomtrial.api.regions.RegionManager;
import me.ikevoodoo.devroomtrial.api.regions.SelectionManager;
import org.jetbrains.annotations.NotNull;

public interface RegionService {

    @NotNull
    FlagManager getFlagRegistry();

    @NotNull
    SelectionManager getSelectionManager();

    @NotNull
    RegionManager getRegionManager();

}
