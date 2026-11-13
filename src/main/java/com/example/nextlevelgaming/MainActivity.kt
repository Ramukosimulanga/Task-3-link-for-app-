
package com.example.nextlevelgaming

import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nextlevelgaming.ui.theme.NextLevelGamingTheme
import kotlinx.coroutines.delay

// ==========================================
// COLOURS AND FONTS
// ==========================================

private val DarkBg = Color(0xFF04070D)
private val CardBg = Color(0xFF111827)
private val NeonBlue = Color(0xFF00B8FF)
private val NeonCyan = Color(0xFF00E5FF)
private val NeonPurple = Color(0xFF8A2BE2)
private val White = Color(0xFFFFFFFF)
private val Gray = Color(0xFFD8D8D8)

private val OrbitronFont = FontFamily.SansSerif
    


// ==========================================
// PACKAGE DATA
// ==========================================

data class PackageItem(
    val name: String,
    val price: Int,
    val description: String,
    val image: Int
)

private val gamingPackages = listOf(
    PackageItem(
        "Ultimate Gamer Pass",
        1500,
        "Premium gaming experience with high-performance gaming stations.",
        R.drawable.one
    ),
    PackageItem(
        "VIP Gaming Experience",
        1500,
        "A premium private gaming experience for serious gamers.",
        R.drawable.two
    ),
    PackageItem(
        "Esports Training Package",
        1500,
        "Train, compete and improve your esports skills.",
        R.drawable.three
    ),
    PackageItem(
        "Birthday Party Package",
        1500,
        "A complete gaming experience for unforgettable celebrations.",
        R.drawable.four
    ),
    PackageItem(
        "Virtual Reality",
        750,
        "Step into an immersive virtual reality experience.",
        R.drawable.five
    ),
    PackageItem(
        "Racing Simulator",
        750,
        "Experience high-speed racing with a realistic simulator.",
        R.drawable.six
    ),
    PackageItem(
        "Escape Room",
        750,
        "Work together, solve puzzles and escape before time runs out.",
        R.drawable.seven
    )
)

// ==========================================
// MAIN ACTIVITY
// ==========================================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NextLevelGamingTheme {
                NextLevelApp()
            }
        }
    }
}

// ==========================================
// MAIN APP AND NAVIGATION
// ==========================================

@Composable
fun NextLevelApp() {

    var selectedPage by rememberSaveable {
        mutableStateOf("Home")
    }

    var chosenPackage by rememberSaveable {
        mutableStateOf("")
    }

    var showLoading by rememberSaveable {
        mutableStateOf(true)
    }

    Box(Modifier.fillMaxSize()) {

        Scaffold(
            containerColor = DarkBg,
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFF050B16),
                    contentColor = White
                ) {
                    NavigationBarItem(
                        selected = selectedPage == "Home",
                        onClick = { selectedPage = "Home" },
                        icon = {
                            Icon(Icons.Default.Home, "Home")
                        },
                        label = { Text("Home") }
                    )

                    NavigationBarItem(
                        selected = selectedPage == "Packages",
                        onClick = { selectedPage = "Packages" },
                        icon = {
                            Icon(Icons.Default.Star, "Packages")
                        },
                        label = { Text("Packages") }
                    )

                    NavigationBarItem(
                        selected = selectedPage == "About",
                        onClick = { selectedPage = "About" },
                        icon = {
                            Icon(Icons.Default.Info, "About")
                        },
                        label = { Text("About") }
                    )

                    NavigationBarItem(
                        selected = selectedPage == "Booking",
                        onClick = { selectedPage = "Booking" },
                        icon = {
                            Icon(Icons.Default.DateRange, "Booking")
                        },
                        label = { Text("Booking") }
                    )

                    NavigationBarItem(
                        selected = selectedPage == "Contact",
                        onClick = { selectedPage = "Contact" },
                        icon = {
                            Icon(Icons.Default.Email, "Contact")
                        },
                        label = { Text("Contact") }
                    )
                }
            }
        ) { paddingValues ->

            when (selectedPage) {

                "Home" -> HomePage(
                    paddingValues = paddingValues,
                    onBookNow = {
                        selectedPage = "Packages"
                    }
                )

                "Packages" -> PackagesPage(
                    paddingValues = paddingValues,
                    selectedPackage = chosenPackage,
                    onSelectPackage = {
                        chosenPackage = it
                    },
                    onBookPackage = {
                        chosenPackage = it
                        selectedPage = "Booking"
                    }
                )

                "About" -> AboutPage(paddingValues)

                "Booking" -> BookingPage(
                    paddingValues = paddingValues,
                    initialPackage = chosenPackage
                )

                "Contact" -> ContactPage(paddingValues)
            }
        }

        AnimatedVisibility(
            visible = showLoading,
            exit = fadeOut(tween(900))
        ) {
            LoadingScreen(
                onFinished = {
                    showLoading = false
                }
            )
        }
    }
}

