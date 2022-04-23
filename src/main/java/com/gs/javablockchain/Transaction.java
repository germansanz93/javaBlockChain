package com.gs.javablockchain;

import com.google.common.primitives.Longs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Transaction {
    /*
     * A transaction includes:
     * - Transaction Hash
     * - Issuer
     * - Receiver
     * - Amount
     * - Timestamp
     * - Signature with Issuer private key
     *  */

    private byte[] hash;
    private byte[] issuer;
    private byte[] receiver;
    private double amount;
    private byte[] signature;
    private long timestamp;

    public Transaction(byte[] issuer, byte[] receiver, double amount, byte[] signature){
        this.issuer = issuer;
        this.receiver = receiver;
        this.amount = amount;
        this.signature = signature;
        this.timestamp = System.currentTimeMillis();
        this.hash = calculateTransactionHash();
    }

    /**
     * Transaction content, signed by the issuer with his private key
     * @Return byte[] byte array that represents the transaction content
     * */
    public byte[] getTransactionContent(){
        byte[] content = ArrayUtils.addAll(String.valueOf(amount).getBytes(StandardCharsets.UTF_8));
        content = ArrayUtils.addAll(content, issuer);
        content = ArrayUtils.addAll(content, receiver);
        content = ArrayUtils.addAll(content, signature);
        content = ArrayUtils.addAll(content, Longs.toByteArray(timestamp));
        return content;
    }

    /**
     * Calculate the transaction's hash
     * @Return Hash SHA256
     * */
    public byte[] calculateTransactionHash(){
        return DigestUtils.sha256(getTransactionContent());
    }
}
