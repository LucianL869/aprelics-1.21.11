package aprelics;

import aprelics.items.ReapersScytheItem;
import aprelics.items.VerdantHaloItem;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.Equippable;

import java.util.function.Function;

public class ModItems {

    // Define them as null initially
    public static Item VERDANT_HALO;
    public static Item TYRANTS_ANKLET;
    public static Item REAPERS_SCYTHE;

    public static void register() {
        VERDANT_HALO = registerItem("verdant_halo", props ->
                new VerdantHaloItem(props.stacksTo(1).component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD).build())));

        TYRANTS_ANKLET = registerItem("tyrants_anklet", props ->
                new Item(props.stacksTo(1).component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.FEET).build())));

        REAPERS_SCYTHE = registerItem("reapers_scythe", ReapersScytheItem::new);

        RelicUtil.registerRelic(VERDANT_HALO);
        RelicUtil.registerRelic(TYRANTS_ANKLET);
        RelicUtil.registerRelic(REAPERS_SCYTHE);
    }

    private static Item registerItem(String name, Function<Item.Properties, Item> itemFactory) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.tryParse("aprelics:" + name));
        Item.Properties props = new Item.Properties().setId(key);
        return Registry.register(BuiltInRegistries.ITEM, key, itemFactory.apply(props));
    }
}