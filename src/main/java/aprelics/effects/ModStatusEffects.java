package aprelics.effects;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier; // Use Identifier if ResourceLocation is red
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Objects;

public class ModStatusEffects {

    public static final MobEffect TYRANTS_MIGHT = new TyrantsMightEffect();
    public static final MobEffect DECAY = new DecayEffect();

    public static void register() {
        // Use the 'of' method or look for 'tryParse' if your IDE suggests it
        Registry.register(BuiltInRegistries.MOB_EFFECT, Objects.requireNonNull(Identifier.tryParse("aprelics:tyrants_might")), TYRANTS_MIGHT);
        Registry.register(BuiltInRegistries.MOB_EFFECT, Objects.requireNonNull(Identifier.tryParse("aprelics:decay")), DECAY);
    }

    private static class TyrantsMightEffect extends MobEffect {
        public TyrantsMightEffect() {
            super(MobEffectCategory.BENEFICIAL, 0xFFD700);

            // Apply the same fix here
            this.addAttributeModifier(Attributes.ATTACK_DAMAGE,
                    java.util.Objects.requireNonNull(Identifier.tryParse("aprelics:tyrants_might_modifier")),
                    2.0,
                    AttributeModifier.Operation.ADD_VALUE);
        }
    }
}