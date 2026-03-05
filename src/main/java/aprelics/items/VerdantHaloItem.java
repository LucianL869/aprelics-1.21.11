package aprelics.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

public class VerdantHaloItem extends Item {
    public VerdantHaloItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        // We leave this empty because your HaloLogic.java handles the
        // passive healing via the ServerTickEvent.register() method!
    }
}