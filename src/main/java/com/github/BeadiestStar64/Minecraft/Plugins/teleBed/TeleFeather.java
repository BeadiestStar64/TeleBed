package com.github.BeadiestStar64.Minecraft.Plugins.teleBed;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.IntStream;

public class TeleFeather implements Listener {

    // このクラスで使用するクリックの種類
    private final Action[] allowActions;

    // 転移の羽のマテリアル
    private final Material feather;

    // 未設定の転移の羽の表示名
    private final Component unSetTeleFeatherName;

    // 設定済みの転移の羽の表示名
    private final Component setTeleFeatherName;

    // 転移の羽共通の説明
    private final ArrayList<Component> teleFeatherLore;

    // 未設定の転移の羽の説明文
    private final ArrayList<Component> unsetTeleFeatherLore;

    // 転移の羽のNameSpace
    private final NamespacedKey setTeleKey;

    public TeleFeather() {
        this.allowActions = new Action[]{Action.RIGHT_CLICK_BLOCK, Action.LEFT_CLICK_AIR};
        this.feather = Material.FEATHER;
        this.unSetTeleFeatherName = Component.text("転移の羽")
                .color(TextColor.color(230, 180, 34))
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true);
        this.setTeleFeatherName = Component.text("転移の羽")
                .color(TextColor.color(103, 69, 152))
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true);
        this.teleFeatherLore = new ArrayList<>(Arrays.asList(
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
        this.unsetTeleFeatherLore = new ArrayList<>(List.of(Component.text("地面に右クリックで、テレポート先を設定")
                .color(TextColor.color(127, 255, 212))
                .decoration(TextDecoration.ITALIC, false))
        );
        this.setTeleKey = new NamespacedKey(TeleBed.getInstance(), "telebed_set_telefeather");
    }

    // 設定済みの転移の羽の説明文
    private ArrayList<Component> getSetTeleFeatherLore(TeleportData data) {
        return new ArrayList<>(Arrays.asList(
                Component.text("右クリックで、テレポート先に移動")
                        .color(TextColor.color(255, 0, 0))
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("[テレポート先]: ")
                        .color(TextColor.color(255, 255, 255))
                        .decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(data.getDimension())
                                .color(TextColor.color(230, 180, 34))
                                .decoration(TextDecoration.ITALIC, false)
                                .decoration(TextDecoration.BOLD, true)),
                Component.text("x座標: ")
                        .color(TextColor.color(255, 255, 255))
                        .decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(data.getLocationX())
                                .color(TextColor.color(230, 180, 34))
                                .decoration(TextDecoration.ITALIC, false)
                                .decoration(TextDecoration.BOLD, true))
                        .append(Component.text("    y座標: ")
                                .color(TextColor.color(255, 255, 255))
                                .decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(data.getLocationY())
                                .color(TextColor.color(230, 180, 34))
                                .decoration(TextDecoration.ITALIC, false)
                                .decoration(TextDecoration.BOLD, true))
                        .append(Component.text("    z座標: ")
                                .color(TextColor.color(255, 255, 255))
                                .decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(data.getLocationZ())
                                .color(TextColor.color(230, 180, 34))
                                .decoration(TextDecoration.ITALIC, false)
                                .decoration(TextDecoration.BOLD, true))));
    }

    // 未設定の転移の羽
    private ItemStack getUnsetTeleFeather() {
        ItemStack item = new ItemStack(feather);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(unSetTeleFeatherName);

        ArrayList<Component> lore = teleFeatherLore;
        lore.addAll(unsetTeleFeatherLore);
        meta.lore(lore);

        item.setItemMeta(meta);

        return item;
    }

    // 設定済みの転移の羽
    private ItemStack getSetTeleFeather(TeleportData data) {
        ItemStack item = new ItemStack(feather);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(setTeleFeatherName);

        ArrayList<Component> appendLore = getSetTeleFeatherLore(data);
        ArrayList<Component> lore = new ArrayList<>();
        IntStream.range(0, teleFeatherLore.size()).forEachOrdered(num -> lore.add(teleFeatherLore.get(num)));
        IntStream.range(0, appendLore.size()).forEachOrdered(num -> lore.add(appendLore.get(num)));
        meta.lore(lore);

        meta.getPersistentDataContainer().set(setTeleKey, new TelePDC(), data);

        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);

        item.setItemMeta(meta);

        if (!meta.getPersistentDataContainer().has(setTeleKey)) System.out.println("作成時点でPDCねーぞ!");

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
            if (!(meta.getPersistentDataContainer().has(setTeleKey))) {
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
        if (!isUnsetTeleFeather(event.getItem())) {
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

        TeleportData data = new TeleportData(event.getPlayer());
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand.getAmount() == 1) {
            player.getInventory().remove(mainHand);
            player.getInventory().setItemInMainHand(getSetTeleFeather(data));
        } else {
            mainHand.setAmount(mainHand.getAmount() - 1);
            player.getInventory().addItem(getSetTeleFeather(data));
        }
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

        PersistentDataContainer container = event.getItem().getItemMeta().getPersistentDataContainer();
        // PDCを含んでいない場合は、処理を中断
        if (!container.has(setTeleKey, new TelePDC())) {
            System.out.println("PDCねーぞ!");
            return;
        }
        TeleportData data = container.get(setTeleKey, new TelePDC());
        // TeleportDataがnullなら、処理を中断
        if (data == null) {
            System.out.println("ぬるぽ!");
            return;
        }
        Teleport(player, data, event.getItem());
    }

    private void Teleport(Player player, TeleportData data, ItemStack item) {
        World targetWorld = Bukkit.getWorld(data.getWorld());
        if (targetWorld == null) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BIG_FALL, 1, 1);
            TeleBed.getAudiences().player(player).sendActionBar(
                    Component.text("そのワールドは存在しない!")
                            .color(TextColor.color(255, 0, 0))
                            .decoration(TextDecoration.ITALIC, false)
            );
            return;
        }

        Location location = new Location(targetWorld, data.getLocationX(), data.getLocationY(), data.getLocationZ());

        // 安全な場所を探す
        Location safeLocation = getSafeLocation(location);
        if (safeLocation == null) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BIG_FALL, 1, 1);
            TeleBed.getAudiences().player(player).sendActionBar(
                    Component.text("安全なテレポート先が見つかりません!")
                            .color(TextColor.color(255, 0, 0))
                            .decoration(TextDecoration.ITALIC, false)
            );
            return;
        }

        // テレポートを実行
        player.teleport(safeLocation);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);

        if (item.getAmount() == 1) {
            player.getInventory().remove(item);
        } else {
            item.setAmount(item.getAmount() - 1);
        }
    }

    private Location getSafeLocation(Location location) {
        World world = location.getWorld();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        // 安全な場所を上下16ブロックの範囲で探す
        final Location[] safeLocation = new Location[1];
        IntStream.range(0, 16)
                .forEachOrdered(i -> {
                    Location check = new Location(world, x, y + i, z);
                    if (isSafeLocation(check)) {
                        safeLocation[0] = check;
                        return;
                    }
                    check = new Location(world, x, y - i, z);
                    if (isSafeLocation(check)) {
                        safeLocation[0] = check;
                        return;
                    }
                    // X軸で安全地帯を検索
                    check = new Location(world, x + i, y, z);
                    if (isSafeLocation(check)) {
                        safeLocation[0] = check;
                        return;
                    }
                    check = new Location(world, x - i, y, z);
                    if (isSafeLocation(check)) {
                        safeLocation[0] = check;
                        return;
                    }
                    // Z軸で安全地帯を検索
                    check = new Location(world, x, y, z + i);
                    if (isSafeLocation(check)) {
                        safeLocation[0] = check;
                        return;
                    }
                    check = new Location(world, x, y, z - i);
                    if (isSafeLocation(check)) {
                        safeLocation[0] = check;
                    }
                });

        return safeLocation[0];
    }

    private boolean isSafeLocation(Location location) {
        Block feet = location.getBlock();
        Block ground = feet.getRelative(BlockFace.DOWN);
        Block head = feet.getRelative(BlockFace.UP);

        return ground.getType().isSolid() &&
                !feet.getType().isSolid() &&
                !head.getType().isSolid();
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

    private boolean isUnsetTeleFeather(ItemStack item) {
        try {
            // itemがnull又は上記のマテリアル以外ならばfalse
            if (item.getType() != feather) return false;
            // メタを取得
            ItemMeta meta = item.getItemMeta();
            // 表示名が一致しないならばfalse
            if (!Objects.requireNonNull(meta.displayName()).equals(unSetTeleFeatherName)) return false;

            List<Component> components = new ArrayList<>();
            components.addAll(teleFeatherLore);
            components.addAll(unsetTeleFeatherLore);
            List<Component> checkedLore = meta.lore();
            // 説明文が完全一致しないならばfalse
            return IntStream.range(0, components.size()).anyMatch(num -> Objects.requireNonNull(checkedLore).get(num).equals(components.get(num)));
        } catch (NullPointerException exception) {
            System.out.println("nullが発生");
            return false;
        }
    }
}