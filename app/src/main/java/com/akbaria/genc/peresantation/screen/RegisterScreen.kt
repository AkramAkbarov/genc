package com.akbaria.genc.peresantation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.akbaria.genc.peresantation.viewmodel.AuthViewModel


@Composable
fun RegisterScreen(
    navigateToLogin: () -> Unit,
    navigateToBuyer: () -> Unit,
    navigateToSeller: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isSeller by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Yeni hesab yaradın",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Ad") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

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
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = isSeller,
                onCheckedChange = { isSeller = it }
            )
            Text("Satıcı hesabı")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { authViewModel.registerUser(email, password, name, isSeller) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Qeydiyyatdan keç")
        }
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = navigateToLogin) {
            Text("Artıq hesabınız var? Daxil olun")
        }

        // Auth nəticəsini yoxlamaq
        LaunchedEffect(authState.isAuthenticated) {
            if (authState.isAuthenticated) {
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