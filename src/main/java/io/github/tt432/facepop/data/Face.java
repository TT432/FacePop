package io.github.tt432.facepop.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

/**
 * @author TT432
 */
public record Face(
        ResourceLocation id,
        String languageKey,
        float offsetX,
        float offsetY,
        ResourceLocation imagePath
) {
    public static final Codec<Face> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(Face::id),
            Codec.STRING.optionalFieldOf("language_key", "").forGetter(Face::languageKey),
            Codec.FLOAT.optionalFieldOf("offset_x", 0F).forGetter(Face::offsetX),
            Codec.FLOAT.optionalFieldOf("offset_y", 0F).forGetter(Face::offsetY),
            ResourceLocation.CODEC.fieldOf("image_path").forGetter(Face::imagePath)
    ).apply(instance, (id, langKey, offx, offy, imgrl) -> {
        if (langKey.equals("")) {
            langKey = id.toLanguageKey();
        }

        return new Face(id, langKey, offx, offy, imgrl);
    }));
}
