package aprelics.mixins;

import aprelics.AnkletLogic;
import aprelics.IPlayerData;
import aprelics.ModItems;
import aprelics.effects.ModStatusEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void aprelics_onHurt(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity victim = (LivingEntity) (Object) this;

        if (victim instanceof Player player) {
            IPlayerData data = (IPlayerData) player;

            if (data.aprelics_getIsRevengeArmed()) {
                // Check if the 5-second window is still active
                if (data.aprelics_getRevengeDuration() > 0) {
                    // Window is active: Trigger Revenge
                    data.aprelics_setIsRevengeArmed(false);
                    data.aprelics_setRevengeDuration(0); // Reset duration

                    if (source.getEntity() instanceof LivingEntity attacker) {
                        cir.setReturnValue(false); // Cancel original damage

                        attacker.hurt(player.damageSources().playerAttack(player), amount * 2);
                        attacker.addEffect(new MobEffectInstance(
                                BuiltInRegistries.MOB_EFFECT.wrapAsHolder(ModStatusEffects.DECAY),
                                600, 1
                        ));

                        player.level().playSound(null, player.blockPosition(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 1.0f, 0.5f);
                    }
                } else {
                    // Window expired, cleanup state
                    data.aprelics_setIsRevengeArmed(false);
                    data.aprelics_setRevengeDuration(0);
                }
            }
        }
    }
}