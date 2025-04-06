package me.ikevoodoo.devroomtrial.commands;

import me.ikevoodoo.devroomtrial.api.flags.Flag;
import me.ikevoodoo.devroomtrial.api.flags.FlagManager;
import me.ikevoodoo.devroomtrial.api.flags.FlagState;
import me.ikevoodoo.devroomtrial.api.regions.RegionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class RegionFlagCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 3) {
            this.sendFormattedUsage(sender);
            return true;
        }

        final var region = RegionManager.instance().getRegion(args[0]);
        if (region == null) {
            sender.sendMessage("§cThat region doesn't exist!");
            return true;
        }

        Flag existingFlag = region.flags().getFlag(args[1]);
        if (existingFlag == null) {
            existingFlag = FlagManager.instance().createFlag(args[1], region);
        }

        if (existingFlag == null) {
            sender.sendMessage("§cUnknown flag!");
            return true;
        }

        try {
            final var state = FlagState.valueOf(args[2].toUpperCase(Locale.ROOT));
            region.flags().updateState(existingFlag.id(), state);

            sender.sendMessage("§aSuccessfully updated flag state!");
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cUnknown flag state!");
            return true;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return List.of();
        }

        if (args.length == 1) {
            final var regionName = args[0].toLowerCase(Locale.ROOT);

            return RegionManager.instance().listRegionNames(name -> name.toLowerCase().startsWith(regionName))
                    .limit(10)
                    .toList();
        }

        if (args.length == 2) {
            final var region = RegionManager.instance().getRegion(args[0]);
            if (region == null) {
                return List.of("!UNKNOWN REGION!");
            }

            final var first = args[1].toLowerCase(Locale.ROOT);
            return region.flags().getFlagIds()
                    .stream()
                    .toList()
                    .stream()
                    .filter(s -> s.toLowerCase().startsWith(first))
                    .toList();
        }

        if (args.length == 3) {
            final var flagValue = args[2].toUpperCase(Locale.ROOT);
            return Arrays.stream(FlagState.values())
                    .map(Enum::name)
                    .filter(s -> s.startsWith(flagValue))
                    .toList();
        }

        return List.of();
    }

    private void sendFormattedUsage(CommandSender sender) {
        sender.sendMessage("§c/region flag <region> <flag> <state> §8| §7Edit a flag's state.");
    }
}
