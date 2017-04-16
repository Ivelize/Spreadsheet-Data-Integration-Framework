package uk.ac.manchester.dstoolkit.service.util.importexport.mappings;

import uk.ac.manchester.dstoolkit.domain.Dataspace;

public interface PredefinedMappingsLoaderService {

	public void loadMappingsForDemo(Dataspace dataspace);

	public void loadMappingsForTests(Dataspace dataspace, boolean loadMappingsForDatasourcesWithRename);

}
