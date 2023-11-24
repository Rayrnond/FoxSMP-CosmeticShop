package com.reflexian.cosmeticshop.inventories;

import com.reflexian.cosmeticshop.CosmeticShop;
import com.reflexian.cosmeticshop.utilities.inventory.ClickAction;
import com.reflexian.cosmeticshop.utilities.inventory.InvUtils;
import com.reflexian.cosmeticshop.utilities.inventory.Inventory;
import com.reflexian.levitycosmetics.data.objects.cosmetics.CosmeticType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ShopInventory implements Inventory {
    @Override
    public void init(Player player) {

        final CosmeticShop instance = CosmeticShop.getInstance();
        final List<ClickAction> actions = new ArrayList<>();

        for (CosmeticType validType : instance.getConverter().getValidTypes()) {
            final String name = validType.name().toLowerCase();
            actions.add(new ClickAction("page-"+name, (p, clickAction) -> {
                new ShopCategoryInventory(validType).init(p);
            }));
        }

        InvUtils.showInventory(player, "shop",
                actions.toArray(new ClickAction[0])
        );
    }
}
