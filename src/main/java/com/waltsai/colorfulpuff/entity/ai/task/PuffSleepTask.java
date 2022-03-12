package com.waltsai.colorfulpuff.entity.ai.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.SleepTask;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class PuffSleepTask extends SleepTask {
    @Override
    protected boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        Optional<GlobalPos> optional = entity.getBrain().getOptionalMemory(MemoryModuleType.HOME);
        if (optional.isEmpty()) {
            return false;
        } else {
            BlockPos blockPos = optional.get().getPos();
            return entity.getBrain().hasActivity(Activity.REST) && entity.getY() > (double)blockPos.getY() + 0.35D && blockPos.isWithinDistance(entity.getPos(), 0.54D);
        }
    }
}