// ==========================================
// LOADING SCREEN
// ==========================================

@Composable
fun LoadingScreen(
    onFinished: () -> Unit
) {
    val progress = remember {
        Animatable(0f)
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(3000)
        )

        delay(200)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020305)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "NEXT LEVEL",
                fontFamily = OrbitronFont,
                fontWeight = FontWeight.Bold,
                fontSize = 35.sp,
                letterSpacing = 3.sp,
                color = NeonCyan,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = Shadow(
                        color = NeonCyan,
                        offset = Offset.Zero,
                        blurRadius = 30f
                    )
                )
            )

            Spacer(Modifier.height(22.dp))

            Text(
                text = "LOADING EXPERIENCE...",
                color = White,
                fontSize = 12.sp,
                letterSpacing = 4.sp
            )

            Spacer(Modifier.height(35.dp))

            Box(
                modifier = Modifier
                    .width(300.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0xFF151515))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress.value)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    NeonCyan,
                                    Color(0xFF008CFF),
                                    NeonCyan
                                )
                            ),
                            RoundedCornerShape(50.dp)
                        )
                )
            }
        }
    }
}

// ==========================================
// HEADER
// ==========================================

@Composable
fun NeonHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF020305))
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Next Level Gaming Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .width(135.dp)
                .height(55.dp)
        )

        Spacer(Modifier.width(10.dp))

        Column {
            Text(
                "NEXT LEVEL",
                fontFamily = OrbitronFont,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = NeonCyan
            )

            Text(
                "GAMING & ESPORTS ARENA",
                fontSize = 8.sp,
                color = White,
                letterSpacing = 1.sp
            )
        }
    }
}

// ==========================================
// SECTION TITLE
// ==========================================

@Composable
fun SectionTitle(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 20.dp)
    ) {
        Text(
            subtitle,
            color = NeonPurple,
            fontSize = 11.sp,
            letterSpacing = 2.sp
        )

        Spacer(Modifier.height(6.dp))

        Text(
            title,
            color = White,
            fontFamily = OrbitronFont,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ==========================================
// FEATURE CARD
// ==========================================

@Composable
fun FeatureCard(
    icon: String,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBg
        ),
        border = BorderStroke(
            1.dp,
            Color(0x4000E5FF)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                icon,
                fontSize = 32.sp
            )

            Spacer(Modifier.width(14.dp))

            Column {
                Text(
                    title,
                    color = NeonCyan,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    description,
                    color = Gray,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }
        }
    }
}

// ==========================================
// HOME PAGE
// ==========================================

