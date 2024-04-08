package gg.norisk.subwaysurfers.mixin.entity;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Invoker("getHandSwingDuration")
    int invokeGetHandSwingDuration();

    @Invoker("applyDamage")
    void invokeApplyDamage(DamageSource source, float amount);

    @Accessor("lastDamageTaken")
    float getLastDamageTaken();

    @Accessor("lastDamageTaken")
    void setLastDamageTaken(float value);

    @Accessor("lastDamageSource")
    DamageSource getLastDamageSource();

    @Accessor("lastDamageSource")
    void setLastDamageSource(DamageSource value);

    @Accessor("lastDamageTime")
    long getLastDamageTime();

    @Accessor("playerHitTimer")
    void setPlayerHitTimer(int value);

    @Accessor("playerHitTimer")
    int getPlayerHitTimer();

    @Accessor("attackingPlayer")
    void setAttackingPlayer(@Nullable PlayerEntity value);

    @Accessor("attackingPlayer")
    @Nullable PlayerEntity getAttackingPlayer();

    @Accessor("lastDamageTime")
    void setLastDamageTime(long value);

    @Invoker("playHurtSound")
    void invokePlayHurtSound(DamageSource source);

    @Invoker("takeShieldHit")
    void invokeTakeShieldHit(LivingEntity source);

    @Invoker("tryUseTotem")
    boolean invokeTryUseTotem(DamageSource source);

    @Invoker("getDeathSound")
    SoundEvent invokeGetDeathSound();

    @Invoker("getFallSound")
    SoundEvent invokeGetFallSound(int distance);

    @Invoker("getDrinkSound")
    SoundEvent invokeGetDrinkSound(ItemStack itemStack);

    @Invoker("getEatSound")
    SoundEvent invokeGetEatSound(ItemStack itemStack);

    @Invoker("getHurtSound")
    SoundEvent invokeGetHurtSound(DamageSource source);

    @Invoker("getSoundVolume")
    float invokeGetSoundVolume();

    @Accessor("leaningPitch")
    float getLeaningPitch();

    @Accessor("leaningPitch")
    void setLeaningPitch(float value);

    @Accessor("lastLeaningPitch")
    float getLastLeaningPitch();

    @Accessor("lastLeaningPitch")
    void setLastLeaningPitch(float value);

    @Invoker("getActiveEyeHeight")
    float invokeGetActiveEyeHeight(EntityPose pose, EntityDimensions dimensions);
}
