package aprelics;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.UUID;

public class HaloLogic {
    private static final HashMap<UUID, Long> COOLDOWN_MAP = new HashMap<>();
    private static final HashMap<UUID, Boolean> NOTIFIED_READY = new HashMap<>();
    private static final long HEAL_COOLDOWN = 30000; // 30 seconds

    public static void register() {
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {

                if (player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.VERDANT_HALO)) {
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

            if (timePassed >= HEAL_COOLDOWN && !NOTIFIED_READY.getOrDefault(uuid, false)) {
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 0.5f);
                player.displayClientMessage(Component.literal("§a✔ Verdant Halo has Recharged!"), true);
                NOTIFIED_READY.put(uuid, true);
            }
        }
    }

    private static void applyPassiveEffects(ServerPlayer player) {
        if (player.level().getBiome(player.blockPosition()).is(net.minecraft.world.level.biome.Biomes.LUSH_CAVES)) {
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 40, 0, false, false, true));
        }
    }

    public static void tryHeal(ServerPlayer player) {

        if (!RelicUtil.canUseRelic(player)) {
            return;
        }


        if (!player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.VERDANT_HALO)) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (COOLDOWN_MAP.containsKey(player.getUUID())) {
            long lastUse = COOLDOWN_MAP.get(player.getUUID());
            if (currentTime - lastUse < HEAL_COOLDOWN) {
                long remaining = (HEAL_COOLDOWN - (currentTime - lastUse)) / 1000;
                player.displayClientMessage(Component.literal("§2⏳ §aHalo is recharging... (" + remaining + "s)"), true);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.PLAYERS, 0.8f, 0.5f);
                return;
            }
        }

        Entity target = getLookedAtEntity(player, 15.0);

        if (target instanceof LivingEntity livingTarget) {
            livingTarget.addEffect(new MobEffectInstance(MobEffects.INSTANT_HEALTH, 1, 0));
            livingTarget.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 120, 2));
            livingTarget.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 200, 0));

            ServerLevel world = (ServerLevel) player.level();
            Vec3 start = player.getEyePosition();
            Vec3 end = livingTarget.getEyePosition();
            Vec3 direction = end.subtract(start);
            int distance = (int) (start.distanceTo(end) * 4);

            for (int i = 0; i < distance; i++) {
                double progress = (double) i / distance;
                double x = start.x + (direction.x * progress);
                double y = start.y + (direction.y * progress);
                double z = start.z + (direction.z * progress);
                world.sendParticles(ParticleTypes.COMPOSTER, x, y, z, 1, 0, 0, 0, 0);
            }

            world.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    livingTarget.getX(), livingTarget.getY() + 1.0, livingTarget.getZ(),
                    15, 0.5, 0.5, 0.5, 0.1);

            player.level().playSound(null, livingTarget.getX(), livingTarget.getY(), livingTarget.getZ(),
                    SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, 1.2f);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 2.0f);

            player.displayClientMessage(Component.literal("§2✨ §aVerdant Blessing Granted to " + livingTarget.getName().getString() + "! §2✨"), true);

            COOLDOWN_MAP.put(player.getUUID(), currentTime);
            NOTIFIED_READY.put(player.getUUID(), false);
        } else {
            player.displayClientMessage(Component.literal("§7[ §c! §7] §aNo creature in sight!"), true);
        }
    }

    private static Entity getLookedAtEntity(ServerPlayer player, double range) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 viewVec = player.getViewVector(1.0F);
        Vec3 reachVec = eyePos.add(viewVec.x * range, viewVec.y * range, viewVec.z * range);
        AABB searchBox = player.getBoundingBox().expandTowards(viewVec.scale(range)).inflate(1.0D);

        EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(
                player,
                eyePos,
                reachVec,
                searchBox,
                (entity) -> !entity.isSpectator() && entity.isAlive() && entity.isPickable(),
                range * range
        );

        return hitResult != null ? hitResult.getEntity() : null;
    }
}