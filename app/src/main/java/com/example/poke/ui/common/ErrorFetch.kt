package com.example.poke.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.poke.ui.theme.PokeTheme

@Composable
fun ErrorState(
    modifier: Modifier = Modifier,
    text: String,
    handleRetry: () -> Unit = {}
){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Error $text",
            style = MaterialTheme.typography.body1.copy(
                    textAlign = TextAlign.Center
                )
            )
        Button(onClick = { handleRetry() }) {
            Text(text = "Retry")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorStatePreview(){
    PokeTheme {
        ErrorState(text = "laskdasldjalsdj")
    }
}