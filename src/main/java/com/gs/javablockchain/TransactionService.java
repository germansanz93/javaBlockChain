package com.gs.javablockchain;

import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private TransactionPool transactionPool = new TransactionPool();

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
}
