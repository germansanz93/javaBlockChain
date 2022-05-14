package com.gs.javablockchain.services;

import com.gs.javablockchain.entities.Transaction;
import com.gs.javablockchain.entities.TransactionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.Collection;

@Service
public class TransactionService {
    private TransactionPool transactionPool = new TransactionPool();
    @Autowired
    public TransactionService(){/*No args*/}
    public TransactionPool getTransactionPool(){
        return transactionPool;
    }

    /**
     * Add transaction to the pool
     * @param transaction to be added
     * @return true if the transaction was added
     * */
    public synchronized boolean addTransaction(Transaction transaction){
        return transactionPool.add(transaction);
    }

    /**
     * remove transaction from the pool
     * @param transaction to be removed
     * */
    public void removeTransaction(Transaction transaction){
        transactionPool.delete(transaction);
    }

    /**
     * Verify if the pool contains a list of transactions
     * @param transactionList to ve verified
     * @return true if all the transactions are in the pool
     * */
    public boolean contains(Collection<Transaction> transactionList){
        return transactionPool.contains(transactionList);
    }

    /**
     * Get transactionPool from another node
     * @param nodeUrl to query the transactionPool
     * @param restTemplate restTemplate to use
     * */
    public void getTransactionPool(URL nodeUrl, RestTemplate restTemplate){
        this.transactionPool =  restTemplate.getForObject(nodeUrl + "/transaction", TransactionPool.class);
    }
}
