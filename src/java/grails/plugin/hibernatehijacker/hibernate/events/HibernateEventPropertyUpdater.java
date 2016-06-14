package grails.plugin.hibernatehijacker.hibernate.events;

import grails.plugin.hibernatehijacker.exception.HibernateHijackerException;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.tuple.NonIdentifierAttribute;
import org.hibernate.tuple.entity.EntityMetamodel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Provides a convenient way of updating entity data before it's persisted to the database.
 *
 * @author Kim A. Betti
 */
public class HibernateEventPropertyUpdater {

    // Class name => Property index cache
    private ConcurrentMap<String, EntityPropertyIndexCache> entityIndexCache = new ConcurrentHashMap<String, EntityPropertyIndexCache>();

    public void updateProperty(PreInsertEvent event, String propertyName, Object newValue) {
        Integer propertyIndex = getPropertyIndexFromCache(event, propertyName);
        updateState(event.getState(), propertyIndex, newValue);
    }

    private Integer getPropertyIndexFromCache(PreInsertEvent event, String propertyName) {
        String entityClassName = event.getEntity().getClass().getCanonicalName();
        if (!entityIndexCache.containsKey(entityClassName)) {
            addEntityIndexesToCache(entityClassName, event);
        }

        EntityPropertyIndexCache entityPropertyIndexCache = entityIndexCache.get(entityClassName);
        return entityPropertyIndexCache.getIndex(propertyName);
    }

    private synchronized void addEntityIndexesToCache(String entityClassName, PreInsertEvent event) {
        EntityMetamodel metamodel = event.getPersister().getEntityMetamodel();
        Map<String, Integer> propertyIndexes = extractPropertyIndexMap(metamodel);
        EntityPropertyIndexCache propertyIndexCache = new EntityPropertyIndexCache(entityClassName, propertyIndexes);
        entityIndexCache.put(entityClassName, propertyIndexCache);
    }

    private Map<String, Integer> extractPropertyIndexMap(EntityMetamodel metaModel) {
        int i = 0;
        Map<String, Integer> propertyIndexes = new HashMap<String, Integer>();
        NonIdentifierAttribute[] properties = metaModel.getProperties();
        for (NonIdentifierAttribute property : properties) {
            String propertyName = property.getName();
            propertyIndexes.put(propertyName, i++);
        }

        return propertyIndexes;
    }

    private void updateState(Object[] state, int propertyIndex, Object newValue) {
        state[propertyIndex] = newValue;
    }

    /**
     * Contains the index of each property for a given
     * entity class. This allows us to look up the index
     * without browsing through a huge object graph.
     *
     * @author Kim A. Betti
     */
    private class EntityPropertyIndexCache {

        private final String entityClassName;
        private final ConcurrentMap<String, Integer> propertyIndex = new ConcurrentHashMap<String, Integer>();

        EntityPropertyIndexCache(String entityClassName, Map<String, Integer> fieldIndexes) {
            this.entityClassName = entityClassName;
            propertyIndex.putAll(fieldIndexes);
        }

        boolean containsIndex(String propertyName) {
            return propertyIndex.containsKey(propertyName);
        }

        Integer getIndex(String propertyName) {
            if (propertyIndex.containsKey(propertyName)) {
                return propertyIndex.get(propertyName);
            }

            throw new HibernateHijackerException(entityClassName + " does not contain a property named " + propertyName);
        }
    }
}
