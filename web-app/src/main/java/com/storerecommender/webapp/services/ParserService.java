package com.storerecommender.webapp.services;

import com.storerecommender.webapp.schemas.ParsedGroceryList;
import com.storerecommender.webapp.schemas.ParsedGroceryListLineItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ParserService extends LlmService {

    public ParserService() {
        super();
        log.info("ParserService created");
    }

    public ParsedGroceryList parseList(String filename, String content) {
        log.info("Parsing {}...", filename);
        var groceryList = new ParsedGroceryList();
        for (String line : content.split("\n")) {
            var lineItem = new ParsedGroceryListLineItem(line, line,1.0,null);
            groceryList.getList().add(lineItem);
        }
        log.info("Parsed {} grocery list lines", groceryList.getList().size());
        return groceryList;
    }
}
