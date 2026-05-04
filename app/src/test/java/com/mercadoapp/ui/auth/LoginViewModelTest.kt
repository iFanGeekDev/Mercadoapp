package com.mercadoapp.ui.auth

import com.mercadoapp.domain.model.User
import com.mercadoapp.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: LoginViewModel

    private val fakeUser = User(id = "u1", email = "test@test.com", name = "Diego")

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        viewModel = LoginViewModel(authRepository)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `login with blank email shows error`() {
        viewModel.onEmailChanged("")
        viewModel.onPasswordChanged("password123")
        viewModel.login()
        assertNotNull(viewModel.state.value.error)
        assertFalse(viewModel.state.value.isSuccess)
    }

    @Test
    fun `login with blank password shows error`() {
        viewModel.onEmailChanged("user@test.com")
        viewModel.onPasswordChanged("")
        viewModel.login()
        assertNotNull(viewModel.state.value.error)
    }

    @Test
    fun `successful login sets isSuccess true`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns Result.success(fakeUser)
        viewModel.onEmailChanged("user@test.com")
        viewModel.onPasswordChanged("pass1234")
        viewModel.login()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.isSuccess)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `failed login shows error message`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns Result.failure(Exception("Credenciales inválidas"))
        viewModel.onEmailChanged("bad@test.com")
        viewModel.onPasswordChanged("wrongpass")
        viewModel.login()
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isSuccess)
        assertEquals("Credenciales inválidas", viewModel.state.value.error)
    }

    @Test
    fun `login sets isLoading true during request`() = runTest {
        coEvery { authRepository.login(any(), any()) } coAnswers {
            kotlinx.coroutines.delay(500)
            Result.success(fakeUser)
        }
        viewModel.onEmailChanged("user@test.com")
        viewModel.onPasswordChanged("pass1234")
        viewModel.login()
        // Before advanceUntilIdle, loading should be true
        assertTrue(viewModel.state.value.isLoading)
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isLoading)
    }
}
