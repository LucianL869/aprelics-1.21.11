package aprelics.client.renderer.armor;

import aprelics.items.TyrantsAnkletItem;
import aprelics.models.TyrantAnkletModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class TyrantsAnkletArmorRenderer <R extends HumanoidRenderState & GeoRenderState> extends GeoArmorRenderer<TyrantsAnkletItem, R> {
    public TyrantsAnkletArmorRenderer() {
        super(new TyrantAnkletModel());
    }
}