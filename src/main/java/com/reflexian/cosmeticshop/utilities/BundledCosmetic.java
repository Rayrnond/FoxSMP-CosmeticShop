package com.reflexian.cosmeticshop.utilities;

import com.reflexian.levitycosmetics.data.objects.cosmetics.helpers.Cosmetic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder@AllArgsConstructor@Getter
public class BundledCosmetic {

    private Cosmetic cosmetic;
    private double price;
    private boolean hot = false;
    private boolean invalid = false;


}
