package com.example.facturedz.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider // Or inject
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.facturedz.databinding.FragmentInvoiceListBinding // Assuming ViewBinding
import com.example.facturedz.domain.model.InvoiceDomain
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class InvoiceListFragment : Fragment() {

    private var _binding: FragmentInvoiceListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel // Shared with DashboardFragment
    private lateinit var invoiceAdapter: InvoiceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInvoiceListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        observeInvoices()
    }

    private fun setupRecyclerView() {
        invoiceAdapter = InvoiceAdapter { invoice ->
            // Navigate to InvoiceDetailFragment
            val action = InvoiceListFragmentDirections.actionInvoiceListFragmentToInvoiceDetailFragment(invoice.id)
            findNavController().navigate(action)
        }
        binding.invoicesRecyclerView.apply {
            adapter = invoiceAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupSearch() {
        binding.searchInvoiceEditText.addTextChangedListener {
            viewModel.onSearchQueryChanged(it.toString())
        }
    }

    private fun observeInvoices() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.invoices.collectLatest {
                invoices -> invoiceAdapter.submitList(invoices)
            }
        }
        // Observe loading state if needed
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collectLatest {
                isLoading ->
                // Show/hide progress bar based on isLoading
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
