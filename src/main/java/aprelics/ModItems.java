package aprelics;

import aprelics.items.ReapersScytheItem;
import aprelics.items.TyrantsAnkletItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.Objects;

public class ModItems {

    // FIX: Link the items to their specific custom classes!
    public static final Item VERDANT_HALO = new aprelics.items.VerdantHaloItem(new Item.Properties().stacksTo(1));
    public static final Item TYRANTS_ANKLET = new TyrantsAnkletItem(new Item.Properties().stacksTo(1));
    public static final Item REAPERS_SCYTHE = new ReapersScytheItem(new Item.Properties().stacksTo(1));

    public static void register() {
        // These look good, but make sure the strings match your .json filenames exactly!
        Registry.register(BuiltInRegistries.ITEM, Objects.requireNonNull(Identifier.tryParse("aprelics:verdant_halo")), VERDANT_HALO);
        Registry.register(BuiltInRegistries.ITEM, Objects.requireNonNull(Identifier.tryParse("aprelics:tyrants_anklet")), TYRANTS_ANKLET);
        Registry.register(BuiltInRegistries.ITEM, Objects.requireNonNull(Identifier.tryParse("aprelics:reapers_scythe")), REAPERS_SCYTHE);

        // Utility registration
        RelicUtil.registerRelic(VERDANT_HALO);
        RelicUtil.registerRelic(TYRANTS_ANKLET);
        RelicUtil.registerRelic(REAPERS_SCYTHE);
    }
}