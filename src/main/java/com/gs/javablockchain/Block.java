package com.gs.javablockchain;

/*
* A block has a header and a content.
* The header includes:
*   Actual Block hash obtained from the header content
*   Previous Block hash
*   Timestamp
*   POW difficulty
*   Nonce
*   Merkle tree root
* The content includes:
*   Transactions list included within the block
* */

import com.google.common.primitives.Longs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Slf4j
public class Block {
    private byte[] hash;
    private byte[] previousBlockHash;
    private long nonce;
    private long timestamp;
    private byte[] merkleTreeRoot;
    private List<Transaction> transactions;

    public Block(byte[] previousBlockHash, List<Transaction> transactions, long nonce){
        this.previousBlockHash = previousBlockHash;
        this.transactions = transactions;
        this.nonce = nonce;
        this.timestamp = System.currentTimeMillis();
        this.merkleTreeRoot = calculateMerkleTreeRoot();
        this.hash = calculateHash();
    }

    /**
     * Generate block hash from the header data
     * @return Hash SHA256
     * */
    private byte[] calculateHash(){
        byte[] hashableData = ArrayUtils.addAll(previousBlockHash, merkleTreeRoot);
        hashableData = ArrayUtils.addAll(hashableData, Longs.toByteArray(nonce));
        hashableData = ArrayUtils.addAll(hashableData, Longs.toByteArray(timestamp));
        return DigestUtils.sha256(hashableData);
    }

    /**
     * Calculate the merkle tree root with the transactions
     * @return Hash SHA256
     * */
    private byte[] calculateMerkleTreeRoot(){
        Queue<byte[]> hashQueue = transactions.stream().map(Transaction::getHash).collect(Collectors.toCollection(LinkedList::new));
        while (hashQueue.size() > 1){
            byte[] info = ArrayUtils.addAll(hashQueue.poll(), hashQueue.poll());
            hashQueue.add(DigestUtils.sha256(info));
        }
        return hashQueue.poll();
    }

    /**
     * Zeroes Qty at the beginning of the hash(POW complexity)
     * @return int number of leading zeroes
     * */
    public int getHashZeroesNumber(){
        for(int i = 0; i < getHash().length; i++){
            if(getHash()[i] != 0) return i;
        }
        return getHash().length;
    }

    public boolean isValid(){
        if(this.hash == null){
            log.error("Invalid hash");
            return false;
        }
        if(this.previousBlockHash != null && this.nonce <= 0){
            log.error("Invalid Nonce");
            return false;
        }
        if(this.merkleTreeRoot == null){
            log.error("Invalid merkle tree");
            return false;
        }
        if(this.transactions == null){
            log.error("Block with no transactions");
            return false;
        }
        if(!Arrays.equals(getMerkleTreeRoot(), calculateMerkleTreeRoot())){
            log.error("Invalid Merkle tree root");
            return false;
        }
        if(!Arrays.equals(getHash(), calculateHash())){
            log.error("Invalid hash");
            return false;
        }
        return true;

    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Block block = (Block) o;
        return Arrays.equals(hash, block.hash);
    }

    @Override
    public int hashCode(){
        return Arrays.hashCode(hash);
    }

    @Override
    public String toString(){
        return "{hash: "+hash+", previousBlockHash: "+previousBlockHash+", merkleTreeRoot: "+merkleTreeRoot+
            ", nonce: "+nonce+", timestamp: "+new Date(timestamp)+", Transactions: "+transactions.toString()+"}";
    }
}
