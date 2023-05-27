package cc.mewcraft.adventurelevel.file;

import cc.mewcraft.adventurelevel.AdventureLevel;
import cc.mewcraft.adventurelevel.data.PlayerData;
import cc.mewcraft.adventurelevel.data.RealPlayerData;
import cc.mewcraft.adventurelevel.level.LevelBeanFactory;
import cc.mewcraft.adventurelevel.level.category.LevelBean;
import cc.mewcraft.adventurelevel.level.category.MainLevelBean;
import com.google.inject.Inject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.lucko.helper.function.chain.Chain;
import me.lucko.helper.promise.Promise;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.*;

import static java.util.Objects.requireNonNull;

public class SQLDataStorage extends AbstractDataStorage {

    // SQL config
    private final @MonotonicNonNull String host;
    private final @MonotonicNonNull String port;
    private final @MonotonicNonNull String database;
    private final @MonotonicNonNull String username;
    private final @MonotonicNonNull String password;
    private final @MonotonicNonNull String prefix;

    // Table names
    private final @MonotonicNonNull String userdataTable;

    // SQL queries
    private final @MonotonicNonNull String updateUserdataQuery;
    private final @MonotonicNonNull String insertUserdataQuery;
    private final @MonotonicNonNull String selectUserdataQuery;

    // Hikari instances
    private @MonotonicNonNull HikariDataSource hikari;

    @Inject
    public SQLDataStorage(final AdventureLevel plugin) {
        super(plugin);

        this.host = requireNonNull(plugin.getConfig().getString("mysql.host"));
        this.port = requireNonNull(plugin.getConfig().getString("mysql.port"));
        this.database = requireNonNull(plugin.getConfig().getString("mysql.database"));
        this.username = requireNonNull(plugin.getConfig().getString("mysql.username"));
        this.password = requireNonNull(plugin.getConfig().getString("mysql.password"));
        this.prefix = requireNonNull(plugin.getConfig().getString("mysql.prefix"));

        this.userdataTable = prefix + "_userdata";

        this.updateUserdataQuery =
            "INSERT INTO `" + userdataTable + " " +
            "(`uuid`, `name`, `main_exp`, `player_death_exp`, `entity_death_exp`, `furnace_exp`, `breed_exp`, `villager_trade_exp`, `fishing_exp`, `block_break_exp`, `exp_bottle_exp`, `grindstone_exp`) " +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
            "`uuid` = VALUES(`uuid`), " +
            "`name` = VALUES(`name`), " +
            "`main_exp` = VALUES(`main_exp`), " +
            "`player_death_exp` = VALUES(`player_death_exp`), " +
            "`furnace_exp` = VALUES(`furnace_exp`)" +
            "`breed_exp` = VALUES(`breed_exp`)" +
            "`villager_trade_exp` = VALUES(`villager_trade_exp`)" +
            "`fishing_exp` = VALUES(`fishing_exp`)" +
            "`block_break_exp` = VALUES(`block_break_exp`)" +
            "`exp_bottle_exp` = VALUES(`exp_bottle_exp`)" +
            "`grindstone_exp` = VALUES(`grindstone_exp`);";
        this.insertUserdataQuery =
            "INSERT INTO `" + userdataTable + " " +
            "(`uuid`, `name`) " +
            "VALUES(?, ?);";
        this.selectUserdataQuery =
            "SELECT * FROM " + userdataTable + " WHERE uuid = ?;";
    }

    private void setupTables(Connection conn) throws SQLException {
        try (
            PreparedStatement stmt1 = conn.prepareStatement(
                "CREATE TABLE IF NOT EXISTS " +
                this.userdataTable + " (" +
                "`uuid` varchar(36) NOT NULL PRIMARY KEY, " +
                "`name` varchar(16), " +
                "`main_exp` int(11) DEFAULT 0, " +
                "`player_death_exp` int(11) DEFAULT 0, " +
                "`furnace_exp` int(11) DEFAULT 0, " +
                "`breed_exp` int(11) DEFAULT 0, " +
                "`villager_trade_exp` int(11) DEFAULT 0, " +
                "`fishing_exp` int(11) DEFAULT 0, " +
                "`block_break_exp` int(11) DEFAULT 0, " +
                "`exp_bottle_exp` int(11) DEFAULT 0, " +
                "`grindstone_exp` int(11) DEFAULT 0 " +
                ");"
            )
        ) {
            stmt1.execute();
        }
    }

