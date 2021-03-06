package me.swirtzly.regeneration.client;

import me.swirtzly.regeneration.RegenerationMod;
import me.swirtzly.regeneration.common.capability.RegenCap;
import me.swirtzly.regeneration.network.NetworkDispatcher;
import me.swirtzly.regeneration.network.messages.ForceRegenerationMessage;
import me.swirtzly.regeneration.network.messages.RegenerateMessage;
import me.swirtzly.regeneration.util.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.lwjgl.glfw.GLFW;

/**
 * Created by Sub
 * on 17/09/2018.
 */
@EventBusSubscriber(Dist.CLIENT)
public class RegenKeyBinds {
	public static KeyBinding REGEN_NOW;
	public static KeyBinding REGEN_FORCEFULLY;
	
	public static void init() {

			REGEN_NOW = new KeyBinding("regeneration.keybinds.regenerate", GLFW.GLFW_KEY_R, RegenerationMod.NAME);
			ClientRegistry.registerKeyBinding(REGEN_NOW);
		
		REGEN_FORCEFULLY = new KeyBinding("regeneration.keybinds.regenerate_forced", GLFW.GLFW_KEY_Y, RegenerationMod.NAME);
		ClientRegistry.registerKeyBinding(REGEN_FORCEFULLY);
	}
	
	
	@SubscribeEvent
	public static void keyInput(InputUpdateEvent e) {
		PlayerEntity player = Minecraft.getInstance().player;
		if (player == null || Minecraft.getInstance().currentScreen != null)
			return;

		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.currentScreen == null && minecraft.player != null) {
			ClientUtil.keyBind = RegenKeyBinds.getRegenerateNowDisplayName();
		}

		RegenCap.get(player).ifPresent((data) -> {
			if (REGEN_NOW.isPressed() && data.getState().isGraceful()) {
				NetworkDispatcher.INSTANCE.sendToServer(new RegenerateMessage());
			}
		});

		if (RegenKeyBinds.REGEN_FORCEFULLY.isPressed()) {
			NetworkDispatcher.sendToServer(new ForceRegenerationMessage());
		}

	}

	@Deprecated //This is not pretty at all, but Mojang seem to have forgotten/didn't add lang entries for A-Z
	public static String getRegenerateNowDisplayName() {
		return REGEN_NOW.getKey().toString().replace("key.keyboard.", "").toUpperCase();
	}
	
}
