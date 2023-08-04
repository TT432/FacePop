package io.github.tt432.facepop.common.capability;

import io.github.tt432.facepop.Facepop;
import io.github.tt432.facepop.data.Face;
import io.github.tt432.facepop.data.FaceBag;
import io.github.tt432.facepop.data.FaceBagManager;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author TT432
 */
@AutoRegisterCapability
public class FaceCapability implements INBTSerializable<CompoundTag> {
    public static final Capability<FaceCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static final ResourceLocation defaultFaceBagLocation = new ResourceLocation(Facepop.MOD_ID, "default");

    private final List<String> unlockedFaceBag = new ArrayList<>();
    private final List<String> wheelFaces = new ArrayList<>(List.of(
            packFace(defaultFaceBagLocation, new ResourceLocation(Facepop.MOD_ID, "awesome")),
            packFace(defaultFaceBagLocation, new ResourceLocation(Facepop.MOD_ID, "happy1")),
            packFace(defaultFaceBagLocation, new ResourceLocation(Facepop.MOD_ID, "happy2")),
            packFace(defaultFaceBagLocation, new ResourceLocation(Facepop.MOD_ID, "question_mark")),
            packFace(defaultFaceBagLocation, new ResourceLocation(Facepop.MOD_ID, "sleeping"))
    ));
    private int currentFacePage = 0;


    public void unlock(String faceBag) {
        unlockedFaceBag.add(faceBag);
    }

    public boolean canUse(String faceBag) {
        return unlockedFaceBag.contains(faceBag);
    }

    public void trySetPackedFace(int index, RegistryAccess access, String packedFace) {
        String[] faceBagAndFace = packedFace.split("#");

        if (access.registry(FaceBagManager.FACE_BAG_KEY)
                .map(registry -> registry.get(new ResourceLocation(faceBagAndFace[0])))
                .map(FaceBag::defaultUnlock)
                .orElse(false)
                || canUse(faceBagAndFace[0])) {

            if (index < 1 || index > 5)
                return;

            wheelFaces.set(index - 1, packedFace);
        }
    }

    public static String packFace(ResourceLocation faceBag, ResourceLocation face) {
        return faceBag.toString() + "#" + face.toString();
    }

    public static Face unpackFace(RegistryAccess registryAccess, String face) {
        String[] faceBagAndFace = face.split("#");

        if (faceBagAndFace.length < 2)
            return null;

        return registryAccess.registry(FaceBagManager.FACE_BAG_KEY)
                .map(registry -> {
                    FaceBag faceBag = registry.get(new ResourceLocation(faceBagAndFace[0]));

                    if (faceBag != null) {
                        for (Face face1 : faceBag.faces()) {
                            if (face1.id().toString().equals(faceBagAndFace[1])) {
                                return face1;
                            }
                        }
                    }

                    return null;
                })
                .orElse(null);
    }

    /**
     * @param index 1 .. 5 ，1 是 中心，右是 2，下是 3，以此类推
     * @return {facebag}#{face}
     */
    public String getFace(int index) {
        if (index < 1 || index > 5)
            return "facepop:default#facepop:awesome";

        return wheelFaces.get(index - 1);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag result = new CompoundTag();

        var unlockFaceBagTag = new ListTag();
        unlockedFaceBag.forEach(fb -> unlockFaceBagTag.add(StringTag.valueOf(fb)));
        result.put("unlockFaceBagTag", unlockFaceBagTag);

        var wheelFacesTag = new ListTag();
        wheelFaces.forEach(wf -> wheelFacesTag.add(StringTag.valueOf(wf)));
        result.put("wheelFacesTag", wheelFacesTag);

        result.putInt("currentFacePage", currentFacePage);

        return result;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt == null)
            return;

        if (nbt.contains("unlockFaceBagTag", Tag.TAG_LIST)) {
            var unlockFaceBagTag = nbt.getList("unlockFaceBagTag", Tag.TAG_STRING);
            unlockedFaceBag.clear();
            unlockFaceBagTag.forEach(tag -> unlockedFaceBag.add(tag.getAsString()));
        }

        if (nbt.contains("wheelFacesTag", Tag.TAG_LIST)) {
            var wheelFacesTag = nbt.getList("wheelFacesTag", Tag.TAG_STRING);
            wheelFaces.clear();
            wheelFacesTag.forEach(tag -> wheelFaces.add(tag.getAsString()));
        }

        if (nbt.contains("currentFacePage"))
            currentFacePage = nbt.getInt("currentFacePage");
    }

    public static final class Provider extends CapabilityProvider<Provider> implements ICapabilitySerializable<CompoundTag> {

        LazyOptional<FaceCapability> capability;

        public Provider() {
            super(Provider.class);

            capability = LazyOptional.of(FaceCapability::new);
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return cap == CAPABILITY ? capability.cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return capability.map(FaceCapability::serializeNBT).orElse(new CompoundTag());
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            capability.ifPresent(cap -> cap.deserializeNBT(nbt));
        }
    }
}
