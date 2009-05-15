package edu.byu.ece.edif.tools.replicate.nmr.dwc;

/**
 * A DetectionType indicates how to perform detection on specified signals
 * and what the detection signal width is.
 */
public abstract class AbstractDetectionType implements DetectionType {

	protected AbstractDetectionType(int signalWidth) {
		_signalWidth = signalWidth;
	}

	public int getSignalWidth() {
		return _signalWidth;
	}
	
	public void setUseComparatorForDownscale(boolean useComparatorForDownscale) {
		_useComparatorForDownscale = useComparatorForDownscale;
	}
	
	public void setUseComparatorForUpscale(boolean useComparatorForUpscale) {
		_useComparatorForUpscale = useComparatorForUpscale;
	}
	
	public String toString() {
		return getClass().getSimpleName();
	}
	
	protected int _signalWidth;
	protected boolean _useComparatorForDownscale;
	protected boolean _useComparatorForUpscale;
}
