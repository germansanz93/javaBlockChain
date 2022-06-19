package com.gs.javablockchain.controllers;

import com.gs.javablockchain.entities.Transaction;
import com.gs.javablockchain.entities.TransactionPool;
import com.gs.javablockchain.services.NodeService;
import com.gs.javablockchain.services.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("transaction")
public class TransactionRestController {
    private final TransactionService transactionService;
    private final NodeService nodeService;

    @Autowired
    public TransactionRestController(TransactionService transactionService, NodeService nodeService) {
        this.transactionService = transactionService;
        this.nodeService = nodeService;
    }

    /**
     * Get the transaction pool pending to be added in a block
     * @return JSON transaction pool
     * */
    @GetMapping
    TransactionPool getTransactionPool(){
        return transactionService.getTransactionPool();
    }

    /**
     * Add a transaction to the pool
     * @param transaction Transaction to be added to the pool
     * @param propagate if the transaction must be sent to other nodes
     * @param response 202 code if the transaction is added to the pool or 406 in other cases
     * */
    @PostMapping
    void addTransaction(@RequestBody Transaction transaction, @RequestParam(required = false) Boolean propagate, HttpServletResponse response){
        log.info("Add transaction {}", Base64.encodeBase64String(transaction.getHash()));
        boolean isSuccess = transactionService.addTransaction(transaction);
        if(isSuccess){
            log.info("transaction added");
            response.setStatus(HttpServletResponse.SC_ACCEPTED);

            if(Objects.nonNull(propagate) && propagate){
                nodeService.registerOnNeighbourNodes("transaction", transaction);
            }
        } else {
            log.info("Transaction failed, will not be added");
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
    }
}
