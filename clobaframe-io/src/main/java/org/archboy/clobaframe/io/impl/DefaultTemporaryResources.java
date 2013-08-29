package org.archboy.clobaframe.io.impl;

import org.archboy.clobaframe.io.*;
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
public class DefaultTemporaryResources implements TemporaryResources {

    /**
     * Tracked resources in LIFO order.
     */
    private final LinkedList<Closeable> resources = new LinkedList<Closeable>();

    /**
     * Directory for temporary files, <code>null</code> for the system default.
     */
    private File tmp = null;

    /**
     * Sets the directory to be used for the temporary files created by
     * the {@link #createTemporaryFile()} method.
     *
     * @param tmp temporary file directory,
     *            or <code>null</code> for the system default
     */
	@Override
    public void setTemporaryFileDirectory(File tmp) {
        this.tmp = tmp;
    }

    /**
     * Creates and returns a temporary file that will automatically be
     * deleted when the {@link #close()} method is called.
     *
     * @return
     * @throws IOException
     */
	@Override
    public File createTemporaryFile() throws IOException {
        final File file = File.createTempFile("clobaframe-io-", ".tmp", tmp);
        addResource(new Closeable() {
			@Override
            public void close() throws IOException {
                file.delete();
            }
        });
        return file;
    }

    /**
     * Adds a new resource to the set of tracked resources that will all be
     * closed when the {@link #close()} method is called.
     *
     * @param resource resource to be tracked
     */
	@Override
    public void addResource(Closeable resource) {
        resources.addFirst(resource);
    }

    /**
     * Returns the latest of the tracked resources that implements or
     * extends the given interface or class.
     *
     * @param clazz interface or class
     * @return matching resource, or <code>null</code> if not found
     */
    //@SuppressWarnings("unchecked")
	@Override
    public <T extends Closeable> T getResource(Class<T> clazz) {
        for (Closeable resource : resources) {
            if (clazz.isAssignableFrom(resource.getClass())) {
                return (T) resource;
            }
        }
        return null;
    }

    /**
     * Closes all tracked resources. The resources are closed in reverse order
     * from how they were added.
     * <p>
     * Any thrown exceptions from managed resources are collected and
     * then re-thrown only once all the resources have been closed.
     *
     * @throws IOException if one or more of the tracked resources
     *                     could not be closed
     */
	@Override
    public void close() throws IOException {
        // Release all resources and keep track of any exceptions
        // List<IOException> exceptions = new LinkedList<IOException>();
        for (Closeable resource : resources) {
            try {
                resource.close();
            } catch (IOException e) {
                // ignore
            }
        }
        resources.clear();
    }
}
