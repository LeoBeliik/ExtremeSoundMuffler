package com.leobeliik.extremesoundmuffler;

import com.leobeliik.extremesoundmuffler.gui.MufflerScreen;
import com.leobeliik.extremesoundmuffler.gui.buttons.ESMInv;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.utils.DataManager;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforgespi.language.IModInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.leobeliik.extremesoundmuffler.Constants.soundMufflerKey;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class SoundMufflerNeoForge {

    public SoundMufflerNeoForge(IEventBus modEventBus, ModContainer container) {
        NeoForge.EVENT_BUS.register(this);
        NeoForgeConfig.init(container);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        Constants.isCustomSkinLoader = ModList.get().isLoaded("customskinloader");
    }

    @EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
    public class ClientModListener {
        @SubscribeEvent
        public static void keyRegistry(final RegisterKeyMappingsEvent event) {
            event.register(soundMufflerKey);
        }
    }

    @SubscribeEvent //on mod keybind press
    public void onKeyInput(InputEvent.Key event) {
        if (soundMufflerKey.consumeClick()) {
            SoundMufflerCommon.openMainScreen();
        }
    }

    @SubscribeEvent //load data when player joins the world
    public void onPlayerJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        Constants.useGlobalConfig = NeoForgeConfig.getGlobalConfig();
        DataManager.loadData();

        if (ISoundLists.modsList.isEmpty()) {
            var mods = ModList.get().getMods();
            Map<String, String> modNames = mods.stream().collect(Collectors.toMap(IModInfo::getDisplayName, IModInfo::getModId, (a, b) ->
                    b, () -> new HashMap<>(mods.size())));
            DataManager.loadMods(modNames);
        }
    }

    @SubscribeEvent //save the new coordinates for the inv button
    public void onMouseRelease(ScreenEvent.MouseButtonReleased.Post event) {
        if (event.getButton() == 1) {
            Screen screen = event.getScreen();
            for (GuiEventListener widget : screen.children()) {
                if (widget instanceof ESMInv btn && btn.isHovered()) {
                    if (screen instanceof CreativeModeInventoryScreen) {
                        NeoForgeConfig.setCreativeInvButtonHorizontal(btn.getX(), btn.getY());
                    } else {
                        NeoForgeConfig.setInvButtonHorizontal(btn.getX(), btn.getY());
                    }
                    break;
                }
            }
        }
    }

    @SubscribeEvent //change global and local config save
    public void onMouseClick(ScreenEvent.MouseButtonPressed.Pre event) {
        if (event.getButton() == 0 && event.getScreen() instanceof MufflerScreen MS && MS.btnGlobal.isHovered()) {
            NeoForgeConfig.setGlobalConfig(!MS.btnGlobal.isToggled());
            Constants.useGlobalConfig = NeoForgeConfig.getGlobalConfig();
            DataManager.reload();
        }
    }
}