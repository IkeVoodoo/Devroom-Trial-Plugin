package me.ikevoodoo.devroomtrial.commands;

import me.ikevoodoo.devroomtrial.menus.Menu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegionCommand implements TabExecutor {

    private final Menu menu;
    private final Map<String, TabExecutor> childCommands = new HashMap<>();

    public RegionCommand(@NotNull final Menu menu) {
        this.menu = menu;

        this.childCommands.put("create", new RegionCreateCommand());
        this.childCommands.put("wand", new RegionWandCommand());
        this.childCommands.put("add", new RegionAddCommand());
        this.childCommands.put("remove", new RegionRemoveCommand());
        this.childCommands.put("whitelist", new RegionWhitelistCommand());
        this.childCommands.put("flag", new RegionFlagCommand());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                if (!this.hasPermission(player, "menu")) {
                    player.sendMessage("§cYou do not have permission to view regions");
                }

                if(!this.menu.open(player, "list_regions_0")) {
                    sender.sendMessage("§cThere are no regions to display!");
                }

                return true;
            }

            this.sendFormattedUsage(sender);
            return true;
        }

        final var child = this.childCommands.get(args[0]);
        if (child == null) {
            this.sendFormattedUsage(sender);
            return true;
        }

        if (!this.hasPermission(sender, args[0])) {
            sender.sendMessage("§cYou may not use the subcommand: §3%s".formatted(args[0]));
            return true;
        }

        return child.onCommand(sender, command, label, Arrays.stream(args).skip(1).toArray(String[]::new));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return this.childCommands.keySet().stream().filter(s -> this.hasPermission(sender, s)).toList();
        }

        final var child = this.childCommands.get(args[0]);
        if (child == null) {
            return this.childCommands.keySet().stream().filter(s -> s.startsWith(args[0])).toList();
        }

        if (!this.hasPermission(sender, args[0])) {
            return List.of();
        }

        return child.onTabComplete(sender, command, label, Arrays.stream(args).skip(1).toArray(String[]::new));
    }

    private void sendFormattedUsage(CommandSender sender) {
        sender.sendMessage("§c/region create <name> §8| §7Create a new region.");
        sender.sendMessage("§c/region wand §8| §7Give yourself a selection wand.");
        sender.sendMessage("§c/region add <region> <player> §8| §7Add a player to a region's whitelist.");
        sender.sendMessage("§c/region remove <region> <player> §8| §7Remove a player from a region's whitelist.");
        sender.sendMessage("§c/region whitelist <region> <page> §8| §7Show a region's whitelist, optionally you can specify a page.");
        sender.sendMessage("§c/region §8| §7Show the regions menu.");
        sender.sendMessage("§c/region flag <region> <flag> <state> §8| §7Edit a flag's state.");
    }

    private boolean hasPermission(CommandSender sender, String perm) {
        return sender.hasPermission("region.bypass") || sender.hasPermission("region." + perm);
    }
}
