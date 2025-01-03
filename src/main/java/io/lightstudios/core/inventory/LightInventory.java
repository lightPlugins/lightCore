package io.lightstudios.core.inventory;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import io.lightstudios.core.LightCore;
import io.lightstudios.core.actions.ActionHandler;
import io.lightstudios.core.inventory.constructor.InventoryConstructor;
import io.lightstudios.core.inventory.handler.ClickItemHandler;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LightInventory {

    private final ChestGui gui = new ChestGui(6, "Init");
    private final InventoryConstructor inventoryConstructor;
    private final Player player;

    private final int refreshRate;
    private BukkitTask bukkitTask;
    private final int cooldownTime;
    private final List<Player> clickCooldown = new ArrayList<>();

    @Setter @Getter
    private StaticPane staticPane;
    private PatternPane patternPane;


    public LightInventory(InventoryConstructor invConstructor, Player player) {

        this.inventoryConstructor = invConstructor;
        this.player = player;

        this.cooldownTime = invConstructor.getClickCooldownTime();
        this.refreshRate = invConstructor.getRefreshRate();

    }

    public void openInventory() {
        // set the rows, that the gui should have
        gui.setRows(this.inventoryConstructor.getRows());
        // set the title of the gui with color + placeholder translation
        gui.setTitle(LightCore.instance.getColorTranslation().adventureTranslator(
                this.inventoryConstructor.getGuiTitle(), this.player));
        // disable the global click event
        gui.setOnGlobalClick(event -> event.setCancelled(true));

        // enable the refresh task
        bukkitTask = Bukkit.getScheduler().runTaskTimer(LightCore.instance, this::refresh, 10, refreshRate);
        // cancel the task when the player closes the gui
        gui.setOnClose(event -> {
            if(bukkitTask != null) {
                bukkitTask.cancel();
            }
        });

        // add the config pattern to the gui
        gui.addPane(this.patternPane);
        // add an extra pane for items -> external
        if(staticPane != null) {
            gui.addPane(this.staticPane);
        }

        // finally, open the gui for the player
        gui.show(this.player);
    }

    public void refresh() {
        this.gui.getPanes().forEach(Pane::clear);

        gui.addPane(this.patternPane);
        gui.addPane(this.staticPane);

        gui.update();
    }

    private void setPatternPane() {

        String[] patternList = this.inventoryConstructor.getPattern().toArray(new String[0]);
        Pattern pattern = new Pattern(patternList);
        PatternPane patternPane = new PatternPane(0, 0, 9, this.inventoryConstructor.getRows(), pattern);

        for(String patternIdentifier : this.inventoryConstructor.getClickItemHandlersSection().getKeys(false)) {

            if(this.inventoryConstructor.getClickItemHandlersSection() == null) {
                return;
            }

            ClickItemHandler clickItemHandler = new ClickItemHandler(
                    Objects.requireNonNull(this.inventoryConstructor.getClickItemHandlersSection()
                            .getConfigurationSection(patternIdentifier)), this.player);

            ItemStack itemStack = clickItemHandler.getGuiItem();

            patternPane.bindItem(patternIdentifier.charAt(0), new GuiItem(itemStack,
                    inventoryClickEvent -> {

                if(!inventoryClickEvent.isLeftClick()) {
                    return;
                }

                // AntiSpam protection for general actions
                if(clickCooldown.contains(player)) {
                    return;
                }

                Bukkit.getScheduler().runTaskLater(LightCore.instance, () -> {
                    clickCooldown.remove(player);
                }, this.cooldownTime);

                // execute the actions from the clickItemHandler -> file
                clickItemHandler.getActionHandlers().forEach(ActionHandler::handleAction);
                clickCooldown.add(player);
            }));
        }

        this.patternPane = patternPane;
    }
}
