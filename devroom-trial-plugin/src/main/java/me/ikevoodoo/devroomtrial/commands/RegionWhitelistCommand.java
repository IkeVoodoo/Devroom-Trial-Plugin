package me.ikevoodoo.devroomtrial.commands;

import me.ikevoodoo.devroomtrial.api.regions.RegionManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class RegionWhitelistCommand implements TabExecutor {

    private static final double NAMES_PER_PAGE = 8;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            this.sendFormattedUsage(sender);
            return true;
        }

        final var region = RegionManager.instance().getRegion(args[0]);
        if (region == null) {
            sender.sendMessage("§cThat region doesn't exist!");
            return true;
        }

        int pageNumber = 0;
        if (args.length > 1) {
            try {
                pageNumber = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid page number!");
                return true;
            }
        }

        if (pageNumber < 0) {
            sender.sendMessage("§cPage number must be positive!");
            return true;
        }

        final var sb = new StringBuilder();
        double remaining = pageNumber * NAMES_PER_PAGE;
        int added = 0;

        for (final var id : region.whitelist()) {
            if (remaining-- > 0) {
                continue;
            }

            if (added > 8) {
                continue;
            }

            final var player = Bukkit.getOfflinePlayer(id);

            final var online = player.getPlayer();

            added++;

            sb.append("§7 - §f");

            if (online == null) {
                sb.append(player.getName()).append('\n');
                continue;
            }

            sb.append(online.getName()).append('\n');
        }

        final var component = new ComponentBuilder()
                .append("=== WHITELIST (")
                .color(ChatColor.GRAY)

                .append(region.name())
                .color(ChatColor.AQUA)

                .append(") ===\n")
                .color(ChatColor.GRAY);

        component.append(sb.toString());

        component.append("<<< ");

        if (pageNumber > 0) {
            component.color(ChatColor.GOLD).event(new ClickEvent(
                    ClickEvent.Action.RUN_COMMAND,
                    "/region whitelist %s %s".formatted(
                            region.name(),
                            pageNumber - 1
                    )
            )).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Previous Page")));;
        } else {
            component.color(ChatColor.GRAY);
        }

        final var maxPages = (int) Math.floor(region.whitelist().size() / NAMES_PER_PAGE);

        component.append((pageNumber + 1) + " / " + maxPages).retain(ComponentBuilder.FormatRetention.NONE).color(ChatColor.WHITE);

        component.append(" >>>");

        if (pageNumber < maxPages - 1) {
            component.color(ChatColor.GOLD).event(new ClickEvent(
                    ClickEvent.Action.RUN_COMMAND,
                    "/region whitelist %s %s".formatted(
                            region.name(),
                            pageNumber + 1
                    )
            )).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Next Page")));
        } else {
            component.color(ChatColor.GRAY);
        }

        sender.spigot().sendMessage(component.build());
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

        if (args.length == 2) {
            return List.of("<page>");
        }

        return List.of();
    }

    private void sendFormattedUsage(CommandSender sender) {
        sender.sendMessage("§c/region whitelist <region> <page> §8| §7Show a region's whitelist, optionally you can specify a page.");
    }
}
