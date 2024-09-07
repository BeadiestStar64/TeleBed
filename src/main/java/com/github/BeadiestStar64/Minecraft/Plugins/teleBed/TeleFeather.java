package com.github.BeadiestStar64.Minecraft.Plugins.teleBed;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TeleFeather implements Listener {

    // このクラスで使用するクリックの種類
    private final Action[] allowActions = {Action.RIGHT_CLICK_BLOCK, Action.LEFT_CLICK_AIR};

    // 転移の羽のマテリアル
    private final Material feather = Material.FEATHER;

    // 未設定の転移の羽の表示名
    Component unSetTeleFeatherName = Component.text("転移の羽")
            .color(TextColor.color(230, 180, 34))
            .decoration(TextDecoration.ITALIC, false)
            .decoration(TextDecoration.BOLD, true);

    // 設定済みの転移の羽の表示名
    Component setTeleFeatherName = Component.text("転移の羽")
            .color(TextColor.color(103, 69, 152))
            .decoration(TextDecoration.ITALIC, false)
            .decoration(TextDecoration.BOLD, true);

    // 転移の羽共通の説明
    ArrayList<Component> teleFeatherLore = new ArrayList<>(Arrays.asList(
            Component.text("布団にも使われている上質な羽...みたいな物")
                    .color(TextColor.color(255, 255, 255))
                    .decoration(TextDecoration.ITALIC, false),
            Component.text("実はオーパーツではないかという噂もある")
                    .color(TextColor.color(255, 255, 255))
                    .decoration(TextDecoration.ITALIC, false),
            Component.text("[")
                    .color(TextColor.color(255, 255, 255))
                    .decoration(TextDecoration.ITALIC, false)
                    .append(Component.text("効果")
                            .color(TextColor.color(230, 180, 34))
                            .decoration(TextDecoration.ITALIC, false)
                            .decoration(TextDecoration.BOLD, true))
                    .append(Component.text("] ")
                            .color(TextColor.color(255, 255, 255))
                            .decoration(TextDecoration.ITALIC, false))
    ));

    // 転移の羽のNameSpace
    NamespacedKey teleKey = new NamespacedKey(TeleBed.getInstance(), "telebed_set_telefeather");

    // 未設定の転移の羽
    private ItemStack getUnsetTeleFeather() {
        ItemStack item = new ItemStack(feather);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(unSetTeleFeatherName);

        ArrayList<Component> lore = teleFeatherLore;
        lore.add(Component.text("地面に右クリックで、テレポート先を設定")
                .color(TextColor.color(127, 255, 212))
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);

        item.setItemMeta(meta);

        return item;
    }

    // 設定済みの転移の羽
    private ItemStack getSetTeleFeather(Player player) {
        ItemStack item = new ItemStack(feather);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(setTeleFeatherName);

        TeleportData data = new TeleportData(player);

        ArrayList<Component> lore = teleFeatherLore;
        lore.add(Component.text("右クリックで、テレポート先に移動")
                .color(TextColor.color(255, 0, 0))
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("[テレポート先]")
                .color(TextColor.color(255, 255, 255))
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("[ディメンション]: ")
                .color(TextColor.color(255, 255, 255))
                .decoration(TextDecoration.ITALIC, false)
                .append(Component.text(data.getDimension())
                        .color(TextColor.color(230, 180, 34))
                        .decoration(TextDecoration.ITALIC, false)
                        .decoration(TextDecoration.BOLD, true)));

        meta.lore(lore);


        meta.getPersistentDataContainer().set(teleKey, new TelePDC(), data);

        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);

        item.setItemMeta(meta);

        return item;
    }

    private boolean checkSetFeather(@NotNull ItemStack item) {
        try {
            // マテリアルが羽以外ならば、処理を中断
            if (item.getType() != feather) {
                return false;
            }

            ItemMeta meta = item.getItemMeta();

            // 名前が定義済みの転移の羽以外ならば、処理を中断
            if (!(Objects.requireNonNull(meta.displayName()).equals(setTeleFeatherName))) {
                return false;
            }

            // PDCが未定義ならば、処理を中断
            if (!(meta.getPersistentDataContainer().has(teleKey))) {
                return false;
            }

            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public void registerRecipe(Plugin plugin) {
        NamespacedKey key = new NamespacedKey(plugin, "telebed_unset_telefeather");
        ShapedRecipe recipe = new ShapedRecipe(key, getUnsetTeleFeather());

        // すべてのベッドを含むリスト
        List<Material> allBeds = Arrays.stream(Material.values())
                .filter(material -> material.name().endsWith("_BED"))
                .toList();

        recipe.shape("AAA", "FBF", "CRC");

        /* RecipeChoice.ExactChoiceクラス
         * 厳密なItemStackでレシピ判定を行う
         * (https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/inventory/RecipeChoice.ExactChoice.html)
         */
        /* RecipeChoice.MaterialChoiceクラス
         * 複数のMaterialをレシピ候補に入れることができる
         * (https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/inventory/RecipeChoice.html)
         */
        recipe.setIngredient('A', new RecipeChoice.ExactChoice(new ItemStack(Material.AIR)));
        recipe.setIngredient('B', new RecipeChoice.MaterialChoice(allBeds));
        recipe.setIngredient('C', new RecipeChoice.ExactChoice(new ItemStack(Material.COMPASS)));
        recipe.setIngredient('F', new RecipeChoice.ExactChoice(new ItemStack(Material.FEATHER)));
        recipe.setIngredient('R', new RecipeChoice.ExactChoice(new ItemStack(Material.RESPAWN_ANCHOR)));

        Bukkit.addRecipe(recipe);
    }

    @EventHandler
    public void onFeatherCall(PlayerInteractEvent event) {
        // ブロック右クリック以外は処理を中断
        if (Arrays.stream(allowActions).noneMatch(action -> action == event.getAction())) {
            return;
        }
        // メインハンド以外ならば処理を中断
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        // スニーク中ではないならば処理を中断
        if (!(event.getPlayer().isSneaking())) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // リスポーン地点設定
            onRecordFeather(event);
        } else {
            // リスポーン地点へ転移
            onTeleportFeather(event);
        }

    }

    private void onRecordFeather(PlayerInteractEvent event) {
        // メインハンドがnullならば処理を中断
        if (event.getItem() == null) {
            return;
        }

        // メインハンドがこのアイテム以外ならば処理を中断
        if (!(event.getItem().equals(getUnsetTeleFeather()))) {
            return;
        }
        // クリックしたブロックがフルブロック以外ならば処理を中断
        if (event.getClickedBlock() == null) {
            return;
        }
        Player player = event.getPlayer();

        World world = player.getWorld();
        Location location = player.getLocation();
        // ブロックが空気なら、警告を出してリスポーン位置をセットしない
        if (new Location(world, location.getBlockX(), (location.getBlockY() - 1), location.getBlockZ()).getBlock().getType() == Material.AIR) {
            UnsafeSetWarning(player);
            return;
        }

        Block[] checkBlock = getBlocks(world, location);

        for (Block block : checkBlock) {

            // ブロックがunsafe_materialで定義されたマテリアルに属するか
            UnsafeBlocks unsafe = UnsafeBlocks.getInstance();
            if (unsafe.isUnsafeBlock(block.getType())) {
                UnsafeSetWarning(player);
                return;
            }
        }

        player.getInventory().setItemInMainHand(getSetTeleFeather(player));
        player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 1, 1);
    }

    private void onTeleportFeather(PlayerInteractEvent event) {
        // メインハンドがnullならば処理を中断
        if (event.getItem() == null) {
            return;
        }

        // メインハンドがこのアイテム以外ならば処理を中断
        if (!(checkSetFeather(event.getItem()))) {
            return;
        }

        // 左クリック以外は処理を中断
        if (!(event.getAction() == Action.LEFT_CLICK_AIR)) {
            return;
        }


        Player player = event.getPlayer();

        World world = player.getWorld();
        Location location = player.getLocation();
        // ブロックが空気なら、警告を出してリスポーン位置をセットしない
        if (new Location(world, location.getBlockX(), (location.getBlockY() - 1), location.getBlockZ()).getBlock().getType() == Material.AIR) {
            UnsafeSetWarning(player);
            return;
        }

        Block[] checkBlock = getBlocks(world, location);

        for (Block block : checkBlock) {
            // ブロックがunsafe_materialで定義されたマテリアルに属するか
            UnsafeBlocks unsafe = UnsafeBlocks.getInstance();
            if (unsafe.isUnsafeBlock(block.getType())) {
                UnsafeSetWarning(player);
                return;
            }
        }

        player.getInventory().setItemInMainHand(getSetTeleFeather(player));
        player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 1, 1);
    }

    private static Block @NotNull [] getBlocks(World world, @NotNull Location location) {
        int currentX = location.getBlockX();
        int currentY = location.getBlockY();
        int currentZ = location.getBlockZ();

        // 足元のブロック
        // テレポート先として設定するブロック
        return new Block[]{
                // 足元のブロック
                new Location(world, currentX, (currentY - 1), currentZ).getBlock(),
                // テレポート先として設定するブロック
                new Location(world, currentX, currentY, currentZ).getBlock()
        };
    }

    private void UnsafeSetWarning(Player player) {
        TeleBed.getAudiences().player(player).sendActionBar(
                Component.text("足元が安全ではありません!")
                        .color(TextColor.color(255, 0, 0))
                        .decoration(TextDecoration.ITALIC, false)
                        .decoration(TextDecoration.BOLD, true)
        );
        player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1, 1);
    }
}