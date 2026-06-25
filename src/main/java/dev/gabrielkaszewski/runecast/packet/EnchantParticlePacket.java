package dev.gabrielkaszewski.runecast.packet;

import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import net.modificationstation.stationapi.api.entity.player.PlayerHelper;
import net.modificationstation.stationapi.api.network.packet.ManagedPacket;
import net.modificationstation.stationapi.api.network.packet.PacketType;
import net.modificationstation.stationapi.api.util.SideUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EnchantParticlePacket extends Packet implements ManagedPacket<EnchantParticlePacket> {
    public static final String PACKET_ID = "enchant_particles";
    public static final PacketType<EnchantParticlePacket> TYPE =
            PacketType.builder(true, false, EnchantParticlePacket::new).build();

    private double x, y, z;

    public EnchantParticlePacket() {}

    public EnchantParticlePacket(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void read(DataInputStream in) {
        try {
            x = in.readDouble();
            y = in.readDouble();
            z = in.readDouble();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(DataOutputStream out) {
        try {
            out.writeDouble(x);
            out.writeDouble(y);
            out.writeDouble(z);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void apply(NetworkHandler handler) {
        SideUtil.run(() -> {
            var player = PlayerHelper.getPlayerFromPacketHandler(handler);
            if (player == null) return;
            ParticleEmitter.spawnEnchantParticles(player.world, x, y, z);
        }, null);
    }

    @Override
    public int size() {
        return 24; // 3 doubles × 8 bytes
    }

    @Override
    public PacketType<EnchantParticlePacket> getType() {
        return TYPE;
    }
}
