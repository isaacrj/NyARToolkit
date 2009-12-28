package jp.nyatla.nyartoolkit.core.utils;

import jp.nyatla.nyartoolkit.core.types.*;
public class NyARMath
{
	/**
	 * p2-p1ベクトルのsquare normを計算する。
	 * @param i_p1
	 * @param i_p2
	 * @return
	 */
	public static final double sqNorm(NyARDoublePoint2d i_p1,NyARDoublePoint2d i_p2)
	{
		double x,y;
		x=i_p2.x-i_p1.x;
		y=i_p2.y-i_p1.y;
		return x*x+y*y;
	}
	public static final double sqNorm(double i_p1x,double i_p1y,double i_p2x,double i_p2y)
	{
		double x,y;
		x=i_p2x-i_p1x;
		y=i_p2y-i_p1y;
		return x*x+y*y;
	}
	/**
	 * p2-p1ベクトルのsquare normを計算する。
	 * @param i_p1
	 * @param i_p2
	 * @return
	 */	
	public static final double sqNorm(NyARDoublePoint3d i_p1,NyARDoublePoint3d i_p2)
	{
		double x,y,z;
		x=i_p2.x-i_p1.x;
		y=i_p2.y-i_p1.y;
		z=i_p2.z-i_p1.z;
		return x*x+y*y+z*z;
	}


}
