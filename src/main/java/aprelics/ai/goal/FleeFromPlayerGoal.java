package aprelics.ai.goal;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FleeFromPlayerGoal extends Goal {

    private final Monster mob;
    private final Player player;
    private final double speedModifier;
    private final double fleeDistance;
    private final PathNavigation pathNav;
    private Vec3 fleePos;

    public FleeFromPlayerGoal(Monster mob, Player player, double speedModifier, double fleeDistance) {
        this.mob = mob;
        this.player = player;
        this.speedModifier = speedModifier;
        this.fleeDistance = fleeDistance;
        this.pathNav = mob.getNavigation();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (mob.distanceToSqr(player) < fleeDistance * fleeDistance) {
            Vec3 vec3 = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.player.position());
            if (vec3 == null) {
                return false;
            }
            if (this.player.distanceToSqr(vec3.x, vec3.y, vec3.z) < mob.distanceToSqr(player)) {
                return false;
            }
            this.fleePos = vec3;
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.pathNav.isDone();
    }

    @Override
    public void start() {
        this.pathNav.moveTo(this.fleePos.x, this.fleePos.y, this.fleePos.z, this.speedModifier);
    }

    @Override
    public void stop() {
        this.fleePos = null;
    }
}