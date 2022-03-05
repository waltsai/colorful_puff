package com.chillycoffey.colorfulpuff.entity.mob;

import com.chillycoffey.colorfulpuff.core.ModEntities;
import com.chillycoffey.colorfulpuff.core.ModTags;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.Tag;
import com.mojang.serialization.Dynamic;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.CallbackI;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class PuffEntity extends PuffBaseEntity {
    protected static final ImmutableList<? extends SensorType<? extends Sensor<? super PuffEntity>>> SENSORS;
    protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_MODULES;
    public static final TrackedData<Integer> CLOTH_TYPE;
    public static final TrackedData<Integer> EYE_TYPE;
    public static final TrackedData<Integer> PERSONALITY;
    private ClothType prevCloth;
    private int maxAir = 300;
    private boolean isFireImmune = false;
    private boolean canGrow;

    public PuffEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.getNavigation().setCanSwim(true);
        ((MobNavigation) this.getNavigation()).setCanPathThroughDoors(true);
        this.setCanPickUpLoot(true);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0F);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
    }

    //Brain

    protected Brain.Profile<PuffEntity> createBrainProfile() {
        return Brain.createProfile(MEMORY_MODULES, SENSORS);
    }

    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        Brain<PuffEntity> brain = this.createBrainProfile().deserialize(dynamic);
        OriginalPuffBrain.create(this, brain);
        return brain;
    }

    public Brain<PuffEntity> getBrain() {
        return (Brain<PuffEntity>) super.getBrain();
    }

    protected void mobTick() {
        this.world.getProfiler().push("puffBrain");
        this.getBrain().tick((ServerWorld) this.world, this);
        this.world.getProfiler().pop();
        OriginalPuffBrain.tickActivities(this);
        super.mobTick();

        if (this.prevCloth != this.getClothType()) {
            this.updateAttributes();
            this.prevCloth = this.getClothType();
        }
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        if(!canGrow) {
            this.setBreedingAge(-36000);
        }
    }

    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    private void reinitializeBrain(ServerWorld world) {
        Brain<PuffEntity> brain = this.getBrain();
        brain.stopAllTasks(world, this);
        this.brain = brain.copy();
        OriginalPuffBrain.create(this, this.getBrain());
        brain.refreshActivities(this.world.getTimeOfDay(), this.world.getTime());
    }

    //Attribute Settings

    public static DefaultAttributeContainer.Builder createPuffAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 80).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 13);
    }

    public void updateAttributes() {
        if (this.havingCloth(ClothType.CAREMAL)) {
            setAttributes(100, 0.31, 13, 8, true);
        } else if (this.havingCloth(ClothType.ORANGE)) {
            setAttributes(150, 0.28, 13, 13, true);
        } else if (this.havingCloth(ClothType.RED)) {
            setAttributes(150, 0.28, 13, 9, true);
        } else if (this.havingCloth(ClothType.HONEY)) {
            setAttributes(150, 0.26, 6, 5, false);
        } else if (this.havingCloth(ClothType.MIFU)) {
            setAttributes(422, 0.3, 13, 20, false);
        } else if (this.havingCloth(ClothType.MIFU)) {
            setAttributes(422, 0.3, 13, 20, false);
        } else if (this.havingCloth(ClothType.BLACK, ClothType.GRAY, ClothType.SILVER, ClothType.YELLOW)) {
            setAttributes(150, 0.28, 13, 15, false);
        } else if (this.havingCloth(ClothType.BLUE, ClothType.CYAN, ClothType.LIGHT_BLUE)) {
            setAttributes(150, 0.28, 13, 30, false);
        } else if (this.havingCloth(ClothType.GREEN)) {
            setAttributes(200, 0.28, 13, 15, false);
        } else if (this.havingCloth(ClothType.LIME)) {
            setAttributes(180, 0.29, 13, 15, false);
        } else if (this.havingCloth(ClothType.MAGENTA, ClothType.PINK)) {
            setAttributes(150, 0.28, 13, 12, false);
        } else if (this.havingCloth(ClothType.UNDYED)) {
            setAttributes(300, 0.3, 13, 12, false);
        } else if (this.havingCloth(ClothType.WHITE)) {
            setAttributes(120, 0.28, 13, 22, false);
        }
    }

    public void setAttributes(double maxHealth, double movementSpeed, double damage, int maxAir, boolean fireImmune) {
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(movementSpeed);
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(damage);
        this.maxAir = maxAir;
        this.isFireImmune = fireImmune;
    }

    @Override
    public int getMaxAir() {
        return maxAir;
    }

    @Override
    public boolean isFireImmune() {
        return isFireImmune;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CLOTH_TYPE, 0);
        this.dataTracker.startTracking(EYE_TYPE, 0);
        this.dataTracker.startTracking(PERSONALITY, 0);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("ClothType", this.getClothType().getId());
        nbt.putInt("EyeType", this.getEyeType().getId());
        nbt.putInt("Personality", this.getPersonality().getId());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setClothType(ClothType.byId(nbt.getInt("ClothType")));
        this.setEyeType(EyeType.byId(nbt.getInt("EyeType")));
        this.setPersonality(PersonalityType.byId(nbt.getInt("Personality")));

        if (this.world instanceof ServerWorld) {
            this.reinitializeBrain((ServerWorld) this.world);
        }

        brain.refreshActivities(this.world.getTimeOfDay(), this.world.getTime());
    }

    //Entity Behaviors

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setClothType(null);
        this.setEyeType(null);
        this.setPersonality(null);
        this.setBaby(true);
        this.canGrow = false;
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        Item item = itemStack.getItem();
        if (this.world.isClient) {
            if (this.isTamed() && this.isOwner(player)) {
                return ActionResult.SUCCESS;
            } else {
                return !this.isBreedingItem(itemStack) || !(this.getHealth() < this.getMaxHealth()) && this.isTamed() ? ActionResult.PASS : ActionResult.SUCCESS;
            }
        } else {
            if(itemStack.isIn(PuffEntity.getPuffBreedItem(this))) {
                if(this.brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET) && this.brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get().equals(player.getUuid())) {
                    if (itemStack.isOf(Items.MILK_BUCKET) || itemStack.isOf(Items.WATER_BUCKET)) {
                        if (!player.getAbilities().creativeMode) {
                            itemStack.decrement(1);
                            player.giveItemStack(new ItemStack(Items.BUCKET, 1));
                        }
                    } else {
                        if (!player.getAbilities().creativeMode) {
                            itemStack.decrement(1);
                        }
                    }

                    this.brain.forget(MemoryModuleType.ANGRY_AT);
                    this.brain.forget(MemoryModuleType.ATTACK_TARGET);
                    return ActionResult.SUCCESS;
                } else if(this.getHealth() < this.getMaxHealth()) {
                    this.world.sendEntityStatus(this, (byte) 14);
                    if(itemStack.getItem().getFoodComponent() != null) {
                        this.heal(itemStack.getItem().getFoodComponent().getSaturationModifier() * 1.75F);
                    }

                    if (itemStack.isOf(Items.MILK_BUCKET) || itemStack.isOf(Items.WATER_BUCKET)) {
                        if (!player.getAbilities().creativeMode) {
                            itemStack.decrement(1);
                            player.giveItemStack(new ItemStack(Items.BUCKET, 1));
                        }
                    } else {
                        if (!player.getAbilities().creativeMode) {
                            itemStack.decrement(1);
                        }
                    }
                }
            }
            else if(this.isTamed()) {
                if (ClothType.byItem(item) != null && !this.havingCloth(ClothType.byItem(item))) {
                    this.setClothType(ClothType.byItem(item));
                    this.world.sendEntityStatus(this, (byte) (61 + ClothType.byItem(item).getId()));
                    if (!player.getAbilities().creativeMode) {
                        if (ClothType.byItem(item).getItemToDye() instanceof DyeItem) {
                            itemStack.decrement(1);
                        } else if ((ClothType.byItem(item).getItemToDye() instanceof PotionItem && PotionUtil.getPotionEffects(itemStack).size() == 0) || ClothType.byItem(item).getItemToDye() instanceof HoneyBottleItem) {
                            player.giveItemStack(new ItemStack(Items.GLASS_BOTTLE, 1));
                            itemStack.decrement(1);
                        }
                    }
                    this.updateAttributes();

                    return ActionResult.SUCCESS;
                }

                if(itemStack.isOf(Items.ENCHANTED_GOLDEN_APPLE)) {
                    this.canGrow = true;
                }
            } else {
                if (itemStack.isIn(PuffEntity.getPuffTamedItem(this))) {
                    if (itemStack.isOf(Items.MILK_BUCKET)) {
                        if (!player.getAbilities().creativeMode) {
                            itemStack.decrement(1);
                            player.giveItemStack(new ItemStack(Items.BUCKET, 1));
                        }
                    } else {
                        if (!player.getAbilities().creativeMode) {
                            itemStack.decrement(1);
                        }
                    }

                    if ((itemStack.isOf(Items.GOLDEN_APPLE) || itemStack.isOf(Items.ENCHANTED_GOLDEN_APPLE)) || this.random.nextInt(3) == 0) {
                        this.setOwner(player);
                        this.navigation.stop();
                        this.setTarget(null);
                        this.setSitting(true);
                        this.world.sendEntityStatus(this, (byte) 7);
                    } else {
                        this.world.sendEntityStatus(this, (byte) 6);
                    }

                    return ActionResult.SUCCESS;
                }
            }

            ActionResult actionResult = super.interactMob(player, hand);
            if (!actionResult.isAccepted() && hand == Hand.MAIN_HAND) {
                this.setSitting(!this.isSitting());
                return ActionResult.SUCCESS;
            }

            return super.interactMob(player, hand);
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean bl = super.damage(source, amount);
        if (this.world.isClient) {
            return false;
        } else {
            return bl;
        }
    }

    public void setBaby(boolean baby) {
        this.setBreedingAge(baby ? -36000 : 0);
    }

    @Override
    public float getScaleFactor() {
        if(this.isBaby()) {
            return this.getClothType() == PuffEntity.ClothType.HONEY ? 0.66F * 0.909F : 0.66F;
        } else {
            return this.getClothType() == PuffEntity.ClothType.HONEY ? 0.88F * 0.909F : 0.88F;
        }
    }

    /*
    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {

        if (OriginalPuffBrain.isDangerousBlockAround(this, pos)) {
            return -1.0F;
        } else {
            return 0.0F;
        }
    }

     */

    @Override
    public void handleStatus(byte status) {
        super.handleStatus(status);
        if ((status > 60 && status < 77) || status == 78) {
            float alpha = this.getRandom().nextFloat() * 0.25F + 0.75F;
            this.produceParticles(new DustParticleEffect(new Vec3f(Vec3d.unpackRgb(ClothType.byId(status - 61).getColorCode())), alpha), 8);
        }
        if (status == 77) {
            this.produceParticles(ParticleTypes.CLOUD, 8);
        }
        if (status == 14) {
            this.produceParticles(ParticleTypes.HAPPY_VILLAGER, 5);
        }
    }

    protected void produceParticles(ParticleEffect parameters, int count) {
        for(int i = 0; i < count; ++i) {
            double d = this.random.nextGaussian() * 0.02D;
            double e = this.random.nextGaussian() * 0.02D;
            double f = this.random.nextGaussian() * 0.02D;
            this.world.addParticle(parameters, this.getParticleX(1.0D), this.getRandomBodyY() + 1.0D, this.getParticleZ(1.0D), d, e, f);
        }

    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    @Override
    public boolean canPickupItem(ItemStack stack) {
        return true;
    }

    //Utils

    public static TagKey<Item> getPuffTemptedItem(PuffEntity entity) {
        if (false && !entity.isBaby()) {

        } else {
            if (entity.getPersonality() == PersonalityType.VIVID) {
                return ModTags.VIVID_PUFF_TEMPTED_ITEM;
            }
            if (entity.getPersonality() == PersonalityType.DIGNIFIED) {
                return ModTags.DIGNIFIED_PUFF_TEMPTED_ITEM;
            }
            if (entity.getPersonality() == PersonalityType.TIMID) {
                return ModTags.TIMID_PUFF_TEMPTED_ITEM;
            }
        }
        return null;
    }

    public static TagKey<Item> getPuffTamedItem(PuffEntity entity) {
        if (false && !entity.isBaby()) {

        } else {
            if (entity.getPersonality() == PersonalityType.VIVID) {
                return ModTags.VIVID_PUFF_TAMED_ITEM;
            }
            if (entity.getPersonality() == PersonalityType.DIGNIFIED) {
                return ModTags.DIGNIFIED_PUFF_TAMED_ITEM;
            }
            if (entity.getPersonality() == PersonalityType.TIMID) {
                return ModTags.TIMID_PUFF_TAMED_ITEM;
            }
        }
        return null;
    }

    public static TagKey<Item> getPuffBreedItem(PuffEntity entity) {
        if (false && !entity.isBaby()) {

        } else {
            if (entity.getPersonality() == PersonalityType.VIVID) {
                return ModTags.VIVID_PUFF_TAMED_ITEM;
            }
            if (entity.getPersonality() == PersonalityType.DIGNIFIED) {
                return ModTags.DIGNIFIED_PUFF_TAMED_ITEM;
            }
            if (entity.getPersonality() == PersonalityType.TIMID) {
                return ModTags.TIMID_PUFF_TAMED_ITEM;
            }
        }
        return null;
    }

    public static boolean isPuffTemptedItem(PuffEntity entity, ItemStack item) {
        if(getPuffTemptedItem(entity) == null) {
            return false;
        }
        return item.isIn(getPuffTemptedItem(entity));
    }

    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(PuffEntity.getPuffBreedItem(this));
    }

    public ClothType getClothType() {
        return ClothType.byId(dataTracker.get(CLOTH_TYPE));
    }

    public EyeType getEyeType() {
        return EyeType.byId(dataTracker.get(EYE_TYPE));
    }

    public PersonalityType getPersonality() {
        return PersonalityType.byId(dataTracker.get(PERSONALITY));
    }

    public static boolean isVivid(PuffEntity entity) {
        return PersonalityType.byId(entity.dataTracker.get(PERSONALITY)) == PersonalityType.VIVID;
    }

    public static boolean isNotVivid(PuffEntity entity) {
        return PersonalityType.byId(entity.dataTracker.get(PERSONALITY)) != PersonalityType.VIVID;
    }

    public void setPersonality(@Nullable PersonalityType type) {
        if(type == null) {
            dataTracker.set(PERSONALITY, PersonalityType.createRandom(this.getRandom()).getId());
            return;
        }
        dataTracker.set(PERSONALITY, type.getId());
    }

    public void setClothType(@Nullable ClothType type) {
        if(type == null) {
            dataTracker.set(CLOTH_TYPE, ClothType.createRandom(this.getRandom()).getId());
            return;
        }
        dataTracker.set(CLOTH_TYPE, type.getId());
    }

    public void setEyeType(@Nullable EyeType type) {
        if(type == null) {
            dataTracker.set(EYE_TYPE, EyeType.createRandom(this.getRandom()).getId());
            return;
        }
        dataTracker.set(EYE_TYPE, type.getId());
    }

    public boolean havingCloth(ClothType... type) {
        return Arrays.stream(type).filter((e) -> e == this.getClothType()).toArray().length > 0;
    }

    public boolean havingEye(EyeType type) {
        return EyeType.byId(dataTracker.get(EYE_TYPE)) == type;
    }

    static {
        CLOTH_TYPE = DataTracker.registerData(PuffEntity.class, TrackedDataHandlerRegistry.INTEGER);
        EYE_TYPE = DataTracker.registerData(PuffEntity.class, TrackedDataHandlerRegistry.INTEGER);
        PERSONALITY = DataTracker.registerData(PuffEntity.class, TrackedDataHandlerRegistry.INTEGER);
        SENSORS = ImmutableList.of(SensorType.HURT_BY, SensorType.NEAREST_ITEMS, SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, ModEntities.PUFF_SPECIFIC_SENSOR, ModEntities.NEAREST_DANGEROUS_ENTITIES);
        MEMORY_MODULES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.PATH, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.IS_TEMPTED, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.BREED_TARGET, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.NEAREST_REPELLENT, ModEntities.VISIBLE_INTERESTED_ENTITIES, MemoryModuleType.HOME, MemoryModuleType.LAST_SLEPT, MemoryModuleType.LAST_WOKEN, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.ANGRY_AT, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.NEAREST_HOSTILE);
    }

    public enum ClothType {
        WHITE(0, "white", 16383998, Items.WHITE_DYE),
        ORANGE(1, "orange", 16351261, Items.ORANGE_DYE),
        MAGENTA(2, "magenta", 13061821, Items.MAGENTA_DYE),
        LIGHT_BLUE(3, "light_blue", 3847130, Items.LIGHT_BLUE_DYE),
        YELLOW(4, "yellow", 16701501, Items.YELLOW_DYE),
        LIME(5, "lime", 8439583, Items.LIME_DYE),
        PINK(6, "pink", 15961002, Items.PINK_DYE),
        GRAY(7, "gray", 4673362, Items.GRAY_DYE),
        SILVER(8, "silver", 10329495, Items.LIGHT_GRAY_DYE),
        CYAN(9, "cyan", 1481884, Items.CYAN_DYE),
        PURPLE(10, "purple", 8991416, Items.PURPLE_DYE),
        BLUE(11, "blue", 3949738, Items.BLUE_DYE),
        BROWN(12, "brown", 8606770, Items.BROWN_DYE),
        GREEN(13, "green", 6192150, Items.GREEN_DYE),
        RED(14, "red", 11546150, Items.RED_DYE),
        BLACK(15, "black", 1908001, Items.BLACK_DYE),
        UNDYED(16, "undyed", -1, Items.POTION),
        HONEY(17, "honey", 13408512, Items.HONEY_BOTTLE),
        NECTAR(18, "nectar", -1, null),
        CAREMAL(19, "caramel", -1, null),
        MIFU(20, "mifu", -1, null);


        private final int id;
        private final String name;
        private final int colorCode;
        private final Item itemToDye;

        ClothType(int id, String name, int colorCode, @Nullable Item itemToDye) {
            this.id = id;
            this.name = name;
            this.colorCode = colorCode;
            this.itemToDye = itemToDye;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public int getColorCode() {
            return this.colorCode;
        }

        @Nullable
        public Item getItemToDye() {
            return this.itemToDye;
        }

        public static ClothType byId(int id) {
            for(int i = 0; i <= 20; i++) {
                if(values()[i].getId() == id) {
                    return values()[i];
                }
            }
            return null;
        }

        public static ClothType byName(String name) {
            for(int i = 0; i <= 20; i++) {
                if(values()[i].getName().equals(name)) {
                    return values()[i];
                }
            }
            return null;
        }

        public static ClothType byItem(Item item) {
            for(ClothType type : values()) {
                if(type.getItemToDye() != null && type.getItemToDye() == item) {
                    return type;
                }
            }
            return null;
        }

        public static ClothType createRandom(Random random) {
            int i = random.nextInt(66);

            if(i < 9) {
                return ClothType.ORANGE;
            } else if(i < 14) {
                return ClothType.RED;
            } else if(i < 19) {
                return ClothType.BLUE;
            } else if(i < 24) {
                return ClothType.GRAY;
            } else if(i < 27) {
                return ClothType.BLACK;
            } else if(i < 31) {
                return ClothType.CYAN;
            } else if(i < 36) {
                return ClothType.BROWN;
            } else if(i < 41) {
                return ClothType.SILVER;
            } else if(i < 43) {
                return ClothType.WHITE;
            } else if(i < 47) {
                return ClothType.PINK;
            } else if(i < 50) {
                return ClothType.MAGENTA;
            } else if(i < 51) {
                return ClothType.PURPLE;
            } else if(i < 52) {
                return ClothType.UNDYED;
            } else if(i < 54) {
                return ClothType.LIGHT_BLUE;
            } else if(i < 57) {
                return ClothType.LIME;
            } else if(i < 62) {
                return ClothType.GREEN;
            } else {
                return ClothType.YELLOW;
            }
        }
    }

    public enum EyeType {
        EYE1(0, "eye1"),
        EYE2(1, "eye2"),
        EYE3(2, "eye3"),
        EYE4(3, "eye4"),
        EYE5(4, "eye5"),
        EYE6(5, "eye6"),
        EYE7(6, "eye7"),
        EYE8(7, "eye8"),
        EYE9(8, "eye9"),
        EYE10(9, "eye10"),
        EYE11(10, "eye11");


        private final int id;
        private final String name;

        EyeType(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public static EyeType byId(int id) {
            for(int i = 0; i <= 20; i++) {
                if(values()[i].getId() == id) {
                    return values()[i];
                }
            }
            return null;
        }

        public static EyeType byName(String name) {
            for(int i = 0; i <= 20; i++) {
                if(values()[i].getName().equals(name)) {
                    return values()[i];
                }
            }
            return null;
        }

        public static EyeType createRandom(Random random) {
            int i = random.nextInt(1000);

            if(i < 880) {
                return EyeType.EYE1;
            } else if(i < 910) {
                return EyeType.EYE2;
            } else if(i < 930) {
                return EyeType.EYE3;
            } else if(i < 950) {
                return EyeType.EYE4;
            } else if(i < 960) {
                return EyeType.EYE5;
            } else if(i < 970) {
                return EyeType.EYE6;
            } else if(i < 980) {
                return EyeType.EYE7;
            } else if(i < 988) {
                return EyeType.EYE8;
            } else if(i < 995) {
                return EyeType.EYE9;
            } else if(i < 999) {
                return EyeType.EYE10;
            } else {
                return EyeType.EYE11;
            }
        }
    }

    public enum PersonalityType {
        VIVID(0, "vivid"),
        TIMID(1, "timid"),
        DIGNIFIED(2, "dignified");


        private final int id;
        private final String name;

        PersonalityType(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public static PersonalityType byId(int id) {
            for(int i = 0; i <= 20; i++) {
                if(values()[i].getId() == id) {
                    return values()[i];
                }
            }
            return null;
        }

        public static PersonalityType byName(String name) {
            for(int i = 0; i <= 20; i++) {
                if(values()[i].getName().equals(name)) {
                    return values()[i];
                }
            }
            return null;
        }

        public static PersonalityType createRandom(Random random) {
            int i = random.nextInt(3);
            return PersonalityType.byId(i);
        }
    }

    public enum Shareables {
        /*
        {
            "item":"minecraft:apple",
                "priority":0,
                "max_amount":1
        },
        {
            "item":"minecraft:netherite_helmet",
                "priority":9,
                "max_amount":0
        },
        {
            "item":"minecraft:netherite_chestplate",
                "priority":9,
                "max_amount":0
        },
        {
            "item":"minecraft:netherite_leggings",
                "priority":9,
                "max_amount":0
        },
        {
            "item":"minecraft:netherite_boots",
                "priority":9,
                "max_amount":0
        },
        {
            "item":"minecraft:elytra",
                "priority":9,
                "max_amount":0
        },
        {
            "item":"minecraft:iron_helmet",
                "priority":1,
                "max_amount":1
        },
        {
            "item":"minecraft:golden_helmet",
                "priority":1,
                "max_amount":1
        },
        {
            "item":"minecraft:diamond_helmet",
                "priority":1,
                "max_amount":1
        },
        {
            "item":"minecraft:appleEnchanted",
                "priority":0,
                "max_amount":1
        },
        {
            "item":"minecraft:baked_potato",
                "priority":0,
                "max_amount":1
        },
        {
            "item":"minecraft:beef",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:beetroot",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:beetroot_soup",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:bread",
                "priority":0,
                "max_amount":1
        },
        {
            "item":"minecraft:carrot",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:chicken",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:chorus_fruit",
                "priority":0,
                "max_amount":1
        },
        {
            "item":"minecraft:clownfish",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:cooked_beef",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:cooked_chicken",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:cooked_fish",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:cooked_porkchop",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:cooked_rabbit",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:cooked_salmon",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:cookie",
                "priority":0,
                "max_amount":1
        },
        {
            "item":"minecraft:dried_kelp",
                "priority":0,
                "max_amount":1
        },
        {
            "item":"minecraft:fish",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:golden_apple",
                "priority":0,
                "max_amount":1
        },
        {
            "item":"minecraft:golden_carrot",
                "priority":0,
                "max_amount":1
        },
        {
            "item":"minecraft:melon",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:mushroom_stew",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:muttonCooked",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:muttonRaw",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:poisonous_potato",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:porkchop",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:potato",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:pufferfish",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:pumpkin_pie",
                "priority":0,
                "max_amount":1
        },
        {
            "item":"minecraft:rabbit",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:rabbit_stew",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:rotten_flesh",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:salmon",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:spider_eye",
                "priority":0,
                "max_amount":0
        },
        {
            "item":"minecraft:sweet_berries",
                "priority":0,
                "max_amount":1
        },
        {
            "item":"minecraft:suspicious_stew",
                "priority":0,
                "max_amount":0
        }
        */
    }
}
