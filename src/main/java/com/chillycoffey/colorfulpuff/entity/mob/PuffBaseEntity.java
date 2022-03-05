package com.chillycoffey.colorfulpuff.entity.mob;

import com.chillycoffey.colorfulpuff.core.ModParticles;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.Optional;
import java.util.function.BiPredicate;

public abstract class PuffBaseEntity extends TameableEntity {
    protected static final TrackedData<Boolean> BLINK;
    public float blinkingAge;
    public boolean isEntityBlinking;
    private int noBlinkingAge;
    private long lastSlept;

    public PuffBaseEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(BLINK, false);
    }

    public boolean isInBlinkAnimation() {
        return dataTracker.get(BLINK);
    }

    public void setInBlinkAnimation(boolean blinking) {
        dataTracker.set(BLINK, blinking);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setInBlinkAnimation(nbt.getBoolean("blink"));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("blink", this.isInBlinkAnimation());
    }

    public void tickMovement() {
        super.tickMovement();
        this.tickHandSwing();
    }

    @Override
    public void tick() {
        super.tick();

        if(this.isInBlinkAnimation()) {
            this.setInBlinkAnimation(false);
        }

        if(this.noBlinkingAge == 0) {
            int i = this.random.nextInt(11999);
            if (i < 35) {
                this.setInBlinkAnimation(false);
                this.noBlinkingAge = 140;
            } else if (i < 235) {
                this.setInBlinkAnimation(true);
                this.noBlinkingAge = 15;
            }
        }

        if(this.isSleeping()) {
            if (this.world.isClient) {
                if(this.random.nextFloat() < 0.025F) {
                    this.world.addParticle(ModParticles.SLEEP, this.getX(), this.getY(), this.getZ(), (this.random.nextFloat() / 450.0F * (float) (this.random.nextBoolean() ? 1 : -1)), 0, (this.random.nextFloat() / 450.0F * (float) (this.random.nextBoolean() ? 1 : -1)));
                }
            }
        }

        this.noBlinkingAge = Math.max(noBlinkingAge - 1, 0);
    }

    public void releaseTicketFor(MemoryModuleType<GlobalPos> memoryModuleType) {
        if (this.world instanceof ServerWorld) {
            MinecraftServer minecraftServer = ((ServerWorld) this.world).getServer();
            this.brain.getOptionalMemory(memoryModuleType).ifPresent((pos) -> {
                ServerWorld serverWorld = minecraftServer.getWorld(pos.getDimension());
                if (serverWorld != null) {
                    PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
                    Optional<PointOfInterestType> optional = pointOfInterestStorage.getType(pos.getPos());
                    BiPredicate<PuffBaseEntity, PointOfInterestType> biPredicate = (puff, poiType) -> poiType == PointOfInterestType.HOME;
                    if (optional.isPresent() && biPredicate.test(this, optional.get())) {
                        pointOfInterestStorage.releaseTicket(pos.getPos());
                        DebugInfoSender.sendPointOfInterest(serverWorld, pos.getPos());
                    }

                }
            });
        }
    }

    public void sleep(BlockPos pos) {
        super.sleep(pos);
        this.brain.remember(MemoryModuleType.LAST_SLEPT, this.world.getTime());
        this.brain.forget(MemoryModuleType.WALK_TARGET);
        this.brain.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    public void wakeUp() {
        super.wakeUp();
        this.brain.remember(MemoryModuleType.LAST_WOKEN, this.world.getTime());
    }

    static {
        BLINK = DataTracker.registerData(PuffBaseEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
}