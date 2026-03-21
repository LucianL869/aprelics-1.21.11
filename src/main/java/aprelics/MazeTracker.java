package aprelics;

import com.mojang.math.Transformation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Display;
import net.minecraft.world.level.block.Blocks;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

public class MazeTracker {
    private final ServerLevel level;
    private final List<BlockPos> barrierPositions;
    private final List<UUID> displayUUIDs;

    public MazeTracker(ServerLevel level, List<BlockPos> barriers, List<UUID> displays) {
        this.level = level;
        this.barrierPositions = barriers;
        this.displayUUIDs = displays;
    }

    public void startDescentTask() {
        new Thread(() -> {
            try {
                Thread.sleep(30000); // Wait 10s

                int descentTicks = 120; // 3 second slow descent

                // 1. Start the Grinding Sound/Particle Loop for descent
                new Thread(() -> {
                    for (int i = 0; i < descentTicks / 5; i++) {
                        level.getServer().execute(() -> {

                            // Screen Shake for anyone near the maze
                            if (!barrierPositions.isEmpty()) {
                                BlockPos center = barrierPositions.get(barrierPositions.size() / 2);
                                level.players().stream()
                                        .filter(p -> p.blockPosition().closerThan(center, 30))
                                        .forEach(p -> p.hurtClient(p.damageSources().generic()));
                            }

                            // LOOP THROUGH EVERY PILLAR FOR SOUND/PARTICLES
                            // This makes it sound "huge" like the rising phase
                            for (int y = 0; y < barrierPositions.size(); y++) {
                                if (y % 5 == 0) { // Only the ground-level block
                                    BlockPos pos = barrierPositions.get(y);

                                    // Warden Dust
                                    level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DIRT.defaultBlockState()),
                                            pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                                            4, 0.3, 0.1, 0.3, 0.02);

                                    // Heavy friction sound at EVERY pillar (0.2f volume so it's not too loud)
                                    level.playSound(null, pos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 0.2f, 0.5f);
                                }
                            }
                        });
                        try { Thread.sleep(250); } catch (InterruptedException ignored) {}
                    }
                }).start();

                // 2. Trigger the Smooth Slide Down
                level.getServer().execute(() -> {
                    for (UUID uuid : displayUUIDs) {
                        var entity = level.getEntity(uuid);
                        if (entity instanceof Display.BlockDisplay display) {
                            display.setTransformationInterpolationDuration(descentTicks);
                            display.setTransformationInterpolationDelay(0);

                            // Slide down 5 blocks while keeping scale 1:1 to prevent "melting"
                            Transformation slideDown = new Transformation(
                                    new Vector3f(0, -5.0f, 0),
                                    null,
                                    new Vector3f(1, 1, 1),
                                    null
                            );
                            display.setTransformation(slideDown);
                        }
                    }
                });

                // Wait for the animation to finish
                Thread.sleep(descentTicks * 50L);

                // 3. Final Cleanup + "Ground Settling" Sound
                level.getServer().execute(() -> {
                    if (!barrierPositions.isEmpty()) {
                        BlockPos center = barrierPositions.get(barrierPositions.size() / 2);
                        level.playSound(null, center, SoundEvents.IRON_GOLEM_DEATH, SoundSource.BLOCKS, 1.0f, 0.5f);
                    }

                    for (UUID uuid : displayUUIDs) {
                        var entity = level.getEntity(uuid);
                        if (entity != null) entity.discard();
                    }
                    for (BlockPos pos : barrierPositions) {
                        if (level.getBlockState(pos).is(Blocks.BARRIER)) {
                            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        }
                    }
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}