package com.example.facturedz.ui.addeditinvoice

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider // Or inject
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.facturedz.R // Assuming R is generated
import com.example.facturedz.databinding.ActivityAddEditInvoiceBinding // Assuming ViewBinding
import com.example.facturedz.domain.model.ClientDomain
import com.example.facturedz.domain.model.InvoiceDomain
import com.example.facturedz.domain.model.ProductLineDomain
import com.example.facturedz.ocr.OcrResult // Placeholder
// Import other necessary classes like OCR Service, Repository, UseCases if not injected to ViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddEditInvoiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditInvoiceBinding
    private lateinit var viewModel: AddEditInvoiceViewModel // To be provided by a factory or DI
    private lateinit var productLineAdapter: EditableProductLineAdapter
    private var currentInvoiceId: Long = 0L
    private val productLines = mutableListOf<ProductLineDomain>()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                // Handle image URI for OCR
                // val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                // viewModel.processOcrResult(ocrService.extractDataFromImage(bitmap)) // Example
                Toast.makeText(this, "Image sélectionnée pour OCR (logique à implémenter)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditInvoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // ViewModel Initialization (replace with DI)
        // val repository = ... // Get repository instance
        // val addInvoiceUseCase = AddInvoiceUseCase(repository)
        // val getInvoiceDetailsUseCase = GetInvoiceDetailsUseCase(repository)
        // val detectAnomaliesUseCase = DetectAnomaliesUseCase(repository)
        // viewModel = ViewModelProvider(this, AddEditInvoiceViewModelFactory(repository, addInvoiceUseCase, getInvoiceDetailsUseCase, detectAnomaliesUseCase))[AddEditInvoiceViewModel::class.java]
        // This is a placeholder for proper ViewModel instantiation
        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[AddEditInvoiceViewModel::class.java]


        currentInvoiceId = intent.getLongExtra("INVOICE_ID", 0L)
        title = if (currentInvoiceId == 0L) "Nouvelle Facture" else "Modifier Facture"

        setupProductLinesRecyclerView()
        setupListeners()
        observeViewModel()

        if (savedInstanceState == null) { // Load only if not restoring from a saved state
            viewModel.loadInvoice(currentInvoiceId)
        }
    }

    private fun setupProductLinesRecyclerView() {
        productLineAdapter = EditableProductLineAdapter(
            onDeleteClicked = { productLine ->
                productLines.remove(productLine)
                productLineAdapter.submitList(productLines.toList()) // Update adapter
                updateTotalsFromProductLines()
            },
            onProductLineChanged = { updatedProductLine, position ->
                if (position >= 0 && position < productLines.size) {
                    productLines[position] = updatedProductLine
                }
                updateTotalsFromProductLines()
            }
        )
        binding.productLinesRecyclerView.apply {
            adapter = productLineAdapter
            layoutManager = LinearLayoutManager(this@AddEditInvoiceActivity)
        }
    }

    private fun setupListeners() {
        binding.invoiceDateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        binding.addProductLineButton.setOnClickListener {
            val newProductLine = ProductLineDomain(invoiceId = currentInvoiceId, designation = "", quantity = 1.0, unitPrice = 0.0, totalLinePrice = 0.0)
            productLines.add(newProductLine)
            productLineAdapter.submitList(productLines.toList())
        }

        binding.scanOcrButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
            // Or launch camera intent
        }

        binding.saveInvoiceButton.setOnClickListener {
            saveInvoiceData()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.currentInvoice.collectLatest {
                invoice ->
                invoice?.let { displayInvoiceData(it) }
            }
        }
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                state ->
                when (state) {
                    is AddEditInvoiceUiState.Loading -> { /* Show loading */ }
                    is AddEditInvoiceUiState.Error -> {
                        Toast.makeText(this@AddEditInvoiceActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                    is AddEditInvoiceUiState.Success -> {
                        // Data is already collected by currentInvoice flow
                    }
                }
            }
        }
    }

    private fun displayInvoiceData(invoice: InvoiceDomain) {
        binding.clientNameEditText.setText(invoice.client?.name ?: "")
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.invoiceDateEditText.setText(dateFormat.format(Date(invoice.date)))
        binding.invoiceNumberEditText.setText(invoice.invoiceNumber ?: "")
        binding.totalHtEditText.setText(String.format(Locale.US, "%.2f", invoice.totalHT))
        binding.totalTvaEditText.setText(String.format(Locale.US, "%.2f", invoice.totalTVA))
        binding.totalTtcEditText.setText(String.format(Locale.US, "%.2f", invoice.totalTTC))
        
        productLines.clear()
        productLines.addAll(invoice.productLines)
        productLineAdapter.submitList(productLines.toList())
    }

    private fun updateTotalsFromProductLines() {
        val totalHt = productLines.sumOf { it.totalLinePrice }
        // Assume a fixed TVA rate for now or get it from somewhere
        val tvaRate = 0.19 // Example 19%
        val totalTva = totalHt * tvaRate
        val totalTtc = totalHt + totalTva

        binding.totalHtEditText.setText(String.format(Locale.US, "%.2f", totalHt))
        binding.totalTvaEditText.setText(String.format(Locale.US, "%.2f", totalTva))
        binding.totalTtcEditText.setText(String.format(Locale.US, "%.2f", totalTtc))
    }

    private fun saveInvoiceData() {
        val clientName = binding.clientNameEditText.text.toString()
        val invoiceDateStr = binding.invoiceDateEditText.text.toString()
        val invoiceNumber = binding.invoiceNumberEditText.text.toString()
        val totalHT = binding.totalHtEditText.text.toString().toDoubleOrNull() ?: 0.0
        val totalTVA = binding.totalTvaEditText.text.toString().toDoubleOrNull() ?: 0.0
        val totalTTC = binding.totalTtcEditText.text.toString().toDoubleOrNull() ?: 0.0

        if (clientName.isBlank()) {
            Toast.makeText(this, "Le nom du client est requis", Toast.LENGTH_SHORT).show()
            return
        }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = try { dateFormat.parse(invoiceDateStr)?.time ?: System.currentTimeMillis() } catch (e: Exception) { System.currentTimeMillis() }

        val invoiceToSave = InvoiceDomain(
            id = currentInvoiceId, // Will be 0 for new invoices
            client = ClientDomain(name = clientName), // Simplified client handling
            invoiceNumber = invoiceNumber,
            date = date,
            totalHT = totalHT,
            totalTVA = totalTVA,
            totalTTC = totalTTC,
            productLines = productLines.toList(),
            ocrStatus = if (currentInvoiceId == 0L) "MANUAL" else viewModel.currentInvoice.value?.ocrStatus ?: "MANUAL"
            // anomalies will be detected by use case
        )
        viewModel.saveInvoice(invoiceToSave)
        Toast.makeText(this, "Facture enregistrée (logique de sauvegarde à finaliser)", Toast.LENGTH_SHORT).show()
        finish() // Close activity after save
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        viewModel.currentInvoice.value?.let {
            calendar.timeInMillis = it.date
        }

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.invoiceDateEditText.setText(dateFormat.format(selectedCalendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    // Placeholder for ViewModelFactory if not using Hilt/Koin
    // class AddEditInvoiceViewModelFactory(...) : ViewModelProvider.Factory { ... }
}
