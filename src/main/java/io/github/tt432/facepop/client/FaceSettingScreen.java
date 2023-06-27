package io.github.tt432.facepop.client;

import io.github.tt432.facepop.data.FaceBag;
import io.github.tt432.facepop.data.FaceBagManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author TT432
 */
public class FaceSettingScreen extends Screen {
    int clicked;
    int select;

    int hoverFaceBag;
    int selectFaceBag = -1;

    boolean openedSubPlane;

    List<FaceBag> faceBagList;

    int subPlaneX;
    int subPlaneY;
    int subPlaneW;
    int subPlaneH;

    int page;
    int subPlanePage;

    protected FaceSettingScreen() {
        super(Component.empty());

        Minecraft mc = Minecraft.getInstance();

        faceBagList = mc.level.registryAccess().registry(FaceBagManager.FACE_BAG_KEY)
                .map(fb -> fb.stream().collect(Collectors.toList()))
                .orElse(new ArrayList<>());
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

        select = FaceSelectorScreen.selectFace(mouseX, mouseY, centerX, centerY, size);
        FaceSelectorScreen.renderWheel(guiGraphics, centerX, centerY, size, select, clicked);

        var lineStart = 170;
        int lineWight = 3;

        int black = 0xFF_00_00_00;

        guiGraphics.fillGradient(lineStart, minY, lineStart + lineWight, maxY, black, black);

        guiGraphics.fillGradient(minX, minY, minX + lineWight, maxY, black, black);
        guiGraphics.fillGradient(minX, minY, maxX, minY + lineWight, black, black);
        guiGraphics.fillGradient(maxX - lineWight, minY, maxX, maxY, black, black);
        guiGraphics.fillGradient(minX, maxY - lineWight, maxX, maxY, black, black);

        if (openedSubPlane) {
            renderSubPlane(mouseX, mouseY);
        } else {
            renderFaceBagButtons(guiGraphics, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (select != 0)
            clicked = select;

        if (!clickSubPlane(pMouseX, pMouseY) && openedSubPlane) {
            openedSubPlane = false;
            selectFaceBag = -1;
            subPlanePage = 0;
        }

        if (hoverFaceBag != -1) {
            openedSubPlane = true;
            selectFaceBag = hoverFaceBag;
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    private void renderFaceBagButtons(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int left = 180;
        int top = 40;

        int faceBagListStart = page * 12;
        int faceBagListEnd = Math.min((page + 1) * 12, faceBagList.size());

        boolean hoverFaceBagSeted = false;

        for (int i = faceBagListStart; i < faceBagListEnd; i++) {
            int size = 40;
            int line = i / 4;
            int inLineIndex = i % 4;

            int x = left + 10 + (3 + size) * inLineIndex;
            int y = top + 10 + (3 + size) * line;

            int backColor = selectFaceBag == i ? 0xFF_AF_AF_AF : 0xFF_00_00_00;
            guiGraphics.fillGradient(x, y, x + size, y + size, backColor, backColor);

            FaceBag faceBag = faceBagList.get(i);
            TextureAtlasSprite sprite = FacesTextureLoader.getInstance().get(faceBag.iconLocation());
            SpriteContents contents = sprite.contents();
            int spriteW = contents.width();
            int spriteH = contents.height();
            float max = Math.max(spriteW, spriteH);
            float actualWidth = spriteW / max * size;
            float actualHeight = spriteH / max * size;

            FaceSelectorScreen.renderTexture(sprite.atlasLocation(), guiGraphics.pose(),
                    x + (size / 2) - actualWidth / 2 + 2, x + (size / 2) + actualWidth / 2 - 2,
                    y + (size / 2) - actualHeight / 2 + 2, y + (size / 2) + actualHeight / 2 - 2,
                    sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(),
                    0xFF_FF_FF_FF);

            hoverFaceBagSeted = testFaceBagButtonHover(guiGraphics, mouseX, mouseY, i, size, x, y);
        }

        if (!hoverFaceBagSeted)
            hoverFaceBag = -1;
    }

    private boolean testFaceBagButtonHover(GuiGraphics guiGraphics,
                                           int mouseX, int mouseY,
                                           int index, int size, int x, int y) {
        if (mouseX > x && mouseX < x + size && mouseY > y && mouseY < y + size) {
            hoverFaceBag = index;

            int color = 0xBF_FF_FF_FF;
            guiGraphics.fillGradient(x, y, x + size, y + size, color, color);
            return true;
        }

        return false;
    }

    private void renderSubPlane(int mouseX, int mouseY) {
        // TODO
    }

    private boolean clickSubPlane(double mouseX, double mouseY) {
        return mouseX > subPlaneX && mouseY > subPlaneY && mouseX < subPlaneX + subPlaneW && mouseY < subPlaneY + subPlaneH;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
