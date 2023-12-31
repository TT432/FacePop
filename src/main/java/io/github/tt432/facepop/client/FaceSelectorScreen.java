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
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

/**
 * @author TT432
 */
public class FaceSelectorScreen extends Screen {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(Facepop.MOD_ID, "textures/gui/6.png");
    public static final ResourceLocation LEFT = new ResourceLocation(Facepop.MOD_ID, "textures/gui/1.png");
    public static final ResourceLocation RIGHT = new ResourceLocation(Facepop.MOD_ID, "textures/gui/2.png");
    public static final ResourceLocation UP = new ResourceLocation(Facepop.MOD_ID, "textures/gui/4.png");
    public static final ResourceLocation BOTTOM = new ResourceLocation(Facepop.MOD_ID, "textures/gui/3.png");
    public static final ResourceLocation CENTER = new ResourceLocation(Facepop.MOD_ID, "textures/gui/5.png");

    /**
     * 1 .. 5 ，1 是 中心，右是 2，下是 3，以此类推
     */
    private int select;

    protected FaceSelectorScreen() {
        super(Component.empty());
    }

    /* 选择器中心框 */
    ImageElement centerElement = new ImageElement(46, 46, CENTER);
    /* 选择器靠右框 */
    ImageElement rightElement = new ImageElement(46, 78, RIGHT);
    /* 选择器靠下框 */
    ImageElement bottomElement = new ImageElement(78, 47, BOTTOM);
    /* 选择器靠左框 */
    ImageElement leftElement = new ImageElement(46, 78, LEFT);
    /* 选择器靠上框 */
    ImageElement upElement = new ImageElement(78, 46, UP);

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float ticks) {
        // 计算屏幕中心位置
        var centerX = width / 2;
        var centerY = height / 2;

        int size = 150;
        this.select = selectFace(mouseX, mouseY, centerX, centerY, size);

        int half = size / 2;

        float offset = (126F / 129F * size) - size;

        int color = 0xFF_FF_FF_FF;

        int left = centerX - half;
        int right = centerX + half;
        int up = centerY - half;
        float bottom = centerY + offset + half;

        // 绘制背景
        renderTexture(BACKGROUND, guiGraphics.pose(),
                left, right, up, bottom,
                0, 1, 0, 1,
                color);

        // 绘制选择轮
        renderWheel(guiGraphics, centerX, centerY, size, select, 126,
                centerElement, rightElement, bottomElement, leftElement, upElement);
    }

    /* 渲染表情选择轮（没有任何选中的情况） */
    public static void renderWheel(@NotNull GuiGraphics guiGraphics, int centerX, int centerY, int size, int select,
                                   int bgWidth,
                                   ImageElement centerElement, ImageElement rightElement, ImageElement bottomElement,
                                   ImageElement leftElement, ImageElement upElement) {
        renderWheel(guiGraphics, centerX, centerY, size, 0, select, bgWidth,
                centerElement, rightElement, bottomElement, leftElement, upElement,
                null, null, null, null, null);
    }

    /* 渲染表情选择轮（有选中的情况） */
    public static void renderWheel(@NotNull GuiGraphics guiGraphics, float centerX, float centerY, int size,
                                   int click, int select,
                                   int bgWidth,
                                   ImageElement centerElement, ImageElement rightElement, ImageElement bottomElement,
                                   ImageElement leftElement, ImageElement upElement,
                                   @Nullable ImageElement clickCenterElement, @Nullable ImageElement clickRightElement,
                                   @Nullable ImageElement clickBottomElement, @Nullable ImageElement clickLeftElement,
                                   @Nullable ImageElement clickUpElement) {
        int half = size / 2;

        int color = 0xFF_FF_FF_FF;

        var left = centerX - half;
        var right = centerX + half;
        var up = centerY - half;
        var bottom = centerY + half;

        if (select == 1) {
            float maxS = Math.max(centerElement.w(), centerElement.h());
            float s = maxS / bgWidth;

            var asx = s * centerElement.w() / maxS * size;
            var asy = s * centerElement.h() / maxS * size;

            renderTexture(centerElement.resourceLocation(), guiGraphics.pose(),
                    centerX - asx / 2, centerX + asx / 2, centerY - asy / 2, centerY + asy / 2,
                    0, 1, 0, 1,
                    color);
        } else if (click == 1 && clickCenterElement != null) {
            float maxS = Math.max(clickCenterElement.w(), clickCenterElement.h());
            float s = maxS / bgWidth;

            var asx = s * clickCenterElement.w() / maxS * size;
            var asy = s * clickCenterElement.h() / maxS * size;

            renderTexture(clickCenterElement.resourceLocation(), guiGraphics.pose(),
                    centerX - asx / 2, centerX + asx / 2, centerY - asy / 2, centerY + asy / 2,
                    0, 1, 0, 1,
                    color);
        }

        if (select == 2) {
            float maxS = Math.max(rightElement.w(), rightElement.h());
            float s = maxS / bgWidth;

            var asx = s * rightElement.w() / maxS * size;
            var asy = s * rightElement.h() / maxS * size;

            renderTexture(rightElement.resourceLocation(), guiGraphics.pose(),
                    right - asx, right, centerY - asy / 2, centerY + asy / 2,
                    0, 1, 0, 1,
                    color);
        } else if (click == 2 && clickRightElement != null) {
            float maxS = Math.max(clickRightElement.w(), clickRightElement.h());
            float s = maxS / bgWidth;

            var asx = s * clickRightElement.w() / maxS * size;
            var asy = s * clickRightElement.h() / maxS * size;

            renderTexture(clickRightElement.resourceLocation(), guiGraphics.pose(),
                    right - asx, right, centerY - asy / 2, centerY + asy / 2,
                    0, 1, 0, 1,
                    color);
        }

        if (select == 3) {
            float maxS = Math.max(bottomElement.w(), bottomElement.h());
            float s = maxS / bgWidth;

            var asx = s * bottomElement.w() / maxS * size;
            var asy = s * bottomElement.h() / maxS * size;

            renderTexture(bottomElement.resourceLocation(), guiGraphics.pose(),
                    centerX - asx / 2, centerX + asx / 2, bottom - asy, bottom,
                    0, 1, 0, 1,
                    color);
        } else if (click == 3 && clickBottomElement != null) {
            float maxS = Math.max(clickBottomElement.w(), clickBottomElement.h());
            float s = maxS / bgWidth;

            var asx = s * clickBottomElement.w() / maxS * size;
            var asy = s * clickBottomElement.h() / maxS * size;

            renderTexture(clickBottomElement.resourceLocation(), guiGraphics.pose(),
                    centerX - asx / 2, centerX + asx / 2, bottom - asy, bottom,
                    0, 1, 0, 1,
                    color);
        }

        if (select == 4) {
            float maxS = Math.max(leftElement.w(), leftElement.h());
            float s = maxS / bgWidth;

            var asx = s * leftElement.w() / maxS * size;
            var asy = s * leftElement.h() / maxS * size;

            renderTexture(leftElement.resourceLocation(), guiGraphics.pose(),
                    left, left + asx, centerY - asy / 2, centerY + asy / 2,
                    0, 1, 0, 1,
                    color);
        } else if (click == 4 && clickLeftElement != null) {
            float maxS = Math.max(clickLeftElement.w(), clickLeftElement.h());
            float s = maxS / bgWidth;

            var asx = s * clickLeftElement.w() / maxS * size;
            var asy = s * clickLeftElement.h() / maxS * size;

            renderTexture(clickLeftElement.resourceLocation(), guiGraphics.pose(),
                    left, left + asx, centerY - asy / 2, centerY + asy / 2,
                    0, 1, 0, 1,
                    color);
        }

        if (select == 5) {
            float maxS = Math.max(upElement.w(), upElement.h());
            float s = maxS / bgWidth;

            var asx = s * upElement.w() / maxS * size;
            var asy = s * upElement.h() / maxS * size;

            renderTexture(upElement.resourceLocation(), guiGraphics.pose(),
                    centerX - asx / 2, centerX + asx / 2, up, up + asy,
                    0, 1, 0, 1,
                    color);
        } else if (click == 5 && clickUpElement != null) {
            float maxS = Math.max(clickUpElement.w(), clickUpElement.h());
            float s = maxS / bgWidth;

            var asx = s * clickUpElement.w() / maxS * size;
            var asy = s * clickUpElement.h() / maxS * size;

            renderTexture(clickUpElement.resourceLocation(), guiGraphics.pose(),
                    centerX - asx / 2, centerX + asx / 2, up, up + asy,
                    0, 1, 0, 1,
                    color);
        }

        Minecraft mc = Minecraft.getInstance();
        float centerSize = (46F / 2) / 126F * size;
        mc.player.getCapability(FaceCapability.CAPABILITY).ifPresent(cap -> {
            RegistryAccess registryAccess = mc.level.registryAccess();

            renderFaceIcon(guiGraphics, centerX, centerY, 1, size, cap, registryAccess);
            renderFaceIcon(guiGraphics, centerX + (centerSize + (size / 2.5F)) / 2, centerY, 2, size, cap, registryAccess);
            renderFaceIcon(guiGraphics, centerX, centerY + (centerSize + (size / 2.5F)) / 2, 3, size, cap, registryAccess);
            renderFaceIcon(guiGraphics, centerX - (centerSize + (size / 2.5F)) / 2, centerY, 4, size, cap, registryAccess);
            renderFaceIcon(guiGraphics, centerX, centerY - (centerSize + (size / 2.5F)) / 2, 5, size, cap, registryAccess);
        });
    }

    private static void renderFaceIcon(@NotNull GuiGraphics guiGraphics,
                                       float centerX, float centerY, int index,
                                       int size, FaceCapability cap, RegistryAccess registryAccess) {
        int faceIconSize = size / 5;
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

    public static int selectFace(int mouseX, int mouseY, float centerX, float centerY, float size) {
        var distance = Math.sqrt(Math.pow(mouseX - centerX, 2) + Math.pow(mouseY - centerY, 2));
        var angle = Math.atan2(mouseY - centerY, mouseX - centerX);

        if (angle < 0)
            angle = 2 * Math.PI + angle;

        float centerSize = (46F / 2) / 126F * size;

        if (distance < centerSize) {
            return 1;
        } else if (distance < size / 2F) {
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
