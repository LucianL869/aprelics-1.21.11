package aprelics.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;

public class DecayEffect extends MobEffect {

    public DecayEffect() {
        super(MobEffectCategory.HARMFUL, 0x484D48); // A dark, sickly green
    }

    // 1.21.11 FIX: The signature now requires the ServerLevel
    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        // Apply slowness effect
        entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, amplifier, false, false, true));

        // Do not deal damage if health is 1 or less
        if (entity.getHealth() > 1.0F) {
            entity.hurtServer(level, entity.damageSources().magic(), 1.0F + amplifier);
        }
        return true; // Return true to indicate the effect was applied
    }

    // 1.21.11 FIX: Method renamed from isDurationEffectTick
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // Ticks every 2 seconds (40 ticks)
        int interval = 40;
        return duration % interval == 0;
    }
}