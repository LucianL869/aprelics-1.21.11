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
    // This makes the keybind changeable in the menu!
    public static KeyMapping abilityKey;

    KeyMapping.Category RELICS = new KeyMapping.Category(
            Identifier.fromNamespaceAndPath(APRelics.MOD_ID, "relics")
    );

    @Override
    public void onInitializeClient() {
        // We use the 'KeyMapping' constructor that explicitly takes the translation key
        abilityKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.aprelics.use_ability",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                RELICS // If this is still red, see below
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (abilityKey.consumeClick()) {
                // This sends a "blank" signal to the server.
                // We'll define 'HealPacket.ID' in the next step.
                if (client.player != null) {
                    ClientPlayNetworking.send(new HealPacket());
                }
            }
        });
    }
}