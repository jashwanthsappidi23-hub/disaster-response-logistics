package com.example.daaproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.daaproject.data.ScenarioRepository
import com.example.daaproject.data.db.AppDatabase
import com.example.daaproject.data.db.SampleDataPopulator
import com.example.daaproject.ui.screens.EocDashboardScreen
import com.example.daaproject.ui.theme.DAAProjectTheme
import com.example.daaproject.ui.viewmodel.ScenarioViewModel
import com.example.daaproject.ui.viewmodel.ScenarioViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val db = AppDatabase.getDatabase(this)
        val repository = ScenarioRepository(db.appDao())
        
        lifecycleScope.launch {
            SampleDataPopulator.populateData(db.appDao())
        }
        
        setContent {
            DAAProjectTheme {
                val viewModel: ScenarioViewModel = viewModel(
                    factory = ScenarioViewModelFactory(repository)
                )
                
                LaunchedEffect(Unit) {
                    viewModel.setScenarioId("SCENARIO_1")
                }
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                        EocDashboardScreen(viewModel)
                    }
                }
            }
        }
    }
}
