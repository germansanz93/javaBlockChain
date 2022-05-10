package com.gs.javablockchain;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

@Slf4j
public final class Configuration {
    private XMLConfiguration xmlConfiguration = null;
    private static Configuration configuration = null;

    public static Configuration getInstance(){
        if(configuration == null){
            configuration = new Configuration();
            configuration.xmlConfiguration = new XMLConfiguration();
            configuration.xmlConfiguration.setFileName("configuration.xml");
            try{
                configuration.xmlConfiguration.load();
            }catch (ConfigurationException e){
                log.error("Error loading configurations");
            }
        }
        return configuration;
    }
    public String getMasterNodeURL(){
        return configuration.xmlConfiguration.getString("masterNodeURL");
    }

    public int getMaxTransactionsNumber(){
        return configuration.xmlConfiguration.getInt("maxTransactionsPerBlock");
    }

    public int getDifficult(){
        return configuration.xmlConfiguration.getInt("difficult");
    }
}
