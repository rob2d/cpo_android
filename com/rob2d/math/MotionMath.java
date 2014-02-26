package com.rob2d.math;

/** Copyright 2011 Robert Concepcion III */
public class MotionMath
{
	public static double coordsToAngle(double x, double y)
	{
		double angle;
		angle = -Math.toDegrees(Math.atan2(y, x));
		
		if(angle < 0)
			angle = (360 + angle);
		return angle;
	}
}
