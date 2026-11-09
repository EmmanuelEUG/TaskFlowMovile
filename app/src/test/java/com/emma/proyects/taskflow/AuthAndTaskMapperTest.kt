package com.emma.proyects.taskflow

import com.emma.proyects.taskflow.auth.data.dto.AuthResponseDto
import com.emma.proyects.taskflow.auth.data.dto.UserDto
import com.emma.proyects.taskflow.auth.data.mapper.toDomain
import com.emma.proyects.taskflow.tasks.data.dto.TaskDto
import com.emma.proyects.taskflow.tasks.data.mapper.toCreateUpdateDto
import com.emma.proyects.taskflow.tasks.data.mapper.toDomain
import com.emma.proyects.taskflow.tasks.domain.model.Importance
import com.emma.proyects.taskflow.tasks.domain.model.RecurrenceUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek

class AuthAndTaskMapperTest {

    @Test
    fun authResponseDto_mapsToDomainSession() {
        val dto = AuthResponseDto(
            accessToken = "jwt.token.value",
            tokenType = "bearer",
            user = UserDto(
                id = "u-1",
                name = "Emmanuel",
                email = "emma@example.com"
            )
        )

        val session = dto.toDomain()

        assertEquals("jwt.token.value", session.accessToken)
        assertEquals("u-1", session.user.id)
        assertEquals("Emmanuel", session.user.name)
        assertEquals("emma@example.com", session.user.email)
    }

    @Test
    fun taskDto_mapsEnumsAndComputesOverdueSafely() {
        val pastMillis = 1_000_000L
        val nowMillis = 2_000_000L

        val dto = TaskDto(
            id = "t-42",
            title = "Entregar proyecto",
            description = "Con Retrofit 3",
            area = "Escuela",
            importance = "HIGH",
            priorityLevel = 9,
            dueDateFormatted = "9 de octubre, 18:00",
            dueDateMillis = pastMillis,
            isOverdue = false,
            isCompleted = false,
            isRecurring = true,
            recurrenceInterval = 2,
            recurrenceUnit = "WEEKS",
            createdDateFormatted = "5 de octubre",
            recurrenceDaysOfWeek = listOf("MONDAY", "INVALID_DAY", "FRIDAY"),
            recurrenceDaysOfMonth = listOf(1, 15)
        )

        val domain = dto.toDomain(nowMillis = nowMillis)

        assertEquals("t-42", domain.id)
        assertEquals(Importance.HIGH, domain.importance)
        assertEquals(5, domain.priorityLevel)
        assertTrue(domain.isOverdue)
        assertEquals(RecurrenceUnit.WEEKS, domain.recurrenceUnit)
        assertEquals(listOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY), domain.recurrenceDaysOfWeek)

        val outboundDto = domain.toCreateUpdateDto()
        assertEquals("HIGH", outboundDto.importance)
        assertEquals("WEEKS", outboundDto.recurrenceUnit)
        assertEquals(listOf("MONDAY", "FRIDAY"), outboundDto.recurrenceDaysOfWeek)
        assertFalse(outboundDto.isCompleted)
    }
}
