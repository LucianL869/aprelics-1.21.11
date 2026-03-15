package aprelics.mixins;

import aprelics.AnkletLogic;
import aprelics.IPlayerData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin implements IPlayerData {

    private  int revengeDuration;

    @Unique private int aprelics_cooldown = 0;
    @Unique private long aprelics_lastRevengeTime = 0L;
    @Unique private boolean aprelics_isVolcanicSlamming = false;
    @Unique private boolean aprelics_isRevengeArmed = false;
    @Unique private int aprelics_airborneTicks = 0;

    @Override
    public int aprelics_getRevengeDuration() {
        return this.revengeDuration;
    }

    @Override
    public void aprelics_setRevengeDuration(int duration) {
        this.revengeDuration = duration;
    }

    @Override
    public void aprelics_setIsVolcanicSlamming(boolean value) { this.aprelics_isVolcanicSlamming = value; }

    @Override
    public boolean aprelics_getIsVolcanicSlamming() { return this.aprelics_isVolcanicSlamming; }

    @Override
    public void aprelics_setIsRevengeArmed(boolean value) { this.aprelics_isRevengeArmed = value; }

    @Override
    public boolean aprelics_getIsRevengeArmed() { return this.aprelics_isRevengeArmed; }

    @Override
    public void aprelics_setLastRevengeTime(long time) {
        this.aprelics_lastRevengeTime = time;
    }

    @Override
    public long aprelics_getLastRevengeTime() {
        return this.aprelics_lastRevengeTime;
    }

    @Override
    public void aprelics_setCooldown(int ticks) {
        this.aprelics_cooldown = ticks;
    }

    @Override
    public int aprelics_getCooldown() {
        return this.aprelics_cooldown;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo info) {
        Player player = (Player) (Object) this;

        if (this.aprelics_isVolcanicSlamming) {


            if (!player.level().isClientSide() && player.level().getGameTime() % 2 == 0) {
                ServerLevel serverLevel = (ServerLevel) player.level();
                serverLevel.sendParticles(ParticleTypes.FLAME,
                        player.getX(), player.getY() + 0.2, player.getZ(),
                        3, 0.1, 0.1, 0.1, 0.05);
            }


            if (!player.onGround()) {
                this.aprelics_airborneTicks++;
            }


            if (player.onGround() && this.aprelics_airborneTicks >= 10) {
                this.aprelics_isVolcanicSlamming = false;
                this.aprelics_airborneTicks = 0;

                if (!player.level().isClientSide()) {
                    AnkletLogic.executeSlamEffects(player);
                }
            }
        } else {
            this.aprelics_airborneTicks = 0;
        }
    }

    @Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
    private void onFall(double d, float f, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {

        if (this.aprelics_isVolcanicSlamming) {
            cir.setReturnValue(false);
        }
    }
}