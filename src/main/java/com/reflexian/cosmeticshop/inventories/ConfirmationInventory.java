package com.reflexian.cosmeticshop.inventories;

import com.reflexian.cosmeticshop.CosmeticShop;
import com.reflexian.cosmeticshop.converter.ConverterImpl;
import com.reflexian.cosmeticshop.utilities.BundledCosmetic;
import com.reflexian.cosmeticshop.utilities.inventory.ClickAction;
import com.reflexian.cosmeticshop.utilities.inventory.InvUtils;
import com.reflexian.cosmeticshop.utilities.inventory.Inventory;
import com.reflexian.levitycosmetics.data.objects.cosmetics.helpers.Cosmetic;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.LinkedList;

@Getter
public class ConfirmationInventory implements Inventory {

    private final BundledCosmetic cosmetic;
    private final ConverterImpl converter;
    private boolean ticket = false;

    public ConfirmationInventory(BundledCosmetic cosmetic) {
        this.cosmetic = cosmetic;
        this.converter = CosmeticShop.getInstance().getConverter();
    }

    public ConfirmationInventory(boolean ticket) {
        this.ticket = ticket;
        this.cosmetic=null;
        this.converter = CosmeticShop.getInstance().getConverter();
    }

    @Override
    public void init(Player player) {
        final LinkedList<BundledCosmetic> cosmetics = new LinkedList<>();
        if (cosmetic != null) cosmetics.add(cosmetic);
        InvUtils.showInventory(player, "confirmation", cosmetics,
                new ClickAction("confirm", (p, clickAction) -> {
                    p.closeInventory();

                    if (ticket) {
                        final int price = CosmeticShop.getDEFAULT_CONFIG().getNicknameTicketPrice();
                        if (converter.canAfford(p, price)) {
                            converter.withdraw(p, price);
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "nicknameticket " + p.getName());
                            p.sendMessage("§aYou have purchased nickname ticket for " + price + " points!");
                        } else {
                            p.sendMessage("§cYou do not have enough points to purchase this cosmetic!");
                        }
                        return;
                    }

                    if (converter.hasCosmetic(p, cosmetic.getCosmetic().getName())) {
                        p.sendMessage("§cYou already own this cosmetic!");
                        return;
                    }
                    if (converter.canAfford(p, cosmetic.getPrice())) {
                        converter.withdraw(p, cosmetic.getPrice());
                        converter.giveCosmetic(p, cosmetic.getCosmetic().getName());
                        p.sendMessage("§aYou have purchased " + cosmetic.getCosmetic().getName() + " for " + cosmetic.getPrice() + " points!");
                    } else {
                        p.sendMessage("§cYou do not have enough points to purchase this cosmetic!");
                    }
                }),
                new ClickAction("decline", (p, clickAction) -> {
                    p.closeInventory();
                })
                );
    }
}
