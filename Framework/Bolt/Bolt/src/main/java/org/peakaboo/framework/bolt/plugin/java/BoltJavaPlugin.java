package org.peakaboo.framework.bolt.plugin.java;

import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;

/**
 * This is the base interface that any plugin system using the Bolt plugin 
 * system must extend or implement. To create a system of plugins, BoltPlugin 
 * should be extended or implemented, and the appropriate interface defined
 * for the plugin system in question. Any actual plugins should extend or 
 * implement <i>that</i> class or interface. In order to distinguish plugins
 * from further subclassing, all classes which are to be used as plugins
 * should be annotated with the {@link Plugin} interface.
 * @author Nathaniel Sherry, 2010-2012
 *
 */

public interface BoltJavaPlugin extends BoltPlugin {


	
	
}
