package com.example.st_client_mobile_tristanjooshowest.ui.shop

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.st_client_mobile_tristanjooshowest.R
import com.example.st_client_mobile_tristanjooshowest.domain.model.Product

fun shareBuildList(context: Context, cartItems: List<Product>) {
    if (cartItems.isEmpty()) return

    val buildText = StringBuilder().apply {
        appendLine("🏎️ Mijn Apex Track Day Build List:")
        appendLine("---------------------------------")
        cartItems.distinctBy { it.id }.forEach { product ->
            val count = cartItems.count { it.id == product.id }
            appendLine("• ${product.name} (x$count) - €${(product.price * count).toInt()}")
        }
        appendLine("---------------------------------")
        appendLine("Totaal: €${cartItems.sumOf { it.price }.toInt()}")
        appendLine("Gemaakt met de Apex Telemetry App! 🚀")
    }.toString()

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, buildText)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, "Deel build-lijst via:")
    context.startActivity(shareIntent)
}

@Composable
fun ShopScreen(
    products: List<Product>,
    cartItems: List<Product>,
    onAddToBuildClick: (Product) -> Unit,
    onRemoveFromBuildClick: (Product) -> Unit,
    onPlaceOrderClick: () -> Unit = {}
) {
    var showSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(colorResource(R.color.background))) {
            ShopHeader()

            AnimatedVisibility(
                visible = cartItems.isNotEmpty(),
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                BuildListSummary(
                    cartItems = cartItems,
                    onViewCartClick = { showSheet = true }
                )
            }

            Text(
                text = "Featured Products",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(products, key = { it.id }) { product ->
                    ProductCard(product, onAddToBuildClick)
                }
            }
        }

        if (showSheet) {
            CartBottomSheet(
                cartItems = cartItems,
                onAddItem = onAddToBuildClick,
                onRemoveItem = onRemoveFromBuildClick,
                onDismiss = { showSheet = false },
                onPlaceOrder = {
                    onPlaceOrderClick()
                    showSheet = false
                }
            )
        }
    }
}

@Composable
fun ShopHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF004AAD), Color(0xFF001A3D))
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column {
            Text(
                text = "Performance Shop",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Parts, gear & essentials",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun BuildListSummary(cartItems: List<Product>, onViewCartClick: () -> Unit) {
    val context = LocalContext.current
    val itemCount = cartItems.size
    val totalPrice = cartItems.sumOf { it.price }.toInt()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = Color(0xFF1A1A1A),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ShoppingCart, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Build List", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("$itemCount items", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Text("$$totalPrice", color = Color(0xFF3B82F6), fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onViewCartClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("View Cart", fontWeight = FontWeight.Bold)
                }
                IconButton(
                    onClick = { shareBuildList(context, cartItems) },
                    modifier = Modifier.background(Color(0xFF2C2C2C), RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Share, null, tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onAddToBuildClick: (Product) -> Unit) {
    val context = LocalContext.current

    val imageModel = remember(product.imageUrl) {
        "android.resource://${context.packageName}/drawable/${product.imageUrl}"
    }

    Surface(
        color = colorResource(R.color.card_color),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color.DarkGray)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageModel)
                        .placeholder(R.drawable.ic_launcher_background)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                if (product.isFeatured) {
                    Surface(
                        color = Color.Red,
                        shape = RoundedCornerShape(bottomStart = 8.dp),
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = "Featured",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(product.category, color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                Text(product.name, color = Color.White, style = MaterialTheme.typography.titleSmall, maxLines = 2, minLines = 2, fontWeight = FontWeight.Bold)
                Text("$$${product.price.toInt()}", color = Color.Red, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, modifier = Modifier.padding(vertical = 4.dp))
                Button(
                    onClick = { onAddToBuildClick(product) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Text(" Add to Build")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartBottomSheet(
    cartItems: List<Product>,
    onAddItem: (Product) -> Unit,
    onRemoveItem: (Product) -> Unit,
    onDismiss: () -> Unit,
    onPlaceOrder: () -> Unit
) {
    LaunchedEffect(cartItems.size) {
        if (cartItems.isEmpty()) {
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A1A),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 20.dp, end = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Build List", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, null, tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val distinctItems = cartItems.distinctBy { it.id }
            distinctItems.forEach { product ->
                val count = cartItems.count { it.id == product.id }
                CartItemRow(
                    product = product,
                    quantity = count,
                    onPlusClick = { onAddItem(product) },
                    onMinusClick = { onRemoveItem(product) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp), color = Color.DarkGray)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("$${cartItems.sumOf { it.price }.toInt()}", color = Color(0xFF3B82F6), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onPlaceOrder,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Place Order to Paddock", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(" Pickup available at circuit locker upon arrival", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun CartItemRow(
    product: Product,
    quantity: Int,
    onPlusClick: () -> Unit,
    onMinusClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(product.name, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1)
            Text("$${product.price.toInt()} each", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onMinusClick,
                modifier = Modifier.size(32.dp).background(Color(0xFF2C2C2C), RoundedCornerShape(8.dp))
            ) {
                Text("-", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Text(" $quantity ", color = Color.White, modifier = Modifier.padding(horizontal = 12.dp))

            IconButton(
                onClick = onPlusClick,
                modifier = Modifier.size(32.dp).background(Color(0xFF2C2C2C), RoundedCornerShape(8.dp))
            ) {
                Text("+", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Text(
            " $${(product.price * quantity).toInt()}",
            color = Color(0xFF3B82F6),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun ShopScreenPreview() {
    val mockProducts = listOf(
        Product(1, "Michelin Pilot Sport Cup 2", "Tires", 1299.0, "tire", true),
        Product(2, "Castrol Edge 5W-30", "Oil", 89.0, "oil", false),
        Product(3, "Apex Racing Suit - Pro", "Gear", 549.0, "gear", true),
        Product(4, "Brembo GT Brake Kit", "Brakes", 2499.0, "brakes", false)
    )
    MaterialTheme {
        ShopScreen(
            products = mockProducts,
            cartItems = emptyList(),
            onAddToBuildClick = {},
            onRemoveFromBuildClick = {}
        )
    }
}