package com.reflexian.cosmeticshop.converter;

import com.reflexian.cosmeticshop.utilities.BundledCosmetic;
import com.reflexian.levitycosmetics.data.objects.cosmetics.CosmeticType;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public interface Converter {

    boolean canAfford(Player player, double price);
    void withdraw(Player player, double price);
    boolean hasCosmetic(Player player, String cosmetic);
    void giveCosmetic(Player player, String cosmetic);

    List<String> getValidCosmetics();
    LinkedList<BundledCosmetic> getTodayCosmetics(CosmeticType type);

    String formattedUntilExpires(boolean hot);
}
