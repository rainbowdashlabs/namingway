/*
 *     SPDX-License-Identifier: LGPL-3.0-or-later
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.chojo.namingway.util.config;

import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * A key for a config file. A config key is considered unique based on the underlying path.
 * <p>
 * Two config keys pointing at the same file, but one being absolute and the other not are not considered equal.
 * However, this should be avoided since it can cause issues when reloading or saving.
 *
 * @param name        name of file
 * @param path        path of file with file ending.
 * @param configClazz class representing the file
 * @param initValue   the initial value when the file does not yet exist.
 * @param <T>         type of file class
 */
public record ConfigKey<T>(String name, Path path, Class<T> configClazz, Supplier<T> initValue) {

    /**
     * Create a key for the default config aka config.yml.
     *
     * @param configClazz class representing the config.yml
     * @param initValue   the initial value when the config does not yet exist.
     * @param <V>         type of config class
     * @return config key for config.yml
     */
    public static <V> ConfigKey<V> defaultConfig(Class<V> configClazz, Supplier<V> initValue) {
        return new ConfigKey<>("config.yml", Path.of("config.yml"), configClazz, initValue);
    }

    /**
     * Create a key for the default config aka config.yml.
     *
     * @param name        name of file
     * @param path        path of file with file ending.
     * @param configClazz class representing the config.yml
     * @param initValue   the initial value when the config does not yet exist.
     * @param <V>         type of config class
     * @return config key for config.yml
     */
    public static <V> ConfigKey<V> of(String name, Path path, Class<V> configClazz, Supplier<V> initValue) {
        return new ConfigKey<>(name, path, configClazz, initValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigKey<?> configKey = (ConfigKey<?>) o;

        return path.equals(configKey.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public String toString() {
        return "%s (%s | %s)".formatted(name, path.toString(), configClazz.getSimpleName());
    }
}
