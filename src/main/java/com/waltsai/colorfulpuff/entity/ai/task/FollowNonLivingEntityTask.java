package com.waltsai.colorfulpuff.entity.ai.task;

import com.waltsai.colorfulpuff.core.ModEntities;
import com.waltsai.colorfulpuff.entity.mob.PuffEntity;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

import java.util.function.Predicate;

public class FollowNonLivingEntityTask extends Task<PuffEntity> {
    private final Predicate<Entity> predicate;
    private final float maxDistanceSquared;

    public FollowNonLivingEntityTask(float maxDistance) {
        this((entity) -> {
            return true;
        }, maxDistance);
    }

    public FollowNonLivingEntityTask(Predicate<Entity> predicate, float maxDistance) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT, ModEntities.VISIBLE_INTERESTED_ENTITIES, MemoryModuleState.VALUE_PRESENT));
        this.predicate = predicate;
        this.maxDistanceSquared = maxDistance * maxDistance;
    }

    protected boolean shouldRun(ServerWorld world, PuffEntity entity) {
        Box box = entity.getBoundingBox().expand(16.0D, 16.0D, 16.0D);
        return entity.getBrain().getOptionalMemory(ModEntities.VISIBLE_INTERESTED_ENTITIES).get().stream().anyMatch(this.predicate);
    }

    protected void run(ServerWorld world, PuffEntity puff, long time) {
        Brain<?> brain = puff.getBrain();
        brain.getOptionalMemory(ModEntities.VISIBLE_INTERESTED_ENTITIES).ifPresent((list) -> {
            list.stream().filter(this.predicate).filter((entity) -> {
                return entity.squaredDistanceTo(puff) <= (double)this.maxDistanceSquared;
            }).findFirst().ifPresent((entity) -> {
                brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(entity, true));
            });
        });
    }
}
