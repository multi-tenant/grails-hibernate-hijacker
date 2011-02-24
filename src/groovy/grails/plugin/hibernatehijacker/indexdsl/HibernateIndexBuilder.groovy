package grails.plugin.hibernatehijacker.indexdsl

import grails.plugin.hibernatehijacker.exception.HibernateHijackerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.Table;

/**
 * DSL for defining single and multi-column indexes.
 * Just add a closure like the one below to your domain 
 * classes and you're ready to go! 
 * 
 * static indexes = {
 *      index_name 'first_column', 'second_column', '...'
 *      second_index '...'
 * }
 * 
 * Important! The column are identified by the database and
 * not property name. Camel cased property names will be translated
 * to a underscore based convention. 
 * 
 * @author Kim A. Betti
 */
class HibernateIndexBuilder {
    
    private static Logger log = LoggerFactory.getLogger(this);

    private Closure closure
    private Table table
    
    protected HibernateIndexBuilder(Table table, Closure closure) {
        this.table = table
        this.closure = closure
    }
    
    static void from(Table table, Closure closure) {
        new HibernateIndexBuilder(table, closure).build()
    }
    
    public void build() {
        closure.delegate = this
        closure.resolveStrategy = Closure.DELEGATE_ONLY
        closure.call()
    }
    
    public void methodMissing(String indexName, arguments) {
        log.debug "Defining $indexName on " + table.getName()
        
        Index index = table.getOrCreateIndex(indexName)
        for (Object argument : arguments) {
            if (argument instanceof String) {
                addColumnToIndex(index, argument)
            } else {
                throw new HibernateHijackerException("Expected column name (String), got instead " + argument)
            }
        }
    }
    
    private void addColumnToIndex(Index index, String columnName) {
        Column column = getColumnByName(columnName)
        if (column == null) {
            String exMessage = "Unable to find column $columnName in table ${table.name}"
            throw new HibernateHijackerException(exMessage)
        }
        
        log.debug "Appending column ${columnName} to index ${index.name}"
        index.addColumn(column)
    }

    private Column getColumnByName(String name) {
        table.columnIterator.find { Column column ->
            column.canonicalName == name
        }
    }

}
