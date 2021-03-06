package me.swirtzly.regeneration.handlers;

import me.swirtzly.regeneration.RegenConfig;
import me.swirtzly.regeneration.common.capability.IRegen;
import me.swirtzly.regeneration.common.capability.RegenCap;
import me.swirtzly.regeneration.util.PlayerUtil;
import me.swirtzly.regeneration.util.RegenUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;

/**
 * Created by Sub
 * on 16/09/2018.
 */
public class CommonHandler {
	
	// =========== CAPABILITY HANDLING =============
	
	@SubscribeEvent
	public void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
		if (event.getEntityLiving() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) event.getEntityLiving();
			RegenCap.get(player).ifPresent(IRegen::tick);

		}
	}

	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone event) {
		IStorage<IRegen> storage = RegenCap.CAPABILITY.getStorage();
		event.getOriginal().revive();
        RegenCap.get(event.getOriginal()).ifPresent((old) -> RegenCap.get(event.getPlayer()).ifPresent((data) -> {
			CompoundNBT nbt = (CompoundNBT) storage.writeNBT(RegenCap.CAPABILITY, old, null);
			storage.readNBT(RegenCap.CAPABILITY, data, null, nbt);
		}));
	}

	@SubscribeEvent
	public void onPlayerTracked(PlayerEvent.StartTracking event) {
		RegenCap.get(event.getPlayer()).ifPresent(IRegen::synchronise);
	}
	
	@SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		RegenCap.get(event.getPlayer()).ifPresent(IRegen::synchronise);
	}
	
	@SubscribeEvent
	public void onDeathEvent(LivingDeathEvent e) {
		if (e.getEntityLiving() instanceof PlayerEntity) {
			RegenCap.get(e.getEntityLiving()).ifPresent(IRegen::synchronise);
		}
	}
	
	// ============ USER EVENTS ==========
	
	@SubscribeEvent
	public void onPunchBlock(PlayerInteractEvent.LeftClickBlock e) {
        if (e.getPlayer().world.isRemote)
			return;
        RegenCap.get(e.getPlayer()).ifPresent((data) -> data.getStateManager().onPunchBlock(e));

	}
	
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onHurt(LivingHurtEvent event) {
		Entity trueSource = event.getSource().getTrueSource();
		
		if (trueSource instanceof PlayerEntity && event.getEntityLiving() instanceof MobEntity) {
			PlayerEntity player = (PlayerEntity) trueSource;
			RegenCap.get(player).ifPresent((data) -> data.getStateManager().onPunchEntity(event));
			return;
		}

		if (!(event.getEntity() instanceof PlayerEntity) || event.getSource() == RegenObjects.REGEN_DMG_CRITICAL || event.getSource() == RegenObjects.REGEN_DMG_KILLED)
			return;
		
		PlayerEntity player = (PlayerEntity) event.getEntity();
		RegenCap.get(player).ifPresent((cap) -> {

			cap.setDeathSource(event.getSource().getDeathMessage(player).getUnformattedComponentText());

			if (cap.getState() == PlayerUtil.RegenState.POST && player.posY > 0) {
				if (event.getSource() == DamageSource.FALL) {
					PlayerUtil.applyPotionIfAbsent(player, Effects.NAUSEA, 200, 4, false, false);
					if (event.getAmount() > 8.0F) {
						if (player.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) && RegenConfig.COMMON.genCrater.get()) {
							RegenUtil.genCrater(player.world, player.getPosition(), 3);
						}
						event.setAmount(0.5F);
						PlayerUtil.sendMessage(player, new TranslationTextComponent("regeneration.messages.fall_dmg"), true);
						return;
					}
				} else {
					event.setAmount(0.5F);
					PlayerUtil.sendMessage(player, new TranslationTextComponent("regeneration.messages.reduced_dmg"), true);


                    if (event.getSource().getTrueSource() instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity) event.getSource().getTrueSource();
                        if (PlayerUtil.isSharp(livingEntity.getHeldItemMainhand())) {
                            if (!cap.hasDroppedHand() && cap.getState() == PlayerUtil.RegenState.POST) {
                                PlayerUtil.createHand(player);
                            }
                        }
                    }

				}
				return;
			}

			if (cap.getState() == PlayerUtil.RegenState.REGENERATING && RegenConfig.COMMON.regenFireImmune.get() && event.getSource().isFireDamage() || cap.getState() == PlayerUtil.RegenState.REGENERATING && event.getSource().isExplosion()) {
				event.setCanceled(true); // TODO still "hurts" the client view
			} else if (player.getHealth() + player.getAbsorptionAmount() - event.getAmount() <= 0) { // player has actually died
				boolean notDead = cap.getStateManager().onKilled(event.getSource());
				event.setCanceled(notDead);
			}
		});
	}
	
	
	@SubscribeEvent
	public void onKnockback(LivingKnockBackEvent event) {
		if (event.getEntityLiving() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) event.getEntityLiving();

			RegenCap.get(player).ifPresent((data) -> {
				if(data.getState() == PlayerUtil.RegenState.REGENERATING){
					event.setCanceled(true);
				}
			});
		}
	}


	@SubscribeEvent
	public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof PlayerEntity) {
			event.addCapability(RegenCap.CAP_REGEN_ID, new ICapabilitySerializable<CompoundNBT>() {
				final RegenCap regen = new RegenCap((PlayerEntity) event.getObject());
				final LazyOptional<IRegen> regenInstance = LazyOptional.of(() -> regen);

				@Nonnull
				@Override
				public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @javax.annotation.Nullable Direction side) {
					if (cap == RegenCap.CAPABILITY)
						return (LazyOptional<T>) regenInstance;
					return LazyOptional.empty();
				}

				@Override
				public CompoundNBT serializeNBT() {
					return regen.serializeNBT();
				}

				@Override
				public void deserializeNBT(CompoundNBT nbt) {
					regen.deserializeNBT(nbt);
				}

			});
		}
	}


	
	// ================ OTHER ==============
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		if (entity.getClass().equals(ItemEntity.class)) {
			ItemStack stack = ((ItemEntity) entity).getItem();
			Item item = stack.getItem();
			if (item.hasCustomEntity(stack)) {
				Entity newEntity = item.createEntity(event.getWorld(), entity, stack);
				if (newEntity != null) {
					entity.remove();
					event.setCanceled(true);
					event.getWorld().addEntity(newEntity);
				}
			}
		}
	}

	@SubscribeEvent
    public void onCut(PlayerInteractEvent.RightClickItem event) {
        if (PlayerUtil.isSharp(event.getItemStack())) {
            PlayerEntity player = event.getPlayer();
            RegenCap.get(player).ifPresent((data) -> {
                if (data.getState() == PlayerUtil.RegenState.POST && !data.hasDroppedHand()) {
                    PlayerUtil.createHand(player);
                }
            });
		}
	}


}
