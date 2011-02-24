package grails.plugin.hibernatehijacker.indexdsl

import grails.plugin.hibernatehijacker.exception.HibernateHijackerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.Table;

/**
 * Small DSL for defining database indexes.
 * Sample usage:
 * 
 * static indexes = {
 *      index_name 'first_column', 'second_column', '...'
 *      second_index '...'
 * }
 * 
 * @author Kim A. Betti
 */
class HibernateIndexBuilder {
    
    private Logger log = LoggerFactory.getLogger(HibernateIndexBuilder);

    private List indexes = []
    private Closure closure
    private Table table
    
    protected HibernateIndexBuilder(Table table, Closure closure) {
        this.table = table
        this.closure = closure
    }
    
    static List<Index> from(Table table, Closure closure) {
        def builder = new HibernateIndexBuilder(table, closure)
        return builder.build()
    }
    
    public List<Index> build() {
        closure.delegate = this
        closure.resolveStrategy = Closure.DELEGATE_ONLY
        closure.call()
        return indexes
    }
    
    void methodMissing(String indexName, columnNames) {
        log.debug "Defining $indexName on ${table.name}"
        
        Index index = table.getOrCreateIndex(indexName)
        for (String columnName : columnNames) {
            Column column = getColumnByName(columnName)
            if (column == null) {
                throw new HibernateHijackerException("Unable to find column $columnName in table ${table.name}")    
            }
            
            index.addColumn(column)
        }
  
        indexes << index
    }
    
    private Column getColumnByName(String name) {
        return table.columnIterator.find { Column column ->
            column.canonicalName == name
        }
    }
    
}
