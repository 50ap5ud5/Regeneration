package me.swirtzly.regeneration.network;

import me.swirtzly.regeneration.common.capability.CapabilityRegeneration;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageUpdateModel {
	
	private String preferred;
	
	public MessageUpdateModel(String preferred) {
		this.preferred = preferred;
	}
	
	public static void encode(MessageUpdateModel model, PacketBuffer buf) {
		buf.writeString(model.preferred);
	}
	
	public static MessageUpdateModel decode(PacketBuffer buffer) {
		return new MessageUpdateModel(buffer.readString(10));
	}
	
	public static class Handler {
		public static void handle(MessageUpdateModel message, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().getSender().getServer().runAsync(() -> CapabilityRegeneration.getForPlayer(ctx.get().getSender()).ifPresent((cap) -> {
				cap.setPreferredModel(message.preferred);
				cap.synchronise();
			}));
			ctx.get().setPacketHandled(true);
		}
	}
}
