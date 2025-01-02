package com.github.jenkaby.transformer;

import com.github.jenkaby.model.Constant;
import com.github.jenkaby.model.MessageLogExpectation;
import io.cucumber.java.DataTableType;

import java.util.Map;

public class MessageLogExpectationTransformer {

    @DataTableType
    public MessageLogExpectation transform(Map<String, String> table) {
        return MessageLogExpectation.builder()
                .messageId(Constant.getTxnId(table))
                .encoding(table.get("encoding"))
                .modifiedBy(table.get("modifiedBy"))
                .status(table.get("status"))
                .topic(table.get("topic"))
                .build();
    }
}
