package aprelics.client.renderer.block;

import aprelics.APRelics;
import aprelics.blocks.entity.DisplayCaseEntity;
import aprelics.blocks.entity.DisplayCaseRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class DisplayCaseRenderer extends GeoBlockRenderer<DisplayCaseEntity, DisplayCaseRenderState> {

    public DisplayCaseRenderer(BlockEntityRendererProvider.Context context) {
        super(new DefaultedBlockGeoModel<>(
                Identifier.fromNamespaceAndPath(APRelics.MOD_ID, "display_case")
        ));
    }
}