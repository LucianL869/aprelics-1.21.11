package aprelics;

import aprelics.items.TyrantsAnkletItem;
import aprelics.models.TyrantAnkletModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class APRelicsClientHelper {
    public static GeoRenderProvider getAnkletRenderer() {
        return new GeoRenderProvider() {
            private GeoArmorRenderer<TyrantsAnkletItem, HumanoidRenderState> renderer;

            public GeoArmorRenderer<TyrantsAnkletItem, HumanoidRenderState> getGeoArmorRenderer() {
                if (this.renderer == null) {
                    this.renderer = new GeoArmorRenderer<>(new TyrantAnkletModel());

                }
                return this.renderer;
            }
        };
    }
}