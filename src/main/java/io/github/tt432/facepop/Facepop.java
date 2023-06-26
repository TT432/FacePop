package io.github.tt432.facepop;

import io.github.tt432.facepop.net.NetworkHandler;
import net.minecraftforge.fml.common.Mod;

@Mod(Facepop.MOD_ID)
public class Facepop {
    public static final String MOD_ID = "facepop";

    public Facepop() {
        NetworkHandler.registerPackets();
    }
}
