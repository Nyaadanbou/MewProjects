package cc.mewcraft.adventurelevel.file;

import cc.mewcraft.adventurelevel.AdventureLevelPlugin;
import cc.mewcraft.adventurelevel.data.PlayerData;
import cc.mewcraft.adventurelevel.data.RealPlayerData;
import cc.mewcraft.adventurelevel.level.LevelBeanFactory;
import cc.mewcraft.adventurelevel.level.category.LevelBean;
import cc.mewcraft.adventurelevel.level.category.MainLevelBean;
import cc.mewcraft.adventurelevel.util.PlayerUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Singleton
public class SQLDataStorage extends AbstractDataStorage {

    private static final String DATA_POOL_NAME = "AdventureLevelHikariPool";

    // SQL config
    private final String host;
    private final String port;
    private final String database;
    private final String username;
    private final String password;
    private final String parameters;

    private final int hikariMaximumPoolSize;
    private final int hikariMinimumIdle;
    private final int hikariMaximumLifetime;
    private final int hikariKeepAliveTime;
    private final int hikariConnectionTimeOut;

    // Table names
    private final String userdataTable;

    // SQL queries
    private final String insertUserdataQuery;
    private final String selectUserdataQuery;

    // Hikari instances
    private HikariDataSource connectionPool;

    @Inject
    public SQLDataStorage(final AdventureLevelPlugin plugin) {
        super(plugin);

        this.host = requireNonNull(plugin.getConfig().getString("database.credentials.host"));
        this.port = requireNonNull(plugin.getConfig().getString("database.credentials.port"));
        this.database = requireNonNull(plugin.getConfig().getString("database.credentials.database"));
        this.username = requireNonNull(plugin.getConfig().getString("database.credentials.username"));
        this.password = requireNonNull(plugin.getConfig().getString("database.credentials.password"));
        this.parameters = requireNonNull(plugin.getConfig().getString("database.credentials.parameters"));

        this.hikariMaximumPoolSize = plugin.getConfig().getInt("database.connection_pool.maximum_pool_size");
        this.hikariMinimumIdle = plugin.getConfig().getInt("database.connection_pool.minimum_idle");
        this.hikariMaximumLifetime = plugin.getConfig().getInt("database.connection_pool.maximum_lifetime");
        this.hikariKeepAliveTime = plugin.getConfig().getInt("database.connection_pool.keep_alive_time");
        this.hikariConnectionTimeOut = plugin.getConfig().getInt("database.connection_pool.connection_timeout");

        this.userdataTable = requireNonNull(plugin.getConfig().getString("database.table_names.userdata"));

        this.insertUserdataQuery = """
            INSERT INTO %userdata_table%
            (`uuid`, `name`, `main_exp`, `player_death_exp`, `entity_death_exp`, `furnace_exp`, `breed_exp`, `villager_trade_exp`, `fishing_exp`, `block_break_exp`, `exp_bottle_exp`, `grindstone_exp`)
            VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE
            `uuid` = VALUES(`uuid`),
            `name` = VALUES(`name`),
            `main_exp` = VALUES(`main_exp`),
            `player_death_exp` = VALUES(`player_death_exp`),
            `entity_death_exp` = VALUES(`entity_death_exp`),
            `furnace_exp` = VALUES(`furnace_exp`),
            `breed_exp` = VALUES(`breed_exp`),
            `villager_trade_exp` = VALUES(`villager_trade_exp`),
            `fishing_exp` = VALUES(`fishing_exp`),
            `block_break_exp` = VALUES(`block_break_exp`),
            `exp_bottle_exp` = VALUES(`exp_bottle_exp`),
            `grindstone_exp` = VALUES(`grindstone_exp`);"""
            .replace("%userdata_table%", userdataTable);
        this.selectUserdataQuery = """
            SELECT * FROM %userdata_table% WHERE uuid = ?;"""
            .replace("%userdata_table%", userdataTable);
    }

