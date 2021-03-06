package me.swirtzly.regeneration.common.traits.positive;

import me.swirtzly.regeneration.common.capability.IRegen;
import me.swirtzly.regeneration.common.traits.TraitManager;
import net.minecraft.entity.player.PlayerEntity;

public class WallClimbingTrait extends TraitManager.IDna {

    public WallClimbingTrait() {
        super("wallclimbing");
    }

    @Override
    public void onUpdate(IRegen cap) {
        PlayerEntity player = cap.getPlayer();
		if (player.collidedHorizontally && player.moveForward > 0) {
            player.getMotion().add(0, 0.2F, 0);
        }
    }

    @Override
    public void onAdded(IRegen cap) {

    }

    @Override
    public void onRemoved(IRegen cap) {

    }

}
