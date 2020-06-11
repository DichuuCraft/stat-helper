package com.hadroncfy.stathelper.mixin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

@Mixin(ServerWorld.class)
public class MixinServerWorld {
    private static final Logger LOGGER = LogManager.getLogger();


    @Inject(method = "onBlockChanged", at = @At("HEAD"))
    private void onBlockChange(BlockPos pos, BlockState oldBlock, BlockState newBlock, CallbackInfo ci){
        if ((Object)this instanceof ServerWorld && oldBlock.getBlock() == Blocks.BEDROCK && newBlock.getBlock() != Blocks.BEDROCK){
            final ServerWorld cela = (ServerWorld)(Object) this;
            ServerPlayerEntity selected = null;
            double r = 0;

            for (ServerPlayerEntity player: cela.getPlayers()){
				double r2 = pos.getSquaredDistance(player.x, player.y, player.z, true);
                if (r2 <= 9 && (selected == null || r2 < r) && player.interactionManager.getGameMode() == GameMode.SURVIVAL){
                    selected = player;
                    r = r2;
                }
            }

            if (selected != null){
                LOGGER.debug("Player {} broke a bedrock at {}", selected.getGameProfile().getName(), pos);
                selected.increaseStat(Stats.MINED.getOrCreateStat(Blocks.BEDROCK), 1);
            }
        }
    }
}