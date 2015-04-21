package com.harrywu.springweb.common;

import java.io.Serializable;
import java.util.Map;

import org.w3c.dom.Document;

public interface Entity extends Serializable {
    
    void clear();
    
    boolean hasValue();
    
    <T> T getProperty(String propertyName) throws Exception;
    
    void setProperty(String propertyName, Object value) throws Exception;

    Map<String, Object> getAllProperties();

    /*
     * Get property has value and return Map
     * From this up to Entity
     */
    Map<String, Object> getValuedProperties();
    /*
     * Get property has value and return Map,
     * From subClass up to superClass
     */
    @SuppressWarnings("rawtypes")
    Map<String, Object> getValuedProperties(Class superClass, Class subClass);
    /*
     * Get property has value and return Map,
     * From this up to superClass
     */
    @SuppressWarnings("rawtypes")
    Map<String, Object> getValuedProperties(Class superClass);

    /*
     * Get persistent(database) property has value and return Map
     */
    Map<String, Object> getValuedPersistentProperties();
    @SuppressWarnings("rawtypes")
    Map<String, Object> getValuedPersistentProperties(Class superClass);
    @SuppressWarnings("rawtypes")
    Map<String, Object> getValuedPersistentProperties(Class superClass, Class subClass);

    /*
     * Get temp property has value and return Map
     */
    Map<String, Object> getValuedTempProperties();
    @SuppressWarnings("rawtypes")
    Map<String, Object> getValuedTempProperties(Class superClass);
    @SuppressWarnings("rawtypes")
    Map<String, Object> getValuedTempProperties(Class superClass, Class subClass);

    void assignValuedPropertiesToEntity(Entity entity);

    void readValuesFromMap(Map<String, Object> values);
    
    Document convertToDocument() throws Exception;
    
    boolean readFromDocument(Document doc) throws Exception;
    
    boolean isChanged(Map<String, Object> originalValues);

//    boolean isChanged(Document originalDoc) throws Exception;

    CustomResult<String> checkRequiredFields();

    <T> T cloneEntity();
}
