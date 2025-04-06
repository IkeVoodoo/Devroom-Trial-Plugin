package me.ikevoodoo.devroomtrial.db;

public final class DatabaseQueries {

    public static final String SETUP_REGION_TABLE = """
                CREATE TABLE IF NOT EXISTS regions (
                    uuid BINARY(16) PRIMARY KEY UNIQUE,
                    name VARCHAR(255) NOT NULL,
                    world_id BINARY(16),
                    min_x DOUBLE,
                    min_y DOUBLE,
                    min_z DOUBLE,
                    max_x DOUBLE,
                    max_y DOUBLE,
                    max_z DOUBLE,
                    flag_set BLOB
                );
                """;

    public static final String SETUP_WHITELIST_TABLE = """
                CREATE TABLE IF NOT EXISTS region_whitelist (
                    region_uuid BINARY(16),
                    player_uuid BINARY(16),
                    PRIMARY KEY (region_uuid, player_uuid),
                    FOREIGN KEY (region_uuid) REFERENCES regions(uuid) ON DELETE CASCADE
                );
                """;

    public static final String READ_REGION_TABLE = """
                SELECT uuid, name, world_id, min_x, min_y, min_z, max_x, max_y, max_z, flag_set FROM regions
                """;

    public static final String READ_WHITELIST_TABLE = """
                SELECT player_uuid FROM region_whitelist WHERE region_uuid = ?
                """;

    public static final String WRITE_REGION_DATA = """
            INSERT IGNORE INTO regions (uuid, name, world_id, min_x, min_y, min_z, max_x, max_y, max_z, flag_set)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;

    public static final String WRITE_WHITELIST_ENTRY = """
            INSERT IGNORE INTO region_whitelist (region_uuid, player_uuid)
            VALUES (?, ?);
            """;

    public static final String DELETE_WHITELIST_ENTRY = """
            DELETE FROM region_whitelist WHERE region_uuid = ? AND player_uuid = ?
            """;

    public static final String WRITE_REGION_BOUNDING_BOX = """
            UPDATE regions
            SET min_x = ?, min_y = ?, min_z = ?, max_x = ?, max_y = ?, max_z = ?
            WHERE uuid = ?;
            """;

    public static final String WRITE_REGION_NAME = """
            UPDATE regions
            SET name = ?
            WHERE uuid = ?;
            """;

    public static final String WRITE_REGION_FLAG_SET = """
            UPDATE regions
            SET flag_set = ?
            WHERE uuid = ?;
            """;
}
