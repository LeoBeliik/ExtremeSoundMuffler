package com.leobeliik.extremesoundmuffler.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import net.minecraft.util.ResourceLocation;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

class JsonIO {

    private static final String serverPath = "saves" + File.separator + "ESM" + File.separatorChar + "ServerWorld";

    static void saveMuffledMap(File file, Map<ResourceLocation, Float> list) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write(new Gson().toJson(list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void saveAnchor(File path, File file, Anchor anchor) {
        if (path.mkdirs() && path.toString().equals(serverPath)) {
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(path + File.separator + "What is this.txt"), StandardCharsets.UTF_8)) {
                writer.write(new Gson().toJson("This is where Extreme sound muffler saves the Anchors for Server Worlds"));
            } catch (Exception ignored) {
            }
        }
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write(new Gson().toJson(anchor));
        } catch (Exception ignored) {
        }
    }

    static Map<String, Float> loadMuffledList(File file) {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            return new Gson().fromJson(new JsonReader(reader), new TypeToken<Map<String, Float>>() {}.getType());
        } catch (IOException e) {
            return new HashMap<>();
        }
    }

    static Anchor loadAnchor(File file, int i) {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            return new Gson().fromJson(new JsonReader(reader), new TypeToken<Anchor>() {}.getType());
        } catch (IOException e) {
            return new Anchor(i, "Anchor " + i);
        }
    }
}
