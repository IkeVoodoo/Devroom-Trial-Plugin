package me.ikevoodoo.devroomtrial.commands;

import me.ikevoodoo.devroomtrial.api.regions.Region;
import me.ikevoodoo.devroomtrial.api.regions.SelectionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RegionCreateCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            this.sendFormattedUsage(sender);
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players may use this command!");
            return true;
        }

        final var creator = player.getUniqueId();
        final var world = player.getWorld();
        final var bounding = SelectionManager.instance().getSelection(player, world);
        if (bounding == null) {
            sender.sendMessage("§cMake a selection first!");
            return true;
        }

        bounding.resize(
                bounding.getMaxX() + 1,
                bounding.getMaxY() + 1,
                bounding.getMaxZ() + 1,
                bounding.getMinX(),
                bounding.getMinY(),
                bounding.getMinZ()
        );

        final var region = Region.create(
                creator,
                args[0],
                world,
                bounding
        );

        if (region == null) {
            sender.sendMessage("§cA region with that name already exists!");
            return true;
        }

        sender.sendMessage("§aSuccessfully created region!");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length <= 1) {
            return List.of("<name>");
        }

        return List.of();
    }

    private void sendFormattedUsage(CommandSender sender) {
        sender.sendMessage("§c/region create <name> §8| §7Create a new region.");
    }
}
