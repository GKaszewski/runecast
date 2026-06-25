package dev.gabrielkaszewski.runecast.events.init;

import dev.gabrielkaszewski.runecast.effect.*;
import dev.gabrielkaszewski.runecast.packet.EnchantParticlePacket;
import dev.gabrielkaszewski.runecast.recipe.RecipeRegistry;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.event.entity.player.PlayerEvent;
import net.modificationstation.stationapi.api.event.mod.InitEvent;
import net.modificationstation.stationapi.api.event.network.packet.PacketRegisterEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.EntrypointManager;
import net.modificationstation.stationapi.api.registry.PacketTypeRegistry;
import net.modificationstation.stationapi.api.registry.Registry;
import net.modificationstation.stationapi.api.util.Namespace;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.List;

public class InitListener {
    static {
        EntrypointManager.registerLookup(MethodHandles.lookup());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static final Namespace NAMESPACE = Namespace.resolve();
    public static final Logger LOGGER = NAMESPACE.getLogger();

    @EventListener
    private static void init(InitEvent event) {
        // Register attribute applicators
        EffectRegistry.registerMining(MiningApplicator.ATTRIBUTE_ID, MiningApplicator.DEFAULT);
        EffectRegistry.registerAttack(AttackApplicator.ATTRIBUTE_ID, AttackApplicator.DEFAULT);
        EffectRegistry.registerFall(FallApplicator.ATTRIBUTE_ID, FallApplicator.DEFAULT);
        EffectRegistry.registerDurability(DurabilityApplicator.ATTRIBUTE_ID, DurabilityApplicator.DEFAULT);
        EffectRegistry.registerKnockback(KnockbackApplicator.ATTRIBUTE_ID, KnockbackApplicator.DEFAULT);
        EffectRegistry.registerReach(ReachApplicator.ATTRIBUTE_ID, ReachApplicator.DEFAULT);
        EffectRegistry.registerLoot(LootApplicator.ATTRIBUTE_ID, LootApplicator.DEFAULT);
        EffectRegistry.registerCustomMining(CustomMiningEffect.UNDERWATER_MINING_BOOST_ID, CustomMiningEffect.UNDERWATER_MINING_BOOST);

        // Load recipes + effect definitions from JSON
        List<EffectDefinition> defs = RecipeRegistry.load();
        EffectRegistry.loadDefinitions(defs);
        LOGGER.info("Runecast: loaded {} recipes", RecipeRegistry.getAll().size());
    }

    @EventListener
    private static void onPlayerReach(PlayerEvent.Reach event) {
        ItemStack held = event.player.inventory.getSelectedItem();
        if (held == null) return;
        float bonus = EffectRegistry.computeReachBonus(held);
        if (bonus > 0) event.currentReach += bonus;
    }

    @EventListener
    private static void registerPackets(PacketRegisterEvent event) {
        Registry.register(PacketTypeRegistry.INSTANCE,
            NAMESPACE.id(EnchantParticlePacket.PACKET_ID), EnchantParticlePacket.TYPE);
    }
}
