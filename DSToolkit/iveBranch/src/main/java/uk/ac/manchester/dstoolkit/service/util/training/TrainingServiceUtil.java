package uk.ac.manchester.dstoolkit.service.util.training;

import com.hp.hpl.jena.shared.PrefixMapping;

public interface TrainingServiceUtil {
	public PrefixMapping createPrefixMap();
	public String getNSPrefixes();
}
