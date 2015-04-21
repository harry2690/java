package com.harrywu.springweb.common;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.harrywu.springweb.common.EntityField.FIELD_TYPE;

@SuppressWarnings("unchecked")
public class EntityUtils {

    private static final Log log = LogFactory.getLog(EntityUtils.class);

    @Deprecated
    public static <ValueClass> ValueClass getProperty(Entity entity, String propertyName, Class<ValueClass> clazz) throws Exception {
        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302954
        // return EntityUtils.getProperty(entity, propertyName);
        return EntityUtils.<ValueClass>getProperty(entity, propertyName); // fixed for jdk bug 6302954
    }

    public static <T> T getProperty(Entity entity, String propertyName) throws Exception {
        if (propertyName != null && propertyName.trim().length() > 0)
            try {
                PropertyDescriptor propDesc = PropertyUtils.getPropertyDescriptor(entity, propertyName);
                if (propDesc != null && propDesc.getReadMethod() != null)
                    return (T) PropertyUtils.getProperty(entity, propertyName);
                return null;
            } catch (Exception e) {
                throw new Exception(entity.getClass().getName() + " getProperty(" + propertyName + "), " + e.getLocalizedMessage());
            }
        return null;
    }

    public static void setProperty(Entity entity, String propertyName, Object value) throws Exception {
        if (propertyName != null && propertyName.trim().length() > 0)
            try {
                PropertyDescriptor propDesc = PropertyUtils.getPropertyDescriptor(entity, propertyName);
                if (propDesc != null && propDesc.getWriteMethod() != null)
                    PropertyUtils.setProperty(entity, propertyName, value);
            } catch (Exception e) {
                throw new Exception(entity.getClass().getName() + " setProperty(" + propertyName + "), " + e.getLocalizedMessage());
            }
    }

    @SuppressWarnings("rawtypes")
    // get only has value property
    public static Map<String, Object> getEntityValuedProperties(Entity entity, EntityField.FIELD_TYPE entityFieldType,
                                                                Class superClass, Class subClass) {
        Map<String, Object> result = new HashMap<String, Object>();
        Field[] fields = subClass.getDeclaredFields();
        try {
            for (Field f: fields) {
                if (entityFieldType != null) {
                    EntityField anno = f.getAnnotation(EntityField.class);
                    if (anno == null)
                        continue;
                    if (anno.fieldType() != entityFieldType)
                        continue;
                }
                String fieldName = f.getName();
                if (PropertyUtils.isReadable(entity, fieldName)) {
                    Object fieldValue = PropertyUtils.getProperty(entity, fieldName);
                    if (fieldValue != null) {
                        if (fieldValue instanceof Collection<?>) {
                            if (((Collection<?>) fieldValue).size() > 0)
                                result.put(f.getName(), fieldValue);
                        } else {
                            if (fieldValue instanceof String) {
                                if (fieldValue.toString().trim().length() > 0)
                                    result.put(f.getName(), fieldValue);
                            } else
                                result.put(f.getName(), fieldValue);
                        }
                    }
                }
            }
            // is sub class of Entity
            if (superClass.isAssignableFrom(subClass.getSuperclass()))
                result.putAll(getEntityValuedProperties(entity, entityFieldType, superClass, subClass.getSuperclass()));
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            return new HashMap<String, Object>();
        }
        return result;
    }

