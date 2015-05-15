package grails.plugin.hibernatehijacker.hibernate.events;

import grails.plugins.hawkeventing.EventBroker;
import grails.util.GrailsNameUtils;

import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.event.*;

/**
 * Brute force approach.
 * 
 * This could probably be implemented in a more dynamic
 * fashion using Groovy, but with more overhead.
 * 
 * @author Kim A. Betti
 */
@SuppressWarnings("serial")
public class HibernateEventListener implements AutoFlushEventListener, DeleteEventListener, DirtyCheckEventListener,
EvictEventListener, FlushEventListener, FlushEntityEventListener, LoadEventListener,
InitializeCollectionEventListener, LockEventListener, MergeEventListener, PersistEventListener,
PostDeleteEventListener, PostInsertEventListener, PostLoadEventListener, PostUpdateEventListener,
PreDeleteEventListener, PreInsertEventListener, PreLoadEventListener, PreUpdateEventListener,
RefreshEventListener, ReplicateEventListener, SaveOrUpdateEventListener {


    private EventBroker eventBroker;

    public void setEventBroker(EventBroker eventBroker) {
        this.eventBroker = eventBroker;
    }

    @Override
    public void onSaveOrUpdate(SaveOrUpdateEvent event) throws HibernateException {
        publishEvent("saveOrUpdate", event, event.getEntity());
    }

    @Override
    public void onReplicate(ReplicateEvent event) throws HibernateException {
        publishEvent("replicate", event);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void onRefresh(RefreshEvent event, Map arg1) throws HibernateException {
        onRefresh(event);
    }

    @Override
    public void onRefresh(RefreshEvent event) throws HibernateException {
        publishEvent("refresh", event);
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        publishEvent("preUpdate", event, event.getEntity());
        return false;
    }

    @Override
    public void onPreLoad(PreLoadEvent event) {
        publishEvent("preLoad", event, event.getEntity());
    }

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        publishEvent("preInsert", event, event.getEntity());
        return false;
    }

    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        publishEvent("preDelete", event, event.getEntity());
        return false;
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        publishEvent("postUpdate", event, event.getEntity());
    }

    @Override
    public void onPostLoad(PostLoadEvent event) {
        publishEvent("postLoad", event, event.getEntity());
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        publishEvent("postInsert", event, event.getEntity());
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        publishEvent("postDelete", event, event.getEntity());
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void onPersist(PersistEvent event, Map arg1) throws HibernateException {
        onPersist(event);
    }

    @Override
    public void onPersist(PersistEvent event) throws HibernateException {
        publishEvent("persist", event);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void onMerge(MergeEvent event, Map arg1) throws HibernateException {
        onMerge(event);
    }

    @Override
    public void onMerge(MergeEvent event) throws HibernateException {
        publishEvent("merge", event, event.getEntity());
    }

    @Override
    public void onLock(LockEvent event) throws HibernateException {
        publishEvent("lock", event);
    }

    @Override
    public void onInitializeCollection(InitializeCollectionEvent event) throws HibernateException {
        publishEvent("initializeCollection", event);
    }

    @Override
    public void onLoad(LoadEvent event, LoadType arg1) throws HibernateException {
        publishEvent("load", event);
    }

    @Override
    public void onFlushEntity(FlushEntityEvent event) throws HibernateException {
        publishEvent("flushEntity", event, event.getEntity());
    }

    @Override
    public void onFlush(FlushEvent event) throws HibernateException {
        publishEvent("flush", event);
    }

    @Override
    public void onEvict(EvictEvent event) throws HibernateException {
        publishEvent("evict", event);
    }

    @Override
    public void onDirtyCheck(DirtyCheckEvent event) throws HibernateException {
        publishEvent("dirtyCheck", event);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void onDelete(DeleteEvent event, Set arg1) throws HibernateException {
        onDelete(event);
    }

    @Override
    public void onDelete(DeleteEvent event) throws HibernateException {
        publishEvent("delete", event);
    }

    @Override
    public void onAutoFlush(AutoFlushEvent event) throws HibernateException {
        publishEvent("autoFlush", event);
    }

    private void publishEvent(String eventName, AbstractEvent event) {
        publishEvent(eventName, event, null);
    }

    /**
     * Publishes events on the format hibernate.<event-name>.<entity-name>
     */
    private void publishEvent(String eventName, AbstractEvent event, Object entity) {
        StringBuilder fullEventName = new StringBuilder(100);
        fullEventName.append("hibernate.").append(eventName);

        if (entity != null) {
            final String entityName = GrailsNameUtils.getPropertyName(entity.getClass());
            fullEventName.append(".").append(entityName);
        }

        eventBroker.publish(fullEventName.toString(), event);
    }

}
