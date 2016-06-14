package grails.plugin.hibernatehijacker.hibernate;

import grails.plugins.hawkeventing.EventBroker;
import org.hibernate.*;
import org.hibernate.cache.spi.QueryCache;
import org.hibernate.cache.spi.Region;
import org.hibernate.cache.spi.UpdateTimestampsCache;
import org.hibernate.cfg.Settings;
import org.hibernate.context.spi.CurrentSessionContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.SQLFunctionRegistry;
import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.profile.FetchProfile;
import org.hibernate.engine.query.spi.QueryPlanCache;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.engine.spi.NamedQueryDefinition;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.engine.spi.SessionBuilderImplementor;
import org.hibernate.exception.spi.SQLExceptionConverter;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.internal.NamedQueryRepository;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.EntityNotFoundDelegate;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.stat.Statistics;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;

import javax.naming.NamingException;
import javax.naming.Reference;
import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


/**
 * Proxy for Session Factory.
 *
 * @author Sandeep Poonia
 */

public class SessionFactoryProxy extends org.codehaus.groovy.grails.orm.hibernate.SessionFactoryProxy {
    private static final long serialVersionUID = 1;

    private static final String OPEN_SESSION = "openSession";
    private static final String GET_CURRENT_SESSION = "getCurrentSession";
    private static final String INTERCEPTED_SESSION_EVENT_NAME = "hibernate.sessionCreated";

    private EventBroker eventBroker;
    private SessionFactoryImpl realSessionFactory;
    private CurrentSessionContext defaultCurrentSessionContext;

    public SessionFactoryProxy(EventBroker eventBroker, SessionFactoryImpl realSessionFactory) {
        this.eventBroker = eventBroker;
        this.realSessionFactory = realSessionFactory;
    }

    public void setDefaultCurrentSessionContext(CurrentSessionContext defaultCurrentSessionContext) {
        this.defaultCurrentSessionContext = defaultCurrentSessionContext;
    }

    public SessionFactoryImpl getCurrentSessionFactory() {
        return realSessionFactory;
    }

    @Override
    public SessionFactoryOptions getSessionFactoryOptions() {
        return getCurrentSessionFactory().getSessionFactoryOptions();
    }

    @Override
    public SessionBuilderImplementor withOptions() {
        return getCurrentSessionFactory().withOptions();
    }

    @Override
    public Session openSession() throws HibernateException {
        Session session = getCurrentSessionFactory().openSession();
        eventBroker.publish(INTERCEPTED_SESSION_EVENT_NAME, session);
        return session;
    }

    @Override
    public Session getCurrentSession() throws HibernateException {
        return defaultCurrentSessionContext.currentSession();
    }

    @Override
    public StatelessSessionBuilder withStatelessOptions() {
        return getCurrentSessionFactory().withStatelessOptions();
    }

    @Override
    public StatelessSession openStatelessSession() {
        return getCurrentSessionFactory().openStatelessSession();
    }

    @Override
    public StatelessSession openStatelessSession(Connection connection) {
        return getCurrentSessionFactory().openStatelessSession(connection);
    }

    @Override
    public ClassMetadata getClassMetadata(Class entityClass) {
        return getCurrentSessionFactory().getClassMetadata(entityClass);
    }

    @Override
    public ClassMetadata getClassMetadata(String entityName) {
        return getCurrentSessionFactory().getClassMetadata(entityName);
    }

    @Override
    public CollectionMetadata getCollectionMetadata(String roleName) {
        return getCurrentSessionFactory().getCollectionMetadata(roleName);
    }

    @Override
    public Map<String, ClassMetadata> getAllClassMetadata() {
        return getCurrentSessionFactory().getAllClassMetadata();
    }

    @Override
    public Map getAllCollectionMetadata() {
        return getCurrentSessionFactory().getAllCollectionMetadata();
    }

    @Override
    public Statistics getStatistics() {
        return getCurrentSessionFactory().getStatistics();
    }

    @Override
    public void close() throws HibernateException {
        getCurrentSessionFactory().close();
    }

    @Override
    public boolean isClosed() {
        return getCurrentSessionFactory().isClosed();
    }

    @Override
    public Cache getCache() {
        return getCurrentSessionFactory().getCache();
    }

