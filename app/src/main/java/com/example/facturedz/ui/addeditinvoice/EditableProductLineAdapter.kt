package com.example.facturedz.ui.addeditinvoice

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.facturedz.databinding.ItemProductLineEditableBinding // Assuming ViewBinding
import com.example.facturedz.domain.model.ProductLineDomain
import java.util.Locale

class EditableProductLineAdapter(
    private val onDeleteClicked: (ProductLineDomain) -> Unit,
    private val onProductLineChanged: (ProductLineDomain, Int) -> Unit // To notify ViewModel of changes
) : ListAdapter<ProductLineDomain, EditableProductLineAdapter.ProductLineViewHolder>(ProductLineDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductLineViewHolder {
        val binding = ItemProductLineEditableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductLineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductLineViewHolder, position: Int) {
        val productLine = getItem(position)
        holder.bind(productLine, position)
    }

    inner class ProductLineViewHolder(private val binding: ItemProductLineEditableBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(productLine: ProductLineDomain, position: Int) {
            binding.productDesignationEditText.setText(productLine.designation)
            binding.productQuantityEditText.setText(String.format(Locale.US, "%.2f", productLine.quantity))
            binding.productUnitPriceEditText.setText(String.format(Locale.US, "%.2f", productLine.unitPrice))
            binding.productTotalLineEditTextView.setText(String.format(Locale.US, "%.2f", productLine.totalLinePrice))

            binding.deleteProductLineButton.setOnClickListener {
                onDeleteClicked(productLine)
            }

            // Add TextWatchers to update the underlying data and recalculate totals
            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val designation = binding.productDesignationEditText.text.toString()
                    val quantity = binding.productQuantityEditText.text.toString().toDoubleOrNull() ?: 0.0
                    val unitPrice = binding.productUnitPriceEditText.text.toString().toDoubleOrNull() ?: 0.0
                    val totalLinePrice = quantity * unitPrice
                    binding.productTotalLineEditTextView.setText(String.format(Locale.US, "%.2f", totalLinePrice))

                    val updatedProductLine = productLine.copy(
                        designation = designation,
                        quantity = quantity,
                        unitPrice = unitPrice,
                        totalLinePrice = totalLinePrice
                    )
                    onProductLineChanged(updatedProductLine, adapterPosition)
                }
            }

            binding.productDesignationEditText.removeTextChangedListener(getTag(R.id.product_designation_edit_text) as? TextWatcher)
            binding.productDesignationEditText.addTextChangedListener(textWatcher)
            binding.productDesignationEditText.setTag(R.id.product_designation_edit_text, textWatcher)

            binding.productQuantityEditText.removeTextChangedListener(getTag(R.id.product_quantity_edit_text) as? TextWatcher)
            binding.productQuantityEditText.addTextChangedListener(textWatcher)
            binding.productQuantityEditText.setTag(R.id.product_quantity_edit_text, textWatcher)

            binding.productUnitPriceEditText.removeTextChangedListener(getTag(R.id.product_unit_price_edit_text) as? TextWatcher)
            binding.productUnitPriceEditText.addTextChangedListener(textWatcher)
            binding.productUnitPriceEditText.setTag(R.id.product_unit_price_edit_text, textWatcher)
        }
        // Helper to get tag, assuming R.id values are accessible for tags
        private fun getTag(id: Int): Any? = itemView.findViewById<android.view.View>(id)?.tag

    }

    class ProductLineDiffCallback : DiffUtil.ItemCallback<ProductLineDomain>() {
        override fun areItemsTheSame(oldItem: ProductLineDomain, newItem: ProductLineDomain): Boolean {
            return oldItem.id == newItem.id && oldItem.id != 0L // Differentiate new items not yet in DB
        }

        override fun areContentsTheSame(oldItem: ProductLineDomain, newItem: ProductLineDomain): Boolean {
            return oldItem == newItem
        }
    }
}
