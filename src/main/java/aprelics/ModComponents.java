package aprelics;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class ModComponents {
    public static final DataComponentType<Integer> STAFF_MODE = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(APRelics.MOD_ID, "staff_mode"),
            DataComponentType.<Integer>builder().persistent(Codec.INT).build()
    );

    public static void register() {}
}
