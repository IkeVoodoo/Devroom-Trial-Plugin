package me.ikevoodoo.devroomtrial.commands;

import me.ikevoodoo.devroomtrial.api.regions.RegionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class RegionAddCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
            this.sendFormattedUsage(sender);
            return true;
        }

        final var region = RegionManager.instance().getRegion(args[0]);
        if (region == null) {
            sender.sendMessage("§cThat region doesn't exist!");
            return true;
        }

        // Safe, we just use this to map to a UUID
        final var player = Bukkit.getOfflinePlayer(args[1]);
        if (!player.hasPlayedBefore()) {
            sender.sendMessage("§cThat player doesn't exist!");
            return true;
        }

        final var whitelist = region.whitelist();
        final var id = player.getUniqueId();

        if (whitelist.contains(id)) {
            sender.sendMessage("§aGood news! That player is already in the whitelist!");
            return true;
        }

        whitelist.add(id);

        // TODO save

        sender.sendMessage("§aSuccessfully added player '§3%s§a' to whitelist in '§6%s§a'".formatted(
                player.getPlayer() == null ? player.getName() : player.getPlayer().getName(),
                region.name()
        ));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return List.of();
        }

        final var regionName = args[0].toLowerCase(Locale.ROOT);

        if (args.length == 1) {
            return RegionManager.instance().listRegionNames(name -> name.toLowerCase().startsWith(regionName))
                    .limit(10)
                    .toList();
        }

        final var playerName = args[1].toLowerCase(Locale.ROOT);

        if (args.length == 2) {
            return Bukkit.getOnlinePlayers()
                    .stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(playerName))
                    .limit(10)
                    .toList();
        }

        return List.of();
    }

    private void sendFormattedUsage(CommandSender sender) {
        sender.sendMessage("§c/region add <region> <player> §8| §7Add a player to a region's whitelist.");
    }
}
