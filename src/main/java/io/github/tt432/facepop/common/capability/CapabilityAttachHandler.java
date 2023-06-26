package io.github.tt432.facepop.common.capability;

import io.github.tt432.facepop.Facepop;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author TT432
 */
@Mod.EventBusSubscriber
public class CapabilityAttachHandler {
    @SubscribeEvent
    public static void onEvent(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player)
            event.addCapability(new ResourceLocation(Facepop.MOD_ID, "capability"), new FaceCapability.Provider());
    }

    private CapabilityAttachHandler() {
        // can't instance...
    }
}
