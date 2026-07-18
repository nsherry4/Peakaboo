package org.peakaboo.curvefit.curve.fitting.solver;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * A cache for work that is expensive to compute but stays valid across many
 * solves -- matrices derived from the curve list, for example. Sharing one
 * cache across the per-spectrum contexts of a map-fitting loop lets solvers
 * reuse loop-invariant work instead of rebuilding it for every pixel.
 *
 * Please be careful: cached values are only valid while the fittings (visible
 * curves, calibration, fitting parameters) and intense channels stay the same.
 * If you share a cache across solves, it's on you to throw it away when any of
 * those change. Thread-safe -- each value is computed at most once, even when
 * solves run in parallel.
 *
 * @author NAS
 */
public final class SolverCache {

	private final ConcurrentHashMap<String, Object> store = new ConcurrentHashMap<>();

	/**
	 * Returns the value for this key, computing and storing it on first
	 * access. The cast to the caller's type is unchecked, so every caller
	 * using a given key must expect the same type.
	 */
	@SuppressWarnings("unchecked")
	public <T> T computeIfAbsent(String key, Supplier<T> supplier) {
		return (T) store.computeIfAbsent(key, k -> supplier.get());
	}

}
