package com.waltsai.colorfulpuff.core;

import com.waltsai.colorfulpuff.ModInit;
import com.waltsai.colorfulpuff.entity.ai.brain.sensor.NearestPuffDangerousLivingEntitySensor;
import com.waltsai.colorfulpuff.entity.ai.brain.sensor.PuffSpecificSensor;
import com.waltsai.colorfulpuff.entity.mob.PuffEntity;
import com.waltsai.colorfulpuff.mixin.MemoryModuleTypeInvoker;
import com.waltsai.colorfulpuff.mixin.SensorTypeInvoker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.ScheduleBuilder;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;


public class ModEntities {
    public static final EntityType<PuffEntity> PUFF = EntityType.Builder.create(PuffEntity::new, SpawnGroup.CREATURE).setDimensions(0.6F,1.68F).maxTrackingRange(32).build("puff");
    public static Schedule PUFF_ADULT;
    public static SensorType<PuffSpecificSensor> PUFF_SPECIFIC_SENSOR;
    public static SensorType<NearestPuffDangerousLivingEntitySensor> NEAREST_DANGEROUS_ENTITIES;
    public static MemoryModuleType<List<Entity>> VISIBLE_INTERESTED_ENTITIES;
    public static MemoryModuleType<AnimalEntity> NEAREST_VISIBLE_RIDABLE_ANIMALS;

    public static void registerEntities() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier(ModInit.MODID, "puff"), PUFF);
    }

    public static void registerSchedules() {
        PUFF_ADULT = Registry.register(Registry.SCHEDULE, "puff_adult", new ScheduleBuilder(new Schedule()).withActivity(0, Activity.IDLE).withActivity(18000, Activity.REST).build());
    }

    public static void registerBrain() {
        PUFF_SPECIFIC_SENSOR = SensorTypeInvoker.invokeRegister("puff_specific_sensor", PuffSpecificSensor::new);
        NEAREST_DANGEROUS_ENTITIES = SensorTypeInvoker.invokeRegister("nearest_dangerous_entities", NearestPuffDangerousLivingEntitySensor::new);
        VISIBLE_INTERESTED_ENTITIES = MemoryModuleTypeInvoker.invokeRegister("visible_interested_entities");
        NEAREST_VISIBLE_RIDABLE_ANIMALS = MemoryModuleTypeInvoker.invokeRegister("nearest_visible_ridable_animal");
    }
}
