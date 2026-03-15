package aprelics;

import aprelics.items.TyrantsAnkletItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ItemRegistry {

    public static final Item TYRANTS_ANKLET = new TyrantsAnkletItem(
            new Item.Properties().setId(ResourceKey.create(Registries.ITEM,
                    Identifier.fromNamespaceAndPath(APRelics.MOD_ID, "tyrants_anklet")))
    );


    public static void registerAll() {
        Registry.register(BuiltInRegistries.ITEM,
                Identifier.fromNamespaceAndPath(APRelics.MOD_ID, "tyrants_anklet"),
                TYRANTS_ANKLET);


    }
}
