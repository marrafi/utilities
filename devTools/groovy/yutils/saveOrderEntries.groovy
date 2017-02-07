import static de.hybris.platform.catalog.constants.CatalogConstants.*
import static com.outiz.ecom.common.constants.OutizConstants.*
import static com.outiz.ecom.core.constants.OutizCoreConstants.*
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.tx.Transaction
import de.hybris.platform.tx.TransactionBody
import de.hybris.platform.util.Config
import de.hybris.platform.jalo.JaloSession
import de.hybris.platform.europe1.jalo.Europe1PriceFactory
import java.sql.Connection

	baseSiteService.setCurrentBaseSite("outiz",false)
	JaloSession currentSession = JaloSession.getCurrentSession();
	currentSession.setAttribute("Europe1PriceFactory_UTG",
								currentSession.getEnumerationManager().getEnumerationValue("UserTaxGroup", "fr-taxes"));

	currentSession.setAttribute("Europe1PriceFactory_UPG",
								Europe1PriceFactory.getInstance().getUserPriceGroup(Config.getParameter(B2C_DEFAULT_PRICE_GROUP)));
	currentSession.setAttribute(
	  "Europe1PriceFactory_PDG",
	  currentSession.getEnumerationManager()
	  .getEnumerationValue("UserDiscountGroup", Config.getParameter(B2C_DEFAULT_DISCOUNT_GROUP)));

	tx = Transaction.current()
	tx.getTXBoundConnection().setAutoCommit(true)
	tx.getTXBoundConnection().setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED)
	tx.setRollbackOnCommitError(false)

	tx.execute( new TransactionBody(){
 
      def execute(){
        searchRestrictionService.disableSearchRestrictions();
        user = userService.getUserForUID("med.arrafi@outizrecette.fr")
        
        contentCat = catalogVersionService.getCatalogVersion(CONTENT_CATALOG,VERSION_ONLINE)
        productCat= catalogVersionService.getCatalogVersion(WEB_CATALOG,VERSION_ONLINE)
        sessionService.setAttribute(SESSION_CATALOG_VERSIONS,java.util.Arrays.asList(contentCat,productCat))
        searchRestrictionService.disableSearchRestrictions();
        userService.setCurrentUser(user);
        totalEntries = 0
        totalNeeded = 100
        start=0
        while(totalEntries<5 && start<11000){
          try{
            catalog = outizOfferService.getCatalogVersionOnline()
            FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery("SELECT {PK} FROM {Offer!} where {catalogVersion}="+catalog.getPk());
            flexibleSearchQuery.setStart(start);
            flexibleSearchQuery.setCount(totalNeeded);
            results = flexibleSearchService.search(flexibleSearchQuery).getResult();
            
            success=0
            for (product in results){
              try{
                outizCartFacade.addToCart(product.getCode(),1,null)
                success++
                  println "success value "+success
              }catch(Exception e){
                Transaction.current().clearRollbackOnly()
                println "failed on offer with code " + product.getCode() + " and Id " + product.getId()
                e.getMessage()
                
                for(element in e.getStackTrace()){
                  println element
                }        
                continue
                  }
              
            }
          }catch(Exception e){
            Transaction.current().clearRollbackOnly()
            println "failed on section ["+ start +","+ (start+20) + "]" 
            e.getMessage()
            
            for(element in e.getStackTrace()){
              println element
            }        
          }
          totalEntries += success
          println totalEntries + " on this iteration"
          start += 20
          if(totalEntries >= 5){
            return totalEntries
          }
          
        }
        return totalEntries
      }
    }
)