    private void setupTables(Connection conn) throws SQLException {
        try (
            PreparedStatement stmt1 = conn.prepareStatement("""
                CREATE TABLE IF NOT EXISTS
                %userdata_table% (
                `uuid` varchar(36) NOT NULL PRIMARY KEY,
                `name` varchar(16),
                `main_exp` int(11) DEFAULT 0,
                `player_death_exp` int(11) DEFAULT 0,
                `entity_death_exp` int(11) DEFAULT 0,
                `furnace_exp` int(11) DEFAULT 0,
                `breed_exp` int(11) DEFAULT 0,
                `villager_trade_exp` int(11) DEFAULT 0,
                `fishing_exp` int(11) DEFAULT 0,
                `block_break_exp` int(11) DEFAULT 0,
                `exp_bottle_exp` int(11) DEFAULT 0,
                `grindstone_exp` int(11) DEFAULT 0);"""
                .replace("%userdata_table%", userdataTable)
            )
        ) {
            stmt1.execute();
        }
    }

    @Override public void init() {
        HikariConfig hikariConfig = new HikariConfig();

        // Set jdbc driver connection url
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + parameters);
        hikariConfig.setPoolName(DATA_POOL_NAME);

        // Set authenticate
        hikariConfig.setPassword(password);
        hikariConfig.setUsername(username);

        // Set various additional parameters
        hikariConfig.setMaximumPoolSize(hikariMaximumPoolSize);
        hikariConfig.setMinimumIdle(hikariMinimumIdle);
        hikariConfig.setMaxLifetime(hikariMaximumLifetime);
        hikariConfig.setKeepaliveTime(hikariKeepAliveTime);
        hikariConfig.setConnectionTimeout(hikariConnectionTimeOut);

