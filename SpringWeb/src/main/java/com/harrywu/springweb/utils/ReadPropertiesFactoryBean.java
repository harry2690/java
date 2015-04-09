package com.harrywu.springweb.utils;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.util.CollectionUtils;

public class ReadPropertiesFactoryBean extends PropertiesFactoryBean {
    
    private Properties[] importProperties;

    @Override
    protected Properties mergeProperties() throws IOException {
        Properties props = super.mergeProperties();
        if (importProperties != null) {
            Properties mergedProps = doMergeProperties(importProperties, props);
            doReplacePlaceholders(props, mergedProps);
        } else
            doReplacePlaceholders(props, props);
        return props;
    }

    private Properties doMergeProperties(Properties[] importProperties, Properties props) {
        Properties result = new Properties();

        for (Properties importProp: this.importProperties)
            CollectionUtils.mergePropertiesIntoMap(importProp, result);
        
        CollectionUtils.mergePropertiesIntoMap(props, result);

        return result;
    }

    private void doReplacePlaceholders(Properties props, Properties refProps) {
        PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper();
        for (String key: props.stringPropertyNames()) {
            String strVal = props.getProperty(key);
            if (strVal != null) {
                String resolvedValue = helper.replacePlaceholders(strVal, refProps);
                props.put(key, resolvedValue);
            }
        }
    }
    
    public void setImportProperties(Properties importProperties) {
        this.importProperties = new Properties[] {importProperties};
    }

    public void setImportPropertiesArray(Properties[] importPropertiesArray) {
        this.importProperties = importPropertiesArray;
    }
}
