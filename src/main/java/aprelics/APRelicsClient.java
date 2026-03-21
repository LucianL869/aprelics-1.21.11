package aprelics;

import aprelics.client.renderer.armor.TyrantsAnkletArmorRenderer;
import aprelics.client.renderer.projectile.BookProjectileRenderer;
import aprelics.items.TyrantsAnkletItem;
import aprelics.client.renderer.armor.TyrantsAnkletArmorRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.function.BiConsumer;

public final class APRelicsClient implements ClientModInitializer {
    public static void registerRenderers(BiConsumer<EntityType<? extends Entity>, EntityRendererProvider> entityRenderers,
                                         BiConsumer<BlockEntityType<? extends BlockEntity>, BlockEntityRendererProvider> blockEntityRenderers) {

    }
    public static KeyMapping abilityKey;

    KeyMapping.Category ABILITYKEY = new KeyMapping.Category(
            Identifier.fromNamespaceAndPath(APRelics.MOD_ID, "ability")
    );

    @Override
    public void onInitializeClient() {

        EntityRendererRegistry.register(ModEntities.BOOK_PROJECTILE, BookProjectileRenderer::new);

        RenderBridge.ankletProvider = () -> new GeoRenderProvider() {
            private GeoArmorRenderer<TyrantsAnkletItem, HumanoidRenderState> renderer;

        };

        ModEvents.register();
        abilityKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.aprelics.use_relic",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                ABILITYKEY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (abilityKey.consumeClick()) {
                if (client.player != null) {
                    ClientPlayNetworking.send(new AbilityPacket());
                }
            }
        });
    }


}