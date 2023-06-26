package io.github.tt432.facepop.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.tt432.facepop.data.Face;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TT432
 */
@Mod.EventBusSubscriber(Dist.CLIENT)
public class FaceRenderHandler {
    private static final Map<Integer, FaceRenderData> renderFace = new HashMap<>();

    public static void putRenderData(int entityId, Face face) {
        renderFace.put(entityId, new FaceRenderData(face, 0));
    }

    @SubscribeEvent
    public static void onEvent(RenderLivingEvent.Post<?, ?> event) {
        var faceData = renderFace.get(event.getEntity().getId());

        if (faceData != null && event.getEntity() instanceof Player player) {
            Face face = faceData.face;
            PoseStack poseStack = event.getPoseStack();
            TextureAtlasSprite sprite = FacesTextureLoader.getInstance().get(face.imagePath());

            poseStack.pushPose();

            poseStack.translate(0, player.getBbHeight() + face.offsetY() , 0);
            poseStack.mulPose(new Quaternionf().rotationZYX(
                    0,
                    (float) -Math.toRadians(player.getViewYRot(event.getPartialTick())),
                    (float) Math.toRadians(player.getViewXRot(event.getPartialTick()))));
            poseStack.translate(face.offsetX() -0.3, 0, 0);

            VertexConsumer buffer = event.getMultiBufferSource().getBuffer(RenderType.entityCutout(sprite.atlasLocation()));

            var x1 = -1;
            var y1 = 1;
            var x2 = 0;
            var y2 = 0;

            var argb = 0xFF_FF_FF_FF;

            var u0 = sprite.getU0();
            var u1 = sprite.getU1();
            var v0 = sprite.getV0();
            var v1 = sprite.getV1();

            Matrix4f matrix4f = poseStack.last().pose();
            Matrix3f normal = poseStack.last().normal();

            int packedLight = LightTexture.FULL_BRIGHT;

            buffer.vertex(matrix4f, x1, y1, 0).color(argb).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight).normal(normal, 0, 0, 1).endVertex();
            buffer.vertex(matrix4f, x1, y2, 0).color(argb).uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight).normal(normal, 0, 0, 1).endVertex();
            buffer.vertex(matrix4f, x2, y2, 0).color(argb).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight).normal(normal, 0, 0, 1).endVertex();
            buffer.vertex(matrix4f, x2, y1, 0).color(argb).uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight).normal(normal, 0, 0, 1).endVertex();

            buffer.vertex(matrix4f, x1, y1, 0).color(argb).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight).normal(normal, 0, 0, 1).endVertex();
            buffer.vertex(matrix4f, x2, y1, 0).color(argb).uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight).normal(normal, 0, 0, 1).endVertex();
            buffer.vertex(matrix4f, x2, y2, 0).color(argb).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight).normal(normal, 0, 0, 1).endVertex();
            buffer.vertex(matrix4f, x1, y2, 0).color(argb).uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight).normal(normal, 0, 0, 1).endVertex();

            poseStack.popPose();
        }
    }

    @SubscribeEvent
    public static void onEvent(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        for (var value : renderFace.values()) {
            value.renderTick++;
        }

        renderFace.entrySet().removeIf(entry -> entry.getValue().renderTick > 60);
    }

    private FaceRenderHandler() {
        // can't instance
    }

    private static class FaceRenderData {
        Face face;
        int renderTick;

        public FaceRenderData(Face face, int renderTick) {
            this.face = face;
            this.renderTick = renderTick;
        }
    }
}
