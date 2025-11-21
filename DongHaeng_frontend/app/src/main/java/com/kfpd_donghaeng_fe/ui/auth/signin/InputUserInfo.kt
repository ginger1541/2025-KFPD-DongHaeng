package com.kfpd_donghaeng_fe.ui.auth.signin

import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kfpd_donghaeng_fe.R // R.drawable.ic_logo_orange 사용을 위해 필요
import com.kfpd_donghaeng_fe.viewmodel.auth.UserInfoUiState

// UI 상태 정의 (새로운 요구사항 반영)


// 프로젝트의 실제 색상으로 대체해야 합니다.
val CustomRed = Color(0xFFE53935)
val CustomGreen = Color(0xFF4CAF50)

val TextBlack = Color(0xFF212121)

// 기본 TextField Shape 정의
val CustomShape = RoundedCornerShape(8.dp)

// 텍스트 스타일 정의 (폰트 스타일 제거)
@Composable
fun AsteriskLabel(text: String) {
    Text(
        text = text,
        color = TextBlack,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
fun UserInfoScreen(
    uiState: UserInfoUiState,
    onUserIdChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordConfirmChange: (String) -> Unit,
    onGenderSelect: (String) -> Unit, // ⭐ onGenderSelect 파라미터 정의
    onPhoneNumberChange: (String) -> Unit,
    onBirthDateChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onTogglePasswordConfirmVisibility: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- 1. 로고 이미지 ---
        Spacer(modifier = Modifier.height(40.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_logo_orange),
            contentDescription = "앱 로고",
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))

        // --- 2. ID 입력 필드 ---
        UserInfoInputField(
            label = "아이디*",
            value = uiState.userId,
            onValueChange = onUserIdChange,
            placeholder = "아이디를 입력해주세요",
            errorMessage = uiState.userIdError,
            keyboardType = KeyboardType.Text // 한글 입력 가능 (ID에 따라 Text 또는 Ascii 설정)
        )
        Spacer(modifier = Modifier.height(24.dp))

        // --- 3. 성별 선택 ---
        GenderSelectionField(
            selectedGender = uiState.gender,
            onSelect = onGenderSelect // ⭐ onGenderSelect 파라미터 정상 전달
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- 4. 비밀번호 입력 필드 ---
        PasswordInputField(
            label = "비밀번호*",
            value = uiState.password,
            onValueChange = onPasswordChange,
            placeholder = "비밀번호를 입력해주세요",
            showPassword = uiState.showPassword,
            onToggleVisibility = onTogglePasswordVisibility
        )
        Spacer(modifier = Modifier.height(12.dp)) // 비밀번호 확인 필드와의 간격 줄임

        // --- 5. 비밀번호 확인 입력 필드 ---
        PasswordInputField(
            label = "비밀번호 확인*",
            value = uiState.passwordConfirm,
            onValueChange = onPasswordConfirmChange,
            placeholder = "비밀번호를 다시 입력해주세요",
            errorMessage = uiState.passwordError, // 불일치 오류 표시
            showPassword = uiState.showPasswordConfirm,
            onToggleVisibility = onTogglePasswordConfirmVisibility
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- 6. 전화번호 입력 필드 ---
        UserInfoInputField(
            label = "전화번호*",
            value = uiState.phoneNumber,
            onValueChange = onPhoneNumberChange,
            placeholder = "전화번호를 입력해주세요 (-) 생략",
            validationMessage = uiState.phoneValidationMessage,
            keyboardType = KeyboardType.Number
        )
        Spacer(modifier = Modifier.height(24.dp))

        // --- 8. 다음 버튼 ---

    }
    Box(modifier = Modifier.fillMaxSize()){
    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter) // Box 하단 중앙에 고정
            .fillMaxWidth()
            .padding(horizontal = 25.dp) // 콘텐츠와 동일한 좌우 패딩
            .padding(bottom = 24.dp) // 하단 여백
    ) {
        // LoginPageButton 컴포넌트를 사용합니다. (이름이 "다음" 대신 "완료"로 가정)

        // 💡 버튼은 enabled 속성으로 선택 상태에 따라 활성화/비활성화됩니다.
        Button(
            onClick =onNextClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
            shape = RoundedCornerShape(8.dp),
            // 사용자 유형이 선택되지 않으면 버튼 비활성화
            //enabled = (selectedType != null)
        ) {
            Text("완료", color = Color.White)
        }   }
    }
}


