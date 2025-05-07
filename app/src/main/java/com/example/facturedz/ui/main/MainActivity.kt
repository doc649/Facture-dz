package com.example.facturedz.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.facturedz.R // Assuming R is generated
import com.example.facturedz.databinding.ActivityMainBinding // Assuming ViewBinding is enabled
import com.example.facturedz.ui.addeditinvoice.AddEditInvoiceActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        // Define top-level destinations for AppBarConfiguration
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.dashboardFragment, R.id.invoiceListFragment, R.id.settingsFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigation.setupWithNavController(navController)

        // Handle FloatingActionButton click to navigate to AddEditInvoiceActivity
        binding.fabAddInvoice.setOnClickListener {
            val intent = Intent(this, AddEditInvoiceActivity::class.java)
            // Pass 0L or a specific constant to indicate a new invoice
            intent.putExtra("INVOICE_ID", 0L) 
            startActivity(intent)
        }
        
        // Disable the placeholder item in bottom navigation if it's still there
        // binding.bottomNavigation.menu.findItem(R.id.navigation_add_invoice_placeholder)?.isEnabled = false
    }

    // Handle Up button navigation
    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
