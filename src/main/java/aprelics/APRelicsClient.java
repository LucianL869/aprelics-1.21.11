package aprelics;

import aprelics.items.TyrantsAnkletItem;
import aprelics.models.TyrantAnkletModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class APRelicsClient implements ClientModInitializer {

    public static KeyMapping abilityKey;

    KeyMapping.Category ABILITYKEY = new KeyMapping.Category(
            Identifier.fromNamespaceAndPath(APRelics.MOD_ID, "ability")
    );

    @Override
    public void onInitializeClient() {



        RenderBridge.ankletProvider = () -> new GeoRenderProvider() {
            private GeoArmorRenderer<TyrantsAnkletItem, HumanoidRenderState> renderer;

            public GeoArmorRenderer<TyrantsAnkletItem, HumanoidRenderState> getGeoArmorRenderer() {
                if (this.renderer == null) {
                    this.renderer = new GeoArmorRenderer<>(new TyrantAnkletModel());
                }
                return this.renderer;
            }
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