    @Override
    public void evict(Class persistentClass) throws HibernateException {
        getCurrentSessionFactory().evict(persistentClass);
    }

    @Override
    public void evict(Class persistentClass, Serializable id) throws HibernateException {
        getCurrentSessionFactory().evict(persistentClass, id);
    }

    @Override
    public void evictEntity(String entityName) throws HibernateException {
        getCurrentSessionFactory().evictEntity(entityName);
    }

    @Override
    public void evictEntity(String entityName, Serializable id) throws HibernateException {
        getCurrentSessionFactory().evictEntity(entityName, id);
    }

    @Override
    public void evictCollection(String roleName) throws HibernateException {
        getCurrentSessionFactory().evictCollection(roleName);
    }

    @Override
    public void evictCollection(String roleName, Serializable id) throws HibernateException {
        getCurrentSessionFactory().evictCollection(roleName, id);
    }

    @Override
    public void evictQueries(String cacheRegion) throws HibernateException {
        getCurrentSessionFactory().evictQueries(cacheRegion);
    }

    @Override
    public void evictQueries() throws HibernateException {
        getCurrentSessionFactory().evictQueries();
    }

    @Override
    public Set getDefinedFilterNames() {
        return getCurrentSessionFactory().getDefinedFilterNames();
    }

    @Override
    public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
        return getCurrentSessionFactory().getFilterDefinition(filterName);
    }

    @Override
    public boolean containsFetchProfileDefinition(String name) {
        return getCurrentSessionFactory().containsFetchProfileDefinition(name);
    }

    @Override
    public TypeHelper getTypeHelper() {
        return getCurrentSessionFactory().getTypeHelper();
    }

    @Override
    public TypeResolver getTypeResolver() {
        return getCurrentSessionFactory().getTypeResolver();
    }

    @Override
    public Properties getProperties() {
        return getCurrentSessionFactory().getProperties();
    }

    @Override
    public EntityPersister getEntityPersister(String entityName) throws MappingException {
        return getCurrentSessionFactory().getEntityPersister(entityName);
    }

    @Override
    public Map<String, EntityPersister> getEntityPersisters() {
        return getCurrentSessionFactory().getEntityPersisters();
    }

    @Override
    public CollectionPersister getCollectionPersister(String role) throws MappingException {
        return getCurrentSessionFactory().getCollectionPersister(role);
    }

    @Override
    public Map<String, CollectionPersister> getCollectionPersisters() {
        return getCurrentSessionFactory().getCollectionPersisters();
    }

    @Override
    public JdbcServices getJdbcServices() {
        return getCurrentSessionFactory().getJdbcServices();
    }

    @Override
    public Dialect getDialect() {
        return getCurrentSessionFactory().getDialect();
    }

    @Override
    public Interceptor getInterceptor() {
        return getCurrentSessionFactory().getInterceptor();
    }

    @Override
    public QueryPlanCache getQueryPlanCache() {
        return getCurrentSessionFactory().getQueryPlanCache();
    }

    @Override
    public Type[] getReturnTypes(String queryString) throws HibernateException {
        return getCurrentSessionFactory().getReturnTypes(queryString);
    }

    @Override
    public String[] getReturnAliases(String queryString) throws HibernateException {
        return getCurrentSessionFactory().getReturnAliases(queryString);
    }

    @Override
    public ConnectionProvider getConnectionProvider() {
        return getCurrentSessionFactory().getConnectionProvider();
    }

    @Override
    public String[] getImplementors(String className) throws MappingException {
        return getCurrentSessionFactory().getImplementors(className);
    }

    @Override
    public String getImportedClassName(String name) {
        return getCurrentSessionFactory().getImportedClassName(name);
    }

    @Override
    public QueryCache getQueryCache() {
        return getCurrentSessionFactory().getQueryCache();
    }

    @Override
    public QueryCache getQueryCache(String regionName) throws HibernateException {
        return getCurrentSessionFactory().getQueryCache();
    }

    @Override
    public UpdateTimestampsCache getUpdateTimestampsCache() {
        return getCurrentSessionFactory().getUpdateTimestampsCache();
    }

    @Override
    public StatisticsImplementor getStatisticsImplementor() {
        return getCurrentSessionFactory().getStatisticsImplementor();
    }

    @Override
    public NamedQueryDefinition getNamedQuery(String queryName) {
        return getCurrentSessionFactory().getNamedQuery(queryName);
    }

    @Override
    public void registerNamedQueryDefinition(String name, NamedQueryDefinition definition) {
        getCurrentSessionFactory().registerNamedQueryDefinition(name, definition);
    }

    @Override
    public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
        return getCurrentSessionFactory().getNamedSQLQuery(queryName);
    }

    @Override
    public void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition) {
        getCurrentSessionFactory().registerNamedSQLQueryDefinition(name, definition);
    }

    @Override
    public ResultSetMappingDefinition getResultSetMapping(String name) {
        return getCurrentSessionFactory().getResultSetMapping(name);
    }

    @Override
    public IdentifierGenerator getIdentifierGenerator(String rootEntityName) {
        return getCurrentSessionFactory().getIdentifierGenerator(rootEntityName);
    }

    @Override
    public Region getSecondLevelCacheRegion(String regionName) {
        return getCurrentSessionFactory().getSecondLevelCacheRegion(regionName);
    }

    @Override
    public Region getNaturalIdCacheRegion(String regionName) {
        return getCurrentSessionFactory().getNaturalIdCacheRegion(regionName);
    }

    @Override
    public Map getAllSecondLevelCacheRegions() {
        return getCurrentSessionFactory().getAllSecondLevelCacheRegions();
    }

    @Override
    public SQLExceptionConverter getSQLExceptionConverter() {
        return getCurrentSessionFactory().getSQLExceptionConverter();
    }

    @Override
    public SqlExceptionHelper getSQLExceptionHelper() {
        return getCurrentSessionFactory().getSQLExceptionHelper();
    }

    @Override
    public Settings getSettings() {
        return getCurrentSessionFactory().getSettings();
    }

    @Override
    public Session openTemporarySession() throws HibernateException {
        return getCurrentSessionFactory().openTemporarySession();
    }

    @Override
    public Set<String> getCollectionRolesByEntityParticipant(String entityName) {
        return getCurrentSessionFactory().getCollectionRolesByEntityParticipant(entityName);
    }

    @Override
    public EntityNotFoundDelegate getEntityNotFoundDelegate() {
        return getCurrentSessionFactory().getEntityNotFoundDelegate();
    }

    @Override
    public SQLFunctionRegistry getSqlFunctionRegistry() {
        return getCurrentSessionFactory().getSqlFunctionRegistry();
    }

    @Override
    public FetchProfile getFetchProfile(String name) {
        return getCurrentSessionFactory().getFetchProfile(name);
    }

    @Override
    public ServiceRegistryImplementor getServiceRegistry() {
        return getCurrentSessionFactory().getServiceRegistry();
    }

    @Override
    public void addObserver(SessionFactoryObserver observer) {
        getCurrentSessionFactory().addObserver(observer);
    }

    @Override
    public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy() {
        return getCurrentSessionFactory().getCustomEntityDirtinessStrategy();
    }

    @Override
    public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
        return getCurrentSessionFactory().getCurrentTenantIdentifierResolver();
    }

    @Override
    public NamedQueryRepository getNamedQueryRepository() {
        return getCurrentSessionFactory().getNamedQueryRepository();
    }

    @Override
    public Iterable<EntityNameResolver> iterateEntityNameResolvers() {
        return getCurrentSessionFactory().iterateEntityNameResolvers();
    }

    @Override
    public Reference getReference() throws NamingException {
        return getCurrentSessionFactory().getReference();
    }

    @Override
    public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
        return getCurrentSessionFactory().getIdentifierGeneratorFactory();
    }

    @Override
    public Type getIdentifierType(String className) throws MappingException {
        return getCurrentSessionFactory().getIdentifierType(className);
    }

    @Override
    public String getIdentifierPropertyName(String className) throws MappingException {
        return getCurrentSessionFactory().getIdentifierPropertyName(className);
    }

    @Override
    public Type getReferencedPropertyType(String className, String propertyName) throws MappingException {
        return getCurrentSessionFactory().getReferencedPropertyType(className, propertyName);
    }
}
