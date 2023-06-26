package io.github.tt432.facepop.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.tt432.facepop.Facepop;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
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

        renderWheel(guiGraphics, centerX, centerY, 100, select);
    }

    public static void renderWheel(@NotNull GuiGraphics guiGraphics, int centerX, int centerY, int size, int select) {
        int half = size / 2;

        int white = 0xFF_FF_FF_FF;
        int red = 0xFF_FF_00_00;

        innerBlit(ELEMENT_LOCATION, guiGraphics.pose(),
                centerX, centerX + size, centerY - half, centerY + half,
                0.5F, 1F, 0F, 0.5F,
                select == 2 ? red : white);
        innerBlit(ELEMENT_LOCATION, guiGraphics.pose(),
                centerX - half, centerX + half, centerY, centerY + size,
                0F, 0.5F, 0.5F, 1F,
                select == 3 ? red : white);
        innerBlit(ELEMENT_LOCATION, guiGraphics.pose(),
                centerX - size, centerX, centerY - half, centerY + half,
                0.5F, 1F, 0.5F, 1F,
                select == 4 ? red : white);
        innerBlit(ELEMENT_LOCATION, guiGraphics.pose(),
                centerX - half, centerX + half, centerY - size, centerY,
                0F, 0.5F, 0F, 0.5F,
                select == 5 ? red : white);
    }

    private static void innerBlit(
            ResourceLocation texture, PoseStack poseStack,
            int x1, int x2, int y1, int y2,
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
