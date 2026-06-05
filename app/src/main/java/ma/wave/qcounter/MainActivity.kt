package ma.wave.qcounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import ma.wave.qcounter.ui.ViewModelFactory
import ma.wave.qcounter.ui.navigation.QCounterNavHost
import ma.wave.qcounter.ui.theme.QCounterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = (application as QCounterApp).repository
        val factory = ViewModelFactory(repository)

        setContent {
            QCounterTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    QCounterNavHost(factory = factory)
                }
            }
        }
    }
}
