package com.waltsai.colorfulpuff.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(EnvType.CLIENT)
public class SleepParticle extends SpriteBillboardParticle {
    protected SleepParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f);
        this.scale(0.5F);
        this.setBoundingBoxSpacing(0.05F, 0.05F);
        this.maxAge = 50;

        this.gravityStrength = 0.025F + (this.random.nextFloat() - 0.5F) * 0.02F;
        this.velocityX = g;
        this.velocityY = h + (double)(this.random.nextFloat() / 500.0F);
        this.velocityZ = i;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ < this.maxAge && !(this.alpha <= 0.0F)) {
            this.velocityX += (double)(this.random.nextFloat() / 2500.0F * (float)(this.random.nextBoolean() ? 1 : -1));
            this.velocityZ += (double)(this.random.nextFloat() / 2500.0F * (float)(this.random.nextBoolean() ? 1 : -1));
            this.velocityY = (double)this.gravityStrength;
            this.move(this.velocityX, this.velocityY, this.velocityZ);
            if (this.age >= this.maxAge - 60 && this.alpha > 0.01F) {
                this.alpha -= 0.005F;
            }

        } else {
            this.markDead();
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            SleepParticle sleepParticle = new SleepParticle(clientWorld, d, e, f, g, h, i);
            sleepParticle.setSprite(this.spriteProvider);
            return sleepParticle;
        }
    }
}