@Composable
fun HomePage(
    paddingValues: PaddingValues,
    onBookNow: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg),
        contentPadding = PaddingValues(
            bottom = paddingValues.calculateBottomPadding()
        )
    ) {

        item {
            NeonHeader()
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(470.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.codee),
                    contentDescription = "Next Level Gaming Background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.55f),
                                    Color.Black.copy(alpha = 0.82f),
                                    DarkBg
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "NEXT LEVEL",
                        fontFamily = OrbitronFont,
                        fontSize = 29.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        "GAMING & ESPORTS ARENA",
                        fontFamily = OrbitronFont,
                        fontSize = 14.sp,
                        color = White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(24.dp))

                    Text(
                        "Where Gamers Become Champions",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        "Established in 2023 by Jason Naidoo, " +
                                "Next Level Gaming & Esports Arena " +
                                "is based in Johannesburg, South Africa.",
                        color = White,
                        fontSize = 14.sp,
                        lineHeight = 23.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "We bring individuals, schools, businesses " +
                                "and gaming communities together through " +
                                "professional gaming facilities and modern entertainment.",
                        color = Gray,
                        fontSize = 13.sp,
                        lineHeight = 21.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(25.dp))

                    Button(
                        onClick = onBookNow,
                        modifier = Modifier.fillMaxWidth(0.8f),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonBlue
                        )
                    ) {
                        Text(
                            "EXPLORE PACKAGES",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onBookNow,
                        modifier = Modifier.fillMaxWidth(0.8f),
                        shape = RoundedCornerShape(30.dp),
                        border = BorderStroke(1.dp, NeonCyan),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = White
                        )
                    ) {
                        Text("BOOK NOW")
                    }
                }
            }
        }

        item {
            SectionTitle(
                "OUR GAMING EXPERIENCES",
                "DISCOVER WHAT WE OFFER"
            )
        }

        item {
            FeatureCard(
                "🎮",
                "GAMING EVENTS",
                "Enjoy exciting gaming experiences with friends and other players."
            )
        }

        item {
            FeatureCard(
                "🏆",
                "ESPORTS TOURNAMENTS",
                "Compete, develop your skills and experience competitive gaming."
            )
        }

        item {
            FeatureCard(
                "🎂",
                "BIRTHDAY PARTIES",
                "Celebrate special occasions with an unforgettable gaming party."
            )
        }

        item {
            FeatureCard(
                "🚌",
                "SCHOOL OUTINGS",
                "Bring students together for an exciting gaming experience."
            )
        }

        item {
            FeatureCard(
                "👥",
                "CORPORATE TEAM BUILDING",
                "Build teamwork and connections through gaming activities."
            )
        }

        item {
            SectionTitle(
                "NEXT LEVEL BY THE NUMBERS",
                "OUR ARENA AT A GLANCE"
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatItem(
                        "2023",
                        "ESTABLISHED",
                        Modifier.weight(1f)
                    )

                    StatItem(
                        "7",
                        "EXPERIENCES",
                        Modifier.weight(1f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatItem(
                        "5000+",
                        "HAPPY GAMERS",
                        Modifier.weight(1f)
                    )

                    StatItem(
                        "5★",
                        "CUSTOMER RATING",
                        Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Spacer(Modifier.height(25.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                NeonCyan,
                                Color(0xFF008CFF),
                                NeonPurple
                            )
                        )
                    )
                    .clickable {
                        onBookNow()
                    }
                    .padding(25.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "READY TO LEVEL UP?",
                        fontFamily = OrbitronFont,
                        color = Color.Black,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(9.dp))

                    Text(
                        "YOUR NEXT GAMING ADVENTURE STARTS HERE",
                        color = Color.Black,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF020305))
                    .padding(25.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "NEXT LEVEL GAMING",
                    fontFamily = OrbitronFont,
                    color = NeonCyan,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "& ESPORTS ARENA",
                    color = White,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    "Established in 2023 by Jason Naidoo",
                    color = Gray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    "Johannesburg, South Africa",
                    color = Gray,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    "Gaming Events  •  Esports  •  Birthday Parties\n" +
                            "School Outings  •  Corporate Team Building",
                    color = Gray,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(Modifier.height(15.dp))

                Text(
                    "© 2026 Next Level Gaming & Esports Arena",
                    color = Gray,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun StatItem(
    number: String,
    title: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBg
        ),
        border = BorderStroke(
            1.dp,
            Color(0x4000E5FF)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 23.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                number,
                fontFamily = OrbitronFont,
                color = NeonCyan,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(7.dp))

            Text(
                title,
                color = Gray,
                fontSize = 9.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ==========================================
// PACKAGES PAGE
// ==========================================

@Composable
fun PackagesPage(
    paddingValues: PaddingValues,
    selectedPackage: String,
    onSelectPackage: (String) -> Unit,
    onBookPackage: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg),
        contentPadding = PaddingValues(
            bottom = paddingValues.calculateBottomPadding()
        )
    ) {
        item {
            NeonHeader()

            SectionTitle(
                "PACKAGES & EXPERIENCES",
                "CHOOSE YOUR EXPERIENCE"
            )

            if (selectedPackage.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF101C30)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(Modifier.padding(15.dp)) {
                        Text(
                            "PACKAGE SELECTED",
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )

                        Spacer(Modifier.height(5.dp))

                        Text(
                            selectedPackage,
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(Modifier.height(10.dp))

                        Button(
                            onClick = {
                                onBookPackage(selectedPackage)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonBlue
                            )
                        ) {
                            Text("BOOK NOW")
                        }
                    }
                }
            }
        }

        items(gamingPackages) { item ->
            PackageCard(
                item = item,
                isSelected = selectedPackage == item.name,
                onSelect = { onSelectPackage(item.name) },
                onBook = { onBookPackage(item.name) }
            )
        }
    }
}

@Composable
fun PackageCard(
    item: PackageItem,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onBook: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .then(
                if (isSelected) {
                    Modifier.border(
                        2.dp,
                        NeonCyan,
                        RoundedCornerShape(18.dp)
                    )
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                Color(0xFF101C30)
            } else {
                CardBg
            }
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(item.image),
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(105.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .clickable { onSelect() }
                )

                Spacer(Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        item.name,
                        color = White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(
                        "R${item.price}",
                        color = NeonCyan,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(
                        item.description,
                        color = Gray,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSelect,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isSelected) NeonCyan else White
                    )
                ) {
                    Text(
                        if (isSelected) "SELECTED ✓" else "SELECT",
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = onBook,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonPurple
                    )
                ) {
                    Text(
                        "BOOK NOW",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ==========================================
// ABOUT PAGE
// ==========================================

@Composable
fun AboutPage(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
    ) {
        NeonHeader()

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "ABOUT NEXT LEVEL",
                color = NeonCyan,
                fontFamily = OrbitronFont,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Where Gamers Become Champions",
                color = White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Established in 2023 by Jason Naidoo, " +
                        "Next Level Gaming & Esports Arena is based " +
                        "in Johannesburg, South Africa.",
                color = Gray,
                fontSize = 15.sp,
                lineHeight = 24.sp
            )

            Text(
                "We provide gaming experiences for individuals, " +
                        "schools, businesses and gaming communities. " +
                        "Our arena brings together entertainment, " +
                        "competitive gaming and unforgettable events.",
                color = Gray,
                fontSize = 15.sp,
                lineHeight = 24.sp
            )

            FeatureCard(
                "🎮",
                "OUR MISSION",
                "To provide an exciting gaming environment where players can enjoy games, develop their skills and connect with others."
            )

            FeatureCard(
                "🏆",
                "OUR VISION",
                "To create a destination for gaming, esports competitions and entertainment."
            )

            FeatureCard(
                "🚀",
                "OUR EXPERIENCES",
                "Explore virtual reality, racing simulators, esports training and gaming parties."
            )

            FeatureCard(
                "🤝",
                "OUR COMMUNITY",
                "Bringing individuals, schools, businesses and gaming communities together."
            )
        }
    }
}

// ==========================================
// BOOKING PAGE
// ==========================================

@Composable
fun BookingPage(
    paddingValues: PaddingValues,
    initialPackage: String
) {
    var selectedPackage by remember(initialPackage) {
        mutableStateOf(
            gamingPackages.firstOrNull {
                it.name == initialPackage
            }
        )
    }

    var bookingDetails by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
    ) {
        if (bookingDetails.isEmpty()) {
            BookingScreen(
                packages = gamingPackages,
                selectedPackage = selectedPackage,
                onPackageSelected = {
                    selectedPackage = it
                },
                onBookingComplete = {
                    bookingDetails = it
                }
            )
        } else {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "BOOKING REQUEST READY",
                    color = NeonCyan,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    bookingDetails,
                    color = White,
                    lineHeight = 24.sp
                )

                Text(
                    "This is a booking request, not a confirmed reservation.",
                    color = Gray
                )

                Button(
                    onClick = {
                        bookingDetails = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonPurple
                    )
                ) {
                    Text("BACK TO BOOKING")
                }
            }
        }
    }
}

// ==========================================
// BOOKING FORM
// ==========================================

@Composable
fun BookingScreen(
    packages: List<PackageItem>,
    selectedPackage: PackageItem?,
    onPackageSelected: (PackageItem) -> Unit,
    onBookingComplete: (String) -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var customerEmail by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var bookingDate by remember { mutableStateOf("") }
    var bookingTime by remember { mutableStateOf("") }
    var players by remember { mutableStateOf("1") }
    var message by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        NeonHeader()

        Text(
            "BOOK YOUR GAMING SESSION",
            color = NeonCyan,
            fontSize = 23.sp,
            fontFamily = OrbitronFont,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Choose a package and complete your booking details.",
            color = Gray
        )

        Text(
            "SELECT PACKAGE",
            color = White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        packages.forEach { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onPackageSelected(item)
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedPackage?.name == item.name) {
                        Color(0xFF302052)
                    } else {
                        CardBg
                    }
                ),
                border = if (selectedPackage?.name == item.name) {
                    BorderStroke(2.dp, NeonPurple)
                } else {
                    null
                }
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        item.name,
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )

                    Text(
                        "R${item.price}",
                        color = NeonCyan,
                        fontSize = 16.sp
                    )

                    Text(
                        item.description,
                        color = Gray,
                        fontSize = 13.sp
                    )

                    if (selectedPackage?.name == item.name) {
                        Text(
                            "SELECTED ✓",
                            color = Color(0xFF4ADE80),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Text(
            "CUSTOMER DETAILS",
            color = White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        BookingInput(
            customerName,
            { customerName = it },
            "Full Name"
        )

        BookingInput(
            customerEmail,
            { customerEmail = it },
            "Email Address"
        )

        BookingInput(
            customerPhone,
            { customerPhone = it },
            "Phone Number"
        )

        BookingInput(
            bookingDate,
            { bookingDate = it },
            "Booking Date (YYYY-MM-DD)"
        )

        BookingInput(
            bookingTime,
            { bookingTime = it },
            "Booking Time (e.g. 14:00)"
        )

        BookingInput(
            players,
            { players = it },
            "Number of Players"
        )

        BookingInput(
            message,
            { message = it },
            "Additional Requests",
            singleLine = false
        )

        if (errorMessage.isNotBlank()) {
            Text(
                errorMessage,
                color = Color(0xFFFF7777),
                fontSize = 14.sp
            )
        }

        Button(
            onClick = {
                val playerCount = players.toIntOrNull()

                errorMessage = when {
                    selectedPackage == null ->
                        "Please select a gaming package."

                    customerName.isBlank() ->
                        "Please enter your full name."

                    !Patterns.EMAIL_ADDRESS
                        .matcher(customerEmail.trim())
                        .matches() ->
                        "Please enter a valid email address."

                    customerPhone.isBlank() ->
                        "Please enter your phone number."

                    bookingDate.isBlank() ->
                        "Please enter your booking date."

                    bookingTime.isBlank() ->
                        "Please enter your booking time."

                    playerCount == null || playerCount < 1 ->
                        "Enter a valid number of players."

                    else -> ""
                }

                if (
                    errorMessage.isEmpty() &&
                    selectedPackage != null
                ) {
                    onBookingComplete(
                        """
                        BOOKING REQUEST

                        Name: $customerName
                        Email: $customerEmail
                        Phone: $customerPhone

                        Package: ${selectedPackage.name}
                        Price: R${selectedPackage.price}

                        Date: $bookingDate
                        Time: $bookingTime
                        Players: $playerCount

                        Additional Requests: $message
                        """.trimIndent()
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonPurple
            )
        ) {
            Text(
                "CONFIRM BOOKING",
                color = White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ==========================================
// BOOKING INPUT
// ==========================================

@Composable
fun BookingInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label)
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        maxLines = if (singleLine) 1 else 4,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = White,
            unfocusedTextColor = White,
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = Gray,
            focusedLabelColor = NeonCyan,
            unfocusedLabelColor = Gray,
            cursorColor = NeonCyan
        )
    )
}

// ==========================================
// CONTACT PAGE
// ==========================================

@Composable
fun ContactPage(paddingValues: PaddingValues) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(rememberScrollState())
            .padding(paddingValues)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NeonHeader()

        Text(
            "CONTACT US",
            color = NeonCyan,
            fontFamily = OrbitronFont,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Next Level Gaming & Esports Arena",
            color = White,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Have questions about our packages, tournaments or bookings? Send us a message.",
            color = Gray,
            fontSize = 14.sp
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = CardBg
            ),
            border = BorderStroke(
                1.dp,
                Color(0x4000E5FF)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "GAMING SUPPORT",
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Text(
                    "Email: Add your official business email",
                    color = Gray
                )

                Text(
                    "Phone: Add your business phone number",
                    color = Gray
                )

                Text(
                    "Location: Johannesburg, South Africa",
                    color = Gray
                )
            }
        }

        Text(
            "SEND US A MESSAGE",
            color = White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        BookingInput(
            name,
            { name = it },
            "Full Name"
        )

        BookingInput(
            email,
            { email = it },
            "Email Address"
        )

        BookingInput(
            subject,
            { subject = it },
            "Subject"
        )

        BookingInput(
            message,
            { message = it },
            "Your Message",
            singleLine = false
        )

        Button(
            onClick = {
                feedback = when {
                    name.isBlank() ->
                        "Please enter your name."

                    !Patterns.EMAIL_ADDRESS
                        .matcher(email.trim())
                        .matches() ->
                        "Please enter a valid email address."

                    subject.isBlank() ->
                        "Please enter a subject."

                    message.isBlank() ->
                        "Please enter your message."

                    else ->
                        "Your message is ready. Connect a backend to send it."
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonPurple
            )
        ) {
            Text(
                "SEND MESSAGE",
                color = White
            )
        }

        if (feedback.isNotBlank()) {
            Text(
                feedback,
                color = NeonCyan,
                fontSize = 14.sp
            )
        }

        Text(
            "© 2026 Next Level Gaming & Esports Arena",
            color = Gray,
            fontSize = 12.sp
        )
    }
}
