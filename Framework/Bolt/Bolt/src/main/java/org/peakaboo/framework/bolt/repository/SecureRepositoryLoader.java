package org.peakaboo.framework.bolt.repository;

import java.util.logging.Level;

import org.peakaboo.framework.accent.log.OneLog;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

public class SecureRepositoryLoader {
        
    /**
     * Securely deserializes YAML content into RepositoryMetadata
     */
    public static RepositoryMetadata loadRepositoryMetadata(String yamlContent) {
        if (yamlContent == null || yamlContent.trim().isEmpty()) {
			OneLog.log(Level.WARNING, "Failed to load empty yaml document");
            throw new IllegalArgumentException("YAML content cannot be null or empty");
        }
        
        // Configure secure loader options
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setAllowDuplicateKeys(false);
        loaderOptions.setAllowRecursiveKeys(false);
        loaderOptions.setMaxAliasesForCollections(0);
        loaderOptions.setProcessComments(false);
        loaderOptions.setNestingDepthLimit(10);
        
        Constructor constructor = new Constructor(RepositoryMetadata.class, loaderOptions);
        
        // Create YAML instance with security configurations
        Yaml yaml = new Yaml(constructor);
        
        try {
            return yaml.loadAs(yamlContent, RepositoryMetadata.class);
        } catch (YAMLException e) {
			OneLog.log(Level.WARNING, "Failed to parse invalid yaml document");
            throw new IllegalArgumentException("Failed to parse YAML: " + e.getMessage(), e);
        }
    }
}