package grails.plugin.hibernatehijacker.indexdsl

import grails.plugin.hibernatehijacker.exception.HibernateHijackerException
import org.hibernate.mapping.Column
import org.hibernate.mapping.Index
import org.hibernate.mapping.Table
import spock.lang.Specification

/**
 * @author Kim A. Betti
 */
class HibernateIndexBuilderSpec extends Specification {

    Table table

    def setup() {
        table = new Table("person")
        table.addColumn(new Column("surname"))
        table.addColumn(new Column("firstname"))
    }

    def "simple example - composite index"() {
        when:
        HibernateIndexBuilder.from(table) {
            name_idx 'surname', 'firstname'
        }

        then:
        Iterator indexIterator = table.getIndexIterator()
        Index nameIndex = indexIterator.next()
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
        when:
        HibernateIndexBuilder.from(table) {
            some_idx 'missing_column'
        }

        then:
        HibernateHijackerException hex = thrown()
    }

}
