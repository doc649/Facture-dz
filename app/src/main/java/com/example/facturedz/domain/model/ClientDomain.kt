package com.example.facturedz.domain.model

// Dans un premier temps, les modèles du domaine peuvent être très similaires aux modèles de données.
// Des différences peuvent apparaître si la logique métier nécessite des champs ou des méthodes spécifiques
// qui ne sont pas directement liés à la persistance des données.

data class ClientDomain(
    val id: Long = 0,
    val name: String,
    val rc: String? = null,
    val nif: String? = null,
    val address: String? = null
)

fun Client.toDomain() = ClientDomain(
    id = id,
    name = name,
    rc = rc,
    nif = nif,
    address = address
)

fun ClientDomain.toEntity() = Client(
    id = id,
    name = name,
    rc = rc,
    nif = nif,
    address = address
)
