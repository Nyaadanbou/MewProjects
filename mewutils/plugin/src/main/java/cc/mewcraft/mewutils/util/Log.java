/*
 * This file is part of helper, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package cc.mewcraft.mewutils.util;

import cc.mewcraft.mewutils.MewUtils;
import org.bukkit.Bukkit;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.logging.Level;

/**
 * Utility for quickly accessing a logger instance without using {@link Bukkit#getLogger()}
 */
@SuppressWarnings("unused")
public class Log {

    public static void debug(@NonNull String s) {
        if (MewUtils.INSTANCE.isDevMode()) info(s);
    }

    public static void info(@NonNull String s) {
        MewUtils.INSTANCE.getLogger().info(s);
    }

    public static void warn(@NonNull String s) {
        MewUtils.INSTANCE.getLogger().warning(s);
    }

    public static void severe(@NonNull String s) {
        MewUtils.INSTANCE.getLogger().severe(s);
    }

    public static void warn(@NonNull String s, Throwable t) {
        MewUtils.INSTANCE.getLogger().log(Level.WARNING, s, t);
    }

    public static void severe(@NonNull String s, Throwable t) {
        MewUtils.INSTANCE.getLogger().log(Level.SEVERE, s, t);
    }

    private Log() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}