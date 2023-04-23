package com.example.poke.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    modifier: Modifier = Modifier,
    onChange: (String) -> Unit = {},
){
    var input by remember{ mutableStateOf("") }
    val shape = RoundedCornerShape(32.dp)

    TextField(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(width = 1.dp, color = Color.LightGray, shape = shape)
        ,
        value = input,
        onValueChange = {
            input = it
            onChange(it)
        },
        trailingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = stringResource(R.string.search_icon))
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
            backgroundColor = MaterialTheme.colors.background
        )
    )
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview(){
    PokeTheme {
        SearchBar()
    }
}
