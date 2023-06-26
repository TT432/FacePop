package io.github.tt432.facepop.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * @author TT432
 */
public class FaceSettingScreen extends Screen {
    protected FaceSettingScreen() {
        super(Component.empty());
    }

    @Override
    public void render(GuiGraphics p_281549_, int p_281550_, int p_282878_, float p_282465_) {
        super.render(p_281549_, p_281550_, p_282878_, p_282465_);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
