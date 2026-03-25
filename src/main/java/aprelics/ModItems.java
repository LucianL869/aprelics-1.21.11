package aprelics;

import aprelics.items.*;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;

import java.util.function.Function;

public class ModItems {

    private static boolean isRegistered = false;


    public static Item VERDANT_HALO;
    public static Item TYRANTS_ANKLET;
    public static Item REAPERS_SCYTHE;
    public static Item AMBRIA_CROWN;
    public static Item BOOK_STAFF;
    public static Item GROW_HORN;

    public static void register() {
        if (isRegistered) return;

        VERDANT_HALO = registerItem("verdant_halo", props ->
                new VerdantHaloItem(props.stacksTo(1).component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD).build())));

        TYRANTS_ANKLET = registerItem("tyrants_anklet", props ->
                new TyrantsAnkletItem(ArmorMaterials.COPPER, ArmorType.BOOTS,
                        props.stacksTo(1).component(DataComponents.MAX_DAMAGE, null)));

        REAPERS_SCYTHE = registerItem("reapers_scythe", ReapersScytheItem::new);

        AMBRIA_CROWN = registerItem("ambria_crown", props ->
                new Item(props.stacksTo(1).component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD).build())));

        BOOK_STAFF = registerItem("book_staff", props ->
                new BookStaffItem(props.stacksTo(1)));

        GROW_HORN = registerItem("grown_horn", props ->
                new GrowHornItem(props.stacksTo(1)));

        RelicUtil.registerRelic(VERDANT_HALO);
        RelicUtil.registerRelic(TYRANTS_ANKLET);
        RelicUtil.registerRelic(REAPERS_SCYTHE);
        RelicUtil.registerRelic(AMBRIA_CROWN);
        RelicUtil.registerRelic(BOOK_STAFF);
        RelicUtil.registerRelic(GROW_HORN);

        isRegistered = true;
    }

    private static Item registerItem(String name, Function<Item.Properties, Item> itemFactory) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.tryParse("aprelics:" + name));
        Item.Properties props = new Item.Properties().setId(key);
        return Registry.register(BuiltInRegistries.ITEM, key, itemFactory.apply(props));
    }

    public static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {

        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(APRelics.MOD_ID, name));

        T item = itemFactory.apply(settings.setId(itemKey));

        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }
}