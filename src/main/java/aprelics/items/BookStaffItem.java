package aprelics.items;

import aprelics.*;
import aprelics.client.renderer.armor.TyrantsAnkletArmorRenderer;
import aprelics.client.renderer.item.BookStaffRenderer;
import com.google.common.base.Suppliers;
import com.mojang.math.Transformation;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class BookStaffItem extends Item implements GeoItem {
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");;
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final HashMap<UUID, Long> COOLDOWN_MAP = new HashMap<>();
    private static final HashMap<UUID, Boolean> NOTIFIED_READY = new HashMap<>();
    private static final long MAZE_COOLDOWN = 120000; // 60 seconds

    public static void register() {
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {

                // 1. Existing Cooldown Check
                if (hasStaff(player)) {
                    BookStaffItem.checkCooldownNotification(player);
                }
            }
        });
    }

    private static boolean hasStaff(ServerPlayer player) {
        // 1. Check Main Hand (Most likely case)
        if (player.getMainHandItem().getItem() instanceof BookStaffItem) return true;

        // 2. Check Off-Hand
        if (player.getOffhandItem().getItem() instanceof BookStaffItem) return true;

        // 3. Check the rest of the inventory
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).getItem() instanceof BookStaffItem) {
                return true;
            }
        }

        return false;
    }

    private static final int[][] MAZE_LAYOUT = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
            {1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1},
            {1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1},
            {1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1},
            {1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1},
            {1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };

    public BookStaffItem(Properties properties) {
        super(properties); GeoItem.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("Main", 20, animationTest -> {
            return animationTest.setAndContinue(IDLE_ANIM);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {

        consumer.accept(new GeoRenderProvider() {
            private GeoItemRenderer<?> renderer;
                public @Nullable GeoItemRenderer<?> getGeoItemRenderer(
                    ItemStack itemStack) {
                    if (this.renderer == null)
                        this.renderer = new BookStaffRenderer();

                    return this.renderer;

                }

        });
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {

        player.startUsingItem(hand);
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (level.isClientSide()) return false;
        if (entity instanceof Player player) {
            if (!RelicUtil.canUseRelic(player)) return false;
            int charge = this.getUseDuration(stack, entity) - timeLeft;
            if (charge >= 10) shootBookProjectile(player, level);
        }
        return false;
    }

    private void shootBookProjectile(Player player, Level level) {

        BookProjectile book = new BookProjectile(level, player);


        book.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.0F, 1.0F);
        level.addFreshEntity(book);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0f, 1.4f);
    }

    public void cycleMode(Player player, ItemStack stack) {
        int nextMode = (getMode(stack) + 1) % 3;
        stack.set(ModComponents.STAFF_MODE, nextMode);
        String modeName = switch (nextMode) {
            case 1 -> "§bTeleport";
            case 2 -> "§dBook Maze";
            default -> "§7Off";
        };
        player.displayClientMessage(Component.literal("§6Staff Mode: " + modeName), true);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.3f, 1.5f);
    }

    public void useSelectedAbility(Player player, ItemStack stack) {
        if (player.level().isClientSide()) return;
        int mode = getMode(stack);

        // MODE 1: Teleport
        if (mode == 1) {
            if (StaffWarpTracker.WORLD != null) {
                player.teleportTo(StaffWarpTracker.X, StaffWarpTracker.Y, StaffWarpTracker.Z);
                player.level().playSound(null, player.blockPosition(),
                        SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
            } else {
                player.displayClientMessage(Component.literal("§cStaff anchor not set!"), true);
            }
        }
        // MODE 2: Maze
        else if (mode == 2) {
            long currentTime = System.currentTimeMillis();
            UUID uuid = player.getUUID();

            if (COOLDOWN_MAP.containsKey(uuid)) {
                long lastUse = COOLDOWN_MAP.get(uuid);
                if (currentTime - lastUse < MAZE_COOLDOWN) {
                    long remaining = (MAZE_COOLDOWN - (currentTime - lastUse)) / 1000;
                    player.displayClientMessage(Component.literal("§5⏳ §dThe Library is Rearranging... (" + remaining + "s)"), true);
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.PLAYERS, 0.8f, 0.5f);
                    return;
                }
            }

            player.displayClientMessage(Component.literal("§d✨ The Library Heeds Your Call! ✨"), true);
            summonAnimatedMaze(player);

            COOLDOWN_MAP.put(uuid, currentTime);
            NOTIFIED_READY.put(uuid, false);
        }
    }

    public static void checkCooldownNotification(ServerPlayer player) {
        UUID uuid = player.getUUID();
        if (COOLDOWN_MAP.containsKey(uuid)) {
            long lastUse = COOLDOWN_MAP.get(uuid);
            long timePassed = System.currentTimeMillis() - lastUse;


            if (timePassed >= MAZE_COOLDOWN && !NOTIFIED_READY.getOrDefault(uuid, false)) {

                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 0.7f, 0.6f);


                player.displayClientMessage(Component.literal("§d📖 The Great Library is ready!"), true);


                NOTIFIED_READY.put(uuid, true);
            }
        }
    }

    public int getMode(ItemStack stack) { return stack.getOrDefault(ModComponents.STAFF_MODE, 0); }
    @Override public ItemUseAnimation getUseAnimation(ItemStack stack) { return ItemUseAnimation.BOW; }
    @Override public int getUseDuration(ItemStack stack, LivingEntity entity) { return 72000; }

    private void summonAnimatedMaze(Player player) {
        ServerLevel level = (ServerLevel) player.level();
        Vec3 look = player.getLookAngle();
        Vec3 target = player.getEyePosition().add(look.scale(16));
        BlockPos centerPos = BlockPos.containing(target);
        List<BlockPos> barriers = new ArrayList<>();
        List<UUID> displays = new ArrayList<>();
        BlockPos startPos = centerPos.offset(-15, 0, -15);

        List<BlockPos> pillarPositions = new ArrayList<>();
        for (int row = 0; row < MAZE_LAYOUT.length; row++) {
            for (int col = 0; col < MAZE_LAYOUT[row].length; col++) {
                if (MAZE_LAYOUT[row][col] == 1) pillarPositions.add(startPos.offset(row, 0, col));
            }
        }
        pillarPositions.sort(java.util.Comparator.comparingDouble(p -> p.distSqr(centerPos)));

        new Thread(() -> {
            int count = 0;
            for (BlockPos pos : pillarPositions) {
                count++;
                BlockPos groundPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
                level.getServer().execute(() -> spawnSingleRisingPillar(level, groundPos, displays, barriers));
                if (count % 10 == 0) { try { Thread.sleep(50); } catch (InterruptedException ignored) {} }
            }
        }).start();

        applyScreenShake(level, centerPos, 80);
        new MazeTracker(level, barriers, displays).startDescentTask();
    }

    private void applyScreenShake(ServerLevel level, BlockPos source, int durationTicks) {
        new Thread(() -> {
            for (int i = 0; i < durationTicks / 5; i++) {
                level.getServer().execute(() -> {
                    level.players().stream().filter(p -> p.blockPosition().closerThan(source, 20))
                            .forEach(p -> p.hurtClient(p.damageSources().generic()));
                });
                try { Thread.sleep(250); } catch (InterruptedException ignored) {}
            }
        }).start();
    }

    private void spawnSingleRisingPillar(ServerLevel level, BlockPos pos, List<UUID> displays, List<BlockPos> barriers) {
        int riseTicks = 120;
        for (int y = 0; y < 5; y++) {
            BlockPos tallPos = pos.above(y);
            Display.BlockDisplay display = EntityType.BLOCK_DISPLAY.create(level, EntitySpawnReason.TRIGGERED);
            if (display == null) continue;
            display.setBlockState(Blocks.BOOKSHELF.defaultBlockState());
            display.setPos(tallPos.getX(), tallPos.getY(), tallPos.getZ());
            display.setTransformation(new Transformation(new Vector3f(0, -5.0f, 0), null, null, null));
            level.addFreshEntity(display);

            level.getServer().execute(() -> {
                display.setTransformationInterpolationDuration(riseTicks);
                display.setTransformationInterpolationDelay(0);
                display.setTransformation(Transformation.identity());
                new Thread(() -> {
                    try {
                        Thread.sleep(riseTicks * 50L);
                        level.getServer().execute(() -> level.playSound(null, pos, SoundEvents.IRON_GOLEM_STEP, SoundSource.BLOCKS, 0.5f, 0.7f));
                    } catch (InterruptedException ignored) {}
                }).start();
            });
            displays.add(display.getUUID());
            if (level.getBlockState(tallPos).canBeReplaced()) {
                level.setBlockAndUpdate(tallPos, Blocks.BARRIER.defaultBlockState());
                barriers.add(tallPos);
            }
        }
        new Thread(() -> {
            for (int i = 0; i < riseTicks / 5; i++) {
                level.getServer().execute(() -> {
                    level.sendParticles(new net.minecraft.core.particles.BlockParticleOption(net.minecraft.core.particles.ParticleTypes.BLOCK, Blocks.DIRT.defaultBlockState()), pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 8, 0.3, 0.1, 0.3, 0.05);
                    level.playSound(null, pos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 0.6f, 0.5f);
                });
                try { Thread.sleep(250); } catch (InterruptedException ignored) {}
            }
        }).start();
    }
}