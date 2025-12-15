package poc.amitk.lambda.java.eventbridge.promotions;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import poc.amitk.lambda.java.eventbridge.infra.EventBridgePublisher;
import poc.amitk.lambda.java.eventbridge.model.CatalogUpdateEvent;
import poc.amitk.lambda.java.eventbridge.model.ProductPromotion;
import software.amazon.lambda.powertools.logging.Logging;
import software.amazon.lambda.powertools.logging.LoggingUtils;
import software.amazon.lambda.powertools.utilities.JsonConfig;
import static software.amazon.lambda.powertools.utilities.EventDeserializer.extractDataFrom;


/**
 * @author amitkapps
 */
public class PromotionsPublisherHandler implements RequestHandler<ScheduledEvent, Void> {
    private Logger logger = LoggerFactory.getLogger(PromotionsPublisherHandler.class);

    private PromotionsCollectorService promotionsCollectorService;
    private EventBridgePublisher eventBridgePublisher;

    public PromotionsPublisherHandler(){
//        Add joda time ane java time handling for
        ObjectMapper mapper = JsonConfig.get().getObjectMapper();
        mapper.registerModule(new JavaTimeModule());
//        Doesn't help, still throws error -
//          Joda date/time type `org.joda.time.DateTime` not supported by default: add Module \"com.fasterxml.jackson.datatype:jackson-datatype-joda\" to enable
//          handling (through reference chain: com.amazonaws.services.lambda.runtime.events.ScheduledEvent[\"time\"])
        mapper.registerModule(new JodaModule());
        this.promotionsCollectorService = new PromotionsCollectorService();
        this.eventBridgePublisher = new EventBridgePublisher();
    }

    @Logging(logEvent = false)
    @Override
    public Void handleRequest(ScheduledEvent event, Context context) {
        logger.debug("message received, {}", event.getDetail());
        CatalogUpdateEvent catalogUpdateEvent = extractDataFrom(event).as(CatalogUpdateEvent.class);
        LoggingUtils.appendKey("sku", catalogUpdateEvent.getSku());
        logger.debug("parsed catalog update event: {}", catalogUpdateEvent);

        processProductCatalogUpdate(catalogUpdateEvent);

        return null;
    }

    private void processProductCatalogUpdate(CatalogUpdateEvent catalogUpdateEvent) {
        logger.info("processing catalog update from, {}", catalogUpdateEvent.getProductName());
        ProductPromotion productPromotion = promotionsCollectorService.gatherPromotionsForProduct(catalogUpdateEvent);
        logger.info("Received promotion: {}", productPromotion);
        logger.info("publishing the promotion");
        
        eventBridgePublisher.publishEvent(productPromotion);
    }
}
