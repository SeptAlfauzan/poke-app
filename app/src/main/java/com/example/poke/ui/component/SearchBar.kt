package com.example.poke.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.poke.R
import com.example.poke.ui.theme.PokeTheme

@Composable
fun SearchBar(
    onChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
){
    var input by remember{ mutableStateOf("") }
    val shape = RoundedCornerShape(8.dp)

    Row() {
        TextField(
            modifier = modifier
                .fillMaxWidth()
                .clip(shape)
                .border(width = 0.dp, color = Color.LightGray, shape = shape)
            ,
            value = input,
            onValueChange = {
                input = it
                onChange(it)
            },
            trailingIcon = {
                if(input.isNotEmpty()) Icon(imageVector = Icons.Default.Close, contentDescription = "reset search", modifier = Modifier.clickable {
                    input = ""
                    onChange
                })
            },
            label = {
                Text(
                    text = stringResource(R.string.search_pokemon),
                    style = MaterialTheme.typography.body1.copy(
                        color = Color.LightGray
                    )
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.surface
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview(){
    PokeTheme {
        SearchBar()
    }
}
