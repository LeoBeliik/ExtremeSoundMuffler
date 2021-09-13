package com.leobeliik.extremesoundmuffler.mufflers.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.client.model.IModelLoader;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class MufflerModelLoader implements IModelLoader<MufflerModelGeometry> {

    @ParametersAreNonnullByDefault
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {}

    @ParametersAreNonnullByDefault
    @Nonnull
    @Override
    public MufflerModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        return new MufflerModelGeometry();
    }
}
