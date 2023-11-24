package com.reflexian.cosmeticshop.utilities.inventory;

import com.reflexian.cosmeticshop.CosmeticShop;
import com.reflexian.cosmeticshop.utilities.BundledCosmetic;
import com.reflexian.cosmeticshop.utilities.ItemBuilder;
import com.reflexian.levitycosmetics.utilities.uncategorizied.GradientUtils;
import dev.lone.itemsadder.api.CustomStack;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class InvLang {

    /**
     *
     * path:
     *  type:
     *  amount:
     *  data:
     *  name:
     *  lore:
     *  - ""
     *  - ""
     *
     */

    public static ItemStack itemStackInvs(YamlConfiguration c, String s, @Nullable BundledCosmetic cosmetic, Player player) {
        ItemBuilder builder = new ItemBuilder(Material.valueOf(c.getString(s + ".material").toUpperCase()));



        if (c.contains(s + ".itemsadderID")) {
            String id = c.getString(s + ".itemsadderID");
            if (id != null && !id.isEmpty()) {
                CustomStack stack = CustomStack.getInstance(id);

                if (stack == null) {
                    throw new RuntimeException("Failed to serialize item with ID: " + s + ". Could not find ItemsAdder item with id " + id);
                }

                builder = new ItemBuilder(stack.getItemStack());
            }
        }

        if (c.contains(s + ".data")) builder.data(new MaterialData(builder.getMaterial(), (byte) c.getInt(s + ".data")));
        if (c.contains(s + ".amount")) builder.amount(c.getInt(s + ".amount"));
        if (c.contains(s + ".displayname")&&cosmetic == null) builder.displayname(format(c.getString(s + ".displayname"), player));
        else if (c.contains(s + ".displayname")&&cosmetic != null) builder.displayname(cosmetic.getCosmetic().getItemStack().getItemMeta().getDisplayName());

        if (cosmetic!=null) {
            builder = new ItemBuilder(cosmetic.getCosmetic().getItemStack());
            builder.clearLore();
        }

        if (c.contains(s + ".lore")) {
            List<String> lore = format(c.getStringList(s + ".lore"),player);
            for (String s1 : lore) {
                if (cosmetic!=null) {
                    s1=s1.replace("%name%", String.valueOf(cosmetic.getCosmetic().getName()));
                    s1=s1.replace("%displayName%", String.valueOf(cosmetic.getCosmetic().getItemStack().getItemMeta().getDisplayName()));
                    s1=s1.replace("%rarity%", String.valueOf(cosmetic.getCosmetic().getRarity()));
                    s1=s1.replace("%price%", String.valueOf(cosmetic.getPrice()));
                    s1=s1.replace("%type%", cosmetic.getCosmetic().getType().name());
                    s1=s1.replace("%timeLeft%", CosmeticShop.getInstance().getConverter().formattedUntilExpires(cosmetic.isHot()));
                } else {
                    s1=s1.replace("%name%", "§cNo Cosmetic");
                    s1=s1.replace("%displayName%", "");
                    s1=s1.replace("%rarity%", "");
                    s1=s1.replace("%price%", "");
                    s1=s1.replace("%type%", "");
                    s1=s1.replace("%timeLeft%", CosmeticShop.getInstance().getConverter().formattedUntilExpires(false));
                }
                builder.lore(s1);
            }
        }
        if (c.getBoolean(s + ".glow")) builder.glow();

        return builder.build();
    }



    private String format(String s,Player player) {
        if (player!=null) s=PlaceholderAPI.setPlaceholders(player,s);
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private List<String> format(List<String> lore,Player player) {
        List<String> l = new ArrayList<>();
        for (String s : lore) {
            if (player!=null) s=PlaceholderAPI.setPlaceholders(player,s);
            l.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return l;
    }

}
