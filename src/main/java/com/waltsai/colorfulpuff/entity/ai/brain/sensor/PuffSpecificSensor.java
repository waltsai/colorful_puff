package com.waltsai.colorfulpuff.entity.ai.brain.sensor;

import com.waltsai.colorfulpuff.core.ModEntities;
import com.waltsai.colorfulpuff.entity.mob.OriginalPuffBrain;
import com.waltsai.colorfulpuff.entity.mob.PuffEntity;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PuffSpecificSensor extends Sensor<PuffEntity> {
    public static final int MAX_DISTANCE = 10;
    private static final TargetPredicate TEMPTER_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(10.0D).ignoreVisibility();

    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_REPELLENT, MemoryModuleType.TEMPTING_PLAYER, ModEntities.VISIBLE_INTERESTED_ENTITIES, ModEntities.NEAREST_VISIBLE_RIDABLE_ANIMALS);
    }

    protected void sense(ServerWorld serverWorld, PuffEntity puff) {
        Brain<?> brain = puff.getBrain();
        brain.remember(MemoryModuleType.NEAREST_REPELLENT, OriginalPuffBrain.findNearestDangerousBlock(serverWorld, puff.getBlockPos(), 8));

        Stream var10000 = serverWorld.getPlayers().stream().filter(EntityPredicates.EXCEPT_SPECTATOR)
                .filter((player) -> TEMPTER_PREDICATE.test(puff, player))
                .filter((player) -> puff.isInRange(player, MAX_DISTANCE))
                .filter((player) -> this.test(player, puff));
        Objects.requireNonNull(puff);
        List<PlayerEntity> list = (List) var10000.sorted(Comparator.<PuffEntity>comparingDouble(puff::squaredDistanceTo)).collect(Collectors.toList());
        if (!list.isEmpty()) {
            PlayerEntity playerEntity = (PlayerEntity)list.get(0);
            brain.remember(MemoryModuleType.TEMPTING_PLAYER, playerEntity);
        } else {
            brain.forget(MemoryModuleType.TEMPTING_PLAYER);
        }

        Box box = puff.getBoundingBox().expand(16.0D, 16.0D, 16.0D);
        List<Entity> list2 = serverWorld.getEntitiesByClass(Entity.class, box, PuffSpecificSensor::puffWantedToLookAt);
        list2.sort(Comparator.comparingDouble(puff::squaredDistanceTo));
        brain.remember(ModEntities.VISIBLE_INTERESTED_ENTITIES, new ArrayList<>(list2));


        LivingTargetCache livingTargetCache = brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).orElse(LivingTargetCache.empty());
        for (LivingEntity livingEntity : livingTargetCache.iterate(livingEntity -> true)) {
            if (livingEntity instanceof AnimalEntity animal && ridable(animal)) {
                brain.remember(ModEntities.NEAREST_VISIBLE_RIDABLE_ANIMALS, Optional.of(animal));
                break;
            }
        }
    }

    private boolean test(PlayerEntity player, PuffEntity puff) {
        return PuffEntity.isPuffTemptedItem(puff, player.getMainHandStack()) || PuffEntity.isPuffTemptedItem(puff, player.getOffHandStack());
    }

    private static boolean puffWantedToLookAt(Entity puff) {
        return puff instanceof FishingBobberEntity || puff instanceof LeashKnotEntity || puff instanceof MinecartEntity || puff instanceof ItemEntity;
    }

    private static boolean ridable(AnimalEntity animal) {
        return (animal instanceof ChickenEntity && !animal.isBaby()) || (animal instanceof SheepEntity && !animal.isBaby()) || (animal instanceof CowEntity && !animal.isBaby()) || animal instanceof PigEntity || animal instanceof HorseEntity || animal instanceof AbstractDonkeyEntity;
    }
}
