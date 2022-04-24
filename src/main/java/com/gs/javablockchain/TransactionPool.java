package com.gs.javablockchain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TransactionPool {

    private Set<Transaction> pool = new HashSet<>();

    /**
     * add a transaction to the pool
     * @param transaction to be added
     * @return true if the transaction is valid and added to the pool
     */
    public synchronized boolean add(Transaction transaction){
        if(transaction.isValid()){
            pool.add(transaction);
            return true;
        }
        return false;
    }

    /**
     * Delete transaction from the pool
     * @param transaction to delete
     * */
    public void delete(Transaction transaction){
        pool.remove(transaction);
    }

    /**
     * verify if the pool contains all the transactions from a list
     * @param transactions list to be verified
     * @return true if all the transactions are in the pool
     * */
    public boolean contains(Collection<Transaction> transactions){
        return pool.containsAll(transactions);
    }
}
