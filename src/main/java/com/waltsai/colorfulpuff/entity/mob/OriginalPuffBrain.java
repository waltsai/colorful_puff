package com.waltsai.colorfulpuff.entity.mob;

import com.waltsai.colorfulpuff.core.ModEntities;
import com.waltsai.colorfulpuff.core.ModTags;
import com.waltsai.colorfulpuff.entity.ai.task.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.mob.*;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.Optional;

public class OriginalPuffBrain {
    private static final UniformIntProvider MEMORY_TRANSFER_TASK_DURATION = TimeHelper.betweenSeconds(10, 40);
    private static final UniformIntProvider RIDE_TARGET_MEMORY_DURATION = TimeHelper.betweenSeconds(10, 30);

    public static Brain<PuffEntity> create(PuffEntity entity, Brain<PuffEntity> brain) {
        addCoreActivities(entity, brain);
        addIdleActivities(entity, brain);
        addRestActivities(entity, brain);
        addFightActivities(entity, brain);
        addRideActivities(entity, brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.setSchedule(ModEntities.PUFF_ADULT);
        brain.resetPossibleActivities();

        return brain;
    }

    public static void addCoreActivities(PuffEntity entity, Brain<PuffEntity> brain) {
        brain.setTaskList(Activity.CORE, 0, ImmutableList.of(new StayAboveWaterTask(0.8F), new AvoidAttackerTask(0.9F, 1.1F), new LookAroundTask(80, 160), new WanderAroundTask(), new OpenDoorsTask(), new TemptationCooldownTask(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS), new FindPointOfInterestTask(PointOfInterestType.HOME, MemoryModuleType.HOME, false, Optional.of((byte)14)), new WakeUpTask(), new ForgetAngryAtTargetTask<>(), new ReactAttackTask()));
    }

    public static void addIdleActivities(PuffEntity entity, Brain<PuffEntity> brain) {
        brain.setTaskList(Activity.IDLE, ImmutableList.of(Pair.of(0, new StayWhileSittingTask()), Pair.of(8, new UpdateTargetTask(OriginalPuffBrain::getAngryAtEntity)), Pair.of(1, GoToRememberedPositionTask.toEntity(MemoryModuleType.NEAREST_HOSTILE, 1.05F, 8, true)), Pair.of(1, new TemptTask((n) -> 1.0F)), Pair.of(2, GoToRememberedPositionTask.toBlock(MemoryModuleType.NEAREST_REPELLENT, 1.0F, 8, true)), Pair.of(3, makeFollowEntitiesTask()), Pair.of(5, new WalkToNearestVisibleWantedItemTask<>(1.06F, true, 6)), Pair.of(6, makeRandomStrollTask(PuffEntity.isVivid(entity))), Pair.of(8, OriginalPuffBrain.makeRememberRideableAnimalTask()), Pair.of(99, new ScheduleActivityTask())), ImmutableSet.of(Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT), Pair.of(MemoryModuleType.RIDE_TARGET, MemoryModuleState.VALUE_ABSENT)));
    }

