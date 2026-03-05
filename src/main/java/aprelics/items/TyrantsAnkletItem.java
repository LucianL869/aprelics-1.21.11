package aprelics.items;

import aprelics.RelicUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The Tyrant's Anklet relic.
 * Passive: Grants Strength I and causes low-health hostiles to flee.
 * Active: Volcanic Slam (Handled via AnkletLogic).
 */
public class TyrantsAnkletItem extends Item {
    public TyrantsAnkletItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        if (entity instanceof Player player) {

            // Only active when equipped in the Feet slot
            if (slot == EquipmentSlot.FEET) {

                // Ensure no clashing relics are present
                if (RelicUtil.countRelicsInInventory(player) == 1) {

                    // 1. TYRANT'S MIGHT: Grant Strength I
                    player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 20, 0, false, false, true));

                    // 2. AUTHORITARIAN PRESENCE: Flee in Fear logic
                    double radius = 6.0;
                    List<Mob> nearbyMonsters = level.getEntitiesOfClass(
                            Mob.class,
                            player.getBoundingBox().inflate(radius),
                            mob -> mob instanceof Monster
                    );

                    for (Mob monster : nearbyMonsters) {
                        // Check if monster health is below 25%
                        if (monster.getHealth() / monster.getMaxHealth() <= 0.25f) {
                            // Calculate a point away from the player
                            Vec3 playerPos = player.position();
                            Vec3 monsterPos = monster.position();
                            Vec3 runDirection = monsterPos.subtract(playerPos).normalize().scale(8);
                            Vec3 targetPos = monsterPos.add(runDirection);

                            // Force the mob to pathfind to that "away" position at high speed
                            monster.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, 1.5);
                        }
                    }
                }
            }
        }
    }
}