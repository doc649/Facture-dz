package com.example.facturedz.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider // Or inject
import androidx.lifecycle.lifecycleScope
import com.example.facturedz.databinding.FragmentDashboardBinding // Assuming ViewBinding
import com.example.facturedz.domain.usecase.DashboardData
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    // ViewModel should be provided, e.g. by Hilt or koin, or activityViewModels()
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        // A proper ViewModel factory or DI should be used here.
        // For now, assuming it can be obtained via a simple Provider from the activity.
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dashboardData.collectLatest { uiState ->
                when (uiState) {
                    is DashboardDataUiState.Loading -> {
                        // Show loading indicator if any
                    }
                    is DashboardDataUiState.Success -> {
                        updateDashboardUI(uiState.data)
                    }
                    is DashboardDataUiState.Error -> {
                        // Show error message
                        binding.totalInvoicedThisMonthTextView.text = "Erreur de chargement"
                        binding.frequentClientsTextView.text = "-"
                        binding.detectedAnomaliesCountTextView.text = "-"
                        binding.invoicesToWatchCountTextView.text = "-"
                    }
                }
            }
        }
    }

    private fun updateDashboardUI(data: DashboardData) {
        binding.totalInvoicedThisMonthTextView.text = String.format(Locale.FRANCE, "%.2f DA", data.totalInvoicedThisMonth)
        binding.frequentClientsTextView.text = data.frequentClients.joinToString("\n") { "- $it" }
        binding.detectedAnomaliesCountTextView.text = data.detectedAnomaliesCount.toString()
        binding.invoicesToWatchCountTextView.text = data.invoicesToWatchCount.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
