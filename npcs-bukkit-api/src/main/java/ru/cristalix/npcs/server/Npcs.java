package ru.cristalix.npcs.server;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent;
import com.google.gson.Gson;
import dev.xdark.feder.NetUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.SneakyThrows;
import lombok.val;
import net.minecraft.server.v1_12_R1.PacketDataSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutCustomPayload;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import ru.cristalix.core.display.DisplayChannels;
import ru.cristalix.core.display.messages.Mod;
import ru.cristalix.npcs.data.NpcData;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class Npcs implements Listener, PluginMessageListener {

	private static final Gson gson = new Gson();
	private static boolean initialized = false;
	private static Plugin plugin;

	private static final Set<Npc> globalNpcs = new HashSet<>();
	private static final ByteBuf mod = Unpooled.buffer();

	private static final Set<Player> active = new HashSet<>();
	private static final Set<Player> clickCooldowns = new HashSet<>();

	@SneakyThrows
	public static void init(Plugin plugin) {
		if (initialized) {
			plugin.getLogger().log(Level.WARNING, "Unable to initialize NPCs because already initialized!",
					new IllegalStateException());
			return;
		}

		Npcs.plugin = plugin;

		Npcs listener = new Npcs();
		Bukkit.getPluginManager().registerEvents(listener, plugin);
		Bukkit.getMessenger().registerIncomingPluginChannel(plugin, "npcs:loaded", listener);

		InputStream resource = plugin.getResource("npcs-client-mod.jar");
		byte[] serialize = IOUtils.readFully(resource, resource.available());
		mod.writeBytes(Mod.serialize(new Mod(serialize)));

		initialized = true;
	}

	public static void spawn(Npc npc) {
		if (globalNpcs.add(npc)) {
			for (Player player : npc.getLocation().getWorld().getPlayers()) {
				show(npc, player);
			}
		}
	}

	public static void hide(Npc npc) {
		if (globalNpcs.remove(npc)) {

			ByteBuf buf = Unpooled.buffer();
			buf.writeInt(npc.getId());

			for (Player player : active) {
				val packet = new PacketPlayOutCustomPayload("npcs:hide", new PacketDataSerializer(buf.retainedSlice()));
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
			}

		}
	}

	public static void show(Npc npc, Player player) {
		ByteBuf internalCachedData = npc.getInternalCachedData();
		if (internalCachedData == null) {
			NpcData data = npc.getData();
			String json = gson.toJson(data);
			ByteBuf buffer = Unpooled.buffer();
			NetUtil.writeUtf8(json, buffer);
			npc.setInternalCachedData(buffer);
		}

		val packet = new PacketPlayOutCustomPayload("npcs", new PacketDataSerializer(npc.getInternalCachedData().retainedSlice()));

		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	@EventHandler
	public void handleJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PacketDataSerializer serializer = new PacketDataSerializer(mod.retainedSlice());
		PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload(DisplayChannels.MOD_CHANNEL, serializer);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	@EventHandler
	public void handleQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		active.remove(player);
		clickCooldowns.remove(player);
	}

	@EventHandler
	public void handleEntityAdd(EntityAddToWorldEvent event) {
		if (!(event.getEntity() instanceof CraftPlayer)) {
			return;
		}

		CraftPlayer player = (CraftPlayer) event.getEntity();
		if (active.contains(player)) {
			for (Npc npcs : globalNpcs) {
				if (npcs.getLocation().getWorld() == event.entity.getWorld()) {
					show(npcs, player);
				}
			}
		}
	}

	@EventHandler
	public void handleNpcInteract(PlayerUseUnknownEntityEvent event) {
		if (event.getHand() == EquipmentSlot.HAND) {
			Player player = event.getPlayer();
			for (Npc npc : globalNpcs) {
				if (npc.getId() == event.getEntityId()) {
					if (!clickCooldowns.contains(player) && npc.getOnClick() != null) {
						clickCooldowns.add(player);
						Bukkit.getScheduler().runTask(plugin, () -> {
							clickCooldowns.remove(player);
						});
						npc.getOnClick().accept(player);
					}
					break;
				}
			}
		}
	}


	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
		active.add(player);

		for (Npc npc : globalNpcs) {
			if (npc.getLocation().getWorld() == player.getWorld()) {
				show(npc, player);
			}
		}
	}

}
