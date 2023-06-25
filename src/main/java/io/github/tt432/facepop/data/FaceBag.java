package io.github.tt432.facepop.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

/**
 * @author TT432
 */
public record FaceBag(
        List<Face> faces
) {
    public static final Codec<FaceBag> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Face.CODEC.listOf().fieldOf("faces").forGetter(FaceBag::faces)
    ).apply(instance, FaceBag::new));
}
