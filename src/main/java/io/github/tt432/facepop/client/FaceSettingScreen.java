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
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float ticks) {
        super.render(guiGraphics, mouseX, mouseY, ticks);

        int minX = 30;
        int minY = 30;

        int maxX = width - 30;
        int maxY = height - 30;

        guiGraphics.fillGradient(minX, minY, maxX, maxY, -1073741824, -1073741824);

        var centerX = 100;
        var centerY = height / 2;
        int size = 60;

        var select = FaceSelectorScreen.selectFace(mouseX, mouseY, centerX, centerY, size);
        FaceSelectorScreen.renderWheel(guiGraphics, centerX, centerY, size, select);

        var lineStart = 170;
        int lineWight = 3;

        int black = 0xFF_00_00_00;

        guiGraphics.fillGradient(lineStart, minY, lineStart + lineWight, maxY, black, black);

        // left
        guiGraphics.fillGradient(minX, minY, minX + lineWight, maxY, black, black);
        // up
        guiGraphics.fillGradient(minX, minY, maxX, minY + lineWight, black, black);
        // right
        guiGraphics.fillGradient(maxX - lineWight, minY, maxX, maxY, black, black);
        // bottom
        guiGraphics.fillGradient(minX, maxY - lineWight, maxX, maxY, black, black);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
