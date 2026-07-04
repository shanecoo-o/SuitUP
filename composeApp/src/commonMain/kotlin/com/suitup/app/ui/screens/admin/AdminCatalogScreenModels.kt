package com.suitup.app.ui.screens.admin

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.suitup.app.data.catalog.AdminCatalogOperationResult
import com.suitup.app.data.catalog.AdminCatalogRepository
import com.suitup.app.data.catalog.AdminCatalogState
import com.suitup.app.data.catalog.CatalogRuntime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminCatalogScreenModel(
    private val repository: AdminCatalogRepository = CatalogRuntime.adminRepository,
) : ScreenModel {
    val state: StateFlow<AdminCatalogState> = repository.state

    init {
        refresh()
    }

    fun refresh() {
        screenModelScope.launch { repository.refresh() }
    }

    fun setAvailability(id: String, active: Boolean) {
        if (state.value.pendingModelId != null) return
        screenModelScope.launch { repository.setAvailability(id, active) }
    }

    fun consumeSessionExpired() {
        repository.consumeSessionExpired()
    }
}

data class AdminSuitFormUiState(
    val form: AdminSuitFormState = AdminSuitFormState(),
    val isEditMode: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val sessionExpired: Boolean = false,
)

class AdminSuitFormScreenModel(
    private val modelId: String?,
    private val repository: AdminCatalogRepository = CatalogRuntime.adminRepository,
) : ScreenModel {
    private val original = modelId?.let { id -> repository.state.value.models.firstOrNull { it.id == id } }
    private val _state = MutableStateFlow(
        AdminSuitFormUiState(
            form = original?.toAdminFormState() ?: AdminSuitFormState(),
            isEditMode = modelId != null,
            errorMessage = if (modelId != null && original == null) "Modelo não encontrado." else null,
        )
    )
    val state: StateFlow<AdminSuitFormUiState> = _state.asStateFlow()

    private val _navigateBack = MutableStateFlow(false)
    val navigateBack: StateFlow<Boolean> = _navigateBack.asStateFlow()

    init {
        repository.clearNotice()
    }

    fun updateForm(form: AdminSuitFormState) {
        if (_state.value.isSaving) return
        _state.update { it.copy(form = form, errorMessage = null) }
    }

    fun save() {
        val current = _state.value
        if (current.isSaving) return
        if (modelId != null && original == null) {
            _state.update { it.copy(errorMessage = "Modelo não encontrado.") }
            return
        }

        val validationError = validate(current.form)
        if (validationError != null) {
            _state.update { it.copy(errorMessage = validationError) }
            return
        }

        screenModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            val model = current.form.toSuitModel(generatedId = modelId.orEmpty())
            val result = if (current.isEditMode) repository.update(model) else repository.create(model)
            when (result) {
                is AdminCatalogOperationResult.Success -> {
                    _state.update { it.copy(isSaving = false) }
                    _navigateBack.value = true
                }
                is AdminCatalogOperationResult.Failure -> _state.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = result.message,
                        sessionExpired = result.sessionExpired,
                    )
                }
            }
        }
    }

    fun navigationConsumed() {
        _navigateBack.value = false
    }

    fun consumeSessionExpired() {
        repository.consumeSessionExpired()
        _state.update { it.copy(sessionExpired = false) }
    }

    private fun validate(form: AdminSuitFormState): String? = when {
        form.name.isBlank() -> "Dados inválidos. Verifique os campos."
        form.category.isBlank() -> "Dados inválidos. Verifique os campos."
        form.description.isBlank() -> "Dados inválidos. Verifique os campos."
        form.basePrice.toIntOrNull()?.let { it > 0 } != true -> "Dados inválidos. Verifique os campos."
        form.fabricType.isBlank() -> "Dados inválidos. Verifique os campos."
        form.color.isBlank() -> "Dados inválidos. Verifique os campos."
        else -> null
    }
}
