package com.test

fun main() {
    val test = MapperClass.getMapper(Test2::class)
    val person = test.mapTwoStringToPerson("bonjour", "hello")
    val person2 = test.mapOneStringToPerson("test", "test2")
    println("Hello world $person")
    println("Hello world 2 $person2")
}