    public static void addRestActivities(PuffEntity entity, Brain<PuffEntity> brain) {
        brain.setTaskList(Activity.REST, ImmutableList.of(Pair.of(0, new StayWhileSittingTask()), Pair.of(1, new PuffSleepTask()), Pair.of(1, new TemptTask((n) -> 1.0F)), Pair.of(1, GoToRememberedPositionTask.toEntity(MemoryModuleType.NEAREST_HOSTILE, 1.05F, 8, true)), Pair.of(2, new PuffWalkTowardsTask(MemoryModuleType.HOME, 1.0F, 1, 150, 1200)), Pair.of(2, GoToRememberedPositionTask.toBlock(MemoryModuleType.NEAREST_REPELLENT, 1.0F, 8, true)), Pair.of(3, new ForgetCompletedPointOfInterestTask(PointOfInterestType.HOME, MemoryModuleType.HOME)), Pair.of(5, new WalkToNearestVisibleWantedItemTask<>(1.06F, true, 6)), Pair.of(8, new UpdateTargetTask(OriginalPuffBrain::getAngryAtEntity)), Pair.of(5, new RandomTask<>(ImmutableMap.of(MemoryModuleType.HOME, MemoryModuleState.VALUE_ABSENT), ImmutableList.of(Pair.of(new WalkHomeTask(1.0F), 1), Pair.of(new WanderIndoorsTask(1.0F), 4), Pair.of(new WalkToPointOfInterestTask(1.0F, 4), 2), Pair.of(new WaitTask(20, 40), 2)))), Pair.of(5, makeBusyFollowTask()), Pair.of(99, new ScheduleActivityTask())), ImmutableSet.of(Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT), Pair.of(MemoryModuleType.RIDE_TARGET, MemoryModuleState.VALUE_ABSENT)));
    }

    private static void addRideActivities(PuffEntity entity, Brain<PuffEntity> brain) {
        brain.setTaskList(Activity.RIDE, 10, ImmutableList.of(new StartRidingTask<>(0.8f), new ConditionalTask<>(Entity::hasVehicle, OriginalPuffBrain.makeFollowEntitiesTask()), new RidingTask<>(8, OriginalPuffBrain::canRide)), MemoryModuleType.RIDE_TARGET);
    }

    public static void addFightActivities(PuffEntity entity, Brain<PuffEntity> brain) {
        brain.setTaskList(Activity.FIGHT, 10, ImmutableList.of(new ForgetAttackTargetTask<>((livingEntity) -> {
            return isAttackTargetInvalid(entity, livingEntity);
        }), new RangedApproachTask(1.0F), new MeleeAttackTask(20)), MemoryModuleType.ATTACK_TARGET);
    }

    public static void tickActivities(PuffEntity puffEntity) {
        Brain<PuffEntity> brain = puffEntity.getBrain();

        if (brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET)) {
            puffEntity.setAttacking(brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
            if(brain.getFirstPossibleNonCoreActivity().isPresent() && brain.getFirstPossibleNonCoreActivity().get() != Activity.FIGHT) {
                brain.resetPossibleActivities(ImmutableList.of(Activity.IDLE, Activity.REST, Activity.FIGHT, Activity.RIDE));
            }
        } else {
            if(brain.getFirstPossibleNonCoreActivity().isPresent() && brain.getFirstPossibleNonCoreActivity().get() == Activity.FIGHT) {
                brain.resetPossibleActivities(ImmutableList.of(Activity.IDLE, Activity.REST, Activity.FIGHT, Activity.RIDE));
            }

            if (brain.hasMemoryModule(MemoryModuleType.RIDE_TARGET)) {
                brain.doExclusively(Activity.RIDE);
            } else {
                if(brain.getFirstPossibleNonCoreActivity().isPresent() && brain.getFirstPossibleNonCoreActivity().get() == Activity.RIDE) {
                    brain.resetPossibleActivities(ImmutableList.of(Activity.IDLE, Activity.REST, Activity.RIDE));
                }
            }
        }

        if (!brain.hasMemoryModule(MemoryModuleType.RIDE_TARGET)) {
            puffEntity.stopRiding();
        }
    }

    private static boolean canRide(PuffEntity puff, Entity ridden) {
        if (ridden instanceof MobEntity mobEntity) {
            return !mobEntity.isAlive() || OriginalPuffBrain.hasBeenHurt(puff);
        }
        return false;
    }

    private static boolean hasBeenHurt(LivingEntity puff) {
        return puff.getBrain().hasMemoryModule(MemoryModuleType.HURT_BY);
    }

    private static Task<PuffEntity> makeFollowEntitiesTask() {
        return new RandomTask<>(ImmutableList.of(Pair.of(new TimeLimitedTask<>(new FollowNonLivingEntityTask(4.0F), UniformIntProvider.create(40, 80)), 4), Pair.of(new TimeLimitedTask<>(new FollowMobTask(OriginalPuffBrain::isNonPlayerMobEntity, 4.0F), UniformIntProvider.create(40, 80)), 4), Pair.of(new TimeLimitedTask<>(new FollowMobTask(EntityType.PLAYER, 4.0F), UniformIntProvider.create(40, 160)), 8)));
    }

    private static Task<PuffEntity> makeBusyFollowTask() {
        return new RandomTask<>(ImmutableList.of(Pair.of(new FollowMobTask(ModEntities.PUFF, 8.0F), 2), Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0F), 2), Pair.of(new WaitTask(30, 60), 8)));
    }

    private static TimeLimitedTask<PuffEntity> makeRememberRideableAnimalTask() {
        return new TimeLimitedTask<>(new MemoryTransferTask<>(PuffEntity::isBaby, ModEntities.NEAREST_VISIBLE_RIDABLE_ANIMALS, MemoryModuleType.RIDE_TARGET, RIDE_TARGET_MEMORY_DURATION), MEMORY_TRANSFER_TASK_DURATION);
    }

    private static Task<PuffEntity> makeRandomStrollTask(boolean isVivid) {
        if(!isVivid) {
            return new RandomTask<>(ImmutableList.of(Pair.of(new StrollTask(1.0F), 2), Pair.of(FindEntityTask.create(ModEntities.PUFF, 8, MemoryModuleType.INTERACTION_TARGET, 1.0F, 2), 2), Pair.of(new GoTowardsLookTarget(0.6F, 3), 2), Pair.of(new WaitTask(40, 80), 4)));
        }
        return new RandomTask<>(ImmutableList.of(Pair.of(new StrollTask(1.06F), 2), Pair.of(FindEntityTask.create(ModEntities.PUFF, 8, MemoryModuleType.INTERACTION_TARGET, 1.06F, 2), 2), Pair.of(new GoTowardsLookTarget(0.6F, 3), 2), Pair.of(new WaitTask(40, 80), 2)));
    }

    //

    private static boolean isNonPlayerMobEntity(LivingEntity entity) {
        if(entity.isMobOrPlayer()) {
            return !entity.isPlayer();
        }
        return false;
    }

    private static boolean isAttackTargetInvalid(PuffEntity puff, LivingEntity target) {
        return !puff.getBrain().hasMemoryModule(MemoryModuleType.ANGRY_AT) || !puff.getBrain().getOptionalMemory(MemoryModuleType.ANGRY_AT).get().equals(target.getUuid());
    }

    public static Optional<LivingEntity> getAngryAtEntity(LivingEntity entity) {
        Optional<LivingEntity> optional = LookTargetUtil.getEntity(entity, MemoryModuleType.ANGRY_AT);
        if (optional.isPresent() && Sensor.testAttackableTargetPredicateIgnoreVisibility(entity, optional.get())) {
            return optional;
        }
        return Optional.empty();
    }

    public static Optional<BlockPos> findNearestDangerousBlock(World world, BlockPos pos, int range) {
        return BlockPos.findClosest(pos, range, 4, (blockPos) -> world.getBlockState(blockPos).isIn(ModTags.PUFF_REPELLENTS));
    }
}
