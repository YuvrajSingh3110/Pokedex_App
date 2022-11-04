package com.example.pokedex_app.pokemonlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.*
import coil.request.ImageRequest
import com.example.pokedex.R
import com.example.pokedex_app.data.models.PokedexListEntry
import com.example.pokedex.ui.theme.RobotoCondensed

@Composable
fun PokemonListScreen(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Spacer(modifier = Modifier.height(20.dp))
            Image(
                painter = painterResource(id = R.drawable.international_pokemon_logo),
                contentDescription = "Pokemon",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(CenterHorizontally)
            )
            Spacer(modifier = Modifier.padding(15.dp))
            searchBar(
                hint = "Search...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ){
                viewModel.searchPokemonList(it)
            }
            Spacer(modifier = Modifier.height(15.dp))
            PokemonList(navController = navController)
        }

    }
}

@Composable
fun searchBar(
    modifier: Modifier = Modifier,
    hint: String = "",
    onSearch: (String) -> Unit = {}
) {
    var text by remember {
        mutableStateOf("")
    }
    var isHintDisplayed by remember {
        mutableStateOf(hint != "") //if we pass a string then this will be true otherwise if an empty string is passes this will be false
    }

    Box(modifier = Modifier){
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    if (it.isFocused)
                        isHintDisplayed = false
                }
        )
        if (isHintDisplayed){
            Text(
                text = hint,
                color = Color.LightGray,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
fun PokemonList(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
){
    val pokemonList by remember {viewModel.pokemonList}
    val endReached by remember {viewModel.endReached}
    val loadError by remember {viewModel.loadError}
    val isLoading by remember {viewModel.isLoading}
    val isSearching by remember {viewModel.isSearching}

    LazyColumn(contentPadding = PaddingValues(16.dp)){
        val itemCount = if(pokemonList.size % 2 == 0) {
            pokemonList.size / 2
        } else {
            (pokemonList.size / 2) + 1
        }
        items(itemCount) {
            if(it >= itemCount - 1 && !endReached && !isLoading && !isSearching){
                viewModel.loadPokemonPaginated()
            }
            PokedexRow(rowIndex = it, entries = pokemonList, navController = navController)
        }
    }
    Box(
        contentAlignment = Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if(isLoading){
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        if (loadError.isNotEmpty()){
            retrySection(error = loadError) {
                viewModel.loadPokemonPaginated()
            }
        }
    }
}

@Composable
fun PokedexEntry(
    entry: PokedexListEntry,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    val defaultDominantColor = MaterialTheme.colorScheme.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    Box(
        contentAlignment = Center,
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(
                Brush.verticalGradient(
                    listOf(
                        dominantColor,
                        defaultDominantColor
                    )
                )
            )
            .clickable {
                navController.navigate(
                    "pokemon_detail_screen/${dominantColor.toArgb()}/${entry.pokemonName}" //toArgb() to get the integer value of color
                )
            }
    ) {
        Column {
//            AsyncImage(  //used to load image
//                request = ImageRequest.Builder(LocalContext.current)
//                    .data(entry.imageUrl)
//                    .target {
//                        viewModel.calcDominantColor(it) { color ->
//                            dominantColor = color
//                        }
//                    }
//                    .build(),
//                contentDescription = entry.pokemonName,
//                fadeIn = true,
//                modifier = Modifier
//                    .size(120.dp)
//                    .align(CenterHorizontally)
//            )
//            {
//                CircularProgressIndicator(
//                    color = MaterialTheme.colorScheme.primary,
//                    modifier = Modifier
//                        .scale(50f)
//                )
//            }

//            AsyncImage(
//                model = ImageRequest.Builder(LocalContext.current)
//                    .data(entry.imageUrl)
//                    .crossfade(true)
//                    .target {
//                        viewModel.calcDominantColor(it) { color ->
//                            dominantColor = color
//                        }
//                    }
//                    .build(),
//                onSuccess = { success ->
//                            val drawable = success.result.drawable
//                },
//                contentDescription = entry.pokemonName,
//                modifier = Modifier
//                    .size(120.dp)
//                    .align(CenterHorizontally),
//                placeholder = painterResource()
//            )


            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(entry.imageUrl)
                    .crossfade(true)
                    .build()
            )
            (painter.state as? AsyncImagePainter.State.Success)
                ?.let { success ->
                    LaunchedEffect(Unit) {
                        val drawable = success.result.drawable
                        viewModel.calcDominantColor(drawable) { color ->
                            dominantColor = color
                        }
                    }
                }
            Image(
                painter = painter,
                contentDescription = entry.pokemonName,
                modifier = Modifier
                    .size(120.dp)
                    .align(CenterHorizontally)
            )

            Text(
                text = entry.pokemonName,
                fontFamily = RobotoCondensed,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PokedexRow(
    rowIndex: Int,
    entries: List<PokedexListEntry>,
    navController: NavController
){
    Column {
        Row {
            PokedexEntry(
                entry = entries[rowIndex * 2],
                navController = navController,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(15.dp))
            if(entries.size >= ((rowIndex * 2) + 2)){
                PokedexEntry(
                    entry = entries[(rowIndex * 2) + 1],
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            }
            else
                Spacer(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun retrySection(
    error: String,
    onRetry: () -> Unit
){
    Column {
        Text(error, color = Color.Red, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onRetry,
            modifier = Modifier.align(CenterHorizontally)
        )
        {
            Text(text = "Retry")
        }
    }
}