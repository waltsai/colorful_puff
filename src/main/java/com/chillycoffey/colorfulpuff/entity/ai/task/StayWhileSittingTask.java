package com.chillycoffey.colorfulpuff.entity.ai.task;

import com.chillycoffey.colorfulpuff.entity.mob.PuffBaseEntity;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class StayWhileSittingTask extends Task<PuffBaseEntity> {
    public StayWhileSittingTask() {
        super(ImmutableMap.of(), Integer.MAX_VALUE);
    }

    @Override
    protected boolean shouldRun(ServerWorld world, PuffBaseEntity entity) {
        return entity.isSitting();
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, PuffBaseEntity entity, long time) {
        return entity.isSitting();
    }

    @Override
    protected void run(ServerWorld world, PuffBaseEntity entity, long time) {
        super.run(world, entity, time);
        entity.setInSittingPose(true);
    }

    @Override
    protected void keepRunning(ServerWorld world, PuffBaseEntity entity, long time) {
        super.keepRunning(world, entity, time);
        entity.getNavigation().stop();
    }

    @Override
    protected void finishRunning(ServerWorld world, PuffBaseEntity entity, long time) {
        super.finishRunning(world, entity, time);
        entity.setInSittingPose(false);
    }
}
