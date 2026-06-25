package dev.gabrielkaszewski.runecast.events.init;

import dev.gabrielkaszewski.runecast.nbt.EnchantmentNbt;
import dev.gabrielkaszewski.runecast.recipe.RecipeRegistry;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.client.event.gui.screen.container.TooltipBuildEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.EntrypointManager;
import net.modificationstation.stationapi.api.util.Namespace;

import java.lang.invoke.MethodHandles;
import java.util.Map;

public class ClientListener {
    static {
        EntrypointManager.registerLookup(MethodHandles.lookup());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static final Namespace NAMESPACE = Namespace.resolve();

    private static final String[] ROMAN = {"", "I", "II", "III", "IV", "V"};

    @EventListener
    private static void onTooltipBuild(TooltipBuildEvent event) {
        Map<String, Integer> enchantments = EnchantmentNbt.getEnchantments(event.itemStack);
        for (Map.Entry<String, Integer> e : enchantments.entrySet()) {
            String name = RecipeRegistry.getDisplayName(e.getKey());
            if (name == null) name = e.getKey();
            int level = e.getValue();
            String roman = (level >= 1 && level <= 5) ? ROMAN[level] : String.valueOf(level);
            event.add("§9" + name + " " + roman);
        }
    }
}
