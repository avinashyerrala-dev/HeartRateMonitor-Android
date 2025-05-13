package com.health.heartratemonitor.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun UserProfileScreen(
    onProfileSaved: () -> Unit
) {
    val viewModel: UserProfileViewModel = hiltViewModel()

    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val dob by viewModel.dob.collectAsState()
    val weight by viewModel.weight.collectAsState()
    val weightUnit by viewModel.weightUnit.collectAsState()
    val heightUnit by viewModel.heightUnit.collectAsState()
    val heightFeet by viewModel.heightFeet.collectAsState()
    val heightInches by viewModel.heightInches.collectAsState()
    val heightMeters by viewModel.heightMeters.collectAsState()
    val heightCentimeters by viewModel.heightCentimeters.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    if (saveSuccess) {
        onProfileSaved()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = firstName,
            onValueChange = { viewModel.firstName.value = it },
            label = { Text("First Name") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = { viewModel.lastName.value = it },
            label = { Text("Last Name") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = dob,
            onValueChange = { viewModel.dob.value = formatDobInput(it) },
            label = { Text("Date of Birth (yyyy-MM-dd)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = weight,
                onValueChange = { viewModel.weight.value = it },
                label = { Text("Weight") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            DropdownMenuBox(
                selectedOption = weightUnit,
                options = listOf("kg", "lb"),
                onOptionSelected = { viewModel.weightUnit.value = it }
            )
        }

        DropdownMenuBox(
            selectedOption = heightUnit,
            options = listOf("Imperial", "Metric"),
            onOptionSelected = { viewModel.heightUnit.value = it }
        )

        if (heightUnit == "Imperial") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = heightFeet ?: "",
                    onValueChange = { viewModel.heightFeet.value = it },
                    label = { Text("Height Feet") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = heightInches ?: "",
                    onValueChange = { viewModel.heightInches.value = it },
                    label = { Text("Height Inches") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = heightMeters ?: "",
                    onValueChange = { viewModel.heightMeters.value = it },
                    label = { Text("Height Meters") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = heightCentimeters ?: "",
                    onValueChange = { viewModel.heightCentimeters.value = it },
                    label = { Text("Height Centimeters") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Button(
            onClick = { viewModel.saveProfile() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Profile")
        }
    }
}

fun formatDobInput(input: TextFieldValue): TextFieldValue {
    val digits = input.text.filter { it.isDigit() }.take(8)
    val newText = when {
        digits.length <= 4 -> digits
        digits.length <= 6 -> "${digits.substring(0, 4)}-${digits.substring(4)}"
        else -> "${digits.substring(0, 4)}-${digits.substring(4, 6)}-${digits.substring(6)}"
    }
    val cursorPos = newText.length
    return TextFieldValue(newText, selection = TextRange(cursorPos))
}

@Composable
fun DropdownMenuBox(
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopStart)
    ) {
        OutlinedButton(onClick = { expanded.value = true }) {
            Text(selectedOption)
        }
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded.value = false
                    }
                )
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun UserProfileScreenPreview() {
//    UserProfileScreen(onProfileSaved = {})
//}
