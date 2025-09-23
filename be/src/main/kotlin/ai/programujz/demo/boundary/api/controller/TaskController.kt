package ai.programujz.demo.boundary.api.controller

import ai.programujz.demo.boundary.api.controller.request.CompleteTaskRequest
import ai.programujz.demo.boundary.api.controller.request.CreateTaskRequest
import ai.programujz.demo.boundary.api.controller.request.ReorderTasksRequest
import ai.programujz.demo.boundary.api.controller.request.UpdateTaskRequest
import ai.programujz.demo.boundary.api.controller.response.TaskCompletionResponse
import ai.programujz.demo.boundary.api.controller.response.TaskResponse
import ai.programujz.demo.domain.TagService
import ai.programujz.demo.domain.TaskService
import ai.programujz.demo.domain.model.TagId
import ai.programujz.demo.domain.model.TaskSearchParams
import ai.programujz.demo.domain.model.TaskStatus
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.*

@RestController
@RequestMapping("/api/v1/tasks")
@CrossOrigin(origins = ["http://localhost:3000"])
class TaskController(
    private val taskService: TaskService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createTask(@Valid @RequestBody request: CreateTaskRequest): TaskResponse {
        return taskService.createTask(request).toResponse()
    }

    @PutMapping("/{id}")
    fun updateTask(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateTaskRequest
    ): TaskResponse {
        return taskService.updateTask(id, request).toResponse()
    }

    @GetMapping("/{id}")
    fun getTask(@PathVariable id: UUID): TaskResponse {
        return taskService.getTask(id).toResponse()
    }

    @GetMapping
    fun getTasks(
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) status: TaskStatus?,
        @RequestParam(required = false) dueDate: LocalDate?,
        @RequestParam(required = false) startDate: LocalDate?,
        @RequestParam(required = false) endDate: LocalDate?,
        @RequestParam(required = false) tags: List<UUID>?
    ): List<TaskResponse> {
        val tagIds = tags?.map { TagId.from(it) }
        
        val searchParams = TaskSearchParams(
            category = category,
            status = status,
            dueDate = dueDate,
            startDate = startDate,
            endDate = endDate,
            tagIds = tagIds
        )
        
        return taskService.getBySearchParams(searchParams).map { it.toResponse() }
    }

    @PostMapping("/{id}/complete")
    fun completeTask(
        @PathVariable id: UUID,
        @RequestBody(required = false) request: CompleteTaskRequest?
    ): TaskResponse {
        return taskService.completeTask(id, request?.notes).toResponse()
    }

    @PostMapping("/{id}/archive")
    fun archiveTask(@PathVariable id: UUID): TaskResponse {
        return taskService.archiveTask(id).toResponse()
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTask(@PathVariable id: UUID) {
        taskService.deleteTask(id)
    }

    @PutMapping("/reorder")
    fun reorderTasks(@Valid @RequestBody request: ReorderTasksRequest): List<TaskResponse> {
        taskService.reorderTasks(request.taskOrders)
        return taskService.getAllTasks().map { it.toResponse() }
    }

    @GetMapping("/{id}/completions")
    fun getTaskCompletions(@PathVariable id: UUID): List<TaskCompletionResponse> {
        return taskService.getTaskCompletions(id).map { it.toResponse() }
    }
}