    @Override public void init() {
        // Setup hikari config
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?allowPublicKeyRetrieval=true&useSSL=false");
        hikariConfig.setPassword(password);
        hikariConfig.setUsername(username);
        hikariConfig.setMaxLifetime(1500000);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("userServerPrepStmts", "true");

        // Establish connection & Create tables if needed
        hikari = new HikariDataSource(hikariConfig);
        try (Connection conn = hikari.getConnection()) {
            this.setupTables(conn);

            //region Perform possible data migration
            Map<String, List<String>> structure = new HashMap<>();
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet tableResultSet = metaData.getTables(database, "public", null, new String[]{"TABLE"})) {
                while (tableResultSet.next()) {
                    String tableCat = tableResultSet.getString("TABLE_CAT");
                    String tableName = tableResultSet.getString("TABLE_NAME");
                    plugin.getSLF4JLogger().debug("Table catalog: %s, Table name: %s".formatted(tableCat, tableName));

                    if (tableName.startsWith(prefix)) {
                        structure.put(tableName, new ArrayList<>());
                        plugin.getSLF4JLogger().debug("Added table: " + tableName);
                    }
                }
            }

            for (String table : structure.keySet()) {
                try (ResultSet columnResultSet = metaData.getColumns(database, "public", table, null)) {
                    while (columnResultSet.next()) {
                        String columnName = columnResultSet.getString("COLUMN_NAME");
                        structure.get(table).add(columnName);
                        plugin.getSLF4JLogger().debug("Added column: " + columnName + " (" + table + ")");
                    }
                }
            }
            //endregion
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override public void close() {
        if (hikari != null) {
            hikari.close();
        }
    }

    @Override public @NotNull Promise<PlayerData> create(final UUID uuid) {
        return Promise.supplyingAsync(() -> {
            try (
                Connection conn = hikari.getConnection();
                PreparedStatement stmt = conn.prepareStatement(insertUserdataQuery)
            ) {
                // Construct Main Level bean
                MainLevelBean mainLevelBean = LevelBeanFactory.createMainLevelBean(plugin);

                // Construct Categorical Level beans
                Map<LevelBean.Category, LevelBean> subLevelMap = new HashMap<>() {{
                    this.put(LevelBean.Category.PLAYER_DEATH, LevelBeanFactory.createCatLevelBean(plugin, LevelBean.Category.PLAYER_DEATH));
                    this.put(LevelBean.Category.ENTITY_DEATH, LevelBeanFactory.createCatLevelBean(plugin, LevelBean.Category.ENTITY_DEATH));
                    this.put(LevelBean.Category.FURNACE, LevelBeanFactory.createCatLevelBean(plugin, LevelBean.Category.FURNACE));
                    this.put(LevelBean.Category.BREED, LevelBeanFactory.createCatLevelBean(plugin, LevelBean.Category.BREED));
                    this.put(LevelBean.Category.VILLAGER_TRADE, LevelBeanFactory.createCatLevelBean(plugin, LevelBean.Category.VILLAGER_TRADE));
                    this.put(LevelBean.Category.FISHING, LevelBeanFactory.createCatLevelBean(plugin, LevelBean.Category.FISHING));
                    this.put(LevelBean.Category.BLOCK_BREAK, LevelBeanFactory.createCatLevelBean(plugin, LevelBean.Category.BLOCK_BREAK));
                    this.put(LevelBean.Category.EXP_BOTTLE, LevelBeanFactory.createCatLevelBean(plugin, LevelBean.Category.EXP_BOTTLE));
                    this.put(LevelBean.Category.GRINDSTONE, LevelBeanFactory.createCatLevelBean(plugin, LevelBean.Category.GRINDSTONE));
                }};

                PlayerData playerData = new RealPlayerData(plugin, uuid, mainLevelBean, subLevelMap);

                stmt.setString(1, uuid.toString());
                stmt.setString(2, Chain.start(playerData.getUuid()).map(Bukkit::getPlayer).map(Player::getName).endOrNull());
                stmt.execute();

                return playerData;
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return PlayerData.DUMMY;
        });
    }

    @Override public @NotNull Promise<PlayerData> load(final UUID uuid) {
        return Promise.supplyingAsync(() -> {
            try (PreparedStatement stmt = hikari.getConnection().prepareStatement(selectUserdataQuery)) {
                // Note: string comparisons are case-insensitive by default in the configuration of SQL server database
                stmt.setString(1, uuid.toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Read values from the query results
                        int mainXp = rs.getInt(3);
                        int playerDeathXp = rs.getInt(4);
                        int entityDeathXp = rs.getInt(5);
                        int furnaceXp = rs.getInt(6);
                        int breedXp = rs.getInt(7);
                        int villagerTradeXp = rs.getInt(8);
                        int fishingXp = rs.getInt(9);
                        int blockBreakXp = rs.getInt(10);
                        int expBottleXp = rs.getInt(11);
                        int grindstoneXp = rs.getInt(12);

                        // Construct Main Level bean
                        MainLevelBean mainLevelBean = LevelBeanFactory.createMainLevelBean(plugin);
                        mainLevelBean.setExperience(mainXp);

                        // Construct Categorical Level beans
                        Map<LevelBean.Category, LevelBean> subLevelMap = new HashMap<>() {{
                            LevelBean playerDeathLvl = LevelBeanFactory.createCatLevelBean(plugin, LevelBean.Category.PLAYER_DEATH);
                            LevelBean entityDeathLvl = LevelBeanFactory.createCatLevelBean(plugin, LevelBean.Category.ENTITY_DEATH);
                            LevelBean furnaceLvl = LevelBeanFactory.createCatLevelBean(plugin, LevelBean.Category.FURNACE);
                            LevelBean breedLvl = LevelBeanFactory.createCatLevelBean(plugin, LevelBean.Category.BREED);
                            LevelBean villagerTradeLvl = LevelBeanFactory.createCatLevelBean(plugin, LevelBean.Category.VILLAGER_TRADE);
                            LevelBean fishingLvl = LevelBeanFactory.createCatLevelBean(plugin, LevelBean.Category.FISHING);
                            LevelBean blockBreakLvl = LevelBeanFactory.createCatLevelBean(plugin, LevelBean.Category.BLOCK_BREAK);
                            LevelBean expBottleLvl = LevelBeanFactory.createCatLevelBean(plugin, LevelBean.Category.EXP_BOTTLE);
                            LevelBean grindstoneLvl = LevelBeanFactory.createCatLevelBean(plugin, LevelBean.Category.GRINDSTONE);

                            playerDeathLvl.setExperience(playerDeathXp);
                            entityDeathLvl.setExperience(entityDeathXp);
                            furnaceLvl.setExperience(furnaceXp);
                            breedLvl.setExperience(breedXp);
                            villagerTradeLvl.setExperience(villagerTradeXp);
                            fishingLvl.setExperience(fishingXp);
                            blockBreakLvl.setExperience(blockBreakXp);
                            expBottleLvl.setExperience(expBottleXp);
                            grindstoneLvl.setExperience(grindstoneXp);

                            this.put(LevelBean.Category.PLAYER_DEATH, playerDeathLvl);
                            this.put(LevelBean.Category.ENTITY_DEATH, entityDeathLvl);
                            this.put(LevelBean.Category.FURNACE, furnaceLvl);
                            this.put(LevelBean.Category.BREED, breedLvl);
                            this.put(LevelBean.Category.VILLAGER_TRADE, villagerTradeLvl);
                            this.put(LevelBean.Category.FISHING, fishingLvl);
                            this.put(LevelBean.Category.BLOCK_BREAK, blockBreakLvl);
                            this.put(LevelBean.Category.EXP_BOTTLE, expBottleLvl);
                            this.put(LevelBean.Category.GRINDSTONE, grindstoneLvl);
                        }};

                        return new RealPlayerData(plugin, uuid, mainLevelBean, subLevelMap);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return PlayerData.DUMMY;
        });
    }

    @Override public void save(final PlayerData playerData) {
        Promise.start().thenRunAsync(() -> {
            if (playerData.equals(PlayerData.DUMMY))
                return;

            try (
                Connection conn = hikari.getConnection();
                PreparedStatement stmt = conn.prepareStatement(updateUserdataQuery)
            ) {
                stmt.setString(1, playerData.getUuid().toString());
                stmt.setString(2, Chain.start(playerData.getUuid()).map(Bukkit::getPlayer).map(Player::getName).endOrNull());
                stmt.setInt(3, playerData.getMainLevel().getExperience());
                stmt.setInt(4, playerData.getCateLevel(LevelBean.Category.PLAYER_DEATH).getExperience());
                stmt.setInt(5, playerData.getCateLevel(LevelBean.Category.ENTITY_DEATH).getExperience());
                stmt.setInt(6, playerData.getCateLevel(LevelBean.Category.FURNACE).getExperience());
                stmt.setInt(7, playerData.getCateLevel(LevelBean.Category.BREED).getExperience());
                stmt.setInt(8, playerData.getCateLevel(LevelBean.Category.VILLAGER_TRADE).getExperience());
                stmt.setInt(9, playerData.getCateLevel(LevelBean.Category.FISHING).getExperience());
                stmt.setInt(10, playerData.getCateLevel(LevelBean.Category.BLOCK_BREAK).getExperience());
                stmt.setInt(11, playerData.getCateLevel(LevelBean.Category.EXP_BOTTLE).getExperience());
                stmt.setInt(12, playerData.getCateLevel(LevelBean.Category.GRINDSTONE).getExperience());
                stmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
