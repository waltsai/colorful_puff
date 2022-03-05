package com.chillycoffey.colorfulpuff.core;

import com.chillycoffey.colorfulpuff.ModInit;
import com.chillycoffey.colorfulpuff.entity.ai.brain.sensor.PuffSpecificSensor;
import com.chillycoffey.colorfulpuff.entity.mob.PuffEntity;
import com.chillycoffey.colorfulpuff.mixin.MemoryModuleTypeInvoker;
import com.chillycoffey.colorfulpuff.mixin.SensorTypeInvoker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.ScheduleBuilder;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.sensor.TemptationsSensor;
import net.minecraft.entity.mob.PiglinBruteBrain;
import net.minecraft.entity.passive.GoatBrain;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Optional;


public class ModEntities {
    public static final EntityType<PuffEntity> PUFF = EntityType.Builder.create(PuffEntity::new, SpawnGroup.CREATURE).setDimensions(0.6F,1.68F).maxTrackingRange(32).build("puff");
    public static Schedule PUFF_ADULT;
    public static SensorType<PuffSpecificSensor> PUFF_SPECIFIC_SENSOR;
    public static MemoryModuleType<List<Entity>> VISIBLE_INTERESTED_ENTITIES;

    public static void registerEntities() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier(ModInit.MODID, "puff"), PUFF);
    }

    public static void registerSchedules() {
        PUFF_ADULT = Registry.register(Registry.SCHEDULE, "puff_adult", new ScheduleBuilder(new Schedule()).withActivity(0, Activity.IDLE).withActivity(18000, Activity.REST).build());
    }

    public static void registerBrain() {
        PUFF_SPECIFIC_SENSOR = SensorTypeInvoker.invokeRegister("puff_specific_sensor", PuffSpecificSensor::new);
        VISIBLE_INTERESTED_ENTITIES = MemoryModuleTypeInvoker.invokeRegister("visible_interested_entities");
    }
}
