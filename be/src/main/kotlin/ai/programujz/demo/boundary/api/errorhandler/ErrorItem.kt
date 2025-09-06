package ai.programujz.demo.boundary.api.errorhandler

data class ErrorItem(
    val field: String,
    val message: String?
) {
    companion object {
        fun of(field: String, message: String?): ErrorItem {
            return ErrorItem(field, message)
        }
    }
}