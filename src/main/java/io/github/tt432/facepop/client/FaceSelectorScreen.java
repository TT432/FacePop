package io.github.tt432.facepop.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.tt432.facepop.Facepop;
import io.github.tt432.facepop.common.capability.FaceCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

/**
 * @author TT432
 */
public class FaceSelectorScreen extends Screen {
    public static final ResourceLocation ELEMENT_LOCATION = new ResourceLocation(Facepop.MOD_ID, "textures/gui/ui.png");

    /**
     * 1 .. 5 ，1 是 中心，右是 2，下是 3，以此类推
     */
    private int select;

    protected FaceSelectorScreen() {
        super(Component.empty());
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float ticks) {
        var centerX = width / 2;
        var centerY = height / 2;

        select = selectFace(mouseX, mouseY, centerX, centerY, 100);

        renderWheel(guiGraphics, centerX, centerY, 100, select, 0);
    }

    public static void renderWheel(@NotNull GuiGraphics guiGraphics, int centerX, int centerY, int size, int select, int clicked) {
        int half = size / 2;

        renderTexture(ELEMENT_LOCATION, guiGraphics.pose(),
                centerX, centerX + size, centerY - half, centerY + half,
                0.5F, 1F, 0F, 0.5F,
                getArgb(select, clicked, 2));
        renderTexture(ELEMENT_LOCATION, guiGraphics.pose(),
                centerX - half, centerX + half, centerY, centerY + size,
                0F, 0.5F, 0.5F, 1F,
                getArgb(select, clicked, 3));
        renderTexture(ELEMENT_LOCATION, guiGraphics.pose(),
                centerX - size, centerX, centerY - half, centerY + half,
                0.5F, 1F, 0.5F, 1F,
                getArgb(select, clicked, 4));
        renderTexture(ELEMENT_LOCATION, guiGraphics.pose(),
                centerX - half, centerX + half, centerY - size, centerY,
                0F, 0.5F, 0F, 0.5F,
                getArgb(select, clicked, 5));


        Minecraft mc = Minecraft.getInstance();
        mc.player.getCapability(FaceCapability.CAPABILITY).ifPresent(cap -> {
            RegistryAccess registryAccess = mc.level.registryAccess();

            renderFaceIcon(guiGraphics, centerX, centerY, 1, size, cap, registryAccess);
            renderFaceIcon(guiGraphics, centerX + size / 2, centerY, 2, size, cap, registryAccess);
            renderFaceIcon(guiGraphics, centerX, centerY + size / 2, 3, size, cap, registryAccess);
            renderFaceIcon(guiGraphics, centerX - size / 2, centerY, 4, size, cap, registryAccess);
            renderFaceIcon(guiGraphics, centerX, centerY - size / 2, 5, size, cap, registryAccess);
        });
    }

    private static void renderFaceIcon(@NotNull GuiGraphics guiGraphics,
                                       int centerX, int centerY, int index,
                                       int size, FaceCapability cap, RegistryAccess registryAccess) {
        int faceIconSize = size / 4;
        var face = FaceCapability.unpackFace(registryAccess, cap.getFace(index));

        TextureAtlasSprite sprite = FacesTextureLoader.getInstance().get(face.imagePath());

        SpriteContents contents = sprite.contents();
        int spriteWidth = contents.width();
        int spriteHeight = contents.height();
        float maxSpriteSize = Math.max(spriteWidth, spriteHeight);
        float actualWidth = spriteWidth / maxSpriteSize * faceIconSize;
        float actualHeight = spriteHeight / maxSpriteSize * faceIconSize;

        renderTexture(sprite.atlasLocation(), guiGraphics.pose(),
                centerX - actualWidth / 2, centerX + actualWidth / 2,
                centerY - actualHeight / 2, centerY + actualHeight / 2,
                sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(),
                0xFF_FF_FF_FF);
    }

    private static int getArgb(int select, int clicked, int curr) {
        int white = 0xFF_FF_FF_FF;
        int red = 0xFF_FF_00_00;
        int green = 0xFF_00_FF_00;

        if (select == curr)
            return red;

        return clicked == curr ? green : white;
    }

    public static void renderTexture(
            ResourceLocation texture, PoseStack poseStack,
            float x1, float x2, float y1, float y2,
            float u0, float u1, float v0, float v1,
            int argb
    ) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.enableBlend();
        Matrix4f matrix4f = poseStack.last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        bufferbuilder.vertex(matrix4f, x1, y1, 0).color(argb).uv(u0, v0).endVertex();
        bufferbuilder.vertex(matrix4f, x1, y2, 0).color(argb).uv(u0, v1).endVertex();
        bufferbuilder.vertex(matrix4f, x2, y2, 0).color(argb).uv(u1, v1).endVertex();
        bufferbuilder.vertex(matrix4f, x2, y1, 0).color(argb).uv(u1, v0).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.disableBlend();
    }

    public static int selectFace(int mouseX, int mouseY, int centerX, int centerY, int size) {
        var distance = Math.sqrt(Math.pow(mouseX - centerX, 2) + Math.pow(mouseY - centerY, 2));
        var angle = Math.atan2(mouseY - centerY, mouseX - centerX);

        if (angle < 0)
            angle = 2 * Math.PI + angle;

        if (distance < size / 2D) {
            return 1;
        } else if (distance < size) {
            if (angle > Math.PI * (7D / 4D) || angle < Math.PI / 4) {
                return 2;
            } else if (angle > Math.PI / 4 && angle < Math.PI * (3D / 4D)) {
                return 3;
            } else if (angle > Math.PI * (3D / 4D) && angle < Math.PI * (5D / 4D)) {
                return 4;
            } else if (angle > Math.PI * (5D / 4D) && angle < Math.PI * (7D / 4D)) {
                return 5;
            }
        }

        return 0;
    }

    public int getSelect() {
        return select;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
