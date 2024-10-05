package number2

import com.test.number2.Test
import com.test.number2.domain.Person
import com.test.number2.domain.PersonDto
import kotlin.String

public class ImplTest : Test {
    override fun mapPersontoPersonDto(person: Person): PersonDto = PersonDto(
        firstName = person.firstName,
        lastName = person.lastName,
    )

    override fun mapPersonAndStringtoPersonDto(person: Person, firstName: String): PersonDto =
        PersonDto(
            firstName = firstName,
            lastName = person.lastName,
        )
}