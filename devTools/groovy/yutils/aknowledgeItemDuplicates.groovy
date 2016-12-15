import de.hybris.platform.catalog.jalo.CatalogManager
import java.util.LinkedHashSet
import java.util.Iterator
import java.util.Map
import org.apache.commons.lang.StringUtils
import java.util.Collections
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch
import de.hybris.platform.jalo.type.AttributeDescriptor
import java.lang.Integer

// you can replace EnrichedMasterToOnlineSyncCronJob with whatever the sync job is called
queryJob = "Select {PK} from {JOB} WHERE {code} = 'EnrichedMasterToOnlineSyncCronJob'";

CatalogManager catman = CatalogManager.getInstance();
searchResult = flexibleSearchService.search(queryJob)
job = modelService.getSource(searchResult.getResult().get(0))
cronJob = job.createCronjob()

for(type in job.getRootTypes()){

	String cvAD = catman.getCatalogVersionAttribute(type).getQualifier();
	LinkedHashSet adquals = new LinkedHashSet();
	Iterator additional = catman.getUniqueKeyAttributes(type).iterator();

		while(additional.hasNext()) {
			AttributeDescriptor uids = (AttributeDescriptor)additional.next();
			adquals.add("{" + uids.getQualifier() + "}");
		}

	String uids1 = StringUtils.join(adquals, ",");
	restriction = type.getProperty("catalog.sync.root.type.restriction")
	String additional1 = StringUtils.isNotBlank(restriction)?restriction:null;
	Map values = Collections.singletonMap("version", job.getSourceVersion());

	String query = "SELECT count(*) FROM ({{SELECT {" + cvAD + "} " + "FROM {" + type.getCode() + "} " + (additional1 != null?"WHERE " + additional1 + " ":"") + (additional1 != null?" AND ":" WHERE ") + "{" + cvAD + "}=?version " + "GROUP BY {" + cvAD + "}," + uids1 + " " + "HAVING count(*) > 1" + "}}) x";

	  println "found for (table="+ type.getJNDIName() + ",ItemPK="+type.getPK()+") => duplicates as such count " +((Integer)FlexibleSearch.getInstance().search(null, query, values, Integer.class).getResult().get(0)).intValue();
}
