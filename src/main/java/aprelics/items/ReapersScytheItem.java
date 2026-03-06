package aprelics.items;

import aprelics.IPlayerData;
import aprelics.effects.ModStatusEffects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReapersScytheItem extends Item {

    private final Map<UUID, Long> decayCooldownMap = new HashMap<>();

    // Cooldown is 60 seconds + 5 seconds (effect duration) = 65,000ms
    private static final long DECAY_TOTAL_COOLDOWN_MS = 65000;

    public ReapersScytheItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof ServerPlayer player) {
            IPlayerData data = (IPlayerData) player;

            if (!data.aprelics_getIsRevengeArmed()) {
                // These variables are now inside the correct scope
                long currentTime = System.currentTimeMillis();
                UUID targetId = target.getUUID();

                if (!decayCooldownMap.containsKey(targetId) || currentTime - decayCooldownMap.get(targetId) >= DECAY_TOTAL_COOLDOWN_MS) {
                    target.addEffect(new MobEffectInstance(
                            BuiltInRegistries.MOB_EFFECT.wrapAsHolder(ModStatusEffects.DECAY),
                            100, 0));
                    target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 2));

                    decayCooldownMap.put(targetId, currentTime);
                }
            }
        }
        super.hurtEnemy(stack, target, attacker);
    }
}