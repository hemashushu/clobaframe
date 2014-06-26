package org.archboy.clobaframe.io.impl;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import org.archboy.clobaframe.io.TemporaryResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultTemporaryResources implements TemporaryResources {

    /**
     * Tracked resources in LIFO order.
     */
    private final LinkedList<Closeable> resources = new LinkedList<Closeable>();

    /**
     * Directory for temporary files, <code>null</code> for the system default.
     */
    private File temporaryDirectory = null;

	private final Logger logger = LoggerFactory.getLogger(DefaultTemporaryResources.class);
	
	public DefaultTemporaryResources() {
	}

	@Override
    public void setTemporaryFileDirectory(File tmp) {
        this.temporaryDirectory = tmp;
    }

	@Override
    public File createTemporaryFile() throws IOException {
        final File file = File.createTempFile("clobaframe-io-", ".tmp", temporaryDirectory);
        addResource(new Closeable() {
			@Override
            public void close() throws IOException {
                file.delete();
            }
        });
        return file;
    }

	@Override
    public void addResource(Closeable resource) {
        resources.addFirst(resource);
    }

	@Override
	@SuppressWarnings("unchecked")
    public <T extends Closeable> T getResource(Class<T> clazz) {
        for (Closeable resource : resources) {
            if (clazz.isAssignableFrom(resource.getClass())) {
                return (T) resource;
            }
        }
        return null;
    }


	@Override
    public void close() throws IOException {
        // Release all resources.
        for (Closeable resource : resources) {
            try {
                resource.close();
            } catch (IOException e) {
                logger.error("Can not close the resource.", e);
            }
        }
        resources.clear();
    }
}
