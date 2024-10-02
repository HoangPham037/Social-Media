package com.example.socialmedia.common

sealed class State<out R> {
    data class Success<out T>(val data: T) : State<T>()
    data class Progress<out T>(val data: T): State<T>()
    data class Add<out T>(val data: T) : State<T>()
    data class Change<out T>(val data: T) : State<T>()
    data class Remove<out T>(val data: T) : State<T>()
    data class Moved<out T>(val data: T): State<T>()
    data class Error<out T>(val exception: String) : State<T>()
    object Loading : State<Nothing>()



    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Progress<*> -> "Progress[data=$data]"
            is Add<*> -> "Add[data=$data]"
            is Remove<*> -> "Remove[data=$data]"
            is Change<*> -> "Change[data=$data]"
            is Moved<*> -> "Moved[$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }

     fun getData() {
        return when (this) {
            is Success<*> -> data
            is Progress<*> -> data
            is Add<*> -> data
            is Remove<*> -> data
            is Change<*> -> data
            is Moved<*> -> data
            else -> {}
        } as Unit
    }

}

/**
 * `true` if [State] is of type [Success] & holds non-null [Success.data].
 */
val State<*>.succeeded
    get() = this is State.Success && data != null
val State<*>.add
    get() = this is State.Add && data != null
val State<*>.change
    get() = this is State.Change && data != null
val State<*>.remove
    get() = this is State.Remove && data != null
val State<*>.moved
    get() = this is State.Moved && data != null