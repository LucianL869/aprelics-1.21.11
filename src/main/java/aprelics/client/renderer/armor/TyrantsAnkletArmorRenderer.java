package aprelics.client.renderer.armor;

import aprelics.APRelics;
import aprelics.models.TyrantAnkletModel;
import aprelics.items.TyrantsAnkletItem;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.equipment.ArmorType;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;

import java.util.List;

public class TyrantsAnkletArmorRenderer <R extends HumanoidRenderState & GeoRenderState> extends GeoArmorRenderer<TyrantsAnkletItem, R> {
    public TyrantsAnkletArmorRenderer() {
        super(new DefaultedItemGeoModel<>(Identifier.fromNamespaceAndPath(APRelics.MOD_ID, "armor/tyrants_anklet")));
        withRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public List<ArmorSegment> getSegmentsForSlot(R renderState, EquipmentSlot slot) {

        if (slot == EquipmentSlot.FEET) {

            return List.of(
                    ArmorSegment.HEAD,
                    ArmorSegment.CHEST,
                    ArmorSegment.RIGHT_FOOT
            );
        }


        return super.getSegmentsForSlot(renderState, slot);
    }
}
