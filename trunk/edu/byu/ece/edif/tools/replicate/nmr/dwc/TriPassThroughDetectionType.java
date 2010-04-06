package edu.byu.ece.edif.tools.replicate.nmr.dwc;

import java.io.ObjectStreamException;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.byu.ece.edif.tools.replicate.nmr.Organ;

/**
 * The TriPassThroughDetectionType simply passes through three replicated
 * signals to top-level ports
 * It is not strictly a RailedDetectionType in that is does not do "railed"
 * detection, but the source code is the same so it is here to avoid
 * unnecessary code replication
 */
public class TriPassThroughDetectionType extends RailedDetectionType {

	protected static Map<Organ, TriPassThroughDetectionType> _instances = new LinkedHashMap<Organ, TriPassThroughDetectionType>();
	
	protected TriPassThroughDetectionType(Organ detectorOrgan) {
		super(detectorOrgan, 3);
	}
	
	public static TriPassThroughDetectionType getInstance(Organ detectorOrgan) {
		TriPassThroughDetectionType detectionType = _instances.get(detectorOrgan);
		if (detectionType == null) {
			detectionType = new TriPassThroughDetectionType(detectorOrgan);
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
		TriPassThroughDetectionType instance = _instances.get(_detectorOrgan);
		if (instance == null) {
			instance = getInstance(_detectorOrgan);
		}
		return instance;
	}
}
