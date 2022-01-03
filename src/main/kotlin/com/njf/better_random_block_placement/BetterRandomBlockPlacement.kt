package com.njf.better_random_block_placement

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.*
import net.minecraft.util.Formatting
import java.lang.Exception

class Layout{
    constructor(list: MutableList<ItemStack>, min: Int, max: Int, name: String){
        this.min = min
        this.max = max
        this.list = list
        this.name = name
    }

    var list: MutableList<ItemStack> = mutableListOf<ItemStack>()
    var min = 1
    var max = 1
    var name = ""

    override fun toString(): String {
        return "$name from $min to $max: ${list.toString()}"
    }
}

@Suppress("UNUSED")
object BetterRandomBlockPlacement: ModInitializer {
    private const val MOD_ID = "mod_id"
    private var min = 1
    private var max = 10
    private var slot = 1
    private var active = false

    private var layouts = mutableListOf<Layout>()

    fun message(text: String, format: Formatting? = null){
        if(format != null){
            var text = LiteralText(text).formatted(format)
            MinecraftClient.getInstance().player!!.sendMessage(text, false)
        }else{
            MinecraftClient.getInstance().player!!.sendMessage(Text.of(text), false)
        }
    }

    override fun onInitialize() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource?>, dedicated: Boolean ->
            dispatcher.register(
                literal("rblock")
                    .then(literal("layout").executes{ctx ->
                        println("layout")
                        message("Use /rblock layout help to get a list of available commands!", Formatting.RED)
                        1
                    })
                    .then(literal("layout")
                        .then(literal("add").then(argument("Layout Name", StringArgumentType.string()).executes{ctx ->
                            println("add")
                            var text = ctx.input.split(" ")
                            var layoutName = text[text.size - 1]

                            if(layoutName === ""){
                                message("Provide a layout name to add a new layout!", Formatting.RED)
                            }else{
                                var inventory = MinecraftClient.getInstance().player!!.inventory
                                val items: MutableList<ItemStack> = mutableListOf<ItemStack>()

                                for(i in (min - 1)..(max - 2))
                                {
                                    items.add(inventory.main[i])
                                }
                                layouts.add(Layout(items, min, max, layoutName))
                                print(layouts[layouts.size-1])
                            }


                            1
                        }))
                    ).then(argument("Layout Name", StringArgumentType.string()).executes{ctx ->
                        println("activate")
                        var text = ctx.input.split(" ")
                        var layoutName = text[text.size - 1]

                        if(layoutName === ""){
                            message("Provide a layout name to activate a layout!", Formatting.RED)
                        }else{
                            var layout = layouts.find{it.name.lowercase() == layoutName.lowercase()}

                            if(layout === null){
                                message("Could not find layout $layoutName!", Formatting.RED)
                            }else{
                                min = layout.min
                                max = layout.max

                                var inv = MinecraftClient.getInstance().player!!.inventory
                                println(inv)

                                outer@
                                for(i in 0 until layout.list.size){
                                    var it = layout.list[i]
                                    println(layout.list)
                                    for(j in 9 until inv.main.size){
                                        var invIt = inv.main[j]
                                        if(invIt.name == it.name){
                                            inv.main[i] = inv.main[j]
                                            //break@outer
                                        }
                                    }
                                }
                            }
                        }
                        1
                    })
                    .then(literal("menu").executes{ctx ->
                        println("Opening menu")
                        try{
                            //MinecraftClient.getInstance().setScreen()
                        }catch(e: Exception){
                            println(e)
                        }
                        1
                    })
                    .then(literal("off").executes { ctx ->
                        println("off")
                        message("Deactivated switching!")
                        active = false
                        1
                    })
                    .then(literal("on").executes { ctx ->
                        println("on")
                        message("Switching between slots " + min + " and " + (max - 1) + "!")
                        active = true
                        println("\n\n\n---   STUFF ---")
                        println(MinecraftClient.getInstance().player!!.inventory)
                        println(MinecraftClient.getInstance().player!!.inventory.selectedSlot)
                        println("\n\n\n---   ARMOR ---")
                        println(MinecraftClient.getInstance().player!!.inventory.armor)

                        println("\n\n\n---   ARMOR ---")
                        MinecraftClient.getInstance().player!!.inventory.armor.forEach{println(it)}

                        println("\n\n\n---   MAIN ---")
                        println(MinecraftClient.getInstance().player!!.inventory.main)
                        MinecraftClient.getInstance().player!!.inventory.main.forEach{println(it)}

                        1
                    })
                    .then(
                    argument("Minimum Slot", IntegerArgumentType.integer(1, 9)).then(
                        argument("Maximum Slot", IntegerArgumentType.integer(1, 9)).executes { ctx ->
                            println("maxmin")
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

                            message("Switching between slots " + curMin + " and " + curMax + "!")

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

