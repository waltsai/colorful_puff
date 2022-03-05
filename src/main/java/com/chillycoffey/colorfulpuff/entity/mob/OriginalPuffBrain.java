package com.chillycoffey.colorfulpuff.entity.mob;

import com.chillycoffey.colorfulpuff.core.ModEntities;
import com.chillycoffey.colorfulpuff.core.ModTags;
import com.chillycoffey.colorfulpuff.entity.ai.task.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.GoatBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestType;

import javax.swing.text.html.Option;
import java.util.Optional;

public class OriginalPuffBrain {
    public static Brain<PuffEntity> create(PuffEntity entity, Brain<PuffEntity> brain) {
        addCoreActivities(brain);
        addIdleActivities(brain);
        addRestActivities(brain);
        addFightActivities(entity, brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.setSchedule(ModEntities.PUFF_ADULT);
        brain.resetPossibleActivities();

        return brain;
    }

    public static void addCoreActivities(Brain<PuffEntity> brain) {
        brain.setTaskList(Activity.CORE, 0, ImmutableList.of(new StayAboveWaterTask(0.8F), new AvoidAttackerTask(0.9F, 1.1F), new LookAroundTask(40, 120), new WanderAroundTask(), new OpenDoorsTask(), new TemptationCooldownTask(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS), new FindPointOfInterestTask(PointOfInterestType.HOME, MemoryModuleType.HOME, false, Optional.of((byte)14)), new WakeUpTask(), new ForgetAngryAtTargetTask<>(), new ReactAttackTask()));
    }

    public static void addIdleActivities(Brain<PuffEntity> brain) {
        brain.setTaskList(Activity.IDLE, ImmutableList.of(Pair.of(0, new StayWhileSittingTask()), Pair.of(8, new UpdateTargetTask(OriginalPuffBrain::getAngryAtEntity)), Pair.of(1, GoToRememberedPositionTask.toEntity(MemoryModuleType.NEAREST_HOSTILE, 1.05F, 8, true)), Pair.of(1, new TemptTask((entity) -> 1.0F)), Pair.of(2, GoToRememberedPositionTask.toBlock(MemoryModuleType.NEAREST_REPELLENT, 1.0F, 8, true)), Pair.of(3, makeFollowEntitiesTask()), Pair.of(5, new WalkToNearestVisibleWantedItemTask<>(1.06F, true, 6)), Pair.of(6, makeRandomStrollTask(true)), Pair.of(99, new ScheduleActivityTask())), ImmutableSet.of(Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT)));
    }

