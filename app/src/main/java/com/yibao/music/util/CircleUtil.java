package com.yibao.music.util;

/**
 * Created by sszz on 2016/12/12.
 */

public class CircleUtil {
	/**
	 * 根据触摸的位置，计算角度
	 *
	 * @param xTouch
	 * @param yTouch
	 * @param d 直径
	 * @return
	 */
	public static float getAngle(float xTouch, float yTouch,int d) {
		double x = xTouch - (d / 2f);
		double y = yTouch - (d / 2f);
		//hypot:通过两条直角边,求斜边
		return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
	}

	/**
	 * 根据当前位置计算象限
	 *
	 * @param x
	 * @param y
	 * @param d 直径
	 * @return
	 */
	public static int getQuadrant(float x, float y,int d) {
		int tmpX = (int) (x - d / 2);
		int tmpY = (int) (y - d / 2);
		if (tmpX >= 0) {
			return tmpY >= 0 ? 4 : 1;
		} else {
			return tmpY >= 0 ? 3 : 2;
		}

	}
}
