package org.archboy.clobaframe.blobstore;

import java.io.IOException;

/**
 * Blob repository manager.
 * 
 * A repository is the collection of blob objects, just like the folder to the file.
 *
 * @author yang
 *
 */
public interface Blobstore {

	/**
	 * Check whether a repository exists.
	 *
	 * @param repoName
	 * @return
	 */
	boolean exist(String repoName);

	/**
	 * Create a repository.
	 * Duplicate name will be ignored.
	 *
	 * @param repoName
	 * @throws IOException
	 */
	void create(String repoName) throws IOException;

	/**
	 * 
	 * @param repoName
	 * @return NULL if the specify bucket does not exists.
	 */
	BlobResourceRepository getRepository(String repoName);
	
	/**
	 * Delete a repository.
	 * This function can only delete an empty repository,
	 * all blob objects must deleted first before delete the repository.
	 * 
	 * It will NOT occurs an exception if the specify repository does not exist.
	 *
	 * @param repoName
	 * @throws IOException Delete failed.
	 */
	void delete(String repoName) throws IOException;


}
