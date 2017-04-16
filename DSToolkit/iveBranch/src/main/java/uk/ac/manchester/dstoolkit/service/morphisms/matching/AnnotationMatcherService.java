package uk.ac.manchester.dstoolkit.service.morphisms.matching;

import java.util.List;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.service.impl.util.SDBStoreServiceImpl;

public interface AnnotationMatcherService {
	public float[][] match(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2);
	
	public void attachMetaDataSDBStore(SDBStoreServiceImpl store);
	public SDBStoreServiceImpl getMetaDataSDBStore();
}//end AnnotationMatcherService
