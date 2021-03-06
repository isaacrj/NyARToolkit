package jp.nyatla.nyartoolkit.dev.pro.core.surfacetracking;

import jp.nyatla.nyartoolkit.core.types.NyARDoublePoint2d;
import jp.nyatla.nyartoolkit.core.types.matrix.NyARDoubleMatrix44;
import jp.nyatla.nyartoolkit.pro.core.surfacetracking.feature.NyARSurfaceFeatureSet;

/**
 * [inner-class]
 * çæ?å¤æ+å°?å½±å¤æè¡å?ã?®ç©ãæ ¼ç´ããè¡å?ã??
 * ç?æ³ç»é¢ä¸ã§ã®è¨ç®é¢æ°ãæã¡ã¾ãã??
 */
public class NyARSurfaceTransMatrixSet
{
	public NyARDoubleMatrix44 ctrans=new NyARDoubleMatrix44();
	public NyARDoubleMatrix44 trans=new NyARDoubleMatrix44();
	/**
	 * dpiãè¨æ¸¬ããæã?®ããããµã¤ãºã?
	 * åºç¹ããæ?å®mmç¯?å²ã®ç»åããdpiãè¨ç®ãã¾ãã??
	 */
	private final static double DPI_BOX=10.0;
	/**
	 * ä¿æ°ãã»ã?ãããã??
	 * @param i_param
	 * @param i_trans
	 */
	public void setValue(NyARDoubleMatrix44 i_projection_mat,NyARDoubleMatrix44 i_trans)
	{
		this.trans.setValue(i_trans);
		this.ctrans.mul(i_projection_mat, i_trans);
	}
	/**
	 * ç?æ³ç¹ãè¨ç®ããã??
	 * @param i_coord
	 * å¤æå?ã®åº§æ¨?(ç?æ³ç¹)
	 */
	public void calculate2dPos(double i_x,double i_y,NyARDoublePoint2d o_idepos)
	{
		NyARDoubleMatrix44 t=this.ctrans;
	    double h  = (t.m20 * i_x + t.m21 * i_y + t.m23);
	    double hx = (t.m00 * i_x + t.m01 * i_y + t.m03)/h;
	    double hy = (t.m10 * i_x + t.m11 * i_y + t.m13)/h;
	    o_idepos.x=hx;
	    o_idepos.y=hy;
	}	
	/**
	 * åºæºç¹ã®dpiãæ¨å®ããã??
	 * @param i_cptrans
	 * å°?å½±å¤æè¡å??. [cparam]*[trans]
	 * @param trans
	 * @param pos
	 * [2]
	 * @param o_dpi
	 * x,yæ¹åããããã?®æ¨å®dpi
	 * @return
	 */
	public void ar2GetResolution2d(jp.nyatla.nyartoolkit.dev.pro.core.surfacetracking.feature.NyAR2FeatureCoord i_pos, NyARDoublePoint2d o_dpi)
	{
		NyARDoubleMatrix44 t=this.ctrans;
		//åºç¹
	    double mx0 = i_pos.mx;
	    double my0 = i_pos.my;
	    double h0  = t.m20 * mx0 + t.m21 * my0 + t.m23;
	    double hx0 = t.m00 * mx0 + t.m01 * my0 + t.m03;
	    double hy0 = t.m10 * mx0 + t.m11 * my0 + t.m13;
	    double x0 = hx0 / h0;
	    double y0 = hy0 / h0;

	    double   h,sx,sy;
	    //+X
	    h=h0+t.m20*DPI_BOX;
	    sx = ((hx0+DPI_BOX*t.m00) / h)-x0;
	    sy = ((hy0+DPI_BOX*t.m10) / h)-y0;
	    //dpi -x
	    o_dpi.x = Math.sqrt(sx*sx+sy*sy)*2.54;
	    
	    //+Y
	    h=h0+t.m21*DPI_BOX;
	    sx = ((hx0+DPI_BOX*t.m01) / h)-x0;
	    sy = ((hy0+DPI_BOX*t.m11) / h)-y0;

	    //dpi -y
	    o_dpi.y = Math.sqrt(sx*sx+sy*sy)* 2.54;
	    return;
	}
	/**
	 * x,yã®ã?ã¡å°ãã?æ¹ã®è§£ååº¦ãè¿ãã?
	 * @param i_pos
	 * ç?æ³ç³»åº§æ¨?
	 * @return
	 * æ¨å®ããdpi
	 */
	public double ar2GetMinResolution(jp.nyatla.nyartoolkit.dev.pro.core.surfacetracking.feature.NyAR2FeatureCoord i_pos)
	{
		NyARDoubleMatrix44 t=this.ctrans;
		//åºç¹
	    double mx0 = i_pos.mx;
	    double my0 = i_pos.my;
	    double h0  = t.m20 * mx0 + t.m21 * my0 + t.m23;
	    double hx0 = t.m00 * mx0 + t.m01 * my0 + t.m03;
	    double hy0 = t.m10 * mx0 + t.m11 * my0 + t.m13;
	    double x0 = hx0 / h0;
	    double y0 = hy0 / h0;

	    double   h,sx,sy;
	    //+X
	    h=h0+t.m20*DPI_BOX;
	    sx = ((hx0+DPI_BOX*t.m00) / h)-x0;
	    sy = ((hy0+DPI_BOX*t.m10) / h)-y0;
	    //dpi -x
	    double dx = Math.sqrt(sx*sx+sy*sy)*2.54;
	    
	    //+Y
	    h=h0+t.m21*DPI_BOX;
	    sx = ((hx0+DPI_BOX*t.m01) / h)-x0;
	    sy = ((hy0+DPI_BOX*t.m11) / h)-y0;

	    //dpi -y
	    double dy = Math.sqrt(sx*sx+sy*sy)* 2.54;
	    
	    return dx<dy?dx:dy;
	}
	/**
	 * ãªãã ããããã?Zä½ç½®?¼?
	 * @param mx
	 * @param my
	 * @return
	 */
	public double calculateVd(double mx,double my)
	{
		NyARDoubleMatrix44 t=this.trans;
		double vd0 = t.m00 * mx+ t.m01 * my+ t.m03;
		double vd1 = t.m10 * mx+ t.m11 * my+ t.m13;
		double vd2 = t.m20 * mx+ t.m21 * my+ t.m23;
		return (vd0*t.m02 + vd1*t.m12 + vd2*t.m22)/Math.sqrt( vd0*vd0 + vd1*vd1 + vd2*vd2 );
	}
}