package com.chillycoffey.colorfulpuff.entity.ai.task;

import com.chillycoffey.colorfulpuff.entity.mob.PuffBaseEntity;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class PuffWalkTowardsTask extends Task<PuffBaseEntity> {
    private final MemoryModuleType<GlobalPos> destination;
    private final float speed;
    private final int completionRange;
    private final int maxRange;
    private final int maxRunTime;

    public PuffWalkTowardsTask(MemoryModuleType<GlobalPos> destination, float speed, int completionRange, int maxRange, int maxRunTime) {
        super(ImmutableMap.of(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleState.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, destination, MemoryModuleState.VALUE_PRESENT));
        this.destination = destination;
        this.speed = speed;
        this.completionRange = completionRange;
        this.maxRange = maxRange;
        this.maxRunTime = maxRunTime;
    }

    private void giveUp(PuffBaseEntity puff, long time) {
        Brain<?> brain = puff.getBrain();
        puff.releaseTicketFor(this.destination);
        brain.forget(this.destination);
        brain.remember(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, time);
    }

    protected void run(ServerWorld serverWorld, PuffBaseEntity puffEntity, long l) {
        Brain<?> brain = puffEntity.getBrain();
        brain.getOptionalMemory(this.destination).ifPresent((globalPos) -> {
            if (!this.dimensionMismatches(serverWorld, globalPos) && !this.shouldGiveUp(serverWorld, puffEntity)) {
                if (this.exceedsMaxRange(puffEntity, globalPos)) {
                    Vec3d vec3d = null;
                    int i = 0;

                    for(boolean var9 = true; i < 1000 && (vec3d == null || this.exceedsMaxRange(puffEntity, GlobalPos.create(serverWorld.getRegistryKey(), new BlockPos(vec3d)))); ++i) {
                        vec3d = NoPenaltyTargeting.findTo(puffEntity, 15, 7, Vec3d.ofBottomCenter(globalPos.getPos()), 1.5707963705062866D);
                    }

                    if (i == 1000) {
                        this.giveUp(puffEntity, l);
                        return;
                    }

                    brain.remember(MemoryModuleType.WALK_TARGET, (new WalkTarget(vec3d, this.speed, this.completionRange)));
                } else if (!this.reachedDestination(serverWorld, puffEntity, globalPos)) {
                    brain.remember(MemoryModuleType.WALK_TARGET, (new WalkTarget(globalPos.getPos(), this.speed, this.completionRange)));
                }
            } else {
                this.giveUp(puffEntity, l);
            }

        });
    }

    private boolean shouldGiveUp(ServerWorld world, PuffBaseEntity puff) {
        Optional<Long> optional = puff.getBrain().getOptionalMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        if (optional.isPresent()) {
            return world.getTime() - (Long)optional.get() > (long)this.maxRunTime;
        } else {
            return false;
        }
    }

    private boolean exceedsMaxRange(PuffBaseEntity puff, GlobalPos pos) {
        return pos.getPos().getManhattanDistance(puff.getBlockPos()) > this.maxRange;
    }

    private boolean dimensionMismatches(ServerWorld world, GlobalPos pos) {
        return pos.getDimension() != world.getRegistryKey();
    }

    private boolean reachedDestination(ServerWorld world, PuffBaseEntity puff, GlobalPos pos) {
        return pos.getDimension() == world.getRegistryKey() && pos.getPos().getManhattanDistance(puff.getBlockPos()) <= this.completionRange;
    }
}