    @SuppressWarnings("rawtypes")
    // get all properties include null properties
    public static Map<String, Object> getEntityProperties(Entity entity, EntityField.FIELD_TYPE entityFieldType,
                                                          Class superClass, Class subClass) {
        Map<String, Object> result = new HashMap<String, Object>();
        Field[] fields = subClass.getDeclaredFields();
        try {
            for (Field f : fields) {
                if (entityFieldType != null) {
                    EntityField anno = f.getAnnotation(EntityField.class);
                    if (anno == null)
                        continue;
                    if (anno.fieldType() != entityFieldType)
                        continue;
                }
                String fieldName = f.getName();
                if (PropertyUtils.isReadable(entity, fieldName)) {
                    Object fieldValue = PropertyUtils.getProperty(entity, fieldName);
                    result.put(f.getName(), fieldValue);
                }
            }
            // is sub class of Entity
            if (superClass.isAssignableFrom(subClass.getSuperclass()))
                result.putAll(getEntityProperties(entity, entityFieldType, superClass, subClass.getSuperclass()));
        } catch(Exception e) {
            log.error(e.getLocalizedMessage(), e);
            return new HashMap<String, Object>();
        }
        return result;
    }
    
    public static void assignValuedPropertiesToEntity(Entity source, Entity target) {
        Map<String, Object> values = source.getValuedProperties();
        Set<String> propertyNames = values.keySet();
        for (String propertyName: propertyNames) {
            try {
                target.setProperty(propertyName, values.get(propertyName));
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }

    public static void readValuesFromMap(Map<String, Object> values, Entity target) {
        Set<String> propertyNames = values.keySet();
        for (String propertyName: propertyNames) {
            try {
                target.setProperty(propertyName, values.get(propertyName));
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
            }
        }
    }
    
    public static boolean isChanged(Map<String, Object> originalValues, Map<String, Object> currentValues) {
        Set<String> props = originalValues.keySet();
        for (String prop: props) {
            Object origValue = originalValues.get(prop);
            Object currValue = currentValues.get(prop);
            if (currValue == null)
                return true;
            if (!origValue.equals(currValue))
                return true;
            currentValues.remove(prop);
        }
        if (currentValues.size() > 0)
            return true;
        return false;
    }
    
    public static String checkRequiredFields(Entity entity) {
        StringBuffer out = new StringBuffer();
        Map<String, Object> values = getEntityProperties(entity, FIELD_TYPE.REQUIRED,
                                                         Entity.class, entity.getClass());
        Set<String> propertyNames = values.keySet();
        for (String propertyName: propertyNames) {
            String message = entity.getClass().getSimpleName() + "." + propertyName + " 蝛箇";
            if (values.get(propertyName) == null) {
                appendMessage(out, message);
            } else {
                Object val = values.get(propertyName);
                if (val instanceof String) {
                    if (((String) val).trim().length() == 0)
                        appendMessage(out, message);
                } else {
                    if (val instanceof Entity) {
                        String str = checkRequiredFields((Entity) val);
                        if (str.length() > 0)
                            appendMessage(out, str);
                    } else
                        if (val instanceof Collection<?>) {
                            // Collection ���銝��
                            if (((Collection<?>) val).size() == 0)
                                appendMessage(out, message);
                            else
                                for (Object obj: (Collection<?>) val)
                                    if (obj instanceof Entity) {
                                        String str = checkRequiredFields((Entity) obj);
                                        if (str.length() > 0)
                                            appendMessage(out, str);
                                    } else
                                        if (obj instanceof String) {
                                            // String ��Collection �芾��摰孵��
                                            if (obj == null || ((String) obj).trim().length() == 0)
                                                appendMessage(out, message);
                                            break;
                                        }
                        }
                }
            }
        }
        return out.toString();
    }
    
    public static void appendMessage(StringBuffer out, String message) {
        if (message != null && message.trim().length() > 0) {
            if (out.length() > 0)
                out.append(", " + message);
            else
                out.append(message);
        }
    }
    
    public static <T> T cloneEntity(Entity source) {
        try {
            return (T) BeanUtils.cloneBean(source);
        } catch(Exception e) {
        }
        return null;
    }
    
    public static boolean isPropertyExists(Entity entity, String propertyName) {
        PropertyDescriptor propDesc;
        try {
            propDesc = PropertyUtils.getPropertyDescriptor(entity, propertyName);
            return (propDesc != null);
        } catch(Exception e) {
        }
        return false;
    }
}
