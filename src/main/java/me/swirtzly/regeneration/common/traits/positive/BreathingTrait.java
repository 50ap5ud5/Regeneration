package me.swirtzly.regeneration.common.traits.positive;

import me.swirtzly.regeneration.RegenerationMod;
import me.swirtzly.regeneration.common.capability.IRegen;
import me.swirtzly.regeneration.common.traits.TraitManager;
import me.swirtzly.regeneration.util.PlayerUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Suffril
 * on 25/01/2019.
 */
public class BreathingTrait extends TraitManager.IDna {
	
	
	private ResourceLocation ID = new ResourceLocation(RegenerationMod.MODID, "swimmer");

    public BreathingTrait() {
        super("swimmer");
    }

	@Override
    public void onUpdate(IRegen cap) {
		PlayerEntity player = cap.getPlayer();
		if (player.isInWater()) {
			PlayerUtil.applyPotionIfAbsent(player, Effects.WATER_BREATHING, 100, 1, true, false);
		}
	}
	
	@Override
    public void onAdded(IRegen cap) {
		
	}
	
	@Override
    public void onRemoved(IRegen cap) {
		PlayerEntity player = cap.getPlayer();
		player.removeActivePotionEffect(Effects.WATER_BREATHING);
	}
}
