package com.suitup.app.domain.model

enum class AppUserRole {
    ADMIN,
    CUSTOMER,
}

data class AuthenticatedUser(
    val profile: Utilizador,
    val roles: Set<AppUserRole>,
) {
    val primaryRole: AppUserRole
        get() = if (AppUserRole.ADMIN in roles) AppUserRole.ADMIN else AppUserRole.CUSTOMER
}

sealed interface AuthSessionState {
    data object Checking : AuthSessionState

    data class Unauthenticated(
        val message: String? = null,
    ) : AuthSessionState

    data class Authenticated(
        val account: AuthenticatedUser,
    ) : AuthSessionState
}
