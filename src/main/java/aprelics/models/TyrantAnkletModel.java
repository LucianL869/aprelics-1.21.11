package aprelics.models;

import aprelics.items.TyrantsAnkletItem;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class TyrantAnkletModel extends GeoModel<TyrantsAnkletItem> {


    @Override
    public Identifier getModelResource(GeoRenderState geoRenderState) {
        return Identifier.fromNamespaceAndPath("aprelics", "geckolib/models/armor/tyrant_anklet.geo.json");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState geoRenderState) {
        return Identifier.fromNamespaceAndPath("aprelics", "geckolib/textures/armor/tyrant_anklet.png");
    }

    @Override
    public Identifier getAnimationResource(TyrantsAnkletItem tyrantsAnkletItem) {
        return Identifier.fromNamespaceAndPath("aprelics", "geckolib/animations/armor/tyrant_anklet.animation.json");
    }
}