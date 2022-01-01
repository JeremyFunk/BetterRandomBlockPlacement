package com.njf.better_random_block_placement.mixins

import com.njf.better_random_block_placement.BetterRandomBlockPlacement
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.network.ClientPlayerInteractionManager
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable


@Mixin(value = [ClientPlayerInteractionManager::class])
class PlacementMixin {
    @Inject(method=["interactBlock"], at=[At(
        value = "INVOKE",
        target = "net/minecraft/client/network/ClientPlayNetworkHandler.sendPacket(Lnet/minecraft/network/Packet;)V"
    )])
    open fun onPlayerInteractBlockSuccessfully(
        player: ClientPlayerEntity?,
        world: ClientWorld?,
        hand: Hand?,
        hitResult: BlockHitResult?,
        cir: CallbackInfoReturnable<*>?
    ) {
        BetterRandomBlockPlacement.click()
    }
}