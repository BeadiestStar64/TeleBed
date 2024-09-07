package com.github.BeadiestStar64.Minecraft.Plugins.teleBed;

import org.apache.commons.lang3.SerializationUtils;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class TelePDC implements PersistentDataType<byte[], TeleportData> {
    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<TeleportData> getComplexType() {
        return TeleportData.class;
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull TeleportData teleportData, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return SerializationUtils.serialize(teleportData);
    }

    @Override
    public @NotNull TeleportData fromPrimitive(byte @NotNull [] bytes, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        try (InputStream input = new ByteArrayInputStream(bytes);
             ObjectInputStream stream = new ObjectInputStream(input)) {
            return (TeleportData) stream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.fillInStackTrace();
        }
        return null;
    }
}
