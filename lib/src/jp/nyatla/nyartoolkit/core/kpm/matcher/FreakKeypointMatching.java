package jp.nyatla.nyartoolkit.core.kpm.matcher;





import jp.nyatla.nyartoolkit.core.kpm.KpmInputDataSet;
import jp.nyatla.nyartoolkit.core.kpm.KpmResult;
import jp.nyatla.nyartoolkit.core.kpm.kpmMatching;
import jp.nyatla.nyartoolkit.core.kpm.dogscalepyramid.DoGScaleInvariantDetector;
import jp.nyatla.nyartoolkit.core.kpm.dogscalepyramid.DogFeaturePointStack;
import jp.nyatla.nyartoolkit.core.kpm.freak.FREAKExtractor;
import jp.nyatla.nyartoolkit.core.kpm.freak.FreakFeaturePointStack;
import jp.nyatla.nyartoolkit.core.kpm.keyframe.KeyframeMap;
import jp.nyatla.nyartoolkit.core.kpm.pyramid.BinomialPyramid32f;


import jp.nyatla.nyartoolkit.core.param.NyARParam;
import jp.nyatla.nyartoolkit.core.raster.gs.INyARGrayscaleRaster;
import jp.nyatla.nyartoolkit.core.types.NyARDoublePoint2d;

import jp.nyatla.nyartoolkit.core.types.NyARIntSize;

/**
 * KpmPose6DOF
 */
public class FreakKeypointMatching {

	final private VisualDatabase freakMatcher;
	final NyARParam _ref_cparam;
	KpmInputDataSet inDataSet = new KpmInputDataSet();
	public KpmResult result;


	final private static double kLaplacianThreshold = 3;
	final private static double kEdgeThreshold = 4;
	final private static int kMaxNumFeatures = 300;
	final private static int kMinCoarseSize = 8;

	public FreakKeypointMatching(NyARParam i_ref_cparam)
	{
		NyARIntSize size=i_ref_cparam.getScreenSize();
		this.freakMatcher = new VisualDatabase(size.w, size.h);
		this._ref_cparam = i_ref_cparam;
		this.inDataSet.coord = null;
		this.inDataSet.num = 0;

		this.result =new KpmResult();
		this.mFeatureExtractor=new FREAKExtractor();
		int octerves=BinomialPyramid32f.octavesFromMinimumCoarsestSize(size.w,size.h,kMinCoarseSize);
		this.mPyramid=new BinomialPyramid32f(size.w,size.h,octerves,3);
		this.mDogDetector = new DoGScaleInvariantDetector(size.w,size.h,octerves,3,kLaplacianThreshold,kEdgeThreshold,kMaxNumFeatures);
	}
	
	final private DogFeaturePointStack _dog_feature_points = new DogFeaturePointStack(2000);// この2000は適当
	final FreakFeaturePointStack mQueryKeyframe=new FreakFeaturePointStack();
	/** Pyramid builder */
	final private BinomialPyramid32f mPyramid;
	/** Interest point detector (DoG, etc) */
	final private DoGScaleInvariantDetector mDogDetector;
	final private FREAKExtractor mFeatureExtractor;
	public boolean kpmMatching(INyARGrayscaleRaster inImage,KeyframeMap i_keymap)
	{
		FreakFeaturePointStack query_keypoint = this.mQueryKeyframe;
		//Freak Extract


		
		// Build the pyramid		
		this.mPyramid.build(inImage);
		// Detect feature points
		this._dog_feature_points.clear();	
		this.mDogDetector.detect(this.mPyramid,this._dog_feature_points);

		// Extract features
		query_keypoint.clear();
		this.mFeatureExtractor.extract(this.mPyramid,this._dog_feature_points,query_keypoint);
		
		if(query_keypoint.isEmpty()){
			return false;
		}
//	}
//	System.out.println(query_keypoint.getLength());
//	System.out.println(System.currentTimeMillis()-s);

		
		// LOG_INFO("Found %d features in query",
		// mQueryKeyframe->store().size());

		this.inDataSet.num  = (int)query_keypoint.getLength();
		this.inDataSet.coord = NyARDoublePoint2d.createArray(this.inDataSet.num);
		for (int i = 0; i < this.inDataSet.num; i++) {
			double x = query_keypoint.getItem(i).x;
			double y = query_keypoint.getItem(i).y;
			if (this._ref_cparam != null) {
				NyARDoublePoint2d tmp = new NyARDoublePoint2d();
				this._ref_cparam.getDistortionFactor().observ2Ideal(x, y, tmp);
				this.inDataSet.coord[i].x = tmp.x;
				this.inDataSet.coord[i].y = tmp.y;
			} else {
				this.inDataSet.coord[i].x = x;
				this.inDataSet.coord[i].y = y;
			}
		}
		int matched_image_id=this.freakMatcher.query(query_keypoint,i_keymap);
		if(matched_image_id<0){
			return false;
		}

		FeaturePairStack matches = this.freakMatcher.inliers();
		return kpmMatching.kpmUtilGetPose_binary(this._ref_cparam, matches,i_keymap.get(matched_image_id).store(),query_keypoint, this.result);

	}



}