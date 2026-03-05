package aprelics.items;

import aprelics.effects.ModStatusEffects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The Reaper's Scythe relic.
 * Passive logic is handled here in hurtEnemy.
 * Active ability logic is handled in AbilityUtil.java.
 */
public class ReapersScytheItem extends Item {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long DECAY_COOLDOWN = 60000; // 1 minute per entity

    public ReapersScytheItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        long currentTime = System.currentTimeMillis();
        UUID targetId = target.getUUID();

        // Apply Decay effect on hit, respecting the per-entity cooldown
        if (!cooldowns.containsKey(targetId) || currentTime - cooldowns.get(targetId) >= DECAY_COOLDOWN) {
            target.addEffect(new MobEffectInstance(
                    BuiltInRegistries.MOB_EFFECT.wrapAsHolder(ModStatusEffects.DECAY),
                    100, 0) // 5 seconds, amplifier 0
            );
            cooldowns.put(targetId, currentTime);
        }

        // Call the original logic (handles durability loss)
        super.hurtEnemy(stack, target, attacker);
    }
}