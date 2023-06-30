package cc.mewcraft.mewcore.util;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class UtilFile {
    public static boolean copyFile(final @Nullable File origin, final @Nullable File targetFile) {
        if (origin == null || targetFile == null) {
            return false;
        }

        try {
            return UtilFile.copyStream(new FileInputStream(origin), new FileOutputStream(targetFile));
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean copyFilesRecursively(final @Nullable File origin, final @Nullable File targetDir) {
        if (origin == null || targetDir == null) {
            return false;
        }

        Preconditions.checkArgument(targetDir.isDirectory());

        if (!origin.isDirectory()) {
            return UtilFile.copyFile(origin, new File(targetDir, origin.getName()));
        } else {
            final File newDestinationDir = new File(targetDir, origin.getName());
            if (!newDestinationDir.exists() && !newDestinationDir.mkdir()) {
                return false;
            }
            for (final File child : Objects.requireNonNull(origin.listFiles())) {
                if (!UtilFile.copyFilesRecursively(child, newDestinationDir)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean copyJarResourcesRecursively(final @Nullable File targetDir, final @Nullable JarURLConnection jarConnection)
        throws IOException {
        if (targetDir == null || jarConnection == null) {
            return false;
        }

        final JarFile jarFile = jarConnection.getJarFile();

        for (final Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements(); ) {
            final JarEntry entry = e.nextElement();
            if (entry.getName().startsWith(jarConnection.getEntryName())) {
                final String filename = StringUtils.removeStart(entry.getName(), jarConnection.getEntryName());

                final File f = new File(targetDir, filename);
                if (!entry.isDirectory()) {
                    final InputStream entryInputStream = jarFile.getInputStream(entry);
                    if (!UtilFile.copyStream(entryInputStream, f)) {
                        return false;
                    }
                    entryInputStream.close();
                } else {
                    if (!UtilFile.ensureDirectoryExists(f)) {
                        throw new IOException("Could not create directory: " + f.getAbsolutePath());
                    }
                }
            }
        }
        return true;
    }

    public static boolean copyResourcesRecursively(final @Nullable URL origin, final @Nullable File target) {
        if (origin == null || target == null) {
            return false;
        }

        try {
            final URLConnection urlConnection = origin.openConnection();
            if (urlConnection instanceof JarURLConnection) {
                return UtilFile.copyJarResourcesRecursively(target, (JarURLConnection) urlConnection);
            } else {
                return UtilFile.copyFilesRecursively(new File(origin.getPath()), target);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean copyStream(final @Nullable InputStream is, final @Nullable File f) {
        if (is == null || f == null) {
            return false;
        }

        try {
            return UtilFile.copyStream(is, new FileOutputStream(f));
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean copyStream(final @Nullable InputStream is, final @Nullable OutputStream os) {
        if (is == null || os == null) {
            return false;
        }

        try {
            final byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
            }
            is.close();
            os.close();
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean ensureDirectoryExists(final @Nullable File f) {
        if (f == null) {
            return false;
        }
        return f.exists() || f.mkdir();
    }
}
