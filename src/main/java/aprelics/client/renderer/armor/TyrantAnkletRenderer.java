package aprelics.client.renderer.armor;

import aprelics.APRelics;
import aprelics.items.TyrantsAnkletItem;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;

public class TyrantAnkletRenderer<R extends HumanoidRenderState & GeoRenderState> extends GeoArmorRenderer<TyrantsAnkletItem, R> {
    public TyrantAnkletRenderer() {
        super(new DefaultedItemGeoModel<>(Identifier.fromNamespaceAndPath(APRelics.MOD_ID, "armor/tyrant_anklet")));

        withRenderLayer(AutoGlowingGeoLayer::new);
    }
}