    public static void addRestActivities(Brain<PuffEntity> brain) {
        brain.setTaskList(Activity.REST, ImmutableList.of(Pair.of(0, new StayWhileSittingTask()), Pair.of(1, new PuffSleepTask()), Pair.of(1, new TemptTask((entity) -> 1.0F)), Pair.of(2, new PuffWalkTowardsTask(MemoryModuleType.HOME, 1.0F, 1, 150, 1200)), Pair.of(2, GoToRememberedPositionTask.toBlock(MemoryModuleType.NEAREST_REPELLENT, 1.0F, 8, true)), Pair.of(3, new ForgetCompletedPointOfInterestTask(PointOfInterestType.HOME, MemoryModuleType.HOME)), Pair.of(5, new WalkToNearestVisibleWantedItemTask<>(1.06F, true, 6)), Pair.of(8, new UpdateTargetTask(OriginalPuffBrain::getAngryAtEntity)), Pair.of(5, new RandomTask<>(ImmutableMap.of(MemoryModuleType.HOME, MemoryModuleState.VALUE_ABSENT), ImmutableList.of(Pair.of(new WalkHomeTask(1.0F), 1), Pair.of(new WanderIndoorsTask(1.0F), 4), Pair.of(new WalkToPointOfInterestTask(1.0F, 4), 2), Pair.of(new WaitTask(20, 40), 2)))), Pair.of(5, makeBusyFollowTask()), Pair.of(99, new ScheduleActivityTask())), ImmutableSet.of(Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT)));
    }

    public static void addFightActivities(PuffEntity entity, Brain<PuffEntity> brain) {
        brain.setTaskList(Activity.FIGHT, 10, ImmutableList.of(new ForgetAttackTargetTask<PuffEntity>((livingEntity) -> {
            return isAttackTargetInvalid(entity, livingEntity);
        }), new RangedApproachTask(1.0F), new MeleeAttackTask(20)), MemoryModuleType.ATTACK_TARGET);
    }
    public static void tickActivities(PuffEntity puffEntity) {
        Brain<PuffEntity> brain = puffEntity.getBrain();

        if (brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET)) {
            puffEntity.setAttacking(brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
            if(brain.getFirstPossibleNonCoreActivity().isPresent() && brain.getFirstPossibleNonCoreActivity().get() != Activity.FIGHT) {
                brain.resetPossibleActivities(ImmutableList.of(Activity.IDLE, Activity.REST, Activity.FIGHT));
            }
        } else {
            if(brain.getFirstPossibleNonCoreActivity().isPresent() && brain.getFirstPossibleNonCoreActivity().get() == Activity.FIGHT) {
                brain.resetPossibleActivities(ImmutableList.of(Activity.IDLE, Activity.REST, Activity.FIGHT));
            }
        }

        if (!brain.hasMemoryModule(MemoryModuleType.RIDE_TARGET) && canRideAnimals(puffEntity)) {
            puffEntity.stopRiding();
        }
    }

    private static boolean canRideAnimals(PuffEntity puffEntity) {
        if (!puffEntity.isBaby()) {
            return false;
        } else {
            Entity entity = puffEntity.getVehicle();
            return entity instanceof PuffEntity && ((PuffEntity)entity).isBaby() || entity instanceof AnimalEntity && !((AnimalEntity)entity).isBaby();
        }
    }

    private static Task<PuffEntity> makeFollowEntitiesTask() {
        return new RandomTask<>(ImmutableList.of(Pair.of(new TimeLimitedTask<>(new FollowNonLivingEntityTask(4.0F), UniformIntProvider.create(40, 80)), 4), Pair.of(new TimeLimitedTask<>(new FollowMobTask(OriginalPuffBrain::isNonPlayerMobEntity, 4.0F), UniformIntProvider.create(40, 80)), 4), Pair.of(new TimeLimitedTask<>(new FollowMobTask(EntityType.PLAYER, 4.0F), UniformIntProvider.create(40, 160)), 8)));
    }

    private static Task<PuffEntity> makeBusyFollowTask() {
        return new RandomTask(ImmutableList.of(Pair.of(new FollowMobTask(ModEntities.PUFF, 8.0F), 2), Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0F), 2), Pair.of(new WaitTask(30, 60), 8)));
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
        if (optional.isPresent() && Sensor.testAttackableTargetPredicateIgnoreVisibility(entity, (LivingEntity)optional.get())) {
            return optional;
        }
        return Optional.empty();
    }

    public static Optional<BlockPos> findNearestDangerousBlock(World world, BlockPos pos, int range) {
        return BlockPos.findClosest(pos, range, 4, (blockPos) -> {
            return world.getBlockState(blockPos).isIn(ModTags.PUFF_REPELLENTS);
        });
    }

    /*
    public static boolean isDangerousBlockAround(PuffEntity puff, BlockPos pos) {
        Optional<BlockPos> optional = puff.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_REPELLENT);
        return optional.isPresent() && ((BlockPos)optional.get()).isWithinDistance(pos, 8.0D);
    }

    protected static void onAttacked(PuffEntity puff, LivingEntity attacker) {
        Brain<PuffEntity> brain = puff.getBrain();
        brain.forget(MemoryModuleType.CELEBRATE_LOCATION);
        brain.forget(MemoryModuleType.DANCING);
        brain.forget(MemoryModuleType.ADMIRING_ITEM);
        if (attacker instanceof PlayerEntity) {
            brain.remember(MemoryModuleType.ADMIRING_DISABLED, true, 400L);
        }

        if (puff.isBaby()) {
            return;
        } else {
            tryRevenge(puff, attacker);
        }
    }

    protected static void tryRevenge(PuffEntity puff, LivingEntity target) {
        if (!puff.getBrain().hasActivity(Activity.AVOID)) {
            if (Sensor.testAttackableTargetPredicateIgnoreVisibility(puff, target)) {
                if (!LookTargetUtil.isNewTargetTooFar(puff, target, 4.0D)) {
                    becomeAngryWith(puff, target);
                }
            }
        }
    }

    protected static void becomeAngryWith(PuffEntity puff, LivingEntity target) {
        if (Sensor.testAttackableTargetPredicateIgnoreVisibility(puff, target)) {
            puff.getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            puff.getBrain().remember(MemoryModuleType.ANGRY_AT, target.getUuid(), 600L);
        }
    }

     */
}
