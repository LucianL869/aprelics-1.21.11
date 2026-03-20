//package aprelics.items;
//
//import aprelics.RelicUtil;
//import aprelics.StaffWarpTracker;
//import net.minecraft.core.component.DataComponents;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.network.chat.Component;
//import net.minecraft.sounds.SoundEvents;
//import net.minecraft.sounds.SoundSource;
//import net.minecraft.world.InteractionHand;
//import net.minecraft.world.InteractionResult;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.entity.projectile.arrow.Arrow;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.ItemUseAnimation;
//import net.minecraft.world.item.component.CustomData;
//import net.minecraft.world.level.Level;
//
//public class BookStaffItem extends Item {
//    public BookStaffItem(Properties properties) {
//        super(properties);
//    }
//
////Bow ability
//
//@Override
//public InteractionResult use(Level level, Player player, InteractionHand hand) {
//    player.startUsingItem(hand);
//    return InteractionResult.SUCCESS;
//}
//
//@Override
//public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
//    if (level.isClientSide()) return false;
//
//    if (entity instanceof Player player) {
//        if (!RelicUtil.canUseRelic(player)) return false;
//
//        int charge = this.getUseDuration(stack, entity) - timeLeft;
//        if (charge >= 10) {
//            this.shootBookProjectile(player, level);
//        }
//    }
//    return false;
//}
//
//private void shootBookProjectile(Player player, Level level) {
//    // For now, using Arrow as a placeholder
//    Arrow arrow = new Arrow(level, player, new ItemStack(net.minecraft.world.item.Items.ARROW), null);
//    arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.0F, 1.0F);
//    arrow.setBaseDamage(5.0);
//    level.addFreshEntity(arrow);
//
//    level.playSound(null, player.getX(), player.getY(), player.getZ(),
//            SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0f, 1.0f);
//}
//
//// Modes
//
//public void cycleMode(Player player, ItemStack stack) {
//    CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
//    CompoundTag tag = customData.copyTag();
//
//    int mode = 0;
//    if (tag.contains("StaffMode")) {
//        mode = tag.getInt("StaffMode");
//    }
//
//    mode = (mode + 1) % 3;
//
//    tag.putInt("StaffMode", mode);
//    stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
//
//    String modeName = switch (mode) {
//        case 1 -> "§bTeleport";
//        case 2 -> "§dBook Maze";
//        default -> "§7Off";
//    };
//
//    player.displayClientMessage(Component.literal("§6Staff Mode: " + modeName), true);
//    player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
//            SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.3f, 1.5f);
//
//
//}
//
//// Abilities
//public void useSelectedAbility(Player player, ItemStack stack) {
//    int mode = getMode(stack);
//    if (mode == 0) return;
//
//    if (mode == 1) {
//        if (StaffWarpTracker.WORLD != null) {
//            player.teleportTo(StaffWarpTracker.X, StaffWarpTracker.Y, StaffWarpTracker.Z);
//            player.level().playSound(null, player.blockPosition(), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
//        } else {
//            player.displayClientMessage(Component.literal("§cStaff anchor not set!"), true);
//        }
//    } else if (mode == 2) {
//        player.displayClientMessage(Component.literal("§dThe Library Heeds Your Call!"), true);
//        //maze Logic
//    }
//}
//
//public int getMode(ItemStack stack) {
//
//    CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
//
//    if (customData == null) return 0;
//
//    CompoundTag tag = customData.copyTag();
//    if (tag.contains("StaffMode")) {
//        return tag.getInt("StaffMode");
//    }
//
//    return 0;
//}
//
//@Override
//public ItemUseAnimation getUseAnimation(ItemStack stack) { return ItemUseAnimation.BOW; }
//@Override
//public int getUseDuration(ItemStack stack, LivingEntity entity) { return 72000; }
//}

