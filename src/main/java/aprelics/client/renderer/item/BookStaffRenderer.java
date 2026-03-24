package aprelics.client.renderer.item;

import aprelics.items.BookStaffItem;
import aprelics.models.BookStaffModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class BookStaffRenderer extends GeoItemRenderer<BookStaffItem> {
    public BookStaffRenderer() {

        super(new BookStaffModel());
    }
}
