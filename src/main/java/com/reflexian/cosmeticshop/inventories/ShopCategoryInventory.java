package com.reflexian.cosmeticshop.inventories;

import com.reflexian.cosmeticshop.CosmeticShop;
import com.reflexian.cosmeticshop.utilities.BundledCosmetic;
import com.reflexian.cosmeticshop.utilities.inventory.ClickAction;
import com.reflexian.cosmeticshop.utilities.inventory.InvUtils;
import com.reflexian.cosmeticshop.utilities.inventory.Inventory;
import com.reflexian.levitycosmetics.data.objects.cosmetics.CosmeticType;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Getter
public class ShopCategoryInventory implements Inventory {

    private final CosmeticType cosmeticType;
    public ShopCategoryInventory(CosmeticType cosmeticType) {
        this.cosmeticType = cosmeticType;
    }

    @Override
    public void init(Player player) {

        final List<ClickAction> actions = new ArrayList<>();

        int i = 0;

        LinkedList<BundledCosmetic> cosmetics = CosmeticShop.getInstance().getConverter().getTodayCosmetics(cosmeticType);

        for (BundledCosmetic todayCosmetic : cosmetics) {
            actions.add(new ClickAction("cosmeticItem"+i, (p, clickAction) -> new ConfirmationInventory(todayCosmetic).init(p)));
            i++;
        }

        actions.add(new ClickAction("nicknameTicketItem", (p, clickAction) -> {
             new ConfirmationInventory(true).init(p);
        }));

        actions.add(new ClickAction("back", (p, clickAction) -> {
            new ShopInventory().init(p);
        }));

        String page = "page";
        if (cosmeticType == CosmeticType.TITLE) page = "titles";
        else if (cosmeticType == CosmeticType.NICKNAME_PAINT) page = "nicknames";
        else if (cosmeticType == CosmeticType.CROWN) page = "crowns";

        InventoryProvider provider = InvUtils.showInventory(player, page, cosmetics, actions.toArray(new ClickAction[0]));
    }
}
