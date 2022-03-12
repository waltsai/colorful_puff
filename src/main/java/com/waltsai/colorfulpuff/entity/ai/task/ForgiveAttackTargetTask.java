package com.waltsai.colorfulpuff.entity.ai.task;

import com.waltsai.colorfulpuff.entity.mob.PuffBaseEntity;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;

public class ForgiveAttackTargetTask extends Task<PuffBaseEntity> {
    public ForgiveAttackTargetTask() {
        super(ImmutableMap.of(MemoryModuleType.ANGRY_AT, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.REGISTERED));
    }


}
