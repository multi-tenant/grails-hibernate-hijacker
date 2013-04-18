package grails.plugin.hibernatehijacker.demo

class Person {

    String firstname
    String surname
    String phone

    static constraints = {
        firstname blank: false
        surname blank: false
    }

    static indexes = {
        name_idx 'surname', 'firstname'
        phone_idx 'phone'
    }
}
