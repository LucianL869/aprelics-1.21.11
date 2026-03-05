package aprelics;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

public class ModItems {

    public static final Identifier HALO_ID = Identifier.fromNamespaceAndPath("aprelics", "verdant_halo");

    public static final ResourceKey<Item> VERDANT_HALO_KEY = ResourceKey.create(Registries.ITEM, HALO_ID);

    public static final Item VERDANT_HALO = new Item(new Item.Properties().setId(VERDANT_HALO_KEY).stacksTo(1)
            .equippable(EquipmentSlot.HEAD));

    public static void register() {
        Registry.register(BuiltInRegistries.ITEM, VERDANT_HALO_KEY, VERDANT_HALO);
    }
}