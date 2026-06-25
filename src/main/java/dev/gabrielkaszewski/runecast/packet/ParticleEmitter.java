package dev.gabrielkaszewski.runecast.packet;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;

public final class ParticleEmitter {
    private static final int PARTICLE_COUNT = 12;

    private ParticleEmitter() {}

    @Environment(EnvType.CLIENT)
    public static void spawnEnchantParticles(World world, double x, double y, double z) {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double ox = (world.random.nextFloat() - 0.5) * 2.0;
            double oy = world.random.nextFloat() * 1.5;
            double oz = (world.random.nextFloat() - 0.5) * 2.0;
            double vx = (world.random.nextFloat() - 0.5) * 0.3;
            double vy = world.random.nextFloat() * 0.3;
            double vz = (world.random.nextFloat() - 0.5) * 0.3;
            world.addParticle("enchantmenttable", x + ox, y + oy, z + oz, vx, vy, vz);
        }
    }
}
