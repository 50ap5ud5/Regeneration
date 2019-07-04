package me.swirtzly.regeneration.client.rendering.types;

import me.swirtzly.regeneration.client.animation.AnimationContext;
import me.swirtzly.regeneration.common.capability.IRegeneration;
import me.swirtzly.regeneration.common.types.IRegenType;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderPlayerEvent.Pre;

public abstract class ATypeRenderer<T> {
	
	protected abstract void renderRegeneratingPre(T type, Pre event, IRegeneration capability);
	
	protected abstract void renderRegeneratingPost(T type, RenderPlayerEvent.Post event, IRegeneration capability);
	
	protected abstract void renderRegenerationLayer(T type, RenderLivingBase<?> renderLivingBase, IRegeneration capability, EntityPlayer entityPlayer, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale);
	
	// Generic casting convenience methods:
	
	@SuppressWarnings("unchecked")
	public final void onRenderRegeneratingPre(IRegenType<?> type, Pre event, IRegeneration capability) {
		try {
			renderRegeneratingPre((T) type, event, capability);
		} catch (ClassCastException e) {
			throw new IllegalStateException("RegenType <-> RegenRenderType mismatch", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public final void onRenderRegeneratingPost(IRegenType<?> type, RenderPlayerEvent.Post event, IRegeneration capability) {
		try {
			renderRegeneratingPost((T) type, event, capability);
		} catch (ClassCastException e) {
			throw new IllegalStateException("RegenType <-> RegenRenderType mismatch", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public final void onRenderRegenerationLayer(IRegenType<?> type, RenderLivingBase<?> renderLivingBase, IRegeneration capability, EntityPlayer entityPlayer, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		try {
			renderRegenerationLayer((T) type, renderLivingBase, capability, entityPlayer, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
		} catch (ClassCastException e) {
			throw new IllegalStateException("RegenType <-> RegenRenderType mismatch", e);
		}
	}

	public abstract boolean onAnimateRegen(AnimationContext animationContext);
	
	public abstract void onRenderHand(EntityPlayer player, EnumHandSide handSide, RenderLivingBase<?> render);
	
}
