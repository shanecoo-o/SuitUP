package com.suitup.app.data.mock

import com.suitup.app.domain.model.CategoriaFato
import com.suitup.app.domain.model.ModeloFato
import com.suitup.app.domain.model.SuitModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object MockCatalogStore {
    // imageKey values (Phase 9.4, Task 4/6): each points to a real, distinct guardafato
    // production photo via SuitImageResources.suitImageResource(key), matched by actually
    // observed color — not reused array-position placeholders. m1 ("Preto"/Clássico) and
    // m6 ("Preto"/Gala) intentionally share "suit_black_classic": both are genuinely black
    // suits and it is the only clean, photoreal black asset available in the current
    // guardafato set (a true tuxedo/black-tie photo was not present in the audited batch).
    // That is a documented content gap, not a silent color mismatch — every other model
    // below got a distinct, color-matched asset (m5 previously reused the black photo for
    // a brown/"Castanho" suit, which was a real mismatch; that one is fixed here).
    private val initialSuitModels = listOf(
        SuitModel(
            id = "m1",
            name = "Fato Clássico Preto",
            category = "Clássico",
            description = "Corte clássico em preto, ideal para eventos formais e uso profissional.",
            basePrice = 8500,
            imageKey = "suit_black_classic",
            fabricType = "Lã Premium",
            color = "Preto",
        ),
        SuitModel(
            id = "m2",
            name = "Fato Azul Executivo",
            category = "Executivo",
            description = "Fato azul-marinho com presença executiva para reuniões e cerimónias.",
            basePrice = 9500,
            imageKey = "suit_navy_executive",
            fabricType = "Lã Premium",
            color = "Azul Marinho",
        ),
        SuitModel(
            id = "m3",
            name = "Fato Cinza Slim Fit",
            category = "Slim Fit",
            description = "Silhueta slim em cinza, com visual moderno e versátil.",
            basePrice = 7800,
            imageKey = "suit_charcoal_slim",
            fabricType = "Algodão",
            color = "Cinza Grafite",
        ),
        SuitModel(
            id = "m4",
            name = "Fato Casual de Linho",
            category = "Casual",
            description = "Fato leve de linho para ocasiões diurnas e clima quente.",
            basePrice = 7200,
            imageKey = "suit_beige_casual",
            fabricType = "Linho",
            color = "Bege",
        ),
        SuitModel(
            id = "m5",
            name = "Fato Castanho Premium",
            category = "Premium",
            description = "Fato premium em tom castanho, pensado para acabamentos distintos.",
            basePrice = 11000,
            imageKey = "suit_brown_premium",
            fabricType = "Cashmere",
            color = "Castanho",
        ),
        SuitModel(
            id = "m6",
            name = "Smoking Preto",
            category = "Gala",
            description = "Smoking preto para gala, cerimónias e eventos de noite.",
            basePrice = 12500,
            imageKey = "suit_black_classic",
            fabricType = "Lã Premium",
            color = "Preto",
        ),
    )

    private val _suitModels = MutableStateFlow(initialSuitModels)
    val suitModels: StateFlow<List<SuitModel>> = _suitModels.asStateFlow()

    fun getAllSuitModels(): List<SuitModel> = _suitModels.value

    fun getActiveSuitModels(): List<SuitModel> = _suitModels.value.filter { it.available }

    fun getSuitModelById(id: String): SuitModel? = _suitModels.value.firstOrNull { it.id == id }

    fun addSuitModel(model: SuitModel) {
        _suitModels.update { models ->
            if (models.any { it.id == model.id }) models else models + model
        }
    }

    fun updateSuitModel(model: SuitModel) {
        _suitModels.update { models ->
            models.map { if (it.id == model.id) model else it }
        }
    }

    fun deactivateSuitModel(id: String) {
        setAvailability(id, available = false)
    }

    fun reactivateSuitModel(id: String) {
        setAvailability(id, available = true)
    }

    fun removeSuitModel(id: String) {
        _suitModels.update { models -> models.filterNot { it.id == id } }
    }

    fun getActiveModeloFatos(): List<ModeloFato> = getActiveSuitModels().map { it.toModeloFato() }

    fun getModeloFatoById(id: String): ModeloFato? = getSuitModelById(id)?.toModeloFato()

    private fun setAvailability(id: String, available: Boolean) {
        _suitModels.update { models ->
            models.map { if (it.id == id) it.copy(available = available) else it }
        }
    }
}

fun SuitModel.toModeloFato(): ModeloFato = ModeloFato(
    id = id,
    nome = name,
    categoria = category.toCategoriaFato(),
    precoBase = basePrice,
    urlImagemPrevia = imageKey,
)

private fun String.toCategoriaFato(): CategoriaFato = when (this) {
    "Clássico" -> CategoriaFato.Classico
    "Slim Fit" -> CategoriaFato.CorteSlim
    "Executivo" -> CategoriaFato.Executivo
    "Casual" -> CategoriaFato.Casual
    "Premium" -> CategoriaFato.Premium
    "Gala" -> CategoriaFato.Gala
    else -> CategoriaFato.Classico
}
