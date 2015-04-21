package com.harrywu.springweb.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import com.harrywu.springweb.exception.MyException;

/**
 * AbstractPersistentObject
 * @author Huseyin OZVEREN
 */
public abstract class AbstractPersistentObject {
    private String id = IdGenerator.createId();

    private Integer version = null;


    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer value) {
        this.version = value;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null ||
                !(o instanceof AbstractPersistentObject )) {
            
            return false;
        }
            
        AbstractPersistentObject other  = (AbstractPersistentObject ) o;
            
        // if the id is missing, return false
        if (id == null) return false;
            
        // equivalence by id
        return id.equals(other.getId());
    }

    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        } else {
            return super.hashCode();
        }
    }

    public String toString() {
        return this.getClass().getName()
            + "[id=" + id + "]";
    }

    public boolean isCreation() {
        return version == null;
    }

    private void putClonedObject(Map<String, AbstractPersistentObject> clonedObjects, AbstractPersistentObject clone) {
        clonedObjects.put(clone.getClass().getName() + ":" + clone.getId(), clone);
    }

    private boolean existClonedObject(Map<String, AbstractPersistentObject> clonedObjects, AbstractPersistentObject object) {
        return clonedObjects.containsKey(object.getClass().getName() + ":" + object.getId());
    }

    private AbstractPersistentObject getClonedObject(Map<String, AbstractPersistentObject> clonedObjects, AbstractPersistentObject object) {
        return clonedObjects.get(object.getClass().getName() + ":" + object.getId());
    }

    @SuppressWarnings("unchecked")
	private Object copy(Map<String, AbstractPersistentObject> clonedObjects) throws CloneNotSupportedException, IllegalArgumentException, IllegalAccessException, InstantiationException {
        AbstractPersistentObject cloneOfThis = (AbstractPersistentObject) super.clone();
        putClonedObject(clonedObjects, cloneOfThis);
        
        /**
         * get all fields of class and super class
         */
        Collection<Field> fields = new ArrayList<Field>();
        fields.addAll(Arrays.asList(cloneOfThis.getClass().getDeclaredFields()));
        Class superClass = cloneOfThis.getClass().getSuperclass();
        while (AbstractPersistentObject.class.isAssignableFrom(superClass)) {
            fields.addAll(Arrays.asList(superClass.getDeclaredFields()));
            superClass = superClass.getSuperclass();
        }
        
        for (Field field : fields) {
            boolean changeAccessible = false;
            //if (Modifier.isPrivate(field.getModifiers())) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            if (field.get(cloneOfThis) instanceof AbstractPersistentObject) {
                AbstractPersistentObject object = (AbstractPersistentObject) field.get(cloneOfThis);
                if (existClonedObject(clonedObjects, object)) {
                    field.set(cloneOfThis, getClonedObject(clonedObjects, object));
                } else {
                    field.set(cloneOfThis, object.copy(clonedObjects));
                }
            } else if (field.get(cloneOfThis) instanceof Collection) {
                Collection<Object> objects = (Collection) field.get(cloneOfThis);
                Collection<Object> clones = new HashSet<Object>(); //(Collection) field.get(cloneOfThis).getClass().newInstance();
                for (Iterator<Object> iter = objects.iterator(); iter.hasNext();) {
                    Object obj = iter.next();
                    if (obj instanceof AbstractPersistentObject) {
                        AbstractPersistentObject object = (AbstractPersistentObject) obj;
                        if (existClonedObject(clonedObjects, object)) {
                            clones.add(getClonedObject(clonedObjects, object));
                        } else {
                            clones.add(object.copy(clonedObjects));
                        }
                    }
                }
                field.set(cloneOfThis, clones);
            }
            if (changeAccessible) {
                field.setAccessible(false);
            }
        }
        return cloneOfThis;
    }

    /**
     * @return return a copy of this object
     */
    public Object copy() {
        try {
            Map<String, AbstractPersistentObject> clonedObjects = new HashMap<String, AbstractPersistentObject>();
            return copy(clonedObjects);
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new MyException(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new MyException(e);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new MyException(e);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new MyException(e);
        }
    }

    public void regenerateId() {
        id = IdGenerator.createId();
        version = null;
    }

    public AbstractPersistentObject() {
    }
}
