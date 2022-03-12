package com.waltsai.colorfulpuff.entity.ai.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class AvoidAttackerTask extends Task<PathAwareEntity> {
    private final float fuzzySpeed;
    private final float fleeSpeed;
    private Entity entity;

    public AvoidAttackerTask(float fuzzySpeed, float fleeSpeed) {
        super(ImmutableMap.of(MemoryModuleType.HURT_BY, MemoryModuleState.VALUE_PRESENT), 80, 100);
        this.fuzzySpeed = fuzzySpeed;
        this.fleeSpeed = fleeSpeed;
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        return true;
    }

    protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        pathAwareEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
        this.entity = pathAwareEntity.getBrain().getOptionalMemory(MemoryModuleType.HURT_BY).get().getAttacker();
    }

    protected void keepRunning(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        if (pathAwareEntity.getNavigation().isIdle()) {
            if(this.entity instanceof LivingEntity && this.entity.distanceTo(pathAwareEntity) < 4.5) {
                Vec3d vec3d = FuzzyTargeting.findFrom(pathAwareEntity, 10, 6, pathAwareEntity.getPos());
                if (vec3d != null) {
                    pathAwareEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, this.fleeSpeed, 0));
                }
            } else {
                Vec3d vec3d = FuzzyTargeting.find(pathAwareEntity, 5, 4);
                if (vec3d != null) {
                    pathAwareEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, (new WalkTarget(vec3d, this.fuzzySpeed, 0)));
                }
            }
            /*
            Vec3d vec3d = FuzzyTargeting.find(pathAwareEntity, 5, 4);
            if (vec3d != null) {
                pathAwareEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, (new WalkTarget(vec3d, this.speed, 0)));
            }
             */
        }
    }
}
