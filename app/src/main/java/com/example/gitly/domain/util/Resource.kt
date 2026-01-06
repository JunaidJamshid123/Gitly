package com.example.gitly.domain.util

/**
 * A sealed class that encapsulates successful outcomes with data of type [T],
 * errors with an optional [message] and [data], and loading states.
 *
 * This is the standard way to wrap API/Database responses in Clean Architecture.
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    /**
     * Represents a successful state with [data].
     */
    class Success<T>(data: T) : Resource<T>(data)
    
    /**
     * Represents an error state with an optional [message] and [data].
     * Data can be useful for showing cached data when network fails.
     */
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    
    /**
     * Represents a loading state with optional [data] (e.g., cached data).
     */
    class Loading<T>(data: T? = null) : Resource<T>(data)
    
    /**
     * Returns true if this is a [Success] state.
     */
    val isSuccess: Boolean
        get() = this is Success
    
    /**
     * Returns true if this is an [Error] state.
     */
    val isError: Boolean
        get() = this is Error
    
    /**
     * Returns true if this is a [Loading] state.
     */
    val isLoading: Boolean
        get() = this is Loading
    
    /**
     * Maps the data of this Resource using the provided [transform] function.
     */
    fun <R> map(transform: (T) -> R): Resource<R> {
        return when (this) {
            is Success -> Success(transform(data!!))
            is Error -> Error(message ?: "Unknown error", data?.let(transform))
            is Loading -> Loading(data?.let(transform))
        }
    }
    
    /**
     * Executes [action] if this is a [Success] state.
     */
    inline fun onSuccess(action: (T) -> Unit): Resource<T> {
        if (this is Success && data != null) {
            action(data)
        }
        return this
    }
    
    /**
     * Executes [action] if this is an [Error] state.
     */
    inline fun onError(action: (String?) -> Unit): Resource<T> {
        if (this is Error) {
            action(message)
        }
        return this
    }
    
    /**
     * Executes [action] if this is a [Loading] state.
     */
    inline fun onLoading(action: () -> Unit): Resource<T> {
        if (this is Loading) {
            action()
        }
        return this
    }
}

/**
 * Extension function to convert a nullable value to a Resource.
 */
fun <T> T?.toResource(errorMessage: String = "Data not found"): Resource<T> {
    return if (this != null) {
        Resource.Success(this)
    } else {
        Resource.Error(errorMessage)
    }
}

/**
 * Extension function to safely execute a block and wrap the result in a Resource.
 */
suspend fun <T> safeCall(
    errorMessage: String = "An error occurred",
    block: suspend () -> T
): Resource<T> {
    return try {
        Resource.Success(block())
    } catch (e: Exception) {
        Resource.Error(e.message ?: errorMessage)
    }
}
