package number1

import com.test.number1.Test2
import com.test.number1.domain.Person
import kotlin.String

public class ImplTest2 : Test2 {
    override fun mapTwoStringToPerson(bonjour: String, hello: String): Person = Person(
        name = bonjour,
        firstName = hello,
    )

    override fun mapOneStringToPerson(name: String, hello: String): Person = Person(
        name = name,
        firstName = hello,
    )
}