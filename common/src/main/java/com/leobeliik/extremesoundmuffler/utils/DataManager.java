package com.leobeliik.extremesoundmuffler.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.leobeliik.extremesoundmuffler.CommonConfig;
import com.leobeliik.extremesoundmuffler.Constants;
import com.leobeliik.extremesoundmuffler.gui.MufflerScreen;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FileUtil;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.Block;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

import static com.leobeliik.extremesoundmuffler.Constants.LOG;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class DataManager implements ISoundLists {

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private static boolean enabledAnchors = !CommonConfig.get().disableAnchors().get() && !Constants.isCustomSkinLoader;
	//Ignore fabric APIs
	private static List<String> hiddenMods = Arrays.asList("fabric-api", "fabric-api-base", "fabric-api-lookup-api-v1", "fabric-biome-api-v1", "fabric-block-api-v1", "fabric-block-view-api-v2",
			"fabric-client-gametest-api-v1", "fabric-command-api-v2", "fabric-content-registries-v0", "fabric-convention-tags-v1",
			"fabric-convention-tags-v2", "fabric-crash-report-info-v1", "fabric-data-attachment-api-v1", "fabric-data-generation-api-v1",
			"fabric-dimensions-v1", "fabric-entity-events-v1", "fabric-events-interaction-v0", "fabric-game-rule-api-v1", "fabric-gametest-api-v1",
			"fabric-item-api-v1", "fabric-item-group-api-v1", "fabric-key-binding-api-v1", "fabric-lifecycle-events-v1", "fabric-loot-api-v2",
			"fabric-loot-api-v3", "fabric-message-api-v1", "fabric-networking-api-v1", "fabric-object-builder-api-v1", "fabric-particles-v1",
			"fabric-recipe-api-v1", "fabric-registry-sync-v0", "fabric-rendering-fluids-v1", "fabric-rendering-v1", "fabric-resource-conditions-api-v1",
			"fabric-resource-loader-v0", "fabric-resource-loader-v1", "fabric-screen-api-v1", "fabric-screen-handler-api-v1",
			"fabric-serialization-api-v1", "fabric-sound-api-v1", "fabric-tag-api-v1", "fabric-transfer-api-v1", "fabric-transitive-access-wideners-v1");


	private static String getFolder() {
		String folder;
		if (Constants.useGlobalConfig) {
			folder = switch (Util.getPlatform()) {
				case WINDOWS ->
						System.getenv("AppData") + File.separator + ".minecraft" + File.separator + "ESM" + File.separator;
				case LINUX -> System.getProperty("user.home") + "/local/.minecraft/ESM/";
				case OSX -> System.getProperty("user.home") + "/Library/Application Support/minecraft/ESM/"; //MAC
				default -> "ESM" + File.separator;
			};
		} else {
			folder = "ESM" + File.separator;
		}
		return folder;
	}

	public static void loadData() {
		MufflerScreen.setMuffling(true);

		reload();

		if (enabledAnchors) {
			anchorList.clear();
			anchorList.addAll(loadAnchors());
		}
		blocksList.addAll(loadBlocks());
		saveData();
	}

	public static void reload() {
		muffledSounds.clear();
		muffledBlocks.clear();

		Optional.ofNullable(loadMuffledMap()).ifPresent(muffledSounds::putAll);
		/*Optional.ofNullable(loadMuffledMods()).ifPresent(sounds -> {
			muffledMods.putAll(sounds);
			muffledSounds.putAll(sounds);
		});*/
		Optional.ofNullable(loadMuffledBlocks()).ifPresent(muffledBlocks::addAll);
	}

	public static void saveData() {
		saveMuffledMap(getFolder());
		saveMuffledBlocks(getFolder());

		if (enabledAnchors) {
			saveAnchors();
		}
	}

	private static String getWorldName() {
		IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
		String name = server != null ? server.getWorldData().getLevelName().strip() : "ServerWorld";

		//prevent to create a directory with reserved characters
		try {
			return FileUtil.findAvailableName(Path.of(""), name, "");
		} catch (IOException e) {
			LOG.error("ESM: error trying to create a folder with the name of the world " + name, e);
			return "ServerWorld";
		}
	}

	private static void saveMuffledMap(String folder) {
		new File(folder).mkdir();
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(folder + "soundsMuffled.dat"), StandardCharsets.UTF_8)) {
			writer.write(gson.toJson(muffledSounds));
			writer.flush();
		} catch (IOException e) {
			LOG.error(Component.translatable("log.error.saveMuffledList", e).getString());
		}
	}

	private static Map<String, Double> loadMuffledMap() {
		try (InputStreamReader reader = new InputStreamReader(new FileInputStream(getFolder() + "soundsMuffled.dat"), StandardCharsets.UTF_8)) {
			return gson.fromJson(new JsonReader(reader), new TypeToken<Map<String, Double>>() {
			}.getType());
		} catch (Exception e) {
			if (e instanceof FileNotFoundException) {
				LOG.warn(Component.translatable("log.warn.loadMuffledList").getString());
			} else {
				LOG.error(Component.translatable("log.error.loadMuffledList", e).getString());
			}
			return new HashMap<>();
		}
	}

	/*private static void saveMuffledMods(String folder) {
		new File(folder).mkdir();
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(folder + "modsMuffled.dat"), StandardCharsets.UTF_8)) {
			writer.write(gson.toJson(muffledMods));
			writer.flush();
		} catch (IOException e) {
			LOG.error(Component.translatable("log.error.saveMuffledModsList", e).getString());
		}
	}

	private static Map<String, Double> loadMuffledMods() {
		try (InputStreamReader reader = new InputStreamReader(new FileInputStream(getFolder() + "modsMuffled.dat"), StandardCharsets.UTF_8)) {
			return gson.fromJson(new JsonReader(reader), new TypeToken<Map<String, Double>>() {
			}.getType());
		} catch (Exception e) {
			if (e instanceof FileNotFoundException) {
				LOG.warn(Component.translatable("log.warn.loadMuffledModsList").getString());
			} else {
				LOG.error(Component.translatable("log.error.loadMuffledModsList", e).getString());
			}
			return new HashMap<>();
		}
	}*/

	public static void loadMods(Map<String, String> mods) {
		//list of mods, ignoring OpenJDK, neoforge and fabric
		mods.forEach((key, value) -> {
			if (!hiddenMods.contains(value) && !CommonConfig.get().modsBlacklisted().get().contains(value)) {
				modsList.put(key, value);
			}
		});
	}

	private static void saveMuffledBlocks(String folder) {
		new File(folder).mkdir();
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(folder + "blocksMuffled.dat"), StandardCharsets.UTF_8)) {
			writer.write(gson.toJson(muffledBlocks));
			writer.flush();
		} catch (IOException e) {
			LOG.error(Component.translatable("log.error.saveMuffledBlocksList", e).getString());
		}
	}

	private static List<String> loadMuffledBlocks() {
		try (InputStreamReader reader = new InputStreamReader(new FileInputStream(getFolder() + File.separator + "blocksMuffled.dat"), StandardCharsets.UTF_8)) {
			return gson.fromJson(new JsonReader(reader), new TypeToken<List<String>>() {
			}.getType());
		} catch (Exception e) {
			if (e instanceof FileNotFoundException) {
				LOG.warn(Component.translatable("log.warn.loadMuffledBlocksList").getString());
			} else {
				LOG.error(Component.translatable("log.error.loadMuffledBlocksList", e).getString());
			}
			return new ArrayList<>();
		}
	}

	private static List<Block> loadBlocks() {
		return BuiltInRegistries.BLOCK.stream().filter(b -> !b.getDescriptionId().contains("air")).toList();
	}

	private static void saveAnchors() {
		String worldName = getWorldName();
		new File("ESM/", worldName).mkdirs();
		try (Writer writer = new OutputStreamWriter(new FileOutputStream("ESM/" + worldName + "/anchors.dat"), StandardCharsets.UTF_8)) {
			writer.write(gson.toJson(anchorList));
			writer.flush();
		} catch (IOException e) {
			LOG.error(Component.translatable("log.error.saveAnchorList", e).getString());
		}
	}

	private static List<Anchor> loadAnchors() {
		try (InputStreamReader reader = new InputStreamReader(new FileInputStream("ESM/" + getWorldName() + "/anchors.dat"), StandardCharsets.UTF_8)) {
			return gson.fromJson(new JsonReader(reader), new TypeToken<List<Anchor>>() {
			}.getType());
		} catch (Exception e) {
			if (e instanceof FileNotFoundException) {
				LOG.warn(Component.translatable("log.warn.loadAnchorList").getString());
			} else {
				LOG.error(Component.translatable("log.error.loadAnchorList", e).getString());
			}
			return new ArrayList<>();
		}
	}
}