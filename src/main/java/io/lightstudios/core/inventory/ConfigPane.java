package io.lightstudios.core.inventory;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import io.lightstudios.core.actions.ActionHandler;
import io.lightstudios.core.inventory.constructor.InventoryConstructor;
import io.lightstudios.core.inventory.handler.ClickItemHandler;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

@Getter
public class ConfigPane {

    public PatternPane patternPane(InventoryConstructor invConstructor, Player player) {

        String[] patternList = invConstructor.getPattern().toArray(new String[0]);
        Pattern pattern = new Pattern(patternList);
        PatternPane patternPane = new PatternPane(0, 0, 9, invConstructor.getRows(), pattern);

        for(String patternIdentifier : invConstructor.getClickItemHandlersSection().getKeys(false)) {

            ClickItemHandler clickItemHandler = new ClickItemHandler(
                    Objects.requireNonNull(invConstructor.getClickItemHandlersSection().getConfigurationSection(
                            patternIdentifier)), player);

            ItemStack itemStack = clickItemHandler.getGuiItem();

            patternPane.bindItem(patternIdentifier.charAt(0), new GuiItem(itemStack, inventoryClickEvent -> {

                if(!inventoryClickEvent.isLeftClick()) {
                    return;
                }
                // execute the actions from the clickItemHandler -> file
                clickItemHandler.getActionHandlers().forEach(ActionHandler::handleAction);
            }));
        }

        return patternPane;

    }
}
