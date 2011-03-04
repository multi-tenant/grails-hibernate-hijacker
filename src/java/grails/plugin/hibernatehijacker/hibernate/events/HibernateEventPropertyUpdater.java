package grails.plugin.hibernatehijacker.hibernate.events;

import grails.plugin.hibernatehijacker.exception.HibernateHijackerException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.event.AbstractPreDatabaseOperationEvent;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.tuple.StandardProperty;
import org.hibernate.tuple.entity.EntityMetamodel;

/**
 * Provides a convenient way of updating entity data before it's persisted to the database.
 * @author Kim A. Betti
 */
public class HibernateEventPropertyUpdater {

    // Mapping key => property index
    private Map<Integer, Integer> propertyIndexMapping = new HashMap<Integer, Integer>();

    public void updateProperty(PreInsertEvent event, String propertyName, Object newValue) {
        int paramIndex = getPropertyIndex(event, propertyName);
        updateState(event.getState(), paramIndex, newValue);
    }

    public void updateProperty(PreUpdateEvent event, String propertyName, Object newValue) {
        int paramIndex = getPropertyIndex(event, propertyName);
        updateState(event.getState(), paramIndex, newValue);
    }

    protected int getPropertyIndex(AbstractPreDatabaseOperationEvent event, String propertyName) {
        int mappingKey = createMappingKey(event.getClass(), propertyName);
        if (!propertyIndexMapping.containsKey(mappingKey)) {
            updateCacheWithPropertyIndex(event, propertyName, mappingKey);
        }

        return propertyIndexMapping.get(mappingKey);
    }

    protected int createMappingKey(Class<?> entityClass, String propertyName) {
        HashCodeBuilder keyBuilder = new HashCodeBuilder();
        keyBuilder.append(entityClass.getCanonicalName()).append(propertyName);
        return keyBuilder.hashCode();
    }

    protected void updateCacheWithPropertyIndex(AbstractPreDatabaseOperationEvent event, String propertyName, int mappingKey) {
        EntityMetamodel metaModel = event.getPersister().getEntityMetamodel();
        int propertyIndex = getPropertyIndexFromMetaModel(metaModel, propertyName);
        propertyIndexMapping.put(mappingKey, propertyIndex);
    }

    protected int getPropertyIndexFromMetaModel(EntityMetamodel metaModel, String propertyName) {
        int i = 0;
        StandardProperty[] properties = metaModel.getProperties();
        for (StandardProperty property : properties) {
            if (property.getName().equals(propertyName)) {
                return i;
            } else {
                i++;
            }
        }

        throw new HibernateHijackerException("Unable to find property index for: " + propertyName);
    }

    protected void updateState(Object[] state, int propertyIndex, Object newValue) {
        state[propertyIndex] = newValue;
    }

}
