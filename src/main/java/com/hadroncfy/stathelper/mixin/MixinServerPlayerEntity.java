package com.hadroncfy.stathelper.mixin;

import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity {

    public MixinServerPlayerEntity(World world, GameProfile profile) {
        super(world, profile);
    }

    private void checkTag(){
        if (!getClass().equals(ServerPlayerEntity.class)){
            getScoreboardTags().add("FAKE");
        }
        else {
            getScoreboardTags().remove("FAKE");
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(MinecraftServer minecraftServer, ServerWorld serverWorld, GameProfile gameProfile, ServerPlayerInteractionManager serverPlayerInteractionManager, CallbackInfo ci){
        checkTag();
    }
    
    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        checkTag();
    }
}