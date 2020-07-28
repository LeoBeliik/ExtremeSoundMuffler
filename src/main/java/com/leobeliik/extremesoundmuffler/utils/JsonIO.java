package com.leobeliik.extremesoundmuffler.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import net.minecraft.util.ResourceLocation;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

class JsonIO {

    static void saveMuffledList(File file, Set<ResourceLocation> list) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write(new Gson().toJson(list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void saveAnchor(File path, File file, Anchor anchor) {
        if (path.mkdirs()) {
            try (Writer writer = new OutputStreamWriter(new FileOutputStream("What is this.txt"), StandardCharsets.UTF_8)) {
                writer.write(new Gson().toJson("This is where Extreme sound muffler saves the anchors data for Server worlds"));
            } catch (Exception ignored) {}
        }
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write(new Gson().toJson(anchor));
        } catch (Exception ignored) {}
    }

    static Set<ResourceLocation> loadMuffledList(File file) {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            return new Gson().fromJson(new JsonReader(reader), new TypeToken<Set<ResourceLocation>>() {}.getType());
        } catch (IOException e) {
            return new HashSet<>();
        }
    }

    static Anchor loadAnchor(File file, int i) {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            return new Gson().fromJson(new JsonReader(reader), new TypeToken<Anchor>(){}.getType());
        } catch (IOException e) {
            return new Anchor(i, "Anchor " + i);
        }
    }
}
