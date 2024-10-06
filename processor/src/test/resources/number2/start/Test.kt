package com.test.number2

import Mapper
import Mapping
import com.test.number2.domain.Person
import com.test.number2.domain.PersonDto

@Mapper("Test")
interface Test {


    fun mapPersontoPersonDto(person: Person): PersonDto

    @Mapping(start = "person.lastName", end = "lastName")
    fun mapPersonAndStringtoPersonDto(person: Person, firstName: String): PersonDto
}