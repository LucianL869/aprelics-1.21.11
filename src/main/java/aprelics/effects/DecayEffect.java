package aprelics.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;

public class DecayEffect extends MobEffect {

    public DecayEffect() {
        super(MobEffectCategory.HARMFUL, 0x484D48); // Sickly green
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        // 1. Add Slowness (Level 2 = Slowness III)
        entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 2, false, false, true));

        // 2. Damage Logic (Wither-like, but won't kill)
        // If the entity has more than 1 health, damage them.
        // This stops at 1.0 HP, preventing the effect from ever killing the target.
        if (entity.getHealth() > 1.0F) {
            entity.hurt(entity.damageSources().magic(), 1.0F);
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // Apply every 2 seconds (40 ticks) for a "slower" feel than Wither
        return duration % 40 == 0;
    }
}