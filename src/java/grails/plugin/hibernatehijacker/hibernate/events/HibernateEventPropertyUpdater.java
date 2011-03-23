package grails.plugin.hibernatehijacker.hibernate.events;

import grails.plugin.hibernatehijacker.exception.HibernateHijackerException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.hibernate.event.PreInsertEvent;
import org.hibernate.tuple.StandardProperty;
import org.hibernate.tuple.entity.EntityMetamodel;

/**
 * Provides a convenient way of updating entity data before it's persisted to the database.
 * @author Kim A. Betti
 */
public class HibernateEventPropertyUpdater {

    // Class name => Property index cache
    private ConcurrentMap<String, EntityPropertyIndexCache> entityIndexCache = new ConcurrentHashMap<String, EntityPropertyIndexCache>();

    public void updateProperty(PreInsertEvent event, String propertyName, Object newValue) {
        Integer propertyIndex = getPropertyIndexFromCache(event, propertyName);
        updateState(event.getState(), propertyIndex, newValue);
    }

    protected Integer getPropertyIndexFromCache(PreInsertEvent event, String propertyName) {
        String entityClassName = event.getEntity().getClass().getCanonicalName();
        if (!entityIndexCache.containsKey(entityClassName)) {
            addEntityIndexesToCache(entityClassName, event);
        }

        EntityPropertyIndexCache entityPropertyIndexCache = entityIndexCache.get(entityClassName);
        return entityPropertyIndexCache.getIndex(propertyName);
    }

    protected synchronized void addEntityIndexesToCache(String entityClassName, PreInsertEvent event) {
        EntityMetamodel metamodel = event.getPersister().getEntityMetamodel();
        Map<String, Integer> propertyIndexes = extractPropertyIndexMap(metamodel);
        EntityPropertyIndexCache propertyIndexCache = new EntityPropertyIndexCache(entityClassName, propertyIndexes);
        entityIndexCache.put(entityClassName, propertyIndexCache);
    }

    protected Map<String, Integer> extractPropertyIndexMap(EntityMetamodel metaModel) {
        int i = 0;
        Map<String, Integer> propertyIndexes = new HashMap<String, Integer>();
        StandardProperty[] properties = metaModel.getProperties();
        for (StandardProperty property : properties) {
            String propertyName = property.getName();
            propertyIndexes.put(propertyName, i++);
        }

        return propertyIndexes;
    }

    protected void updateState(Object[] state, int propertyIndex, Object newValue) {
        state[propertyIndex] = newValue;
    }

    /**
     * Contains the index of each property for a given
     * entity class. This allows us to look up the index
     * without browsing through a huge object graph.
     * @author Kim A. Betti
     */
    protected class EntityPropertyIndexCache {

        private final String entityClassName;
        private final ConcurrentMap<String, Integer> propertyIndex = new ConcurrentHashMap<String, Integer>();

        public EntityPropertyIndexCache(String entityClassName, Map<String, Integer> fieldIndexes) {
            this.entityClassName = entityClassName;
            propertyIndex.putAll(fieldIndexes);
        }

        public boolean containsIndex(String propertyName) {
            return propertyIndex.containsKey(propertyName);
        }

        public Integer getIndex(String propertyName) {
            if (propertyIndex.containsKey(propertyName)) {
                return propertyIndex.get(propertyName);
            } else {
                throw new HibernateHijackerException(entityClassName + " does not contain a property named " + propertyName);
            }
        }

    }

}


