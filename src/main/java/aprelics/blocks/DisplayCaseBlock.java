package aprelics.blocks;

import aprelics.blocks.entity.DisplayCaseEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class DisplayCaseBlock extends Block implements EntityBlock {
    public DisplayCaseBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DisplayCaseEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof DisplayCaseEntity caseEntity) {
                ItemStack relic = caseEntity.getDisplayedItem();

                if (player.isCreative() && player.isShiftKeyDown()) {
                    caseEntity.setDisplayedItem(player.getMainHandItem().split(1));
                    return InteractionResult.SUCCESS;
                }

                if (!relic.isEmpty()) {

                    player.getInventory().add(relic.copy());

                    aprelics.ModEvents.triggerRelicEvent(level, player, relic);

                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());

                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.SUCCESS;
    }
}
