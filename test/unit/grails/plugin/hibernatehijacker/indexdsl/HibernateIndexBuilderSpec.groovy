package grails.plugin.hibernatehijacker.indexdsl

import org.hibernate.mapping.Column
import org.hibernate.mapping.Index
import org.hibernate.mapping.Table;

import grails.plugin.hibernatehijacker.exception.HibernateHijackerException;
import grails.plugin.spock.UnitSpec

/**
 * 
 * @author Kim A. Betti
 */
class HibernateIndexBuilderSpec extends UnitSpec {

    def "simple example"() {
        given:
        Table table = new Table("person")
        table.addColumn(new Column("surname"))
        table.addColumn(new Column("firstname"))
        
        when:
        List<Index> indexes = HibernateIndexBuilder.from (table) {
            name_idx 'surname', 'firstname'
        }
        
        then:
        indexes.size() == 1
        Index nameIndex = indexes.get(0)
        nameIndex.getName() == "name_idx"
        
        and:
        Iterator columnIterator = nameIndex.getColumnIterator()
        Column surnameColumn = columnIterator.next()
        surnameColumn.canonicalName == "surname"
        
        and:
        Column firstnameColumn = columnIterator.next()
        firstnameColumn.canonicalName == "firstname"
        
        and:
        !columnIterator.hasNext()
    }
    
    def "trying to map unknown column should throw exception"() {
        given:
        Table table = new Table("test_table")
        
        when:
        HibernateIndexBuilder.from (table) {
            some_idx 'missing_column'
        }
        
        then:
        HibernateHijackerException hex = thrown()
    }
    
}
