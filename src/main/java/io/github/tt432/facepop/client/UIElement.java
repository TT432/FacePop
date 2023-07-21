package io.github.tt432.facepop.client;

/**
 * @author TT432
 */
class UIElement {
    float bgWs;
    float left;
    float right;
    float up;
    float bottom;
    float x0;
    float x1;
    float y0;
    float y1;
    float centerX;
    float centerY;
    float sizeX;
    float sizeY;

    public UIElement(float bgWs, float uiLeft, float uiUp, float x0, float x1, float y0, float y1) {
        this.bgWs = bgWs;
        this.left = uiLeft + bgWs * x0;
        this.right = uiLeft + bgWs * x1;
        this.up = uiUp + bgWs * y0;
        this.bottom = uiUp + bgWs * y1;
        this.x0 = x0;
        this.x1 = x1;
        this.y0 = y0;
        this.y1 = y1;
        this.centerX = (left + right) / 2;
        this.centerY = (up + bottom) / 2;
        this.sizeX = right - left;
        this.sizeY = bottom - up;
    }
}
