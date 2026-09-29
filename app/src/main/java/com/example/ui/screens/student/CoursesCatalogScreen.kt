package com.example.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CourseEntity
import com.example.ui.components.CourseCard
import com.example.ui.components.EmptyStateView
import com.example.ui.navigation.Screen
import com.example.ui.theme.KgnGold
import com.example.ui.viewmodel.InstituteViewModel

@Composable
fun CoursesCatalogScreen(
    viewModel: InstituteViewModel,
    onNavigate: (Screen) -> Unit
) {
    val allCourses by viewModel.allCourses.collectAsState()
    var selectedCategory by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }
    var checkoutCourse by remember { mutableStateOf<CourseEntity?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val categories = listOf("ALL", "IIT-JEE", "NEET-UG", "Class 12th", "Class 10th", "Foundation")

    val filteredCourses = remember(allCourses, selectedCategory, searchQuery) {
        allCourses.filter { c ->
            val matchCat = selectedCategory == "ALL" || c.category.equals(selectedCategory, ignoreCase = true)
            val matchSearch = searchQuery.isBlank() ||
                    c.title.contains(searchQuery, ignoreCase = true) ||
                    c.description.contains(searchQuery, ignoreCase = true) ||
                    c.instructorName.contains(searchQuery, ignoreCase = true)
            matchCat && matchSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search
        Box(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search batches, exams, or faculties...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("catalog_search_input"),
                singleLine = true
            )
        }

        // Category Filter
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(if (cat == "ALL") "All Programs" else cat) },
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filteredCourses.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.SearchOff,
                title = "No Courses Match",
                description = "Try searching with a different term or clear the filter."
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredCourses, key = { it.id }) { course ->
                    CourseCard(
                        course = course,
                        onClick = {
                            if (course.isEnrolled) {
                                onNavigate(Screen.LiveClasses)
                            } else {
                                checkoutCourse = course
                            }
                        },
                        onEnrollClick = { checkoutCourse = course }
                    )
                }
            }
        }
    }

    // Checkout & Purchase Dialog
    if (checkoutCourse != null) {
        val course = checkoutCourse!!
        var couponCode by remember { mutableStateOf("") }
        var referralCode by remember { mutableStateOf("") }
        var discountAmount by remember { mutableStateOf(0.0) }
        var couponMessage by remember { mutableStateOf<String?>(null) }

        val payableAmount = (course.price - discountAmount).coerceAtLeast(0.0)

        AlertDialog(
            onDismissRequest = { checkoutCourse = null },
            title = { Text("Enroll in Course") },
            text = {
                Column {
                    Text(course.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text("Batch: ${course.category} • Validity: ${course.duration}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Coupon & Referral Inputs
                    OutlinedTextField(
                        value = couponCode,
                        onValueChange = { couponCode = it.uppercase() },
                        label = { Text("Discount Coupon (e.g. KGN50)") },
                        trailingIcon = {
                            TextButton(onClick = {
                                if (couponCode == "KGN50") {
                                    discountAmount = course.price * 0.5
                                    couponMessage = "🎉 50% discount applied successfully!"
                                } else {
                                    couponMessage = "Invalid coupon code."
                                }
                            }) {
                                Text("Apply")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = referralCode,
                        onValueChange = { referralCode = it.uppercase() },
                        label = { Text("Student Referral Code (e.g. KGN-AAMIR-77)") },
                        trailingIcon = {
                            TextButton(onClick = {
                                if (referralCode.isNotBlank()) {
                                    discountAmount += 500.0
                                    couponMessage = "🎁 Friend referral applied: ₹500 discount!"
                                }
                            }) {
                                Text("Verify")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (couponMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = couponMessage ?: "",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (discountAmount > 0) Color(0xFF059669) else Color(0xFFDC2626)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Course Fee:")
                        Text("₹${course.price.toInt()}")
                    }
                    if (discountAmount > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Discount Applied:", color = Color(0xFF059669))
                            Text("- ₹${discountAmount.toInt()}", color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Payable:", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text(
                            "₹${payableAmount.toInt()}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.purchaseCourse(course.id, discountAmount, referralCode)
                        checkoutCourse = null
                        showSuccessDialog = true
                    },
                    modifier = Modifier.testTag("confirm_purchase_btn")
                ) {
                    Text("Pay & Enroll (₹${payableAmount.toInt()})")
                }
            },
            dismissButton = {
                TextButton(onClick = { checkoutCourse = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Payment Successful! 🎉") },
            text = {
                Column {
                    Text("Congratulations! You are now enrolled in the course. All live sessions, study materials, and tests are now unlocked.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("A confirmation receipt and invoice have been generated and sent to your email.")
                }
            },
            confirmButton = {
                Button(onClick = { showSuccessDialog = false }) {
                    Text("Go to My Classes")
                }
            }
        )
    }
}
