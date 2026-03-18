package aprelics;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class APRelicsLangProvider extends FabricLanguageProvider {


    public APRelicsLangProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void generateTranslations(HolderLookup.@NonNull Provider registryLookup, @NonNull TranslationBuilder translationBuilder) {
        // Add your items here
        translationBuilder.add(ModItems.VERDANT_HALO, "Verdant Halo");
        translationBuilder.add(ModItems.TYRANTS_ANKLET, "Tyrant's Anklet");
        translationBuilder.add(ModItems.REAPERS_SCYTHE, "Reaper's Scythe");
    }
}