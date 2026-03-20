package aprelics.items;

import com.google.common.base.Suppliers;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;

import aprelics.RelicUtil;
import aprelics.client.renderer.armor.TyrantsAnkletArmorRenderer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TyrantsAnkletItem extends Item implements GeoItem {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("tail_idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("tail_walk");

    public TyrantsAnkletItem(ArmorMaterial material, ArmorType type, Properties properties) {
        super(properties.humanoidArmor(material, type));
        GeoItem.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("Main", 20, animationTest -> {
            return animationTest.setAndContinue(IDLE_ANIM);
        }));
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {

        consumer.accept(new GeoRenderProvider() {
            /*  private final Supplier<TyrantsAnkletArmorRenderer<?>> renderer =
                      Suppliers.memoize(() -> new TyrantsAnkletArmorRenderer<>());*/
            private GeoArmorRenderer<?, ?> renderer;

            @Override
            public @Nullable GeoArmorRenderer<?, ?> getGeoArmorRenderer(
                    ItemStack itemStack, EquipmentSlot equipmentSlot) {
                //  return this.renderer.get();
                if (this.renderer == null)
                    this.renderer = new TyrantsAnkletArmorRenderer<>();

                return this.renderer;
            }
           /* private final Supplier<TyrantsAnkletArmorRenderer<?>> renderer = Suppliers.memoize(TyrantsAnkletArmorRenderer::new);

            @Nullable
            @Override
            public GeoArmorRenderer<?, ?> getGeoArmorRenderer(ItemStack itemStack, EquipmentSlot equipmentSlot) {
                return this.renderer.get();
            }*/
        });
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        if (entity instanceof Player player) {

            if (slot == EquipmentSlot.FEET) {

                if (RelicUtil.countRelicsInInventory(player) == 1) {

                    player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 20, 0, false, false, true));

                    double radius = 6.0;
                    List<Mob> nearbyMonsters = level.getEntitiesOfClass(

                            Mob.class,
                            player.getBoundingBox().inflate(radius),
                            mob -> mob instanceof Monster
                    );

                    for (Mob monster : nearbyMonsters) {

                        if (monster.getHealth() / monster.getMaxHealth() <= 0.25f) {

                            Vec3 playerPos = player.position();
                            Vec3 monsterPos = monster.position();
                            Vec3 runDirection = monsterPos.subtract(playerPos).normalize().scale(8);
                            Vec3 targetPos = monsterPos.add(runDirection);

                            monster.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, 1.5);
                        }
                    }
                }
            }
        }
    }
}