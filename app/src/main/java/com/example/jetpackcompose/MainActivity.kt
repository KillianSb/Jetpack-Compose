package com.example.jetpackcompose

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Entity
import com.example.jetpackcompose.ui.theme.JetpackComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposeTheme {
                MyApp(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

data class Task(val name: String, val detail: String)

@Composable
fun MyApp(modifier: Modifier = Modifier) {

    var shouldShowOnboarding by rememberSaveable { mutableStateOf(true) }
    var shouldShowHome by rememberSaveable { mutableStateOf(false) }
    var shouldShowForm by rememberSaveable { mutableStateOf(false) }

    val tasks = remember { mutableStateListOf<Task>() }

    Surface(modifier) {
        if (shouldShowOnboarding) {
            OnboardingScreen(onContinueClicked = { shouldShowOnboarding = false; shouldShowHome = true })
        }
        else if (shouldShowHome) {
            Greetings(
                onContinueClicked = { shouldShowHome = false; shouldShowForm = true },
                tasks = tasks
            )
        }
        else if (shouldShowForm) {
            FormTache(
                onContinueClicked = { name, detail ->
                    // Traitez les valeurs de nom et de détail ici si nécessaire
                    shouldShowForm = false
                    shouldShowHome = true
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormTache(
    onContinueClicked: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var taskName by remember { mutableStateOf("") }
    var taskDetail by remember { mutableStateOf("") }

    var tasks by remember { mutableStateOf(listOf<Task>()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Formulaire de Tâche",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Nom de la tâche") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = taskDetail,
            onValueChange = { taskDetail = it },
            label = { Text("Détail de la tâche") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                if (taskName.isNotEmpty() || taskDetail.isNotEmpty()) {
                    val newTask = Task(taskName, taskDetail)
                    tasks += newTask // Ajouter la tâche à la liste
                    taskName = "" // Réinitialiser le champ de nom
                    taskDetail = "" // Réinitialiser le champ de détail
                    onContinueClicked(taskName, taskDetail) // Appeler la fonction de continuation
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Continuer")
        }
    }
}

@Composable
private fun Greetings(
    modifier: Modifier = Modifier,
    onContinueClicked: () -> Unit,
    tasks: List<Task>
) {
    Column() {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                text = "Tâches",
            )
            Button(
                onClick = onContinueClicked,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Créer une tâche")
            }
        }
        LazyColumn(modifier = modifier.padding(vertical = 4.dp)) {
            items(items = tasks) { task ->
                Greeting(task = task)
            }
        }
    }
}

@Composable
private fun Greeting(task: Task) {
    var expended by remember { mutableStateOf(false) }

    val extraPadding by animateDpAsState(
        if (expended) 48.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = ""
    )
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 8.dp),
        shape = MaterialTheme.shapes.large
        ){
        Row(modifier = Modifier.padding(24.dp)) {
            Column(modifier = Modifier
                .weight(1f)
                .padding(bottom = extraPadding.coerceAtLeast(0.dp))
                ) {
                Text(text = "Tâche :")
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                Text(
                    text = if (expended) task.detail else "",
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            IconButton(onClick = { expended = !expended }) {
                Icon(
                    imageVector = if (expended) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expended) {
                        stringResource(R.string.show_less)
                    } else {
                        stringResource(R.string.show_more)
                    }
                )
            }
        }
    }
}

@Composable
fun OnboardingScreen(
    onContinueClicked: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bienvenue sur JetPack Compose !",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold
            ),
            textAlign = TextAlign.Center
        )
        Button(
            modifier = Modifier
                .padding(vertical = 24.dp),
            onClick = onContinueClicked
        ) {
            Text("Continue")
        }
    }

}

@Preview
@Composable
fun FormPreview() {
    JetpackComposeTheme {
        FormTache(onContinueClicked = { name, detail ->
            // Traitez les valeurs de nom et de détail ici si nécessaire
        })
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = UI_MODE_NIGHT_YES,
    name = "Dark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
fun DefaultPreview() {
    JetpackComposeTheme {
        val tasks = listOf(
            Task("Tâche 1", "Détail de la tâche 1"),
            Task("Tâche 2", "Détail de la tâche 2"),
            Task("Tâche 3", "Détail de la tâche 3")
        )
        Greetings(onContinueClicked = {}, tasks = tasks)
    }
}

@Preview
@Composable
fun MyAppPreview() {
    JetpackComposeTheme {
        MyApp(Modifier.fillMaxSize())
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun OnboardingPreview() {
    JetpackComposeTheme {
        OnboardingScreen(onContinueClicked = {}) // Do nothing on click.
    }
}