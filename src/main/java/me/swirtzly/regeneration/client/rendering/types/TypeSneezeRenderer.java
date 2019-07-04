package me.swirtzly.regeneration.client.rendering.types;

import me.swirtzly.regeneration.client.animation.AnimationContext;
import me.swirtzly.regeneration.client.animation.AnimationHelper;
import me.swirtzly.regeneration.common.capability.CapabilityRegeneration;
import me.swirtzly.regeneration.common.capability.IRegeneration;
import me.swirtzly.regeneration.common.types.TypeSneeze;
import me.swirtzly.regeneration.util.ClientUtil;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.RenderPlayerEvent;

import static me.swirtzly.regeneration.client.animation.AnimationHandler.copyAndReturn;

public class TypeSneezeRenderer extends ATypeRenderer<TypeSneeze>  {
	
	public static final TypeSneezeRenderer INSTANCE = new TypeSneezeRenderer();
	
	@Override
	protected void renderRegeneratingPre(TypeSneeze type, RenderPlayerEvent.Pre event, IRegeneration capability) {
	
	}
	
	@Override
	protected void renderRegeneratingPost(TypeSneeze type, RenderPlayerEvent.Post event, IRegeneration capability) {
	
	}
	
	@Override
	protected void renderRegenerationLayer(TypeSneeze type, RenderLivingBase<?> renderLivingBase, IRegeneration capability, EntityPlayer entityPlayer, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
	
	}
	
	@Override
	public boolean onAnimateRegen(AnimationContext animationContext) {
		ModelBiped model = animationContext.getModelBiped();
		EntityPlayer player = animationContext.getEntityPlayer();
		IRegeneration data = CapabilityRegeneration.getForPlayer(player);
		double animationProgress = data.getAnimationTicks();
		
	//	AnimationHelper.setAllOffsets(model.bipedBody, -4,0,-2);
		AnimationHelper.setAllRotations(model.bipedBody, -8,0,0);
		
//		AnimationHelper.setAllOffsets(model.bipedHead, -4,-8,-4);
		AnimationHelper.setAllRotations(model.bipedHead, -40,0,1);
		
	//	AnimationHelper.setAllOffsets(model.bipedLeftArm, -1,-2,-2);
		AnimationHelper.setAllRotations(model.bipedLeftArm, 20,0,-10);
		
		AnimationHelper.setAllRotations(model.bipedRightArm, 20,0,-10);
		
		return copyAndReturn(model, true);
	}
	
	@Override
	public void onRenderHand(EntityPlayer player, EnumHandSide handSide, RenderLivingBase<?> render) {
	
	}
}
