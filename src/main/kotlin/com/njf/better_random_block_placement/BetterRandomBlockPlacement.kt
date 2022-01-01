package com.njf.better_random_block_placement
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text


@Suppress("UNUSED")
object BetterRandomBlockPlacement: ModInitializer {
    private const val MOD_ID = "mod_id"
    private var min = 1
    private var max = 10
    private var slot = 1
    private var active = false

    override fun onInitialize() {


        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource?>, dedicated: Boolean ->
            dispatcher.register(
                literal("rblock")
                    .then(literal("off").executes { ctx ->
                        MinecraftClient.getInstance().player!!.sendMessage(Text.of("Deactivated switching!"), false)
                        active = false
                        1
                    })
                    .then(literal("on").executes { ctx ->
                        MinecraftClient.getInstance().player!!.sendMessage(Text.of("Switching between slots " + min + " and " + (max - 1) + "!"), false)
                        active = true
                        1
                    })
                    .then(
                    argument("Minimum Slot", IntegerArgumentType.integer(1, 9)).then(
                        argument("Maximum Slot", IntegerArgumentType.integer(1, 9)).executes { ctx ->
                            var curMin = IntegerArgumentType.getInteger(ctx, "Minimum Slot")
                            var curMax = IntegerArgumentType.getInteger(ctx, "Maximum Slot")

                            if(curMax < curMin){
                                var temp = curMax
                                curMax = curMin
                                curMin = temp
                            }
                            if(curMax > 9){
                                curMax = 9
                            }
                            if(curMin < 1){
                                curMin = 1
                            }

                            MinecraftClient.getInstance().player!!.sendMessage(Text.of("Switching between slots " + curMin + " and " + curMax + "!"), false)

                            min = curMin
                            max = curMax + 1
                            active = true
                            1
                        }
                    )
                )
            )
        })

        println("Example mod has been initialized.")
    }

    fun click(){
        if(active){
            MinecraftClient.getInstance().player!!.inventory.selectedSlot = (min + ((max - min) * Math.random()).toInt()) - 1
        }
    }

}

