package me.ikevoodoo.devroomtrial.db;

import me.ikevoodoo.devroomtrial.api.flags.Flag;
import me.ikevoodoo.devroomtrial.api.flags.FlagManager;
import me.ikevoodoo.devroomtrial.api.flags.FlagSet;
import me.ikevoodoo.devroomtrial.api.flags.FlagState;
import me.ikevoodoo.devroomtrial.api.regions.Region;
import org.bukkit.util.BoundingBox;

import java.io.*;
import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public final class DatabaseHelper {

    public static void setFlagSet(PreparedStatement preparedStatement, int index, FlagSet flags, Flag... extras) throws SQLException {
        final var baos = new ByteArrayOutputStream();
        final var bytes = new DataOutputStream(baos);

        try {
            bytes.writeInt(flags.size() + extras.length);

            for (final var flag : flags) {
                bytes.writeUTF(flag.id());
                bytes.writeInt(flag.state().ordinal());
            }

            for (final var flag : extras) {
                bytes.writeUTF(flag.id());
                bytes.writeInt(flag.state().ordinal());
            }
        } catch (IOException ignored) {
            // Ignore
        }

        preparedStatement.setBytes(index, baos.toByteArray());
    }

    public static int setBoundingBox(PreparedStatement preparedStatement, int index, BoundingBox boundingBox) throws SQLException {
        preparedStatement.setDouble(index, boundingBox.getMinX());
        preparedStatement.setDouble(index + 1, boundingBox.getMinY());
        preparedStatement.setDouble(index + 2, boundingBox.getMinZ());
        preparedStatement.setDouble(index + 3, boundingBox.getMaxX());
        preparedStatement.setDouble(index + 4, boundingBox.getMaxY());
        preparedStatement.setDouble(index + 5, boundingBox.getMaxZ());

        return index + 6;
    }

    public static void setUuid(PreparedStatement preparedStatement, int index, UUID uuid) throws SQLException {
        final var buffer = java.nio.ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());

        preparedStatement.setBytes(index, buffer.array());
    }

    public static void getFlagSet(ResultSet resultSet, int index, Region region) throws SQLException {
        final var bytes = resultSet.getBytes(index);

        final var bais = new ByteArrayInputStream(bytes);
        final var dataInputStream = new DataInputStream(bais);

        try {
            final var totalSize = dataInputStream.readInt();

            for (int i = 0; i < totalSize; i++) {
                final var id = dataInputStream.readUTF();
                final var stateOrdinal = dataInputStream.readInt();
                final var flag = FlagManager.instance().createFlag(id, region);
                flag.updateState(FlagState.values()[stateOrdinal]);
                region.flags().addFlag(flag);
            }
        } catch (Exception ignored) {
            // Ignored
        }
    }

    public static BoundingBox getBoundingBox(ResultSet resultSet, int index) throws SQLException {
        final var minX = resultSet.getDouble(index);
        final var minY = resultSet.getDouble(index + 1);
        final var minZ = resultSet.getDouble(index + 2);
        final var maxX = resultSet.getDouble(index + 3);
        final var maxY = resultSet.getDouble(index + 4);
        final var maxZ = resultSet.getDouble(index + 5);

        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static UUID getUuid(ResultSet resultSet, int index) throws SQLException {
        final var uuidBytes = resultSet.getBytes(index);

        if (uuidBytes.length != 16) {
            throw new SQLException("Invalid UUID byte array length");
        }

        final var byteBuffer = ByteBuffer.wrap(uuidBytes);
        final var mostSigBits = byteBuffer.getLong();
        final var leastSigBits = byteBuffer.getLong();

        return new UUID(mostSigBits, leastSigBits);
    }
}
