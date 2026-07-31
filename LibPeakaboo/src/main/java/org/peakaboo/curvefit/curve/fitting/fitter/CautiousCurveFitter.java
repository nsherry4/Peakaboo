package org.peakaboo.curvefit.curve.fitting.fitter;

import org.peakaboo.curvefit.curve.fitting.FittingResult;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;

/**
 * Fits one element's curve to the measured spectrum. The curve's shape is fixed
 * and known; the only unknown is how tall it should be -- a single number, the
 * scale, that we multiply the curve by to best match the data. This fitter
 * finds that number.
 *
 * <p>At each channel the curve covers we have the measured height {@code d} and
 * the curve's height {@code c}. After scaling by {@code s} the leftover -- the
 * residual -- is {@code r = d - s*c}: positive where there is still measured
 * signal to explain, negative where the fit claims more than was there. Least
 * squares picks the {@code s} that minimises {@code sum r^2}. But plain least
 * squares treats overshooting and undershooting alike, and for spectroscopy
 * they aren't alike: fabricating signal that was never measured (an overshoot,
 * {@code r < 0}) is worse than leaving real signal unexplained. So we tilt the
 * score -- an overshot channel's residual is multiplied by {@link #overfitPenalty}
 * before squaring, so it counts for {@code overfitPenalty^2} as much.
 *
 * <p>For a single {@code s} the score is a parabola, lowest where its slope is
 * zero, so we differentiate {@code sum (d - s*c)^2} with respect to {@code s},
 * set the slope to zero and solve. That leaves the one-dimensional normal
 * equation, no searching required:
 * <pre>    s = sum(d*c) / sum(c*c)</pre>
 * The asymmetric penalty keeps the score a parabola in each region, just steeper
 * on the overshoot side, so the same formula holds once each channel is
 * weighted:
 * <pre>    s = sum(w*d*c) / sum(w*c*c)</pre>
 * where {@code w} is {@code overfitPenalty^2} on overshot channels and 1
 * elsewhere.
 *
 * <p>The catch: which channels are overshot depends on {@code s}, and {@code s}
 * depends on the weights -- chicken and egg. We break it by iterating: guess
 * {@code s}, see what it overshoots, recompute the weights, solve again, repeat.
 * This is iteratively reweighted least squares (IRLS). The tilted score still
 * has a single minima, so the loop walks straight down to it and stops once the
 * overshot set -- and with it {@code s} -- stops changing, usually within a few
 * passes.
 *
 * <p>{@link OptimizingCurveFitter} and {@link LeastSquaresCurveFitter} are this
 * same algorithm at other settings of the two fields below.
 *
 * @author NAS
 */
public class CautiousCurveFitter implements CurveFitter {

	/**
	 * How many times worse an overshoot is than an undershoot. An overshot
	 * channel's residual is multiplied by this before being squared, so it ends
	 * up squared in the weighting: the default 3 makes such a channel count for
	 * 9x a normal one.
	 */
	protected float overfitPenalty = 3f;

	/**
	 * Whether to accept negative signal as input or clamp to 0
	 */
	protected boolean clampDataAtZero = true;

	private static final int MAX_IRLS_ITERATIONS = 10;
	private static final float CONVERGENCE_TOLERANCE = 1e-5f;

	@Override
	public FittingResult fit(CurveFitterContext ctx) {
		float scale = this.findScale(ctx);
		return new FittingResult(ctx.curve(), scale);
	}

	private float findScale(CurveFitterContext ctx) {

		// The intense channels are the handful of energy bins where this curve
		// actually has meaningful height -- its peak(s). We only fit against
		// those.
		int[] channels = ctx.curve().getIntenseChannelList();

		// Pack the data and curves at intense channels into dense arrays so we
		// don't have to deal with that indirection in the loop. d[i] and c[i]
		// both describe the i-th such channel.
		SpectrumView data = ctx.data();
		float[] dataAll = ((Spectrum)data).backingArray();
		float[] curveAll = ((Spectrum) ctx.curve().get()).backingArray();
		int dataSize = data.size();

		float[] d = new float[channels.length]; // Data
		float[] c = new float[channels.length]; // Curve
		int count = 0; // Not every channel lands in c/d: out-of-bounds ones are skipped
		for (int ch : channels) {
			if (ch < 0 || ch >= dataSize) continue;
			// Clamp signal at zero, we don't own another fitting's overshoot.
			d[count] = clampDataAtZero ? Math.max(0f, dataAll[ch]) : dataAll[ch];
			c[count] = curveAll[ch];
			count++;
		}
		if (count == 0) {
			return 0f;
		}

		// Plain least squares for a starting guess, then IRLS to fold in the
		// overfit penalty.
		float guess = initialScale(d, c, count);
		if (Float.isNaN(guess)) {
			// The curve is flat zero across every intense channel; no scale fits.
			return 0f;
		}
		return refine(d, c, count, guess);
	}

	/**
	 * The plain, un-penalised least squares scale, {@code sum(d*c)/sum(c*c)},
	 * clamped at zero. This is our starting guess before we know which channels
	 * the fit overshoots. Returns {@code NaN} when the curve is flat zero across
	 * the whole window, where no scale fits.
	 */
	private static float initialScale(float[] d, float[] c, int count) {
		float num = 0f, den = 0f;
		for (int i = 0; i < count; i++) {
			num += d[i] * c[i];
			den += c[i] * c[i];
		}
		// den is sum(c*c); zero only if the curve is flat zero everywhere here.
		if (den == 0f) {
			return Float.NaN;
		}
		// Clamp: a negative scale would mean the element emits negative signal.
		return Math.max(0f, num / den);
	}

	/**
	 * Refines a starting scale by IRLS: reweight whatever the current scale
	 * overshoots, re-solve the weighted normal equation, repeat until the scale
	 * stops moving. We keep the best-scoring iterate as a safety net.
	 */
	private float refine(float[] d, float[] c, int count, float scale) {
		float overfitWeight = overfitPenalty * overfitPenalty;

		float best = scale;
		float bestScore = Float.MAX_VALUE;
		for (int iter = 0; iter < MAX_IRLS_ITERATIONS; iter++) {
			// One pass does double duty: score the current scale, and accumulate
			// the weighted sums for the next one.
			float score = 0f;
			float num = 0f, den = 0f;
			for (int i = 0; i < count; i++) {
				float r = d[i] - scale * c[i];
				float w = (r < 0f) ? overfitWeight : 1f;
				score += w * r * r;
				num += w * d[i] * c[i];
				den += w * c[i] * c[i];
			}
			if (score < bestScore) {
				bestScore = score;
				best = scale;
			}

			// Solve for the next scale (clamped at zero, same reason as the
			// guess). If the scale barely moved then we've converged.
			float next = Math.max(0f, num / den);
			if (Math.abs(next - scale) <= CONVERGENCE_TOLERANCE * Math.max(1f, scale)) {
				return next;
			}
			scale = next;
		}

		// Ran out of iterations without settling (rare): hand back the
		// best-scored scale rather than the last, possibly mid-oscillation, one.
		return best;
	}

	@Override
	public String pluginName() {
		return "Cautious Least Squares";
	}

	@Override
	public String toString() {
		return pluginName();
	}

	@Override
	public String pluginDescription() {
		return "Least squares curve fitting with a bias against overfitting";
	}

	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public String pluginUUID() {
		return "4e5b95fa-a873-4732-bdab-918107c87e20";
	}

}
