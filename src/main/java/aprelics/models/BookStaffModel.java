package aprelics.models;

import aprelics.items.BookStaffItem;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class BookStaffModel extends GeoModel<BookStaffItem> {


    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath("aprelics", "geckolib/models/item/book_staff.geo.json");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath("aprelics", "textures/item/book_staff.png");
    }

    @Override
    public Identifier getAnimationResource(BookStaffItem animatable) {
        return Identifier.fromNamespaceAndPath("aprelics", "geckolib/animations/item/book_staff.animation.json");
    }
}
