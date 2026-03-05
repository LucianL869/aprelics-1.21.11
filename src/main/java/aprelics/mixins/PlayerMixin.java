package aprelics.mixins;

import aprelics.IPlayerData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Player.class)
public abstract class PlayerMixin implements IPlayerData {

    @Unique
    private boolean aprelics_isVolcanicSlamming = false;
    @Unique
    private boolean aprelics_wasOnGround = true;
    @Unique
    private boolean aprelics_isRevengeArmed = false;

    // --- Interface Implementation ---
    @Override
    public void aprelics_setIsVolcanicSlamming(boolean value) { this.aprelics_isVolcanicSlamming = value; }
    @Override
    public void aprelics_setIsRevengeArmed(boolean value) { this.aprelics_isRevengeArmed = value; }
    @Override
    public boolean aprelics_getIsRevengeArmed() { return this.aprelics_isRevengeArmed; }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo info) {
        Player player = (Player) (Object) this;

        if (this.aprelics_isVolcanicSlamming) {
            // Check if we just hit the ground
            if (player.onGround() && !this.aprelics_wasOnGround) {
                this.aprelics_isVolcanicSlamming = false;

                Level world = player.level();
                // 1.21.11 FIX: world.isClientSide is a public field in MojMap
                if (!world.isClientSide()) {
                    AABB area = player.getBoundingBox().inflate(5.0);
                    List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, area, e -> e != player);

                    for (LivingEntity entity : entities) {
                        entity.hurt(player.damageSources().playerAttack(player), 8.0f);

                        // 1.21.11 FIX: addDeltaMovement needs a Vec3 object
                        entity.addDeltaMovement(new Vec3(0, 0.6, 0));

                        // 1.21.11 FIX: setSecondsOnFire is now igniteForSeconds
                        entity.igniteForSeconds(5);
                    }

                    // 1.21.11 FIX: playSound parameters
                    world.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0f, 1.0f);
                }
            }
        }
        this.aprelics_wasOnGround = player.onGround();
    }
}