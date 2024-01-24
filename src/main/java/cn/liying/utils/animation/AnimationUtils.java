package cn.liying.utils.animation;

import net.ccbluex.liquidbounce.utils.render.RenderUtils;

public class AnimationUtils {
	public static float rotateDirection = 0;
	public static double delta;
	public static float lstransition(float now, float desired, double speed) {
		final double dif = Math.abs(desired - now);
		float a = (float) Math.abs((desired - (desired - (Math.abs(desired - now)))) / (100 - (speed * 10)));
		float x = now;

		if (dif > 0) {
			if (now < desired)
				x += a * RenderUtils.deltaTime;
			else if (now > desired)
				x -= a * RenderUtils.deltaTime;
		} else
			x = desired;

		if(Math.abs(desired - x) < 10.0E-3 && x != desired)
			x = desired;

		return x;
	}
	public static float getAnimationState(float animation, float finalState, float speed) {
		final float add = (float) (delta * (speed / 1000f));
		if (animation < finalState) {
			if (animation + add < finalState) {
				animation += add;
			} else {
				animation = finalState;
			}
		} else if (animation - add > finalState) {
			animation -= add;
		} else {
			animation = finalState;
		}
		return animation;
	}

	public static float smoothAnimation(float ani, float finalState, float speed, float scale) {
		return getAnimationState(ani, finalState, (float) (Math.max(10, (Math.abs(ani - finalState)) * speed) * scale));
	}
}
