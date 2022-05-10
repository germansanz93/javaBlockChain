package com.gs.javablockchain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@Slf4j
public class BlockService {

    private final TransactionService transactionService;

    private Blockchain blockchain = Blockchain.builder().build();

    public BlockService (TransactionService transactionService){
        this.transactionService = transactionService;
    }

    public Blockchain getBlockchain(){
        return blockchain;
    }
    private final int MAX_TX_QTY = 10; //TODO remove when Configuration is present
    private final int DIFFICULT = 3; //TODO remove when Configuration is present
    /**
     * Add block to the blockchain
     * @param block to be added
     * @return true if the block is added to the blockchain
     * */
    public synchronized boolean addBlock(Block block) {
        if(verifyBlock(block)){
            this.blockchain.addBlock(block);
            block.getTransactions().forEach(transactionService::removeTransaction);
            log.info("Block added to the chain");
            return true;
        } else return false;
    }

    /**
     * Validate a block
     * @param block to ve verified
     * @return true if the block is valid
     * */
    private boolean verifyBlock(Block block){
        if(!block.isValid()) return false;
        if(!blockchain.isEmpty()){
            byte[] lastBlockHash = blockchain.getLastBlock().getHash();
            if(!Arrays.equals(block.getPreviousBlockHash(), lastBlockHash)){
                log.error("Invalid previous Block Hash");
                return false;
            }
        } else {
            if(block.getPreviousBlockHash() != null) {
                log.error("Previous Hash invalid, must be null");
                return false;
            }
        }
        if(block.getTransactions().size() > MAX_TX_QTY){ //TODO remove constant here
            log.error("Transactions qty is over limit");
            return false;
        }
        if(!transactionService.contains(block.getTransactions())){
            log.error("Some transactions are not in pool");
            return false;
        }
        if(block.getHashZeroesNumber() != DIFFICULT) { //TODO remove constant here
           log.error("Block difficult invalid");
            return false;
        }
        return true;
    }

}
