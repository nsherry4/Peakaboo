package org.peakaboo.framework.bolt.repository;

import static org.junit.Assert.*;

import org.junit.Test;

public class RepositoryMetadataTest {

	private static final String REPO_URL = "https://example.com/plugins/repo";
	private static final int APP_VERSION = 100;

	/** Builds a fully valid RepositoryMetadata with one plugin. */
	private static RepositoryMetadata buildValidRepo(String repoUrl) {
		RepositoryMetadata repo = new RepositoryMetadata();
		repo.specVersion = 1;
		repo.repositoryName = "Test Repo";
		repo.repositoryUrl = repoUrl;
		repo.repositoryDescription = "A test repository";
		repo.applicationName = "Peakaboo";
		repo.plugins.add(buildValidPlugin(repoUrl));
		return repo;
	}

	/** Builds a fully valid PluginMetadata that passes all checks. */
	private static PluginMetadata buildValidPlugin(String repoUrl) {
		PluginMetadata p = new PluginMetadata();
		p.name = "Test Plugin";
		p.category = "Filter";
		p.version = "1-0-0";
		p.minAppVersion = 1;
		p.uuid = "12345678-1234-1234-1234-123456789abc";
		p.downloadUrl = repoUrl + "/test-plugin.jar";
		p.repositoryUrl = repoUrl;
		p.description = "A test plugin";
		p.author = "Test Author";
		p.checksum = "d41d8cd98f00b204e9800998ecf8427e";
		p.releaseNotes = "Initial release";
		return p;
	}

	@Test
	public void validate_allValid_returnsTrue() {
		RepositoryMetadata repo = buildValidRepo(REPO_URL);
		assertTrue(repo.validate(REPO_URL, APP_VERSION));
	}

	@Test
	public void validate_specVersionZero_returnsFalse() {
		RepositoryMetadata repo = buildValidRepo(REPO_URL);
		repo.specVersion = 0;
		assertFalse(repo.validate(REPO_URL, APP_VERSION));
	}

	@Test
	public void validate_repoNameContainsBuilt_returnsFalse() {
		RepositoryMetadata repo = buildValidRepo(REPO_URL);
		repo.repositoryName = "Built-In Extras";
		assertFalse(repo.validate(REPO_URL, APP_VERSION));
	}

	@Test
	public void validate_repoUrlMismatch_returnsFalse() {
		RepositoryMetadata repo = buildValidRepo(REPO_URL);
		assertFalse(repo.validate("https://evil.com/repo", APP_VERSION));
	}

	@Test
	public void validate_pluginDownloadUrlNotPrefixedByRepoUrl_returnsFalse() {
		RepositoryMetadata repo = buildValidRepo(REPO_URL);
		repo.plugins.get(0).downloadUrl = "https://evil.com/malware.jar";
		assertFalse(repo.validate(REPO_URL, APP_VERSION));
	}

	/**
	 * Bug: line 78 checks {@code this.repositoryUrl.contains("..")} instead of
	 * {@code plugin.downloadUrl.contains("..")}. A download URL with path traversal
	 * should be rejected but passes because the wrong field is checked.
	 */
	@Test
	public void validate_pluginDownloadUrlContainsTraversal_returnsFalse() {
		RepositoryMetadata repo = buildValidRepo(REPO_URL);
		repo.plugins.get(0).downloadUrl = REPO_URL + "/../../../etc/passwd";
		assertFalse("Download URL with path traversal should be rejected", repo.validate(REPO_URL, APP_VERSION));
	}

	/**
	 * Documents bug: line 85 checks {@code this.repositoryUrl.contains("..")} instead
	 * of {@code plugin.repositoryUrl.contains("..")}. The traversal check is dead code
	 * because:
	 * <ol>
	 *   <li>Line 55 already rejects {@code this.repositoryUrl} containing ".."</li>
	 *   <li>Line 85's equality check catches any mismatch between plugin and repo URLs</li>
	 * </ol>
	 * So a plugin.repositoryUrl with ".." is caught by the equality check, not
	 * the traversal check. This test verifies the equality check does catch it,
	 * and documents that the traversal check on line 85 is checking the wrong field.
	 */
	@Test
	public void validate_pluginRepoUrlTraversalCheck_isDeadCode() {
		RepositoryMetadata repo = buildValidRepo(REPO_URL);
		// Plugin has ".." but repo does not — should be caught by traversal check,
		// but that check looks at this.repositoryUrl (clean), so it passes.
		// The equality check (!plugin.repositoryUrl.equals(this.repositoryUrl)) catches
		// it instead — but only because the URLs don't match, not because of "..".
		repo.plugins.get(0).repositoryUrl = REPO_URL + "/../../../etc/passwd";
		assertFalse("Caught by equality check, not traversal check", repo.validate(REPO_URL, APP_VERSION));
	}

	@Test
	public void validate_pluginMinAppVersionExceedsApp_returnsFalse() {
		RepositoryMetadata repo = buildValidRepo(REPO_URL);
		repo.plugins.get(0).minAppVersion = APP_VERSION + 1;
		assertFalse(repo.validate(REPO_URL, APP_VERSION));
	}

}
