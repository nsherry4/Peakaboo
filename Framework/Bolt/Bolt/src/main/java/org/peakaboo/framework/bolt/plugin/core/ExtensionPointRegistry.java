package org.peakaboo.framework.bolt.plugin.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Registry for extension points in the plugin system.
 * This class is responsible for tracking and providing access to
 * extension points, which are the  (PluginRegistry) against which plugins can register.
 */
public class ExtensionPointRegistry {

	private List<BoltPluginRegistry<? extends BoltPlugin>> registries = new ArrayList<>();
	
	public ExtensionPointRegistry() {
		// TODO Auto-generated constructor stub
	}
	
	public List<BoltPluginRegistry<? extends BoltPlugin>> getRegistries() {
		return List.copyOf(registries);
	}
	
	/**
	 * Adds a new registry to the extension point registry. This method ensures that
	 * the registry (or another which implements the same interface) is not already present.
	 * @param registry
	 */
	public void addRegistry(BoltPluginRegistry<? extends BoltPlugin> registry) {
		if (findRegistryForInterface(registry.getInterfaceName()).isPresent()) {
			throw new IllegalArgumentException("A registry for the interface " + registry.getInterfaceName() + " already exists.");
		}
		registries.add(registry);
	}
	
	public Optional<BoltPluginRegistry<? extends BoltPlugin>> findRegistryForInterface(String interfaceName) {
		return registries.stream()
				.filter(registry -> registry.getInterfaceName().equals(interfaceName))
				.findFirst();
	}
	
	public Set<String> getInterfaceNames() {
		return registries.stream()
				.map(BoltPluginRegistry::getInterfaceName)
				.collect(Collectors.toSet());
	}

	public boolean hasUUID(String uuid) {
		for (var reg : getRegistries()) {
			boolean result = reg.hasUUID(uuid);
			if (result) return result;
		}
		return false;
	}

	public Optional<PluginDescriptor<? extends BoltPlugin>> getByUUID(String uuid) {
		for (var reg : getRegistries()) {
			Optional<?> result = reg.getByUUID(uuid);
			if (result.isPresent()) return (Optional<PluginDescriptor<? extends BoltPlugin>>) result;
		}
		return Optional.empty();
	}

	
}


