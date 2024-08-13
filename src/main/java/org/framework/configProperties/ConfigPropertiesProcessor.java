package org.framework.configProperties;

import org.framework.annotations.Autowired;
import org.framework.annotations.ConfigurationProperties;
import org.framework.annotations.EnableConfigurationProperties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

public class ConfigPropertiesProcessor {
    Properties properties = new Properties();

    public ConfigPropertiesProcessor(){
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        try {
            // load the properties file
            properties.load(new FileInputStream(rootPath + "/config.properties"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object process(Class<?> primaryClass){
        try {

            EnableConfigurationProperties annotation = primaryClass.getAnnotation(EnableConfigurationProperties.class);
            Class<?> configClass = annotation.value();
            ConfigurationProperties configurationProperties = configClass.getAnnotation(ConfigurationProperties.class);
            String prefix = configurationProperties.prefix();
            Object configInstance = configClass.getConstructor().newInstance();
            if(!prefix.isEmpty()){
                prefix += ".";
            }else{
                prefix = "";
            }
                for (Field field : configInstance.getClass().getDeclaredFields()) {
                        field.setAccessible(true);
                        String propertyValue = properties.getProperty(prefix+field.getName());
                        if(field.getType() == int.class || field.getType() == Integer.class) {
                            field.set(configInstance, Integer.parseInt(propertyValue));
                        }
                        else if(field.getType() == double.class || field.getType() == Double.class) {
                            field.set(configInstance, Double.parseDouble(propertyValue));
                        }else{
                            field.set(configInstance,propertyValue);
                        }
                }
            return configInstance;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
