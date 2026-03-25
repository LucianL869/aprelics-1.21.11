package aprelics.blocks.entity;

import aprelics.APRelics;
import aprelics.ModBlocks;
import aprelics.blocks.DisplayCaseBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DisplayCaseEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private ItemStack displayedItem = ItemStack.EMPTY;

    public DisplayCaseEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DISPLAY_CASE_ENTITY, pos, state);
    }

    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {

        controllers.add(new AnimationController<>(state -> {
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }


    public DisplayCaseRenderState createRenderState() {
        return new DisplayCaseRenderState();
    }


    public void setRenderState(DisplayCaseRenderState state) {

        state.displayedItem = this.getDisplayedItem();
    }

    public ItemStack getDisplayedItem() {
        return this.displayedItem;
    }

    public void setDisplayedItem(ItemStack stack) {
        this.displayedItem = stack;
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }


    @Override
    protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
        super.saveAdditional(output);

        if (!this.displayedItem.isEmpty()) {

            output.store("HeldItem", net.minecraft.world.item.ItemStack.CODEC, this.displayedItem);
        }
    }

    @Override
    protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        super.loadAdditional(input);

        this.displayedItem = input.read("HeldItem", net.minecraft.world.item.ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

}