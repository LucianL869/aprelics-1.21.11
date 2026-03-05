package aprelics;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class APRelicsClient implements ClientModInitializer {

    public static KeyMapping abilityKey;

    KeyMapping.Category ABILITYKEY = new KeyMapping.Category(
            Identifier.fromNamespaceAndPath(APRelics.MOD_ID, "ability")
    );

    @Override
    public void onInitializeClient() {
        // This specific constructor (String, int, String) is the most reliable for 1.21.1
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