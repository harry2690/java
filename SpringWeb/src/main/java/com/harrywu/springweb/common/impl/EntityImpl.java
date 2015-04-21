package com.harrywu.springweb.common.impl;

import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.harrywu.springweb.common.CustomResult;
import com.harrywu.springweb.common.Entity;
import com.harrywu.springweb.common.EntityUtils;
import com.harrywu.springweb.common.EntityField;

@JsonIgnoreProperties({"allProperties", "valuedPersistentProperties", "valuedProperties", "valuedTempProperties"})
public abstract class EntityImpl implements Entity {
    
    private static final long serialVersionUID = -325473009698259045L;

    protected EntityImpl() {
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean hasValue() {
        return false;
    }
    
    @SuppressWarnings("cast")
    @Override
    public <T> T getProperty(String propertyName) throws Exception {
        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302954
        // return EntityUtils.getProperty(this, propertyName);
        return (T) EntityUtils.<T>getProperty(this, propertyName); // fixed for jdk bug 6302954
    }

    @Override
    public void setProperty(String propertyName, Object value) throws Exception {
        EntityUtils.setProperty(this, propertyName, value);
    }
    
    @Override
    public Map<String, Object> getAllProperties() {
        return EntityUtils.getEntityProperties(this, null, Entity.class, this.getClass());
    }
    
    @Override
    public Map<String, Object> getValuedProperties() {
        return getValuedProperties(Entity.class, this.getClass());
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public Map<String, Object> getValuedProperties(Class superClass) {
        return getValuedProperties(superClass, this.getClass());
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public Map<String, Object> getValuedProperties(Class superClass, Class subClass) {
        return EntityUtils.getEntityValuedProperties(this, null, superClass, subClass);
    }
    
    @Override
    public Map<String, Object> getValuedPersistentProperties() {
        return getValuedPersistentProperties(Entity.class, this.getClass());
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public Map<String, Object> getValuedPersistentProperties(Class superClass) {
        return getValuedPersistentProperties(superClass, this.getClass());
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map<String, Object> getValuedPersistentProperties(Class superClass, Class subClass) {
        Map<String, Object> all = getValuedProperties(superClass, subClass);
        Map<String, Object> temp = getValuedTempProperties(superClass, subClass);
        Set<String> tempKeys = temp.keySet();
        for (String tempKey: tempKeys)
            all.remove(tempKey);
        return all;
    }

    @Override
    public Map<String, Object> getValuedTempProperties() {
        return getValuedTempProperties(Entity.class, this.getClass());
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map<String, Object> getValuedTempProperties(Class superClass) {
        return getValuedTempProperties(superClass, this.getClass());
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map<String, Object> getValuedTempProperties(Class superClass, Class subClass) {
        return EntityUtils.getEntityValuedProperties(this, EntityField.FIELD_TYPE.TEMP, superClass, subClass);
    }
    
    @Override
    public void assignValuedPropertiesToEntity(Entity entity) {
        EntityUtils.assignValuedPropertiesToEntity(this, entity);
    }

    @Override
    public void readValuesFromMap(Map<String, Object> values) {
        EntityUtils.readValuesFromMap(values, this);
    }

    @Override
    public Document convertToDocument() throws Exception {
        return null;
    }
    
    @Override
    public boolean readFromDocument(Document doc) throws Exception {
        return false;
    }
    
    @Override
    public boolean isChanged(Map<String, Object> originalValues) {
        Map<String, Object> currentValues = getValuedProperties();
        return EntityUtils.isChanged(originalValues, currentValues);
    }

//    @Override
//    public boolean isChanged(Document originalDoc) throws Exception {
//        Document currentDoc = convertToDocument();
//        if (currentDoc != null) {
//            if (originalDoc != null)
//                return !currentDoc.
//            // currentDoc != null, originalDoc == null
//            return true;
//        } else if (originalDoc != null)
//            // currentDoc == null, originalDoc != null
//            return true;
//        // currentDoc == null, originalDoc == null
//        return true;
//    }

    @Override
    public CustomResult<String> checkRequiredFields() {
        String str = doCheckRequiredFields();
        if (str != null && str.trim().length() > 0)
            return new CustomResultImpl<String>(true, str);
        return new CustomResultImpl<String>();
    }
    
    @Override
    public <T> T cloneEntity() {
        return EntityUtils.<T>cloneEntity(this);
    }
    
    protected String doCheckRequiredFields() {
        return EntityUtils.checkRequiredFields(this);
    }
}
