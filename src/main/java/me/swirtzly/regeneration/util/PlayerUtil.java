package me.swirtzly.regeneration.util;

import me.swirtzly.regeneration.client.skinhandling.SkinManipulation;
import me.swirtzly.regeneration.common.capability.RegenCap;
import me.swirtzly.regeneration.common.item.HandItem;
import me.swirtzly.regeneration.handlers.RegenObjects;
import me.swirtzly.regeneration.network.NetworkDispatcher;
import me.swirtzly.regeneration.network.messages.ThirdPersonMessage;
import me.swirtzly.regeneration.network.messages.UpdateSkinMapMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.HandSide;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sub
 * on 20/09/2018.
 */
public class PlayerUtil {
	
	public static ArrayList<Effect> POTIONS = new ArrayList<>();
	
	public static void createPostList() {
		POTIONS.add(Effects.WEAKNESS);
		POTIONS.add(Effects.MINING_FATIGUE);
		POTIONS.add(Effects.RESISTANCE);
		POTIONS.add(Effects.HEALTH_BOOST);
		POTIONS.add(Effects.HUNGER);
		POTIONS.add(Effects.WATER_BREATHING);
		POTIONS.add(Effects.HASTE);
	}

    public static void lookAt(double px, double py, double pz, PlayerEntity me) {
        double dirx = me.getPosition().getX() - px;
        double diry = me.getPosition().getY() - py;
        double dirz = me.getPosition().getZ() - pz;

        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);

        dirx /= len;
        diry /= len;
        dirz /= len;

        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);

        //to degree
        pitch = pitch * 180.0 / Math.PI;
        yaw = yaw * 180.0 / Math.PI;

        yaw += 90f;
        me.rotationPitch = (float) pitch;
        me.rotationYaw = (float) yaw;
    }

	public static void sendMessage(PlayerEntity player, String message, boolean hotBar) {
		if (!player.world.isRemote) {
			player.sendStatusMessage(new TranslationTextComponent(message), hotBar);
		}
	}
	
	public static void sendMessage(PlayerEntity player, TranslationTextComponent translation, boolean hotBar) {
		if (!player.world.isRemote) {
			player.sendStatusMessage(translation, hotBar);
		}
	}
	
	public static void sendMessageToAll(TranslationTextComponent translation) {
		List<ServerPlayerEntity> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
		players.forEach(playerMP -> sendMessage(playerMP, translation, false));
	}
	
	public static void setPerspective(ServerPlayerEntity player, boolean thirdperson, boolean resetPitch) {
		NetworkDispatcher.sendTo(new ThirdPersonMessage(thirdperson), player);
	}


    public static void updateModel(SkinManipulation.EnumChoices choice) {
		NetworkDispatcher.INSTANCE.sendToServer(new UpdateSkinMapMessage(choice.name()));
	}
	
	public static boolean applyPotionIfAbsent(PlayerEntity player, Effect potion, int length, int amplifier, boolean ambient, boolean showParticles) {
		if (potion == null) return false;
		if (player.getActivePotionEffect(potion) == null) {
			player.addPotionEffect(new EffectInstance(potion, length, amplifier, ambient, showParticles));
			return true;
		}
		return false;
	}

    public static boolean isSharp(ItemStack stack) {
        return stack.getItem() instanceof ToolItem || stack.getItem() instanceof SwordItem;
    }

    public static void createHand(PlayerEntity player) {
        RegenCap.get(player).ifPresent((data) -> {
            ItemStack hand = new ItemStack(RegenObjects.Items.HAND);
            HandItem.setTextureString(hand, data.getEncodedSkin());
            HandItem.setSkinType(hand, data.getSkinType().name());
            HandItem.setOwner(hand, player.getUniqueID());
            HandItem.setTimeCreated(hand, System.currentTimeMillis());
            HandItem.setTrait(hand, data.getDnaType().toString());
            data.setDroppedHand(true);
            //RegenTriggers.HAND.trigger((EntityPlayerMP) player);
            data.setCutOffHand(player.getPrimaryHand() == HandSide.LEFT ? HandSide.RIGHT : HandSide.LEFT);
            data.setDroppedHand(true);
            InventoryHelper.spawnItemStack(player.world, player.posX, player.posY, player.posZ, hand);
        });
    }

    public static void handleCutOffhand(PlayerEntity player) {
        RegenCap.get(player).ifPresent((data) -> {
            if (data.hasDroppedHand()) {
                if (!player.getHeldItemOffhand().isEmpty()) {
                    player.dropItem(player.getHeldItemOffhand(), false);
                    player.setItemStackToSlot(EquipmentSlotType.OFFHAND, new ItemStack(Items.AIR));
                }
            }
        });
    }

	public enum RegenState {
		
		ALIVE,
		GRACE, GRACE_CRIT, POST,
		REGENERATING;
		
		public boolean isGraceful() {
			return this == GRACE || this == GRACE_CRIT;
		}
		
		public enum Transition {
			HAND_GLOW_START(Color.YELLOW.darker()), HAND_GLOW_TRIGGER(Color.ORANGE),
			ENTER_CRITICAL(Color.BLUE),
			CRITICAL_DEATH(Color.RED),
			FINISH_REGENERATION(Color.GREEN.darker()),
			END_POST(Color.PINK.darker());
			
			public final Color color;
			
			Transition(Color col) {
				this.color = col;
			}
		}
	}
}
