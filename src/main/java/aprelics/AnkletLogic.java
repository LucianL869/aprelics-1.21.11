package aprelics;

import aprelics.ai.goal.FleeFromPlayerGoal;
import aprelics.effects.ModStatusEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AnkletLogic {

    private static final HashMap<UUID, Long> COOLDOWN_MAP = new HashMap<>();
    private static final HashMap<UUID, Boolean> NOTIFIED_READY = new HashMap<>();
    private static final long SLAM_COOLDOWN = 20000; // 20 seconds

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.TYRANTS_ANKLET)) {
                    applyPassiveEffects(player);
                    checkCooldownNotification(player);
                }
            }
        });
    }

    private static void checkCooldownNotification(ServerPlayer player) {
        UUID uuid = player.getUUID();
        if (COOLDOWN_MAP.containsKey(uuid)) {
            long lastUse = COOLDOWN_MAP.get(uuid);
            long timePassed = System.currentTimeMillis() - lastUse;

            if (timePassed >= SLAM_COOLDOWN && !NOTIFIED_READY.getOrDefault(uuid, false)) {
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 0.5f);
                player.displayClientMessage(Component.literal("§4✔ §cTyrant's Anklet is ready!").withStyle(ChatFormatting.RED), true);
                NOTIFIED_READY.put(uuid, true);
            }
        }
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
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 3.0f, 0.7f);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    player.getX(), player.getY() + 0.1, player.getZ(),
                    20, 0.5, 0.1, 0.5, 0.05);

            serverLevel.sendParticles(ParticleTypes.CLOUD,
                    player.getX(), player.getY() + 0.1, player.getZ(),
                    30, 0.8, 0.2, 0.8, 0.1);
            serverLevel.sendParticles(ParticleTypes.POOF,
                    player.getX(), player.getY() + 0.1, player.getZ(),
                    10, 0.3, 0.1, 0.3, 0.1);

            for (int i = 0; i < 360; i += 15) {
                double angle = Math.toRadians(i);
                double xOffset = Math.cos(angle) * 2.0;
                double zOffset = Math.sin(angle) * 2.0;

                serverLevel.sendParticles(ParticleTypes.FLAME,
                        player.getX() + xOffset, player.getY() + 0.1, player.getZ() + zOffset,
                        1, 0, 0, 0, 0.05);
                serverLevel.sendParticles(ParticleTypes.LAVA,
                        player.getX() + (xOffset * 0.5), player.getY() + 0.1, player.getZ() + (zOffset * 0.5),
                        1, 0, 0, 0, 0);
            }
        }

        double radius = 8.0;
        List<Entity> targets = level.getEntities(player, player.getBoundingBox().inflate(radius));

        for (Entity target : targets) {
            if (target instanceof LivingEntity living && target != player) {

                Vec3 knockbackDir = living.position().subtract(player.position()).normalize();

                living.hurt(player.damageSources().explosion(player, player), 8.0f);

                living.setDeltaMovement(knockbackDir.x * 1.5, 1.0, knockbackDir.z * 1.5);
                living.igniteForSeconds(3);
            }
        }
    }

    public static void useVolcanicSlam(Player player) {
        long currentTime = System.currentTimeMillis();
        UUID uuid = player.getUUID();


        if (COOLDOWN_MAP.containsKey(uuid)) {
            long lastUse = COOLDOWN_MAP.get(uuid);
            if (currentTime - lastUse < SLAM_COOLDOWN) {
                long remaining = (SLAM_COOLDOWN - (currentTime - lastUse)) / 1000;
                player.displayClientMessage(Component.literal("§4⏳ §cAnklet is Recharging... (" + remaining + "s)"), true);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.PLAYERS, 0.8f, 0.5f);
                return;
            }
        }




        player.displayClientMessage(Component.literal("§4☄ §cTyrant's Judgement Activated!").withStyle(ChatFormatting.RED), true);


        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0f, 0.5f);


        ((IPlayerData) player).aprelics_setIsVolcanicSlamming(false);
        double horizontalMultiplier = 2.5;
        Vec3 forward = player.getLookAngle();
        player.setDeltaMovement(forward.x * horizontalMultiplier, 1.5, forward.z * horizontalMultiplier);
        player.hurtMarked = true;
        player.setOnGround(false);

        ((IPlayerData) player).aprelics_setIsVolcanicSlamming(true);


        COOLDOWN_MAP.put(uuid, currentTime);
        NOTIFIED_READY.put(uuid, false);
    }
}