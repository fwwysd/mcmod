package com.fwwysd.maidmining;

import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidExtension;
import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.manager.TaskManager;
import com.yourname.maidmining.task.MiningTask;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MaidMining.MOD_ID)
@LittleMaidExtension
public class MaidMining implements ILittleMaid {
    public static final String MOD_ID = "maidmining";

    public MaidMining() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void addMaidTask(TaskManager manager) {
        manager.register(new ResourceLocation(MOD_ID, "mining"), MiningTask::new);
    }
                         }
