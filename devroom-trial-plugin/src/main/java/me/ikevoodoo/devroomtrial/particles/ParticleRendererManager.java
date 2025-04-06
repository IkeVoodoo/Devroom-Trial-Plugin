package me.ikevoodoo.devroomtrial.particles;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ParticleRendererManager {

    private final Map<UUID, SelectionParticleRenderer> renderers = new HashMap<>();
    private final Plugin plugin;

    public ParticleRendererManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void startRenderer(@NotNull final Player player) {
        final var uuid = player.getUniqueId();
        final var result = this.renderers.computeIfAbsent(uuid, this::createParticleRenderer);

        Bukkit.getScheduler().runTaskTimer(
                this.plugin,
                result,
                10,
                10
        );
    }

    private SelectionParticleRenderer createParticleRenderer(UUID uuid) {
        return new SelectionParticleRenderer(
                uuid,
                new ParticleSpawnInfo(
                        Particle.DUST,
                        new Particle.DustOptions(
                                Color.GREEN,
                                1f
                        ),
                        1,
                        0.5d
                ),
                this.renderers::remove
        );
    }

}
