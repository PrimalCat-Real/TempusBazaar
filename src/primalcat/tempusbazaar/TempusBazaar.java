package primalcat.tempusbazaar;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import primalcat.tempusbazaar.category.Category;
import primalcat.tempusbazaar.category.atributes.Product;
import primalcat.tempusbazaar.commands.*;
import primalcat.tempusbazaar.gui.GuiType;
import primalcat.tempusbazaar.serializers.JsonUpdater;
import primalcat.tempusbazaar.trades.ProductFinder;
import primalcat.tempusbazaar.utils.ConfigUtil;
import primalcat.tempusbazaar.utils.DisplayUtil;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.logging.Logger;

public class TempusBazaar extends JavaPlugin {
    private static Plugin plugin;
    private static File dataFolder;
    public static Logger logger;
    public static @NotNull FileConfiguration configPath;

    public static File getFolder() {
        return dataFolder;
    }

    public static Plugin getPlugin() {
        return plugin;
    }


    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        dataFolder = this.getDataFolder();
        configPath = this.getConfig();
        logger = this.getLogger();

        ConfigUtil.readConfigValues(configPath);

        // init
        ConfigUtil.createJsonFile(dataFolder, "buyer.json");
        ConfigUtil.createJsonFile(dataFolder, "gunsmith.json");
        ConfigUtil.createJsonFile(dataFolder, "shelter_barman.json");

        // @TODO remove this
        getCommand("bazaarrarity").setExecutor(new TestRarityCommand());

        getCommand("bazaaropen").setExecutor(new OpenGuiCommand());
        getCommand("bazaarsave").setExecutor(new SaveGuiCommand());
        getCommand("bazaarreload").setExecutor(new ReloadGuiCommand());
        getCommand("bazaarreroll").setExecutor(new ReRollGuiCommand());

        // Запуск асинхронного таймера
        scheduleUpdate();

        scheduleDailyTask(this);
    }


    public static void scheduleUpdate() {
        List<Category> categories = GuiType.getCategories(GuiType.BUYER);
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimerAsynchronously(plugin, () -> {
            Random random = new Random();
            if (random.nextInt(100) > 90) {  // 10% вероятность на выполнение
                ProductFinder.findTopLowestAmountProducts(categories);
                ProductFinder.findTopHighestPricedProducts(categories);
            }
            if (random.nextInt(100) > 90) {  // 10% вероятность на выполнение
                ProductFinder.findTopHighestAmountProducts(categories);
                ProductFinder.findTopLowestPricedProducts(categories);
            }
            if (random.nextInt(100) > 95) {  // 5% вероятность на выполнение пересортировки категорий
                GuiType.updateRandomBuyerCategories();
                getPlugin().getLogger().info("Buyer Categories have been rerolled.");
            }

            saveAllCategories();
//            JsonUpdater.saveCurrentValuesToFile(TempusBazaar.getPlugin().getDataFolder(), GuiType.BUYER.name().toLowerCase() + ".json", GuiType.getCategories(GuiType.BUYER));
//            ConfigUtil.saveCategoriesToFile(getPlugin().getDataFolder(), GuiType.BUYER.name().toLowerCase() + ".json", GuiType.getCategories(GuiType.BUYER));

        }, 0L, 20L * 60L * 20L);  // Задержка 10 минут (20 тиков * 60 секунд * 10 минут)
    }

    public static void scheduleDailyTask(JavaPlugin plugin) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT")); // используйте временную зону вашего сервера
        calendar.set(Calendar.HOUR_OF_DAY, 19); // 19:00
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long delay = calendar.getTimeInMillis() - System.currentTimeMillis();
        if (delay < 0) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            delay = calendar.getTimeInMillis() - System.currentTimeMillis();
        }

        long period = 24 * 60 * 60 * 1000; // 24 часа в миллисекундах

        Bukkit.getScheduler().runTaskTimer(plugin, GuiType::restoreSellerProducts, delay / 50, period / 50);
    }

    @Override
    public void onDisable() {
        try {
            saveAllCategories();
            logger.info("Categories successfully saved on plugin disable.");
        }catch (Exception e) {
            System.err.println("Failed to save categories on plugin disable: " + e.getMessage());
            e.printStackTrace();
        }
        Bukkit.getScheduler().cancelTasks(this);
    }

    public static void saveAllCategories() {
        try {
            for (GuiType guiType : GuiType.values()) {
                JsonUpdater.saveCurrentValuesToFile(plugin.getDataFolder(), guiType.name().toLowerCase() + ".json", GuiType.getCategories(guiType));
                Bukkit.getLogger().info(guiType.name() + " categories saved automatically.");
            }
        }catch (Exception e){
            TempusBazaar.logger.warning("Error while saving data: " + e.getMessage());
        }
    }
}