// --- 하위 컴포넌트 (성별 선택) ---
@Composable
fun GenderSelectionField(
    selectedGender: String?,
    onSelect: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        AsteriskLabel(text = "성별*")
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GenderRadioButton(
                label = "남자",
                isSelected = selectedGender == "남자",
                onClick = { onSelect("남자") }
            )
            Spacer(modifier = Modifier.width(24.dp))
            GenderRadioButton(
                label = "여자",
                isSelected = selectedGender == "여자",
                onClick = { onSelect("여자") }
            )
        }
    }
}

@Composable
fun GenderRadioButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = BrandOrange,
                unselectedColor = Color.Gray
            )
        )
        Text(
            text = label,
            color = TextBlack,
            fontSize = 16.sp
        )
    }
}

// --- 하위 컴포넌트 (일반 텍스트 입력) ---
@Composable
fun UserInfoInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    errorMessage: String? = null,
    validationMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        AsteriskLabel(text = label)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(placeholder, color = Color.Gray, fontSize = 16.sp) },
            shape = CustomShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (errorMessage != null) CustomRed else BrandOrange,
                unfocusedBorderColor = if (errorMessage != null) CustomRed else Color.LightGray,
                cursorColor = BrandOrange
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = CustomRed,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        } else if (validationMessage != null) {
            Text(
                text = validationMessage,
                color = CustomGreen,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

// --- 하위 컴포넌트 (비밀번호 입력) ---
@Composable
fun PasswordInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    errorMessage: String? = null,
    showPassword: Boolean,
    onToggleVisibility: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        AsteriskLabel(text = label)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(placeholder, color = Color.Gray, fontSize = 16.sp) },
            shape = CustomShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (errorMessage != null) CustomRed else BrandOrange,
                unfocusedBorderColor = if (errorMessage != null) CustomRed else Color.LightGray,
                cursorColor = BrandOrange
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (showPassword)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                val description = if (showPassword) "Hide password" else "Show password"

                IconButton(onClick = onToggleVisibility) {
                    Icon(imageVector = image, contentDescription = description)
                }
            }
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = CustomRed,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

/*

// --- 미리보기 ---
@Preview(showBackground = true)
@Composable
fun UserInfoScreenPreview() {
    var uiState by remember {
        mutableStateOf(
            UserInfoUiState(
                userId = "user_id_123",
                password = "Password123",
                passwordConfirm = "Password1234", // 불일치 유도
                passwordError = "비밀번호가 일치하지 않습니다.", // 오류 메시지 표시
                gender = "남자", // 성별 선택
                phoneNumber = "01012345678",
            )
        )
    }

    // 상태 업데이트 로직 (ViewModel 없이 Preview에서 상태 변화를 테스트하기 위함)
    fun updateState(newState: UserInfoUiState): UserInfoUiState {
        // 비밀번호 일치 여부 검사 로직 재사용
        val pwError = if (newState.password.isNotEmpty() && newState.passwordConfirm.isNotEmpty() && newState.password != newState.passwordConfirm) {
            "비밀번호가 일치하지 않습니다."
        } else {
            null
        }
        return newState.copy(passwordError = pwError)
    }

    MaterialTheme {
        UserInfoScreen(
            uiState = uiState,
            onUserIdChange = { newId ->
                uiState = updateState(uiState.copy(userId = newId))
            },
            onPasswordChange = { newPw ->
                uiState = updateState(uiState.copy(password = newPw))
            },
            onPasswordConfirmChange = { newPwC ->
                uiState = updateState(uiState.copy(passwordConfirm = newPwC))
            },
            onGenderSelect = { newGender -> // ⭐ onGenderSelect 핸들러 연결
                uiState = updateState(uiState.copy(gender = newGender))
            },
            onPhoneNumberChange = { newPhone ->
                uiState = updateState(uiState.copy(phoneNumber = newPhone))
            },
            onBirthDateChange = { newBirthDate ->
                uiState = updateState(uiState.copy(birthDate = newBirthDate))
            },
            onTogglePasswordVisibility = {
                uiState = updateState(uiState.copy(showPassword = !uiState.showPassword))
            },
            onTogglePasswordConfirmVisibility = {
                uiState = updateState(uiState.copy(showPasswordConfirm = !uiState.showPasswordConfirm))
            }
        )
    }
}

*/
