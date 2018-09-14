package com.learn.apache.camel.springbootcamel.processor;

import com.learn.apache.camel.springbootcamel.domain.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import static com.learn.apache.camel.springbootcamel.domain.constants.ApplicationConstants.*;

@Slf4j
@Component
public class BuildSQLProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Item item = exchange.getIn().getBody(Item.class);
        String query = StringUtils.EMPTY;

        log.info("Item in Processor is : {}", item);

        String transaction = item.getTransactionType();

        switch (transaction) {
            case ADD:
                query = createQueryInsertItems(item);
                break;
            case UPDATE:
                query = createQueryUpdateItems(item);
                break;
            case DELETE:
                query = createQueryDeleteItems(item);
                break;
            default:
                log.warn("The operation: {}, is not recognized by the system", transaction);
        }

        log.info("Final query is: {}", query);
        exchange.getIn().setBody(query);
    }

    private String createQueryInsertItems(Item item) {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO ITEMS (SKU, ITEM_DESCRIPTION, PRICE) VALUES ('");
        query.append(item.getSku()+"','"+item.getItemDescriptions()+"','"+item.getPrice()+"')");

        return query.toString();

    }

    private String createQueryUpdateItems(Item item) {
        return StringUtils.EMPTY;
    }
    private String createQueryDeleteItems(Item item) {
        return StringUtils.EMPTY;
    }




}
