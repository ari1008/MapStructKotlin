package com.test.number1

import Mapper
import Mapping
import com.test.number1.domain.Person


@Mapper("Hello-World1")
interface Test2 {

    @Mapping(start = "bonjour", end = "name")
    @Mapping(start = "hello", end = "firstName")
    fun mapTwoStringToPerson(bonjour: String, hello: String): Person

    @Mapping(start = "hello", end = "firstName")
    fun mapOneStringToPerson(name: String, hello: String): Person



}
