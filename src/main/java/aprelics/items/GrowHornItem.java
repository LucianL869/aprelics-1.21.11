package aprelics.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class GrowHornItem extends Item {
    private static final HashMap<UUID, Long> COOLDOWN_MAP = new HashMap<>();
    private static final HashMap<UUID, Boolean> NOTIFIED_READY = new HashMap<>();
    private static final long HORN_COOLDOWN = 1200000; // 1 Day

    public GrowHornItem(Properties properties) {
        super(properties);
    }

    public static void register() {
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (hasHorn(player)) {
                    GrowHornItem.checkCooldownNotification(player);
                }
            }
        });
    }

    private static boolean hasHorn(ServerPlayer player) {
        return player.getMainHandItem().getItem() instanceof GrowHornItem ||
                player.getOffhandItem().getItem() instanceof GrowHornItem;
    }

    public static void checkCooldownNotification(ServerPlayer player) {
        UUID uuid = player.getUUID();
        if (COOLDOWN_MAP.containsKey(uuid)) {
            long lastUse = COOLDOWN_MAP.get(uuid);
            long timePassed = System.currentTimeMillis() - lastUse;


            if (timePassed >= HORN_COOLDOWN && !NOTIFIED_READY.getOrDefault(uuid, false)) {

                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.GOAT_AMBIENT, SoundSource.PLAYERS, 0.7f, 0.6f);


                player.displayClientMessage(Component.literal("§dThe Horn Has Regained It's Voice!"), true);


                NOTIFIED_READY.put(uuid, true);
            }
        }
    }



    //PASSIVE GROWTH

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide() && entity instanceof Player player && level.getGameTime() % 20 == 0) {
            BlockPos pos = player.blockPosition();
            int radius = 3;

            for (BlockPos nearbyPos : BlockPos.betweenClosed(pos.offset(-radius, -1, -radius), pos.offset(radius, 1, radius))) {
                BlockState state = level.getBlockState(nearbyPos);
                if (state.getBlock() instanceof BonemealableBlock && level.random.nextInt(2) == 0) {
                    state.randomTick((ServerLevel) level, nearbyPos, level.random);

                    ((ServerLevel)level).sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            nearbyPos.getX() + 0.5, nearbyPos.getY() + 0.5, nearbyPos.getZ() + 0.5, 1, 0.2, 0.2, 0.2, 0.05);
                }
            }
        }
    }

    //ITS RAINING FOOD
    public void useAbility(Player player) {
        if (player.level().isClientSide()) return;

        UUID uuid = player.getUUID();
        long currentTime = System.currentTimeMillis();

        if (COOLDOWN_MAP.containsKey(uuid)) {
            long lastUse = COOLDOWN_MAP.get(uuid);
            if (currentTime - lastUse < HORN_COOLDOWN) {
                long secondsLeft = (HORN_COOLDOWN - (currentTime - lastUse)) / 1000;
                player.displayClientMessage(Component.literal("§cThe Horn is Silent.. (" + secondsLeft + "s)"), true);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.PLAYERS, 0.8f, 0.5f);
                return;
            }
        }


        ServerLevel serverLevel = (ServerLevel) player.level();
        player.displayClientMessage(Component.literal("§eThe Horn Calls Forth The Storm!"), true);

        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(0).value(), SoundSource.PLAYERS, 2.0f, 1.0f);

        double cloudY = player.getY() + 8;
        for (int i = 0; i < 50; i++) {
            double rx = player.getX() + (serverLevel.random.nextDouble() - 0.5) * 10;
            double rz = player.getZ() + (serverLevel.random.nextDouble() - 0.5) * 10;
            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, rx, cloudY, rz, 2, 0.5, 0.2, 0.5, 0.2);
        }

        List<Item> foodPool = List.of(Items.APPLE, Items.GOLDEN_CARROT, Items.BREAD, Items.COOKED_BEEF,
                Items.BAKED_POTATO, Items.CAKE, Items.COOKED_SALMON, Items.COOKIE, Items.GOLDEN_APPLE);
        Random rand = new Random();

        for (int i = 0; i < 12; i++) {
            Item randomFood = foodPool.get(rand.nextInt(foodPool.size()));
            double rx = player.getX() + (rand.nextDouble() - 0.5) * 6;
            double rz = player.getZ() + (rand.nextDouble() - 0.5) * 6;

            ItemEntity foodEntity = new ItemEntity(player.level(), rx, cloudY, rz, new ItemStack(randomFood));
            foodEntity.setDeltaMovement(0, -0.2, 0);
            player.level().addFreshEntity(foodEntity);
        }

        COOLDOWN_MAP.put(uuid, currentTime);
        NOTIFIED_READY.put(uuid, false);
    }
}