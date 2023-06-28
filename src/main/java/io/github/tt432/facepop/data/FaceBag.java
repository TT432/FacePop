package io.github.tt432.facepop.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * @author TT432
 */
public record FaceBag(
        ResourceLocation id,
        List<Face> faces,
        ResourceLocation iconLocation,
        boolean defaultUnlock,
        String lockMsgLangKey
) {
    public static final Codec<FaceBag> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(FaceBag::id),
            Face.CODEC.listOf().fieldOf("faces").forGetter(FaceBag::faces),
            ResourceLocation.CODEC.fieldOf("icon").forGetter(FaceBag::iconLocation),
            Codec.BOOL.optionalFieldOf("default_unlock", false).forGetter(FaceBag::defaultUnlock),
            Codec.STRING.optionalFieldOf("lock_message", "facebag.lock").forGetter(FaceBag::lockMsgLangKey)
    ).apply(instance, FaceBag::new));
}
