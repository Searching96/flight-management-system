import React from 'react'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { BrowserRouter } from 'react-router-dom'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import LoginForm from '../LoginForm'
import { AuthProvider } from '../../../hooks/useAuth'
import { accountService } from '../../../services'

// Mock the services
vi.mock('../../../services', () => ({
  accountService: {
    login: vi.fn(),
  },
}))

const mockedAccountService = vi.mocked(accountService)

const TestWrapper = ({ children }: { children: React.ReactNode }) => (
  <BrowserRouter>
    <AuthProvider>{children}</AuthProvider>
  </BrowserRouter>
)

describe('LoginForm', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    // localStorage is already mocked in setup.ts
  })

  it('should render login form with all fields', () => {
    // Arrange & Act
    render(
      <TestWrapper>
        <LoginForm />
      </TestWrapper>
    )

    // Assert
    expect(screen.getByLabelText(/email/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /sign in/i })).toBeInTheDocument()
    expect(screen.getByText(/don't have an account/i)).toBeInTheDocument()
  })

  it('should submit login form with valid credentials', async () => {
    // Arrange
    const user = userEvent.setup()
    const mockLoginResponse = {
      accountId: 1,
      accountName: 'Test User',
      email: 'test@example.com',
      accountType: 2,
      token: 'mock-token',
    }
    mockedAccountService.login.mockResolvedValueOnce(mockLoginResponse)

    render(
      <TestWrapper>
        <LoginForm />
      </TestWrapper>
    )    // Act
    await user.type(screen.getByLabelText(/email/i), 'test@example.com')
    await user.type(screen.getByLabelText(/password/i), 'password123')
    await user.click(screen.getByRole('button', { name: /sign in/i }))

    // Assert
    await waitFor(() => {
      expect(mockedAccountService.login).toHaveBeenCalledWith({
        email: 'test@example.com',
        password: 'password123',
      })
    })
  })

  it('should display validation errors for empty fields', async () => {
    // Arrange
    const user = userEvent.setup()
    render(
      <TestWrapper>
        <LoginForm />
      </TestWrapper>
    )    // Act
    await user.click(screen.getByRole('button', { name: /sign in/i }))

    // Assert
    await waitFor(() => {
      expect(screen.getByText(/email is required/i)).toBeInTheDocument()
      expect(screen.getByText(/password is required/i)).toBeInTheDocument()
    })
  })

  it('should display error message on login failure', async () => {
    // Arrange
    const user = userEvent.setup()
    const errorMessage = 'Invalid credentials'
    mockedAccountService.login.mockRejectedValueOnce(new Error(errorMessage))

    render(
      <TestWrapper>
        <LoginForm />
      </TestWrapper>
    )

    // Act    await user.type(screen.getByLabelText(/email/i), 'test@example.com')
    await user.type(screen.getByLabelText(/password/i), 'wrongpassword')
    await user.click(screen.getByRole('button', { name: /sign in/i }))

    // Assert
    await waitFor(() => {
      expect(screen.getByText(errorMessage)).toBeInTheDocument()
    })
  })

  it('should disable submit button while loading', async () => {
    // Arrange
    const user = userEvent.setup()
    let resolveLogin: (value: any) => void
    const loginPromise = new Promise((resolve) => {
      resolveLogin = resolve
    })
    mockedAccountService.login.mockReturnValueOnce(loginPromise as Promise<any>)

    render(
      <TestWrapper>
        <LoginForm />
      </TestWrapper>
    )    // Act
    await user.type(screen.getByLabelText(/email/i), 'test@example.com')
    await user.type(screen.getByLabelText(/password/i), 'password123')
    await user.click(screen.getByRole('button', { name: /sign in/i }))

    // Assert
    expect(screen.getByRole('button', { name: /signing in/i })).toBeDisabled()

    // Cleanup
    resolveLogin!({
      accountId: 1,
      accountName: 'Test User',
      email: 'test@example.com',
      accountType: 2,
    })
  })

  it('should validate email format', async () => {
    // Arrange
    const user = userEvent.setup()
    render(
      <TestWrapper>
        <LoginForm />
      </TestWrapper>
    )    // Act
    await user.type(screen.getByLabelText(/email/i), 'invalid-email')
    await user.type(screen.getByLabelText(/password/i), 'password123')
    await user.click(screen.getByRole('button', { name: /sign in/i }))

    // Assert
    await waitFor(() => {
      expect(screen.getByText(/invalid email format/i)).toBeInTheDocument()
    })
  })

  it('should navigate to registration page when link is clicked', async () => {
    // Arrange
    const user = userEvent.setup()
    render(
      <TestWrapper>
        <LoginForm />
      </TestWrapper>
    )

    // Act
    const registerLink = screen.getByRole('link', { name: /create one/i })
    await user.click(registerLink)

    // Assert
    expect(registerLink).toHaveAttribute('href', '/register')
  })
})
