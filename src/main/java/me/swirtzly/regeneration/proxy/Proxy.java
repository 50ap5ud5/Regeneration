package me.swirtzly.regeneration.proxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

/**
 * Created by Sub
 * on 17/09/2018.
 */
public interface Proxy {
	
	default void preInit() {
	}
	
	default void init() {
	}
	
	default void postInit() {
	}

    World getClientWorld();

    PlayerEntity getClientPlayer();
	
}
