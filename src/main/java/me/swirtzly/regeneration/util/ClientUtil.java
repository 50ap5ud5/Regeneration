package me.swirtzly.regeneration.util;

import me.swirtzly.regeneration.client.MovingSound;
import me.swirtzly.regeneration.client.skinhandling.SkinInfo;
import me.swirtzly.regeneration.client.skinhandling.SkinManipulation;
import me.swirtzly.regeneration.network.NetworkDispatcher;
import me.swirtzly.regeneration.network.messages.UpdateSkinMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.function.Supplier;

public class ClientUtil {

    public static String keyBind = "???";

	public static void createToast(TranslationTextComponent title, TranslationTextComponent subtitle) {
		Minecraft.getInstance().getToastGui().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT, title, subtitle));
	}
	
	public static void playPositionedSoundRecord(SoundEvent sound, float pitch, float volume) {
		Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(sound, pitch, volume));
	}
	
	/**
	 * This is a method that sends a packet to the server telling the server to reset the players Player model and skin
	 * back to the ones supplied by Mojang
	 */
	public static void sendSkinResetPacket() {
		NetworkDispatcher.sendToServer(new UpdateSkinMessage(RegenUtil.NO_SKIN, SkinManipulation.getSkinType(Minecraft.getInstance().player) == SkinInfo.SkinType.ALEX));
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void playSound(Object entity, ResourceLocation soundName, SoundCategory category, boolean repeat, Supplier<Boolean> stopCondition, float volume) {
		Minecraft.getInstance().getSoundHandler().play(new MovingSound(entity, new SoundEvent(soundName), category, repeat, stopCondition, volume));
	}
	
	/**
	 * Helper method that copy pastes the angles of the ModelPlayer limbs to the players wear
	 * @param biped
	 */
	public static void copyAnglesToWear(PlayerModel biped) {
		RenderUtil.copyModelAngles(biped.bipedRightArm, biped.bipedRightArmwear);
		RenderUtil.copyModelAngles(biped.bipedLeftArm, biped.bipedLeftArmwear);
		RenderUtil.copyModelAngles(biped.bipedRightLeg, biped.bipedRightLegwear);
		RenderUtil.copyModelAngles(biped.bipedLeftLeg, biped.bipedLeftLegwear);
		RenderUtil.copyModelAngles(biped.bipedBody, biped.bipedBodyWear);
		RenderUtil.copyModelAngles(biped.bipedHead, biped.bipedHeadwear);

		copyRotationPoints(biped.bipedRightArm, biped.bipedRightArmwear);
		copyRotationPoints(biped.bipedLeftArm, biped.bipedLeftArmwear);
		copyRotationPoints(biped.bipedRightLeg, biped.bipedRightLegwear);
		copyRotationPoints(biped.bipedLeftLeg, biped.bipedLeftLegwear);
		copyRotationPoints(biped.bipedBody, biped.bipedBodyWear);
		copyRotationPoints(biped.bipedHead, biped.bipedHeadwear);
	}

	public static void copyRotationPoints(RendererModel src, RendererModel dest) {
		dest.rotationPointX = src.rotationPointX;
		dest.rotationPointY = src.rotationPointY;
		dest.rotationPointZ = src.rotationPointZ;
	}

	
	public static class ImageFixer {
		
		private static int[] imageData;
		private static int imageWidth;
		private static int imageHeight;
		
		public static BufferedImage convertSkinTo64x64(BufferedImage image) {
			if (image == null) {
				return null;
			} else {
				if (image.getHeight() == 64 && image.getWidth() == 64) {
					return image;
				}
				imageWidth = 64;
				imageHeight = 64;
				BufferedImage bufferedimage = new BufferedImage(imageWidth, imageHeight, 2);
				Graphics graphics = bufferedimage.getGraphics();
				graphics.drawImage(image, 0, 0, null);
				boolean flag = image.getHeight() == 32;
				
				if (flag) {
					graphics.setColor(new Color(0, 0, 0, 0));
					graphics.fillRect(0, 32, 64, 32);
					graphics.drawImage(bufferedimage, 24, 48, 20, 52, 4, 16, 8, 20, null);
					graphics.drawImage(bufferedimage, 28, 48, 24, 52, 8, 16, 12, 20, null);
					graphics.drawImage(bufferedimage, 20, 52, 16, 64, 8, 20, 12, 32, null);
					graphics.drawImage(bufferedimage, 24, 52, 20, 64, 4, 20, 8, 32, null);
					graphics.drawImage(bufferedimage, 28, 52, 24, 64, 0, 20, 4, 32, null);
					graphics.drawImage(bufferedimage, 32, 52, 28, 64, 12, 20, 16, 32, null);
					graphics.drawImage(bufferedimage, 40, 48, 36, 52, 44, 16, 48, 20, null);
					graphics.drawImage(bufferedimage, 44, 48, 40, 52, 48, 16, 52, 20, null);
					graphics.drawImage(bufferedimage, 36, 52, 32, 64, 48, 20, 52, 32, null);
					graphics.drawImage(bufferedimage, 40, 52, 36, 64, 44, 20, 48, 32, null);
					graphics.drawImage(bufferedimage, 44, 52, 40, 64, 40, 20, 44, 32, null);
					graphics.drawImage(bufferedimage, 48, 52, 44, 64, 52, 20, 56, 32, null);
				}
				
				graphics.dispose();
				imageData = ((DataBufferInt) bufferedimage.getRaster().getDataBuffer()).getData();
				setAreaOpaque(0, 0, 32, 16);
				
				if (flag) {
					setAreaTransparent(32, 0, 64, 32);
				}
				
				setAreaOpaque(0, 16, 64, 32);
				setAreaOpaque(16, 48, 48, 64);
				return bufferedimage;
			}
		}
		
		private static void setAreaTransparent(int x, int y, int width, int height) {
			for (int i = x; i < width; ++i) {
				for (int j = y; j < height; ++j) {
					int k = imageData[i + j * imageWidth];
					
					if ((k >> 24 & 255) < 128) {
						return;
					}
				}
			}
			
			for (int l = x; l < width; ++l) {
				for (int i1 = y; i1 < height; ++i1) {
					imageData[l + i1 * imageWidth] &= 16777215;
				}
			}
		}
		
		/**
		 * Makes the given area of the image opaque
		 */
		private static void setAreaOpaque(int x, int y, int width, int height) {
			for (int i = x; i < width; ++i) {
				for (int j = y; j < height; ++j) {
					imageData[i + j * imageWidth] |= -16777216;
				}
			}
		}
	}
}
