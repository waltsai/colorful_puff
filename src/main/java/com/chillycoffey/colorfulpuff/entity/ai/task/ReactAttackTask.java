package com.chillycoffey.colorfulpuff.entity.ai.task;

import com.chillycoffey.colorfulpuff.entity.mob.PuffBaseEntity;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class ReactAttackTask extends Task<PuffBaseEntity> {
    private LivingEntity attacker;
    public ReactAttackTask() {
        super(ImmutableMap.of(MemoryModuleType.ANGRY_AT, MemoryModuleState.REGISTERED, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleState.REGISTERED), 20, 60);
    }

    protected boolean shouldRun(ServerWorld serverWorld, PuffBaseEntity mobEntity) {
        if (mobEntity.getRecentDamageSource() == null) {
            return false;
        } else if (mobEntity.isBaby()) {
            return false;
        } else {
            Entity target = mobEntity.getRecentDamageSource().getAttacker();
            if(target instanceof LivingEntity) {
                this.attacker = (LivingEntity) target;
                return mobEntity.canTarget((LivingEntity) target);
            }
            return false;
        }
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, PuffBaseEntity entity, long time) {
        return true;
    }

    @Override
    protected void finishRunning(ServerWorld world, PuffBaseEntity entity, long time) {
        if(this.attacker != null) {
            this.angryAt(entity, (LivingEntity) this.attacker);
        }
    }

    private void angryAt(PuffBaseEntity entity, LivingEntity target) {
        entity.getBrain().remember(MemoryModuleType.ANGRY_AT, target.getUuid(), 600L);
        entity.getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);

        if (target.getType() == EntityType.PLAYER && entity.world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER)) {
            entity.getBrain().remember(MemoryModuleType.UNIVERSAL_ANGER, true, 600L);
        }
    }
}