        // Establish connection & Create tables if needed
        connectionPool = new HikariDataSource(hikariConfig);
        try (Connection conn = connectionPool.getConnection()) {
            this.setupTables(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override public void close() {
        if (connectionPool != null) {
            connectionPool.close();
        }
    }

    @Override public @NotNull PlayerData create(final UUID uuid) {
        try (
            Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(insertUserdataQuery)
        ) {
            // Construct an empty main level bean
            MainLevelBean mainLevelBean = LevelBeanFactory.createMainLevelBean();

            // Construct a map of empty categorical level beans
            Map<LevelBean.Category, LevelBean> cateLevelMap = new HashMap<>() {{
                put(LevelBean.Category.PLAYER_DEATH, LevelBeanFactory.createCateLevelBean(LevelBean.Category.PLAYER_DEATH));
                put(LevelBean.Category.ENTITY_DEATH, LevelBeanFactory.createCateLevelBean(LevelBean.Category.ENTITY_DEATH));
                put(LevelBean.Category.FURNACE, LevelBeanFactory.createCateLevelBean(LevelBean.Category.FURNACE));
                put(LevelBean.Category.BREED, LevelBeanFactory.createCateLevelBean(LevelBean.Category.BREED));
                put(LevelBean.Category.VILLAGER_TRADE, LevelBeanFactory.createCateLevelBean(LevelBean.Category.VILLAGER_TRADE));
                put(LevelBean.Category.FISHING, LevelBeanFactory.createCateLevelBean(LevelBean.Category.FISHING));
                put(LevelBean.Category.BLOCK_BREAK, LevelBeanFactory.createCateLevelBean(LevelBean.Category.BLOCK_BREAK));
                put(LevelBean.Category.EXP_BOTTLE, LevelBeanFactory.createCateLevelBean(LevelBean.Category.EXP_BOTTLE));
                put(LevelBean.Category.GRINDSTONE, LevelBeanFactory.createCateLevelBean(LevelBean.Category.GRINDSTONE));
            }};

            PlayerData playerData = new RealPlayerData(plugin, uuid, mainLevelBean, cateLevelMap);

            stmt.setString(1, uuid.toString());
            stmt.setString(2, PlayerUtils.getNameFromUUID(uuid).toLowerCase());
            stmt.setInt(3, 0);
            stmt.setInt(4, 0);
            stmt.setInt(5, 0);
            stmt.setInt(6, 0);
            stmt.setInt(7, 0);
            stmt.setInt(8, 0);
            stmt.setInt(9, 0);
            stmt.setInt(10, 0);
            stmt.setInt(11, 0);
            stmt.setInt(12, 0);
            stmt.execute();

            plugin.getSLF4JLogger().info("Created userdata in database: name={},uuid={},mainXp={}",
                PlayerUtils.getNameFromUUID(uuid),
                playerData.getUuid(),
                mainLevelBean.getExperience()
            );

            return playerData;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        plugin.getSLF4JLogger().error("Failed to create userdata in database: name={},uuid={}",
            PlayerUtils.getNameFromUUID(uuid),
            uuid
        );

        return PlayerData.DUMMY;
    }

    @Override public @NotNull PlayerData load(final UUID uuid) {
        try (
            Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(selectUserdataQuery)
        ) {
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

                    // Construct the main level bean with loaded xp
                    MainLevelBean mainLevelBean = LevelBeanFactory.createMainLevelBean().withExperience(mainXp);

                    // Construct the map of categorical level beans with loaded xp
                    Map<LevelBean.Category, LevelBean> subLevelMap = new HashMap<>() {{
                        put(LevelBean.Category.PLAYER_DEATH, LevelBeanFactory.createCateLevelBean(LevelBean.Category.PLAYER_DEATH).withExperience(playerDeathXp));
                        put(LevelBean.Category.ENTITY_DEATH, LevelBeanFactory.createCateLevelBean(LevelBean.Category.ENTITY_DEATH).withExperience(entityDeathXp));
                        put(LevelBean.Category.FURNACE, LevelBeanFactory.createCateLevelBean(LevelBean.Category.FURNACE).withExperience(furnaceXp));
                        put(LevelBean.Category.BREED, LevelBeanFactory.createCateLevelBean(LevelBean.Category.BREED).withExperience(breedXp));
                        put(LevelBean.Category.VILLAGER_TRADE, LevelBeanFactory.createCateLevelBean(LevelBean.Category.VILLAGER_TRADE).withExperience(villagerTradeXp));
                        put(LevelBean.Category.FISHING, LevelBeanFactory.createCateLevelBean(LevelBean.Category.FISHING).withExperience(fishingXp));
                        put(LevelBean.Category.BLOCK_BREAK, LevelBeanFactory.createCateLevelBean(LevelBean.Category.BLOCK_BREAK).withExperience(blockBreakXp));
                        put(LevelBean.Category.EXP_BOTTLE, LevelBeanFactory.createCateLevelBean(LevelBean.Category.EXP_BOTTLE).withExperience(expBottleXp));
                        put(LevelBean.Category.GRINDSTONE, LevelBeanFactory.createCateLevelBean(LevelBean.Category.GRINDSTONE).withExperience(grindstoneXp));
                    }};

                    plugin.getSLF4JLogger().info("Loaded userdata from database: name={},uuid={},mainXp={}",
                        PlayerUtils.getNameFromUUID(uuid),
                        uuid,
                        mainXp
                    );

                    // Collect all above and construct the final data
                    return new RealPlayerData(plugin, uuid, mainLevelBean, subLevelMap);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        plugin.getSLF4JLogger().info("Userdata not found in database: name={},uuid={}",
            PlayerUtils.getNameFromUUID(uuid),
            uuid
        );

        return PlayerData.DUMMY;
    }

    @Override public void save(final PlayerData playerData) {
        /*if (playerData.equals(PlayerData.DUMMY) || !playerData.complete()) {
            plugin.getSLF4JLogger().info("Skipped saving userdata to database: name={},uuid={}",
                PlayerUtils.getNameFromUUID(playerData.getUuid()),
                playerData.getUuid()
            );
            return;
        }*/

        try (
            Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(insertUserdataQuery)
        ) {
            stmt.setString(1, playerData.getUuid().toString());
            stmt.setString(2, PlayerUtils.getNameFromUUID(playerData.getUuid()).toLowerCase());
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

            plugin.getSLF4JLogger().info("Saved userdata to database: name={},uuid={},mainXp={}",
                PlayerUtils.getNameFromUUID(playerData.getUuid()),
                playerData.getUuid(),
                playerData.getMainLevel().getExperience()
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
