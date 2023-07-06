package io.github.tt432.facepop.client;

import io.github.tt432.facepop.common.capability.FaceCapability;
import io.github.tt432.facepop.data.Face;
import io.github.tt432.facepop.data.FaceBag;
import io.github.tt432.facepop.data.FaceBagManager;
import io.github.tt432.facepop.net.NetworkHandler;
import io.github.tt432.facepop.net.SetFacePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
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
    int hoverWheel;
    int hoverFaceBag;
    int hoverFace;

    int selectWheel;
    int selectFaceBag = -1;

    boolean openedSubPlane;

    List<FaceBag> faceBagList;
    List<Face> faceList = new ArrayList<>();

    int page;
    int subPlanePage;

    boolean init;

    Button returnButton;
    Button leftPageButton;
    Button rightPageButton;

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

        if (!init) {
            initSettingScreen();
        }

        int minX = 30;
        int minY = 30;

        int maxX = width - 30;
        int maxY = height - 30;

        guiGraphics.fillGradient(minX, minY, maxX, maxY, -1073741824, -1073741824);

        var centerX = 100;
        var centerY = height / 2;
        int size = 80;

        hoverWheel = FaceSelectorScreen.selectFace(mouseX, mouseY, centerX, centerY, size);
        FaceSelectorScreen.renderWheel(guiGraphics, centerX, centerY, size, hoverWheel, selectWheel);

        var lineStart = 170;
        int lineWight = 3;

        int black = 0xFF_00_00_00;

        guiGraphics.fillGradient(lineStart, minY, lineStart + lineWight, maxY, black, black);

        guiGraphics.fillGradient(minX, minY, minX + lineWight, maxY, black, black);
        guiGraphics.fillGradient(minX, minY, maxX, minY + lineWight, black, black);
        guiGraphics.fillGradient(maxX - lineWight, minY, maxX, maxY, black, black);
        guiGraphics.fillGradient(minX, maxY - lineWight, maxX, maxY, black, black);

        returnButton.visible = returnButton.active = openedSubPlane;

        if (openedSubPlane) {
            renderSubPlane(guiGraphics, mouseX, mouseY);
        } else {
            renderFaceBagButtons(guiGraphics, mouseX, mouseY);
        }
    }

    private void initSettingScreen() {
        init = true;

        int left = 180;
        int top = height - 30 - 30;

        int xSize = 50;
        int ySize = 20;

        // todo translation component
        returnButton = Button.builder(Component.literal("return"), button -> {
            openedSubPlane = false;
            selectFaceBag = -1;
            subPlanePage = 0;
        }).bounds(left, top, xSize, ySize).build();

        leftPageButton = Button.builder(Component.literal("<"), button -> {
            if (openedSubPlane) {
                if (subPlanePage > 0)
                    subPlanePage--;
            } else {
                if (page > 0)
                    page--;
            }
        }).bounds(left + xSize + 10, top, ySize, ySize).build();

        rightPageButton = Button.builder(Component.literal(">"), button -> {
            if (openedSubPlane) {
                if (subPlanePage > faceList.size() / 12)
                    subPlanePage++;
            } else {
                if (page > faceBagList.size() / 12)
                    page++;
            }
        }).bounds(left + xSize + ySize + 20, top, ySize, ySize).build();

        addRenderableWidget(returnButton);
        addRenderableWidget(leftPageButton);
        addRenderableWidget(rightPageButton);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (hoverWheel != 0)
            selectWheel = hoverWheel;

        if (hoverFaceBag != -1 && !openedSubPlane) {
            FaceBag faceBag = faceBagList.get(hoverFaceBag);

            if (faceBag.defaultUnlock() || Minecraft.getInstance().player.getCapability(FaceCapability.CAPABILITY)
                    .map(cap -> cap.canUse(faceBag.id().toString())).orElse(false)) {
                openedSubPlane = true;
                selectFaceBag = hoverFaceBag;
                hoverFaceBag = -1;
                faceList = faceBag.faces();
            }
        }

        if (openedSubPlane && hoverFace != -1 && selectWheel != 0) {
            NetworkHandler.INSTANCE.sendToServer(new SetFacePacket(selectWheel,
                    FaceCapability.packFace(faceBagList.get(selectFaceBag).id(), faceList.get(hoverFace).id())));
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    private void renderFaceBagButtons(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int left = 180;
        int top = 40;

        // TODO lang key
        guiGraphics.drawString(Minecraft.getInstance().font, "Face Bag",180 ,top, 0xFF_FF_FF_FF);

        int faceBagListStart = page * 12;
        int faceBagListEnd = Math.min((page + 1) * 12, faceBagList.size());

        boolean hoverFaceBagSeted = false;

        for (int i = faceBagListStart; i < faceBagListEnd; i++) {
            int size = 40;
            int line = i / 4;
            int inLineIndex = i % 4;

            int x = left + 10 + (3 + size) * inLineIndex;
            int y = top + 10 + (3 + size) * line;

            guiGraphics.fillGradient(x, y, x + size, y + size, 0xFF_00_00_00, 0xFF_00_00_00);

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

            if (!hoverFaceBagSeted)
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

    private void renderSubPlane(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int left = 180;
        int top = 40;

        // TODO lang key
        guiGraphics.drawString(Minecraft.getInstance().font, "Face",180 ,top, 0xFF_FF_FF_FF);

        int faceBagListStart = page * 12;
        int faceBagListEnd = Math.min((page + 1) * 12, faceList.size());

        boolean hoverFaceSeted = false;

        for (int i = faceBagListStart; i < faceBagListEnd; i++) {
            int size = 40;
            int line = i / 4;
            int inLineIndex = i % 4;

            int x = left + 10 + (3 + size) * inLineIndex;
            int y = top + 10 + (3 + size) * line;

            guiGraphics.fillGradient(x, y, x + size, y + size, 0xFF_00_00_00, 0xFF_00_00_00);

            var faceBag = faceList.get(i);
            TextureAtlasSprite sprite = FacesTextureLoader.getInstance().get(faceBag.imagePath());
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

            if (!hoverFaceSeted)
                hoverFaceSeted = testFaceButtonHover(guiGraphics, mouseX, mouseY, i, size, x, y);
        }

        if (!hoverFaceSeted)
            hoverFace = -1;
    }

    private boolean testFaceButtonHover(GuiGraphics guiGraphics,
                                        int mouseX, int mouseY,
                                        int index, int size, int x, int y) {
        if (mouseX > x && mouseX < x + size && mouseY > y && mouseY < y + size) {
            hoverFace = index;

            int color = 0xBF_FF_FF_FF;
            guiGraphics.fillGradient(x, y, x + size, y + size, color, color);
            return true;
        }

        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
