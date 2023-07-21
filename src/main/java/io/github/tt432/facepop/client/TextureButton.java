package io.github.tt432.facepop.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * @author TT432
 */
public class TextureButton extends Button {
    protected TextureButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, CreateNarration pCreateNarration) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress, pCreateNarration);
    }

    public TextureButton(Button.Builder builder) {
        super(builder);
    }

    ResourceLocation tex;
    ResourceLocation hTex;

    public void setLocation(ResourceLocation tex, ResourceLocation hTex) {
        this.tex = tex;
        this.hTex = hTex;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int p_282682_, int p_281714_, float p_282542_) {
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        if (isHovered()) {
            FaceSelectorScreen.renderTexture(hTex, new PoseStack(), getX(), getX() + width, getY(), getY() + height,
                    0, 1, 0, 1, 0xFF_FF_FF_FF);
        } else {
            FaceSelectorScreen.renderTexture(tex, new PoseStack(), getX(), getX() + width, getY(), getY() + height,
                    0, 1, 0, 1, 0xFF_FF_FF_FF);
        }
    }
}
