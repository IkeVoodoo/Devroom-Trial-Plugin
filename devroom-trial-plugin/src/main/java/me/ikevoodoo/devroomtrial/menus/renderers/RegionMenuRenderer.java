package me.ikevoodoo.devroomtrial.menus.renderers;

import me.ikevoodoo.devroomtrial.api.regions.Region;
import me.ikevoodoo.devroomtrial.api.regions.SelectionManager;
import me.ikevoodoo.devroomtrial.menus.Menu;
import me.ikevoodoo.devroomtrial.menus.MenuPage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Deque;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class RegionMenuRenderer implements MenuRenderer {

    private final Region region;

    public RegionMenuRenderer(Region region) {
        this.region = region;
    }

    @Override
    public @NotNull Inventory createInventory(@NotNull Menu menu, @NotNull Player player, int page) {
        return Bukkit.createInventory(null, 9 * 3, "Edit Region");
    }

    @Override
    public void render(@NotNull Menu menu, @NotNull Player player, @NotNull Inventory inventory, int page) {
        for (int i = 0; i < 9 * 3; i++) {
            inventory.setItem(i, this.createStack(Material.GRAY_STAINED_GLASS_PANE, itemMeta -> {}));
        }

        inventory.setItem(9, this.createStack(Material.NAME_TAG, itemMeta -> {
            itemMeta.setDisplayName("§aRename region.");
            itemMeta.setLore(List.of(
                    "§7Current Name: §f" + this.region.name()
            ));
        }));

        inventory.setItem(11, this.createStack(Material.GREEN_DYE, itemMeta -> {
            itemMeta.setDisplayName("§aAdd to whitelist.");
            itemMeta.setLore(this.region.whitelist().createItemLore(5));
        }));

        inventory.setItem(13, this.createStack(Material.RED_DYE, itemMeta -> {
            itemMeta.setDisplayName("§cRemove from whitelist.");
            itemMeta.setLore(this.region.whitelist().createItemLore(5));
        }));

        inventory.setItem(15, this.createStack(Material.COMPASS, itemMeta -> {
            itemMeta.setDisplayName("§aChange location.");

            final var bb = this.region.boundingBox();

            itemMeta.setLore(List.of(
                    "§7Bounding Box:",
                    "§7  Min x: §c%d§7, y: §a%d§7, z: §3%d".formatted((int) bb.getMinX(), (int) bb.getMinY(), (int) bb.getMinZ()),
                    "§7  Max x: §c%d§7, y: §a%d§7, z: §3%d".formatted((int) bb.getMaxX(), (int) bb.getMaxY(), (int) bb.getMaxZ())
            ));
        }));

        inventory.setItem(17, this.createStack(Material.WRITABLE_BOOK, itemMeta -> itemMeta.setDisplayName("§aEdit flags.")));
    }

    @Override
    public void onClick(@NotNull Menu menu, @NotNull Player player, @NotNull Inventory inventory, int page, int slot) {
        switch (slot) {
            case 9 -> {
                final var pageId = menu.getPage(player).getPageName();
                this.startConversation(menu, player, new RenamePrompt(this.region, menu, player, pageId));
                return;
            }

            case 11, 13 -> {
                final var pageId = menu.getPage(player).getPageName();
                this.startConversation(menu, player, new WhitelistPrompt(this.region, menu, player, pageId, slot == 11));
                return;
            }

            case 15 -> {
                final var selection = SelectionManager.instance().getSelection(player, player.getWorld());
                if (selection == null) {
                    player.sendMessage("§cPlease make a selection first!");
                    return;
                }

                selection.resize(
                        selection.getMaxX() + 1,
                        selection.getMaxY() + 1,
                        selection.getMaxZ() + 1,
                        selection.getMinX(),
                        selection.getMinY(),
                        selection.getMinZ()
                );

                if(this.region.updatePosition(selection)) {
                    player.sendMessage("§aSuccessfully updated position!");
                } else {
                    player.sendMessage("§cA plugin cancelled the resize!");
                }
            }

            case 17 -> {
                menu.open(player, "edit_flags_" + this.region.uniqueId() + 0);
                return;
            }

            default -> {
                return;
            }
        }

        this.render(menu, player, inventory, page);
    }

    private void startConversation(Menu menu, Player player, Prompt prompt) {
        final var queue = menu.saveHistory(player);
        queue.removeLast(); // We will reopen the page anyway
        menu.closeAll(player);

        final var plugin = JavaPlugin.getProvidingPlugin(this.getClass());

        final var conversation = new ConversationFactory(plugin)
                .withFirstPrompt(new RestoreQueuePrompt(prompt, menu, player, queue))
                .withEscapeSequence("cancel")
                .withPrefix(new PluginNameConversationPrefix(plugin, " >> ", ChatColor.GOLD))
                .withLocalEcho(false)
                .buildConversation(player);

        conversation.begin();
    }

    private ItemStack createStack(Material material, Consumer<ItemMeta> consumer) {
        final var stack = new ItemStack(material);
        final var meta = stack.getItemMeta();
        if (meta == null) {
            return stack;
        }

        consumer.accept(meta);

        stack.setItemMeta(meta);

        return stack;
    }

    private record RestoreQueuePrompt(Prompt delegate, Menu menu, Player player, Deque<MenuPage> queue) implements Prompt {

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            return this.delegate.getPromptText(context);
        }

        @Override
        public boolean blocksForInput(@NotNull ConversationContext context) {
            return this.delegate.blocksForInput(context);
        }

        @Override
        public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
            final var result = this.delegate.acceptInput(context, input);
            if (result == Prompt.END_OF_CONVERSATION) {
                this.menu.restoreHistory(this.player, this.queue);
                return Prompt.END_OF_CONVERSATION;
            }

            return result;
        }

    }

    private static class WhitelistPrompt extends ValidatingPrompt {

        private final Region region;
        private final Menu menu;
        private final Player player;
        private final String page;
        private final boolean add;

        private WhitelistPrompt(Region region, Menu menu, Player player, String page, boolean add) {
            this.region = region;
            this.menu = menu;
            this.player = player;
            this.page = page;
            this.add = add;
        }

        @Override
        protected boolean isInputValid(@NotNull ConversationContext context, @NotNull String input) {
            return Bukkit.getOfflinePlayer(input).hasPlayedBefore();
        }

        @Override
        protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
            final var uuid = Bukkit.getOfflinePlayer(input).getUniqueId();

            if (this.add) {
                this.processAdd(uuid, context);
            } else {
                this.processRemove(uuid, context);
            }

            this.menu.open(this.player, this.page);

            return END_OF_CONVERSATION;
        }

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            if (this.add) {
                return "§aEnter the name of the player you want to whitelist";
            }

            return "§aEnter the name of the player you want to blacklist";
        }

        @Override
        protected @Nullable String getFailedValidationText(@NotNull ConversationContext context, @NotNull String invalidInput) {
            return "§cThat player doesn't exist!";
        }

        private void processAdd(UUID uuid, ConversationContext context) {
            if(!this.region.whitelist().add(uuid)) {
                context.getForWhom().sendRawMessage("§cA plugin cancelled whitelisting that player!");
                return;
            }

            context.getForWhom().sendRawMessage("§aSuccessfully whitelisted player!");
        }

        private void processRemove(UUID uuid, ConversationContext context) {
            if(!this.region.whitelist().remove(uuid)) {
                context.getForWhom().sendRawMessage("§cA plugin cancelled blacklisting that player!");
                return;
            }

            context.getForWhom().sendRawMessage("§aSuccessfully blacklisted player!");
        }
    }

    private static class RenamePrompt extends StringPrompt {

        private final Region region;
        private final Menu menu;
        private final Player player;
        private final String page;

        private RenamePrompt(Region region, Menu menu, Player player, String page) {
            this.region = region;
            this.menu = menu;
            this.player = player;
            this.page = page;
        }

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            return "§aWhat would you like the region to be called?";
        }

        @Override
        public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
            if (input == null) {
                context.getForWhom().sendRawMessage("§cPlease input a name!");
                return END_OF_CONVERSATION;
            }

            if(this.region.updateName(input)) {
                context.getForWhom().sendRawMessage("§aSuccessfully renamed region!");
            } else {
                context.getForWhom().sendRawMessage("§cA plugin cancelled the rename!");
            }

            this.menu.open(this.player, this.page);

            return END_OF_CONVERSATION;
        }
    }
}
