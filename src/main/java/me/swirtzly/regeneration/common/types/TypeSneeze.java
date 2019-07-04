package me.swirtzly.regeneration.common.types;

import me.swirtzly.regeneration.client.rendering.types.ATypeRenderer;
import me.swirtzly.regeneration.client.rendering.types.TypeSneezeRenderer;
import me.swirtzly.regeneration.common.capability.IRegeneration;
import net.minecraft.entity.player.EntityPlayer;

public class TypeSneeze implements IRegenType {
	
	@Override
	public int getAnimationLength() {
		return 2550;
	}
	
	@Override
	public ATypeRenderer<?> getRenderer() {
		return TypeSneezeRenderer.INSTANCE;
	}
	
	@Override
	public void onStartRegeneration(EntityPlayer player, IRegeneration capability) {
	
	}
	
	@Override
	public void onUpdateMidRegen(EntityPlayer player, IRegeneration capability) {
	
	}
	
	@Override
	public void onFinishRegeneration(EntityPlayer player, IRegeneration capability) {
	
	}
	
	@Override
	public double getAnimationProgress(IRegeneration cap) {
		return Math.min(1, cap.getAnimationTicks() / (double) getAnimationLength());
	}
	
	@Override
	public TypeHandler.RegenType getTypeID() {
		return TypeHandler.RegenType.SNEEZE;
	}
}
