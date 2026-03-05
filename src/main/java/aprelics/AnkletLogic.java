package aprelics;

import aprelics.ai.goal.FleeFromPlayerGoal;
import aprelics.effects.ModStatusEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class AnkletLogic {

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.TYRANTS_ANKLET)) {
                    applyPassiveEffects(player);
                }
            }
        });
    }

    private static void applyPassiveEffects(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(
                BuiltInRegistries.MOB_EFFECT.wrapAsHolder(ModStatusEffects.TYRANTS_MIGHT),
                40, 0, false, false, true
        ));

        AABB scanBox = new AABB(player.blockPosition()).inflate(8.0);
        List<Monster> nearbyHostiles = player.level().getEntitiesOfClass(Monster.class, scanBox);

        for (Monster mob : nearbyHostiles) {
            if (mob.getHealth() < mob.getMaxHealth() * 0.25) {
                // Now that goalSelector is 'accessible', we don't need fancy casts
                boolean hasFleeGoal = mob.goalSelector.getAvailableGoals().stream()
                        .anyMatch(wrapped -> wrapped.getGoal() instanceof FleeFromPlayerGoal);

                if (!hasFleeGoal) {
                    mob.goalSelector.addGoal(1, new FleeFromPlayerGoal(mob, player, 1.2, 10.0));
                }
            }
        }
    }
    public static void executeSlamEffects(Player player) {
        Level level = player.level();

        // 1. Visuals & Sound
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 1.0f, 1.0f);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION, player.getX(), player.getY(), player.getZ(), 1, 0, 0, 0, 0);
            serverLevel.sendParticles(ParticleTypes.FLAME, player.getX(), player.getY(), player.getZ(), 50, 1.0, 0.5, 1.0, 0.2);
        }

        // 2. Damage & Knockback Area
        double radius = 5.0;
        List<Entity> targets = level.getEntities(player, player.getBoundingBox().inflate(radius));

        for (Entity target : targets) {
            if (target instanceof LivingEntity living) {
                // Deal damage
                living.hurt(player.damageSources().explosion(player, player), 6.0f);

                // Launch into the air
                living.setDeltaMovement(living.getDeltaMovement().add(0, 0.8, 0));

                // Set on fire (briefly)
                living.igniteForSeconds(3);
            }
        }
    }

    public static void useVolcanicSlam(Player player) {
        ItemStack ankletStack = player.getItemBySlot(EquipmentSlot.FEET);

        // Use the ItemStack for the cooldown check
        if (player.getCooldowns().isOnCooldown(ankletStack)) return;

        // The Leap
        Vec3 look = player.getLookAngle();
        player.setDeltaMovement(new Vec3(look.x * 0.5, 1.2, look.z * 0.5));

        // Tag the player using your IPlayerData interface
        ((IPlayerData) player).aprelics_setIsVolcanicSlamming(true);

        // FIX: Pass the ItemStack for the cooldown
        player.getCooldowns().addCooldown(ankletStack, 400);
    }
}