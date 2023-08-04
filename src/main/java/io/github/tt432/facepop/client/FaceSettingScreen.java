package io.github.tt432.facepop.client;

import io.github.tt432.facepop.Facepop;
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
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.tt432.facepop.client.FaceSelectorScreen.renderTexture;
import static io.github.tt432.facepop.client.FaceSelectorScreen.renderWheel;

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

    // 当前页面
    int currentPage;
    // 子页面
    int subPlanePage;

    boolean init;

    TextureButton returnButton;
    TextureButton leftPageButton;
    TextureButton rightPageButton;

    protected FaceSettingScreen() {
        super(Component.empty());

        Minecraft mc = Minecraft.getInstance();

        faceBagList = mc.level.registryAccess().registry(FaceBagManager.FACE_BAG_KEY)
                .map(fb -> fb.stream().collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Facepop.MOD_ID, "textures/gui/ui.png");
    public static final ResourceLocation WHEEL_BG = new ResourceLocation(Facepop.MOD_ID, "textures/gui/lp.png");
    public static final ResourceLocation WHEEL_H_D = new ResourceLocation(Facepop.MOD_ID, "textures/gui/settingwheel/lp-xf-down.png");
    public static final ResourceLocation WHEEL_H_L = new ResourceLocation(Facepop.MOD_ID, "textures/gui/settingwheel/lp-xf-lf-2.png");
    public static final ResourceLocation WHEEL_H_R = new ResourceLocation(Facepop.MOD_ID, "textures/gui/settingwheel/lp-xf-lf-1.png");
    public static final ResourceLocation WHEEL_H_U = new ResourceLocation(Facepop.MOD_ID, "textures/gui/settingwheel/lp-xf-up.png");
    public static final ResourceLocation WHEEL_H_M = new ResourceLocation(Facepop.MOD_ID, "textures/gui/settingwheel/lp-xf-mid.png");


    public static final ResourceLocation WHEEL_H_D_P = new ResourceLocation(Facepop.MOD_ID, "textures/gui/settingwheel/press/4.png");
    public static final ResourceLocation WHEEL_H_L_P = new ResourceLocation(Facepop.MOD_ID, "textures/gui/settingwheel/press/2.png");
    public static final ResourceLocation WHEEL_H_R_P = new ResourceLocation(Facepop.MOD_ID, "textures/gui/settingwheel/press/1.png");
    public static final ResourceLocation WHEEL_H_U_P = new ResourceLocation(Facepop.MOD_ID, "textures/gui/settingwheel/press/3.png");
    public static final ResourceLocation WHEEL_H_M_P = new ResourceLocation(Facepop.MOD_ID, "textures/gui/settingwheel/press/5.png");

    void renderElement(ResourceLocation texture, GuiGraphics guiGraphics,
                       float bgWs, float left, float up,
                       int x0, int x1, int y0, int y1) {
        renderTexture(texture, guiGraphics.pose(),
                left + x0 * bgWs, left + x1 * bgWs, up + y0 * bgWs, up + y1 * bgWs,
                0, 1, 0, 1,
                0xFF_FF_FF_FF);
    }

    void renderElement(ResourceLocation texture, GuiGraphics guiGraphics, UIElement UIElement) {
        renderTexture(texture, guiGraphics.pose(),
                UIElement.left, UIElement.right, UIElement.up, UIElement.bottom,
                0, 1, 0, 1,
                0xFF_FF_FF_FF);
    }

    ImageElement centerElement = new ImageElement(44, 44, WHEEL_H_M);
    ImageElement rightElement = new ImageElement(45, 74, WHEEL_H_R);
    ImageElement bottomElement = new ImageElement(74, 45, WHEEL_H_D);
    ImageElement leftElement = new ImageElement(45, 74, WHEEL_H_L);
    ImageElement upElement = new ImageElement(74, 45, WHEEL_H_U);

    ImageElement clickCenterElement = new ImageElement(44, 44, WHEEL_H_M_P);
    ImageElement clickRightElement = new ImageElement(45, 74, WHEEL_H_R_P);
    ImageElement clickBottomElement = new ImageElement(74, 45, WHEEL_H_D_P);
    ImageElement clickLeftElement = new ImageElement(45, 74, WHEEL_H_L_P);
    ImageElement clickUpElement = new ImageElement(74, 45, WHEEL_H_U_P);

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float ticks) {
        int bgW = 316;
        int bgH = 167;
        float bgSM = Math.max(bgW, bgH);

        float w8 = width * 0.8F;
        float bgAw = bgW / bgSM * w8;
        float bgAh = bgH / bgSM * w8;

        float left = width / 2F - bgAw / 2;
        float right = left + bgAw;
        float up = height / 2F - bgAh / 2;
        float bottom = up + bgAh;

        renderTexture(BACKGROUND, guiGraphics.pose(),
                left, right, up, bottom,
                0, 1, 0, 1,
                0xFF_FF_FF_FF);

        float bgWs = bgAw / bgW;

        if (!init) {
            initSettingScreen(bgWs, left, up);
        }

        UIElement wheelUIElement = new UIElement(bgWs, left, up, 16, 138, 25, 148);
        renderElement(WHEEL_BG, guiGraphics, wheelUIElement);

        this.hoverWheel = FaceSelectorScreen.selectFace(mouseX, mouseY, wheelUIElement.centerX, wheelUIElement.centerY, wheelUIElement.sizeX);

        renderWheel(guiGraphics, wheelUIElement.centerX, wheelUIElement.centerY, (int) wheelUIElement.sizeX,
                selectWheel, hoverWheel, 124,
                centerElement, rightElement, bottomElement, leftElement, upElement,
                clickCenterElement, clickRightElement, clickBottomElement, clickLeftElement, clickUpElement);

        returnButton.visible = returnButton.active = openedSubPlane;

        if (openedSubPlane) {
            renderSubPlane(guiGraphics, bgWs, left, up, mouseX, mouseY);
        } else {
            renderFaceBagButtons(guiGraphics, bgWs, left, up, mouseX, mouseY);
        }

        super.render(guiGraphics, mouseX, mouseY, ticks);
    }


    /* 控件：右翻页 */
    public static final ResourceLocation RBE = new ResourceLocation(Facepop.MOD_ID, "textures/gui/buttons/7.png");

    /* 控件：不可以右翻页 */
    public static final ResourceLocation RBE_H = new ResourceLocation(Facepop.MOD_ID, "textures/gui/buttons/5.png");

    /* 控件：左翻页 */
    public static final ResourceLocation LBE = new ResourceLocation(Facepop.MOD_ID, "textures/gui/buttons/1.png");

    /* 控件：不可以左翻页 */
    public static final ResourceLocation LBE_H = new ResourceLocation(Facepop.MOD_ID, "textures/gui/buttons/2.png");

    /* 控件：可以返回 */
    public static final ResourceLocation RETURN_BE = new ResourceLocation(Facepop.MOD_ID, "textures/gui/buttons/9.png");

    /* 控件：不可以返回 */
    public static final ResourceLocation RETURN_BE_H = new ResourceLocation(Facepop.MOD_ID, "textures/gui/buttons/10.png");

    /* 初始化设置页面 */
    private void initSettingScreen(float bgWs, float uiLeft, float uiUp) {
        init = true;

        UIElement returnBe = new UIElement(bgWs, uiLeft, uiUp, 204, 261, 141, 158);

        this.returnButton = (TextureButton) Button.builder(Component.empty(), button -> {
            this.openedSubPlane = false;
            this.selectFaceBag = -1;
            this.subPlanePage = 0;
        }).bounds((int) returnBe.left, (int) returnBe.up, (int) returnBe.sizeX, (int) returnBe.sizeY).build(b -> {
            TextureButton result = new TextureButton((b));
            result.setLocation(RETURN_BE, RETURN_BE_H);
            return result;
        });

        UIElement lbe = new UIElement(bgWs, uiLeft, uiUp, 181, 198, 141, 158);

        this.leftPageButton = (TextureButton) Button.builder(Component.empty(), button -> {
            if (this.openedSubPlane) {
                if (this.subPlanePage > 0)
                    this.subPlanePage--;
            } else {
                if (this.currentPage > 0)
                    this.currentPage--;
            }
        }).bounds((int) lbe.left, (int) lbe.up, (int) lbe.sizeX, (int) lbe.sizeY).build(b -> {
            TextureButton result = new TextureButton((b));
            result.setLocation(LBE, LBE_H);
            return result;
        });

        UIElement rbe = new UIElement(bgWs, uiLeft, uiUp, 267, 284, 141, 158);

        this.rightPageButton = (TextureButton) Button.builder(Component.empty(), button -> {
            if (openedSubPlane) {
                if (subPlanePage > faceList.size() / countPerPage)
                    this.subPlanePage++;
            } else {
                if (currentPage > (faceBagList.size() / countPerPage))
                    this.currentPage++;
            }
        }).bounds((int) rbe.left, (int) rbe.up, (int) rbe.sizeX, (int) rbe.sizeY).build(b -> {
            TextureButton result = new TextureButton((b));
            result.setLocation(RBE, RBE_H);
            return result;
        });

        this.addRenderableWidget(returnButton);
        this.addRenderableWidget(leftPageButton);
        this.addRenderableWidget(rightPageButton);
    }

    /* 处理点击逻辑 */
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (hoverWheel != 0)
            selectWheel = hoverWheel;

        if (hoverFaceBag != -1 && !openedSubPlane) {
            FaceBag faceBag = faceBagList.get(hoverFaceBag);

            if (faceBag.defaultUnlock() || Minecraft.getInstance().player.getCapability(FaceCapability.CAPABILITY)
                    .map(cap -> cap.canUse(faceBag.id().toString())).orElse(false)) {
                this.openedSubPlane = true;
                this.selectFaceBag = hoverFaceBag;
                this.hoverFaceBag = -1;
                this.faceList = faceBag.faces();
            } else {
                // 这里是未解锁表情包处理逻辑
                // 也就是说点击了没有解锁的表情包
            }
        }

        if (openedSubPlane && hoverFace != -1 && selectWheel != 0) {
            NetworkHandler.INSTANCE.sendToServer(new SetFacePacket(selectWheel,
                    FaceCapability.packFace(faceBagList.get(selectFaceBag).id(), faceList.get(hoverFace).id())));
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    /* 表情包 Title */
    public static final ResourceLocation FACEBAG_FONT = new ResourceLocation(Facepop.MOD_ID, "textures/gui/words/bqb.png");

    List<UIElement> iconElements(float bgWs, float uiLeft, float uiUp) {
        return List.of(
                new UIElement(bgWs, uiLeft, uiUp, 158, 193, 35, 70),
                new UIElement(bgWs, uiLeft, uiUp, 196, 231, 35, 70),
                new UIElement(bgWs, uiLeft, uiUp, 234, 269, 35, 70),
                new UIElement(bgWs, uiLeft, uiUp, 272, 307, 35, 70),
                new UIElement(bgWs, uiLeft, uiUp, 158, 193, 73, 108),
                new UIElement(bgWs, uiLeft, uiUp, 196, 231, 73, 108),
                new UIElement(bgWs, uiLeft, uiUp, 234, 269, 73, 108),
                new UIElement(bgWs, uiLeft, uiUp, 272, 307, 73, 108)
        );
    }

    public static final ResourceLocation FACE_ICON_1 = new ResourceLocation(Facepop.MOD_ID, "textures/gui/faceicon/1.png");
    public static final ResourceLocation FACE_ICON_2 = new ResourceLocation(Facepop.MOD_ID, "textures/gui/faceicon/2.png");

    private static final int countPerPage = 8;

    /* 渲染表情包的按钮 */
    private void renderFaceBagButtons(GuiGraphics guiGraphics, float bgWs, float uiLeft, float uiUp, int mouseX, int mouseY) {
        UIElement faceFontTexture = new UIElement(bgWs, uiLeft, uiUp, 212, 248, 11, 24);
        this.renderElement(FACEBAG_FONT, guiGraphics, faceFontTexture);

        int faceBagListStart = currentPage * countPerPage;
        int faceBagListEnd = Math.min((currentPage + 1) * countPerPage, faceBagList.size());

        boolean hoverFaceBagSeted = false;

        List<UIElement> iconUIElements = iconElements(bgWs, uiLeft, uiUp);

        for (int i = faceBagListStart; i < faceBagListEnd; i++) {
            UIElement element = iconUIElements.get(i);

            float size = element.sizeX;
            float iconCenterX = element.centerX;
            float iconCenterY = element.centerY;

            this.renderElement(FACE_ICON_1, guiGraphics, element);

            if (!hoverFaceBagSeted) {
                hoverFaceBagSeted = testFaceBagButtonHover(mouseX, mouseY, i, element.left, element.up, element.right, element.bottom);

                if (hoverFaceBagSeted)
                    this.renderElement(FACE_ICON_2, guiGraphics, element);
            }

            FaceBag faceBag = faceBagList.get(i);
            TextureAtlasSprite sprite = FacesTextureLoader.getInstance().get(faceBag.iconResourceLocation());
            SpriteContents contents = sprite.contents();
            int spriteW = contents.width();
            int spriteH = contents.height();
            float max = Math.max(spriteW, spriteH);
            float actualWidth = spriteW / max * size / 2;
            float actualHeight = spriteH / max * size / 2;

            renderTexture(sprite.atlasLocation(), guiGraphics.pose(),
                    iconCenterX - actualWidth + 2, iconCenterX + actualWidth - 2,
                    iconCenterY - actualHeight + 2, iconCenterY + actualHeight - 2,
                    sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(),
                    // 判断是否未解锁
                    faceBag.defaultUnlock() ? 0xFF_FF_FF_FF : 0x88_FF_FF_FF);
        }

        if (!hoverFaceBagSeted) this.hoverFaceBag = -1;
    }

    private boolean testFaceBagButtonHover(int mouseX, int mouseY,
                                           int index,
                                           float x, float y, float x1, float y1) {
        if (mouseX > x && mouseX < x1 && mouseY > y && mouseY < y1) {
            this.hoverFaceBag = index;

            return true;
        }

        return false;
    }

    public static final ResourceLocation FACE_FONT = new ResourceLocation(Facepop.MOD_ID, "textures/gui/words/bq.png");

    /* 渲染子面板 */
    private void renderSubPlane(GuiGraphics guiGraphics, float bgWs, float uiLeft, float uiUp, int mouseX, int mouseY) {
        UIElement faceFontTex = new UIElement(bgWs, uiLeft, uiUp, 220, 244, 11, 24);
        this.renderElement(FACE_FONT, guiGraphics, faceFontTex);

        int faceBagListStart = currentPage * countPerPage;
        int faceBagListEnd = Math.min((currentPage + 1) * countPerPage, faceList.size());

        boolean hoverFaceSeted = false;

        List<UIElement> iconUIElements = iconElements(bgWs, uiLeft, uiUp);

        for (int i = faceBagListStart; i < faceBagListEnd; i++) {
            UIElement element = iconUIElements.get(i);

            float size = element.sizeX;
            float iconCenterX = element.centerX;
            float iconCenterY = element.centerY;

            this.renderElement(FACE_ICON_1, guiGraphics, element);

            if (!hoverFaceSeted) {
                hoverFaceSeted = testFaceButtonHover(mouseX, mouseY, i, element.left, element.up, element.right, element.bottom);

                if (hoverFaceSeted)
                    renderElement(FACE_ICON_2, guiGraphics, element);
            }

            var faceBag = faceList.get(i);
            TextureAtlasSprite sprite = FacesTextureLoader.getInstance().get(faceBag.imagePath());
            SpriteContents contents = sprite.contents();
            int spriteW = contents.width();
            int spriteH = contents.height();
            float max = Math.max(spriteW, spriteH);
            float actualWidth = spriteW / max * size / 2;
            float actualHeight = spriteH / max * size / 2;

            renderTexture(sprite.atlasLocation(), guiGraphics.pose(),
                    iconCenterX - actualWidth + 2, iconCenterX + actualWidth - 2,
                    iconCenterY - actualHeight + 2, iconCenterY + actualHeight - 2,
                    sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(),
                    0xFF_FF_FF_FF);
        }

        if (!hoverFaceSeted)
            hoverFace = -1;
    }

    private boolean testFaceButtonHover(int mouseX, int mouseY,
                                        int index,
                                        float x, float y, float x1, float y1) {
        if (mouseX > x && mouseX < x1 && mouseY > y && mouseY < y1) {
            hoverFace = index;

            return true;
        }

        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
