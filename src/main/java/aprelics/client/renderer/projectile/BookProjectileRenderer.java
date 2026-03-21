package aprelics.client.renderer.projectile;

import aprelics.BookProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class BookProjectileRenderer extends ThrownItemRenderer<BookProjectile> {
    public BookProjectileRenderer(EntityRendererProvider.Context context) {
        // This makes the entity look like a Book!
        super(context, 1.0f, true);
    }
}
