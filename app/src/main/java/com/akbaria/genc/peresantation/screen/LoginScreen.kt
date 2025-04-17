package com.akbaria.genc.peresantation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.akbaria.genc.peresantation.viewmodel.AuthViewModel


@Composable
fun LoginScreen(
    navigateToRegister: () -> Unit,
    navigateToBuyer: () -> Unit,
    navigateToSeller: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Art Marketplace",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifrə") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { authViewModel.loginUser(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Daxil ol")
        }
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = navigateToRegister) {
            Text("Hesab yoxdur? Qeydiyyatdan keç")
        }

        // Auth nəticəsini yoxlamaq
        LaunchedEffect(authState.isAuthenticated) {
            if (authState.isAuthenticated) {
                val isSeller = authViewModel.userState.value.user?.isSeller ?: false
                if (isSeller) navigateToSeller() else navigateToBuyer()
            }
        }

        // Xəta mesajı
        if (authState.error.isNotEmpty()) {
            Text(
                text = authState.error,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}