package org.archboy.clobaframe.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class for tracking and ultimately closing or otherwise disposing
 * a collection of temporary resources.
 * <p>
 * Note that this class is not thread-safe.
 * It's from Apache Tika.
 */
public interface TemporaryResources extends Closeable {

    /**
     * Sets the directory to be used for the temporary files created by
     * the {@link #createTemporaryFile()} method.
     *
     * @param tmp temporary file directory,
     *            or <code>null</code> for the system default
     */
    //void setTemporaryFileDirectory(File tmp);

    /**
     * Creates and returns a temporary file that will automatically be
     * deleted when the {@link #close()} method is called.
     *
     * @return
     * @throws IOException
     */
    File createTemporaryFile() throws IOException;

    /**
     * Adds a new resource to the set of tracked resources that will all be
     * closed when the {@link #close()} method is called.
     *
     * @param resource resource to be tracked
     */
    void addResource(Closeable resource);

    /**
     * Returns the latest of the tracked resources that implements or
     * extends the given interface or class.
     *
     * @param clazz interface or class
     * @return matching resource, or <code>null</code> if not found
     */
    <T extends Closeable> T getResource(Class<T> clazz);

}
