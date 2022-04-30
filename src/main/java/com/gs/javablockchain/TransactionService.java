package com.gs.javablockchain;

import org.springframework.stereotype.Service;

import java.util.Collection;

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

    /**
     * remove transaction from the pool
     * @param transaction to be removed
     * */
    public void removeTransaction(Transaction transaction){
        transactionPool.delete(transaction);
    }

    /**
     * Verify if the pool contains a list of transactions
     * @param transactionList
     * @return true if all the transactions are in the pool
     * */
    public boolean contains(Collection<Transaction> transactionList){
        return transactionPool.contains(transactionList);
    }

}
