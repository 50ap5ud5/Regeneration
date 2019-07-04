package me.swirtzly.regeneration.client.animation;

import net.minecraft.client.model.ModelRenderer;

public class AnimationHelper {
	
	public static void setAllRotations(ModelRenderer modelRenderer, float x, float y, float z){
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
	
	public static void setAllOffsets(ModelRenderer modelRenderer, float x, float y, float z){
		modelRenderer.offsetX = x;
		modelRenderer.offsetY = y;
		modelRenderer.offsetZ = z;
	}
	
}
