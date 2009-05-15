package edu.byu.ece.edif.tools.replicate.nmr.dwc;

import java.io.ObjectStreamException;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.byu.ece.edif.tools.replicate.nmr.Organ;

/**
 * The SingleRailDetectionType uses a 1-bit error code output that, when
 * high, indicates that an error has been detected.
 */
public class SingleRailDetectionType extends RailedDetectionType {

	protected static Map<Organ, SingleRailDetectionType> _instances = new LinkedHashMap<Organ, SingleRailDetectionType>();
	
	protected SingleRailDetectionType(Organ detectorOrgan) {
		super(detectorOrgan, 1);
	}
	
	public static SingleRailDetectionType getInstance(Organ detectorOrgan) {
		SingleRailDetectionType detectionType = _instances.get(detectorOrgan);
		if (detectionType == null) {
			detectionType = new SingleRailDetectionType(detectorOrgan);
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
		SingleRailDetectionType instance = _instances.get(_detectorOrgan);
		if (instance == null) {
			instance = getInstance(_detectorOrgan);
		}
		return instance;
	}
}
