package aprelics;

import it.unimi.dsi.fastutil.Hash;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CrownLogic {
    private static final HashMap<UUID, Long> COOLDOWN_MAP = new HashMap<>();
    private static final HashMap<UUID, Boolean> NOTIFIED_READY = new HashMap<>();
    private static final HashMap<UUID, Long> REMOVE_TICK_MAP = new HashMap<>();

    private static final long CROWN_COOLDOWN = 50000; // 50 seconds
    private static final Identifier HEALTH_MODIFIER_ID = Identifier.fromNamespaceAndPath("aprelics", "crown_extra_heart");

    public static void register() {
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server -> {
            long currentTime = System.currentTimeMillis();
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                UUID uuid = player.getUUID();

                if (player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.AMBRIA_CROWN)) {
                    checkCooldownNotification(player);
                }

                if (REMOVE_TICK_MAP.containsKey(uuid) && currentTime >= REMOVE_TICK_MAP.get(uuid)) {
                    removeExtraHeart(player);
                    REMOVE_TICK_MAP.remove(uuid);
                }
            }
        });
    }

    private static void checkCooldownNotification(ServerPlayer player) {
        UUID uuid = player.getUUID();
        if (COOLDOWN_MAP.containsKey(uuid)) {
            long lastUse = COOLDOWN_MAP.get(uuid);
            long timePassed = System.currentTimeMillis() - lastUse;

            if (timePassed >= CROWN_COOLDOWN && !NOTIFIED_READY.getOrDefault(uuid, false)) {
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 0.5f);
                player.displayClientMessage(Component.literal("§a✔ The Verdantian Crown has Recharged!"), true);
                NOTIFIED_READY.put(uuid, true);
            }
        }
    }

    public static void tryCrownAbility(ServerPlayer player) {

        if (!RelicUtil.canUseRelic(player)) return;

        long currentTime = System.currentTimeMillis();
        UUID uuid = player.getUUID();

        if (COOLDOWN_MAP.containsKey(player.getUUID())) {
            long lastUse = COOLDOWN_MAP.get(uuid);
            if (currentTime - lastUse < CROWN_COOLDOWN) {
                long remaining = (CROWN_COOLDOWN - (currentTime - lastUse)) / 1000;
                player.displayClientMessage(Component.literal("§2⏳ §aCrown is recharging... (" + remaining + "s)"), true);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.PLAYERS, 0.8f, 0.5f);
                return;
            }
        }

        double radius = 10.0;
        ServerLevel world = player.level();
        AABB area = player.getBoundingBox().inflate(radius);
        List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, area,
                e -> e != player && e.isAlive());

        spawnExpandingSphere(world, player.position().add(0, 1, 0), radius);

        if (!targets.isEmpty()) {
            for (LivingEntity target : targets) {

                target.addEffect(new MobEffectInstance(MobEffects.INSTANT_HEALTH, 1, 5));

                world.sendParticles(ParticleTypes.HAPPY_VILLAGER, target.getX(), target.getY() + 1, target.getZ(), 10, 0.2, 0.2, 0.2, 0.1);

            }

            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 400, 1, false, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 400, 1, false, false, true));

            applyExtraHeart(player);
            REMOVE_TICK_MAP.put(uuid, currentTime + 20000); //20 seconds or 400 ticks

            player.setHealth(player.getMaxHealth());

            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.5f);
            world.playSound(null, player.getX(), player.getX(), player.getY(),
                    SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.PLAYERS);


            player.displayClientMessage(Component.literal("§6👑 §eAmbria's Wisdom Activated! §6👑"), true);

            COOLDOWN_MAP.put(player.getUUID(), currentTime);
            NOTIFIED_READY.put(player.getUUID(), false);

        } else {

            player.displayClientMessage(Component.literal("§7[ §c! §7] §eNo subjects nearby to heal!"), true);

        }
    }

    private static void applyExtraHeart(ServerPlayer player) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {

            maxHealth.removeModifier(HEALTH_MODIFIER_ID);

            maxHealth.addTransientModifier(new AttributeModifier(HEALTH_MODIFIER_ID, 2.0, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    private static void removeExtraHeart (ServerPlayer player) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.removeModifier(HEALTH_MODIFIER_ID);

            if (player.getHealth() > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }
        }
    }

    private static void spawnExpandingSphere(ServerLevel world, Vec3 center, double maxRadius) {

        int points = 50;
        for (double r = 1.0; r <= maxRadius; r += 2.0) {
            for (int i = 0; i < points; i++) {
                double theta = Math.random() * Math.PI * 2;
                double phi = Math.random() * Math.PI;

                double x = center.x + r * Math.sin(phi) * Math.cos(theta);
                double y = center.y + r * Math.cos(phi);
                double z = center.z + r * Math.sin(phi) * Math.sin(theta);

                world.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z,1, 0, 0, 0, 0.01);
            }
        }

    }
}

