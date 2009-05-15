package edu.byu.ece.edif.tools.replicate.nmr.dwc;

import java.io.ObjectStreamException;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.byu.ece.edif.tools.replicate.nmr.Organ;

/**
 * DualRailDetectionType uses a 2-bit signal to encode error occurrences.
 * (00 = no error, 11 = error, 10 or 01 = detector error).
 */
public class DualRailDetectionType extends RailedDetectionType {

	protected static Map<Organ, DualRailDetectionType> _instances = new LinkedHashMap<Organ, DualRailDetectionType>();
	
	protected DualRailDetectionType(Organ detectorOrgan) {
		super(detectorOrgan, 2);
	}
	
	public static DualRailDetectionType getInstance(Organ detectorOrgan) {
		DualRailDetectionType detectionType = _instances.get(detectorOrgan);
		if (detectionType == null) {
			detectionType = new DualRailDetectionType(detectorOrgan);
			_instances.put(detectorOrgan, detectionType);			
		}
		return detectionType;
	}
	
	/**
	 * This method ensures that during deserialization, the _instances map will be referred
	 * to and possibly modified so that extra instances (more than necessary) don't get created.
	 * @return
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException {
		DualRailDetectionType type = _instances.get(_detectorOrgan);
		if (type == null) {
			type = getInstance(_detectorOrgan);
		}
		return type;
	}
}
