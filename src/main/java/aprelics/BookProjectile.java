package aprelics;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class BookProjectile extends AbstractArrow implements ItemSupplier {

    public BookProjectile(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
    }

    public BookProjectile(Level level, LivingEntity owner) {

        super(ModEntities.BOOK_PROJECTILE, owner, level, new ItemStack(Items.BOOK), null);
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Items.BOOK);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        this.pop();
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        this.pop();
    }

    private void pop() {
        if (!this.level().isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) this.level();


            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0f, 0.8f);


            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.5f, 1.2f);

            serverLevel.sendParticles(ParticleTypes.ENCHANT,
                    this.getX(), this.getY(), this.getZ(),
                    15, 0.2, 0.2, 0.2, 0.1);
        }
        this.discard();
    }

    @Override
    protected net.minecraft.sounds.SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.BOOK_PAGE_TURN;
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return ItemStack.EMPTY;
    }
}