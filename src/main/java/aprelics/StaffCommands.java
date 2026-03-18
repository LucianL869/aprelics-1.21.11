package aprelics;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

public class StaffCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("setstaffwarp")
                .executes(context -> {
                    ServerLevel world = context.getSource().getLevel();
                    BlockPos pos = BlockPos.containing(context.getSource().getPosition());

                    // Save to our tracker
                    StaffWarpTracker.X = pos.getX() + 0.5; // +0.5 centers them on the block
                    StaffWarpTracker.Y = pos.getY();
                    StaffWarpTracker.Z = pos.getZ() + 0.5;
                    StaffWarpTracker.WORLD = world.dimension().registry().toString();

                    context.getSource().sendSuccess(() -> Component.literal("§b[Relics] Library Anchor set to: " + pos.toShortString()), true);
                    return 1;
                })
        );
    }
}