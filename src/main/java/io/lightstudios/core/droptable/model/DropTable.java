package io.lightstudios.core.droptable.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@Getter
@Setter
public class DropTable {

    private Map<String, Drops> dropsList;
    private String dropTableID;

    @Getter
    @Setter
    public static class Drops {

        private String dropsID;
        private String chance;
        private double chanceAsDouble;
        private VanillaItem vanillaItem;
        private NexoItem nexoItem;
        private MMOItemsItem mmoItemsItem;
        private Actions actions;

        /**
         * Validates the Drops object to ensure only one type of item is set.
         * @return true if the Drops object is valid, false otherwise.
         */
        public boolean isValid() {
            int itemCount = 0;
            if (vanillaItem != null) itemCount++;
            if (nexoItem != null) itemCount++;
            if (mmoItemsItem != null) itemCount++;

            // Ensure that only one item type is set
            return itemCount == 1;
        }


        @Setter
        @Getter
        @EqualsAndHashCode
        public static class VanillaItem {

            private String vanillaItemBuilder;
            private int amountMin;
            private int amountMax;
            private ItemSettings itemSettings;
            @EqualsAndHashCode.Exclude
            private ItemStack itemStack;
        }

        @Setter
        @Getter
        @EqualsAndHashCode
        public static class NexoItem {

            private String nexoID;
            private int amountMin;
            private int amountMax;
            private ItemSettings itemSettings;
            @EqualsAndHashCode.Exclude
            private ItemStack itemStack;
        }

        @Setter
        @Getter
        @EqualsAndHashCode
        public static class MMOItemsItem {

            private String mmoItem;
            private int amountMin;
            private int amountMax;
            private ItemSettings itemSettings;
            @EqualsAndHashCode.Exclude
            private ItemStack itemStack;
        }
    }

    @Getter
    @Setter
    public static class ItemSettings {

        private boolean directDrop;
        private boolean pickUpOnlyOwner;
        private boolean enableGlow;
        private TextColor glowColor;
    }

    @Getter
    @Setter
    public static class Actions {

        private Message message;
        private Title title;
        private Sound sound;

        @Getter
        @Setter
        public static class Message {

            private Component message;
        }

        @Getter
        @Setter
        public static class Title {

            private Component upperTitle;
            private Component lowerTitle;
            private int fadeIn;
            private int stay;
            private int fadeOut;
            private net.kyori.adventure.title.Title title;
        }

        @Getter
        @Setter
        public static class Sound {

            private org.bukkit.Sound sound;
            private float volume;
            private float pitch;
        }
    }
}
