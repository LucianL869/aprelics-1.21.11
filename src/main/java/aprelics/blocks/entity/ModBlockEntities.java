package aprelics.blocks.entity;

import aprelics.APRelics;
import aprelics.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static BlockEntityType<DisplayCaseEntity> DISPLAY_CASE_ENTITY;

    public static void registerBlockEntities() {
        DISPLAY_CASE_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                Identifier.fromNamespaceAndPath(APRelics.MOD_ID, "display_case"),
                FabricBlockEntityTypeBuilder.create(DisplayCaseEntity::new, ModBlocks.DISPLAY_CASE).build()
        );
    }
}
