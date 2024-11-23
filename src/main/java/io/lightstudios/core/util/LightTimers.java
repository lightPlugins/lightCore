package io.lightstudios.core.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class LightTimers {

    private static JavaPlugin plugin;

    public LightTimers(JavaPlugin plugin) {
        LightTimers.plugin = plugin;
    }

    public static void startTask(Consumer<BukkitTask> taskConsumer, long delay, long period) {
        BukkitTask[] task = new BukkitTask[1];
        task[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> taskConsumer.accept(task[0]), delay, period);
    }

    public static void startTaskWithCounter(BiConsumer<BukkitTask, Integer> taskConsumer, long delay, long period) {
        AtomicInteger counter = new AtomicInteger();
        BukkitTask[] task = new BukkitTask[1];
        task[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> taskConsumer.accept(task[0], counter.incrementAndGet()), delay, period);
    }

    public static void startTaskAsync(Consumer<BukkitTask> taskConsumer, long delay, long period) {
        BukkitTask[] task = new BukkitTask[1];
        task[0] = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> taskConsumer.accept(task[0]), delay, period);
    }

    public static void startTaskWithCounterAsync(BiConsumer<BukkitTask, Integer> taskConsumer, long delay, long period) {
        AtomicInteger counter = new AtomicInteger();
        BukkitTask[] task = new BukkitTask[1];
        task[0] = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> taskConsumer.accept(task[0], counter.incrementAndGet()), delay, period);
    }

    public static void doSync(Consumer<BukkitTask> taskConsumer, long delay) {
        BukkitTask[] task = new BukkitTask[1];
        task[0] = Bukkit.getScheduler().runTaskLater(plugin, () -> taskConsumer.accept(task[0]), delay);
    }

    public static void doAsync(Consumer<BukkitTask> taskConsumer, long delay) {
        BukkitTask[] task = new BukkitTask[1];
        task[0] = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> taskConsumer.accept(task[0]), delay);
    }
}