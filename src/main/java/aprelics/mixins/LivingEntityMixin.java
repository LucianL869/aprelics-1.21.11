package aprelics.mixins;

import aprelics.AnkletLogic;
import aprelics.IPlayerData;
import aprelics.effects.ModStatusEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(
            method = "hurtServer",
            at = @At("HEAD"),
            cancellable = true
    )
    private void aprelics_onHurt(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity victim = (LivingEntity) (Object) this;

        if (victim instanceof Player player) {
            IPlayerData data = (IPlayerData) player;

            if (data.aprelics_getIsRevengeArmed()) {
                data.aprelics_setIsRevengeArmed(false);

                if (source.getEntity() instanceof LivingEntity attacker) {
                    cir.setReturnValue(false);

                    attacker.hurtServer(level, player.damageSources().playerAttack(player), amount * 2);

                    attacker.addEffect(new MobEffectInstance(
                            net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(aprelics.effects.ModStatusEffects.DECAY),
                            600, 1
                    ));
                }
            }
        }
    }

    @Inject(method = "checkFallDamage", at = @At("HEAD"))
    private void onLand(double d, boolean bl, BlockState blockState, BlockPos blockPos, CallbackInfo ci) {
        if ((Object) this instanceof Player player) {
            IPlayerData data = (IPlayerData) player;

            // FIX: Use your IPlayerData interface instead of ExtraCustomData
            if (data.aprelics_getIsVolcanicSlamming()) {
                aprelics.AnkletLogic.executeSlamEffects(player);

                // Reset the state so they don't explode again on the next tiny fall
                data.aprelics_setIsVolcanicSlamming(false);
            }
        }
    }
}