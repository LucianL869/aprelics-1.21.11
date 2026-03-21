package aprelics.mixins;

import aprelics.items.BookStaffItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class HandSwingMixin {

    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;)V", at = @At("HEAD"))
    private void onSwing(InteractionHand hand, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        ItemStack stack = player.getItemInHand(hand);

        if (stack.getItem() instanceof BookStaffItem staff) {
            // Check if the player is NOT currently drawing the bow/using the item
            // This prevents the maze from triggering during a right-click!
            if (!player.isUsingItem()) {
                int mode = staff.getMode(stack);

                // Trigger for BOTH Teleport (1) and Maze (2)
                if (mode == 1 || mode == 2) {
                    staff.useSelectedAbility(player, stack);
                }
            }
        }
    }
}