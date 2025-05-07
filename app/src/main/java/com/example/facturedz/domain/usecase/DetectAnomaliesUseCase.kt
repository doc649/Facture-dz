package com.example.facturedz.domain.usecase

import com.example.facturedz.data.repository.InvoiceRepository
import com.example.facturedz.domain.model.AnomalyDomain
import com.example.facturedz.domain.model.InvoiceDomain
import com.example.facturedz.domain.model.toEntity
import java.math.BigDecimal
import java.math.RoundingMode

class DetectAnomaliesUseCase(private val repository: InvoiceRepository) {

    // Define a small tolerance for floating point comparisons
    private val tolerance = 0.01 // For example, 1 centime

    suspend operator fun invoke(invoice: InvoiceDomain, suspiciousAmountThreshold: Double): List<AnomalyDomain> {
        val anomaliesFound = mutableListOf<AnomalyDomain>()

        // 1. Vérifier si TVA = Total TTC - Total HT
        val calculatedTVA = BigDecimal(invoice.totalTTC).subtract(BigDecimal(invoice.totalHT))
        if (BigDecimal(invoice.totalTVA).subtract(calculatedTVA).abs() > BigDecimal(tolerance)) {
            anomaliesFound.add(
                AnomalyDomain(
                    invoiceId = invoice.id,
                    type = "TVA_MISMATCH",
                    description = "La TVA calculée (TTC - HT = ${calculatedTVA.setScale(2, RoundingMode.HALF_UP)}) ne correspond pas à la TVA déclarée (${invoice.totalTVA})."
                )
            )
        }

        // 2. Vérifier si somme des lignes = Total HT
        val sumOfProductLines = invoice.productLines.sumOf { it.totalLinePrice }
        if (BigDecimal(invoice.totalHT).subtract(BigDecimal(sumOfProductLines)).abs() > BigDecimal(tolerance)) {
            anomaliesFound.add(
                AnomalyDomain(
                    invoiceId = invoice.id,
                    type = "TOTAL_HT_MISMATCH",
                    description = "La somme des lignes de produits (${BigDecimal(sumOfProductLines).setScale(2, RoundingMode.HALF_UP)}) ne correspond pas au Total HT déclaré (${invoice.totalHT})."
                )
            )
        }

        // 3. Détecter facture en double (même client, même date, même montant)
        // Ensure client ID is available for duplicate check
        invoice.client?.id?.let {
            val duplicates = repository.findDuplicateInvoices(
                clientId = it,
                date = invoice.date,
                totalTTC = invoice.totalTTC,
                currentInvoiceId = invoice.id // Exclude the current invoice itself from duplicate check if it's already saved
            )
            if (duplicates.isNotEmpty()) {
                anomaliesFound.add(
                    AnomalyDomain(
                        invoiceId = invoice.id,
                        type = "DUPLICATE_INVOICE",
                        description = "Une facture similaire (même client, date, montant TTC) existe déjà (ID(s): ${duplicates.joinToString { d -> d.id.toString() }})."
                    )
                )
            }
        }


        // 4. Signaler si le montant TTC dépasse un seuil suspect
        if (invoice.totalTTC > suspiciousAmountThreshold) {
            anomaliesFound.add(
                AnomalyDomain(
                    invoiceId = invoice.id,
                    type = "SUSPICIOUS_AMOUNT",
                    description = "Le montant TTC (${invoice.totalTTC}) dépasse le seuil suspect configuré (${suspiciousAmountThreshold})."
                )
            )
        }

        // Save detected anomalies
        if (anomaliesFound.isNotEmpty()) {
            // First, delete existing anomalies for this invoice to avoid duplicates if re-checked
            // This assumes anomalies are always re-evaluated. Alternatively, manage updates.
            // For now, let's assume we clear and add new ones.
            // repository.deleteAnomaliesForInvoice(invoice.id) // This might be too aggressive if user can justify some.
            // Better to add only new, distinct anomalies or handle updates in ViewModel/Repository.
            // For this use case, we just return the list. Saving will be handled by calling code or another use case.
        }

        return anomaliesFound
    }
}
