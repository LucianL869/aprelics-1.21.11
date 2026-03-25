package aprelics.client.renderer.block;

import aprelics.APRelics;
import aprelics.blocks.entity.DisplayCaseEntity;
import aprelics.blocks.entity.DisplayCaseRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.model.BakedGeoModel;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.renderer.layer.builtin.BlockAndItemGeoLayer;

import java.util.List;

public class DisplayCaseRenderer extends GeoBlockRenderer<DisplayCaseEntity, DisplayCaseRenderState> {

    public DisplayCaseRenderer(BlockEntityRendererProvider.Context context) {
        super(new DefaultedBlockGeoModel<>(
                Identifier.fromNamespaceAndPath(APRelics.MOD_ID, "display_case")
        ));


        this.withRenderLayer(new BlockAndItemGeoLayer<DisplayCaseEntity, DisplayCaseRenderState>(this) {


            @Override
            protected List<RenderData> getRelevantBones(GeoRenderState renderState, BakedGeoModel model) {
                return List.of();
            }

            @Override
            public void addRenderData(GeoAnimatable animatable, @Nullable Object relatedObject, GeoRenderState renderState, float partialTick) {

            }

            protected List<RenderData> getRelevantBones(DisplayCaseRenderState renderState, BakedGeoModel model) {

                return model.getBone("item_hook")
                        .map(bone -> new RenderData(bone, "item_hook", ItemStack))
                        .map(List::of)
                        .orElse(List.of());
            }



            public void addRenderData(DisplayCaseEntity animatable, Object relatedObject, DisplayCaseRenderState renderState, float partialTick) {

            }


            protected ItemStack getStackForBone(GeoBone bone, DisplayCaseRenderState renderState) {

                if (bone.name().equals("item_hook")) {
                    return renderState.displayedItem;
                }
                return ItemStack.EMPTY;
            }


            protected void submitItemStackRenderer(PoseStack poseStack, GeoBone bone, ItemStack stack, ItemDisplayContext contex, GeoRenderState state, SubmitNodeCollector partialTick, CameraRenderState packedLight, int packedOverlay, int colour, int alpha) {
                poseStack.pushPose();

                poseStack.translate(0, 0.25f, 0);
                poseStack.scale(0.5f, 0.5f, 0.5f);

                super.submitItemStackRender(poseStack, bone, stack, contex, state, partialTick, packedLight, packedOverlay, colour, alpha);

                poseStack.popPose();
            }
        });
    }
}