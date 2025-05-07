package com.example.facturedz.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.facturedz.R // Assuming R is generated
import com.example.facturedz.databinding.ItemInvoiceBinding // Assuming ViewBinding is enabled
import com.example.facturedz.domain.model.InvoiceDomain
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InvoiceAdapter(private val onItemClicked: (InvoiceDomain) -> Unit) :
    ListAdapter<InvoiceDomain, InvoiceAdapter.InvoiceViewHolder>(InvoiceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val binding = ItemInvoiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InvoiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        val invoice = getItem(position)
        holder.bind(invoice)
        holder.itemView.setOnClickListener {
            onItemClicked(invoice)
        }
    }

    inner class InvoiceViewHolder(private val binding: ItemInvoiceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        fun bind(invoice: InvoiceDomain) {
            binding.itemInvoiceClientName.text = invoice.client?.name ?: "Client inconnu"
            binding.itemInvoiceDate.text = dateFormat.format(Date(invoice.date))
            binding.itemInvoiceAmountTtc.text = String.format(Locale.FRANCE, "%.2f DA", invoice.totalTTC)

            // Handle anomaly icon visibility and type
            val mostSevereAnomaly = invoice.anomalies.firstOrNull() // Simplified: take the first or define severity
            if (mostSevereAnomaly != null) {
                binding.itemInvoiceAnomalyIcon.visibility = android.view.View.VISIBLE
                when (mostSevereAnomaly.type) {
                    "TVA_MISMATCH", "TOTAL_HT_MISMATCH", "DUPLICATE_INVOICE" -> {
                        binding.itemInvoiceAnomalyIcon.setImageResource(R.drawable.ic_error_red) // ic_error_red to be defined
                    }
                    "SUSPICIOUS_AMOUNT" -> {
                        binding.itemInvoiceAnomalyIcon.setImageResource(R.drawable.ic_warning_amber) // ic_warning_amber to be defined
                    }
                    else -> {
                        binding.itemInvoiceAnomalyIcon.visibility = android.view.View.GONE
                    }
                }
            } else {
                binding.itemInvoiceAnomalyIcon.visibility = android.view.View.GONE
            }
        }
    }
}

class InvoiceDiffCallback : DiffUtil.ItemCallback<InvoiceDomain>() {
    override fun areItemsTheSame(oldItem: InvoiceDomain, newItem: InvoiceDomain): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: InvoiceDomain, newItem: InvoiceDomain): Boolean {
        return oldItem == newItem
    }
}
