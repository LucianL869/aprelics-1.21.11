package aprelics;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {
    // 1. Define the ResourceLocation first
    public static final Identifier BOOK_PROJECTILE_ID = Identifier.fromNamespaceAndPath("aprelics", "book_projectile");

    // 2. Create the EntityType using the ResourceKey version of .build()
    public static final EntityType<BookProjectile> BOOK_PROJECTILE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            BOOK_PROJECTILE_ID,
            EntityType.Builder.<BookProjectile>of(BookProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    // This creates the ResourceKey the compiler is looking for
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, BOOK_PROJECTILE_ID))
    );
    public static void registerModEntities() {
        // Call this in your main Mod Initializer
    }
}
