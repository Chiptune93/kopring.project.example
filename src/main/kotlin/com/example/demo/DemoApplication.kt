package com.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.query
import org.springframework.web.bind.annotation.*
import java.util.*

@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}

/**
 * Controller
 */
@RestController
class MessageController(val service: MessageService, var dbService: MessageDbService) {
    /**
     * 기본적인 컨트롤러 사용
     */
    @GetMapping("/")
    fun index(@RequestParam("name") name: String) = "Hello, $name!"

    @GetMapping("/index")
    fun index() = listOf(
        Message("1", "Hello!"),
        Message("2", "Bonjour!"),
        Message("3", "Privet!"),
    )

    /**
     * 데이터 클래스를 이용한 객체 사용하기
     */
    @GetMapping("/data")
    fun data(): List<Message> = service.findMessages()

    @GetMapping("/data/{id}")
    fun dataById(@PathVariable id: String): List<Message> =
        service.findMessageById(id)

    @PostMapping("/data")
    fun data(@RequestBody message: Message) {
        service.save(message)
    }


    /**
     * H2 데이터베이스를 이용한 데이터 주고받기
     */
    @GetMapping("/db/data")
    fun dbData(): List<MessageDb> = dbService.findMessages()

    @GetMapping("/db/data/{id}")
    fun dbDataById(@PathVariable id: String): List<MessageDb> =
        dbService.findMessageById(id)

    @PostMapping("/db/data")
    fun dbData(@RequestBody message: MessageDb) {
        dbService.save(message)
    }
}

/**
 * JDBC 템플릿을 이용한 데이터 조회하기(h2)
 */
@Service
class MessageService(val db: JdbcTemplate) {
    fun findMessages(): List<Message> = db.query("select * from messages") { response, _ ->
        Message(response.getString("id"), response.getString("text"))
    }

    fun findMessageById(id: String): List<Message> =
        db.query("select * from messages where id = ?", id) { response, _ ->
            Message(response.getString("id"), response.getString("text"))
        }

    fun save(message: Message) {
        val id = message.id ?: UUID.randomUUID().toString()
        db.update(
            "insert into messages values ( ?, ? )",
            id, message.text
        )
    }
}

/**
 * Spring Data Jpa 를 이용한 데이터 조회하기(h2)
 */
@Service
class MessageDbService(val db: MessageDbRepository) {
    fun findMessages(): List<MessageDb> = db.findAll().toList()

    fun findMessageById(id: String): List<MessageDb> = db.findById(id).toList()

    fun save(messageDb: MessageDb) {
        db.save(messageDb)
    }

    fun <T : Any> Optional<out T>.toList(): List<T> =
        if (isPresent) listOf(get()) else emptyList()
}

/**
 * Spring Data Jpa Crud Repo
 */
interface MessageDbRepository : CrudRepository<MessageDb, String>

/**
 * Spring Data Jpa Data Class
 */
@Table("MESSAGESDB")
data class MessageDb(@Id var id: String?, val text: String)

/**
 * Custom Data class with jdbc
 */
data class Message(val id: String?, val text: String)

