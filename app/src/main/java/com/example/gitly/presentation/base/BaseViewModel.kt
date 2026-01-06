package com.example.gitly.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel class that provides common functionality for all ViewModels.
 * Implements the MVI (Model-View-Intent) pattern with:
 * - UI State: Represents the current state of the screen
 * - UI Events: One-time events like navigation, showing snackbars
 * - UI Actions: User interactions that trigger state changes
 *
 * @param S The type of UI State
 * @param E The type of UI Event
 * @param A The type of UI Action
 */
abstract class BaseViewModel<S : UiState, E : UiEvent, A : UiAction>(
    initialState: S
) : ViewModel() {
    
    /**
     * Mutable state flow for the UI state.
     * Use [updateState] to modify the state.
     */
    private val _uiState = MutableStateFlow(initialState)
    
    /**
     * Publicly exposed UI state as immutable StateFlow.
     */
    val uiState: StateFlow<S> = _uiState.asStateFlow()
    
    /**
     * Channel for one-time UI events.
     */
    private val _uiEvent = Channel<E>(Channel.BUFFERED)
    
    /**
     * Publicly exposed UI events as a Flow.
     */
    val uiEvent = _uiEvent.receiveAsFlow()
    
    /**
     * Current state value.
     */
    protected val currentState: S
        get() = _uiState.value
    
    /**
     * Update the UI state.
     * @param reduce A lambda that takes the current state and returns the new state
     */
    protected fun updateState(reduce: S.() -> S) {
        _uiState.value = currentState.reduce()
    }
    
    /**
     * Send a one-time UI event.
     * @param event The event to send
     */
    protected fun sendEvent(event: E) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
    
    /**
     * Handle user actions/intents.
     * Subclasses must implement this to handle specific actions.
     * @param action The action to handle
     */
    abstract fun onAction(action: A)
}
