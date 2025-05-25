import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import './AuthForms.css';

interface RegisterFormData {
  accountName: string;
  password: string;
  confirmPassword: string;
  email: string;
  phoneNumber: string;
  citizenId: string;
}

const RegisterForm: React.FC = () => {
  const { register: registerUser } = useAuth();
  const navigate = useNavigate();
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors }
  } = useForm<RegisterFormData>();

  const password = watch('password');

  const onSubmit = async (data: RegisterFormData) => {
    try {
      setLoading(true);
      setError('');
      
      const { confirmPassword, ...registerData } = data;
      // Set account type to customer (type 2)
      const registerRequest = {
        ...registerData,
        accountType: 2
      };
      
      await registerUser(registerRequest);
      
      navigate('/login', { 
        state: { message: 'Registration successful! Please sign in.' }
      });
    } catch (err: any) {
      setError(err.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-header">
          <h2>Create Account</h2>
          <p>Join us to start booking flights</p>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="auth-form">
          {error && <div className="error-message">{error}</div>}

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="accountName">Username</label>
              <input
                id="accountName"
                type="text"
                {...register('accountName', {
                  required: 'Username is required',
                  minLength: { value: 3, message: 'Username must be at least 3 characters' },
                  pattern: { value: /^[a-zA-Z0-9_]+$/, message: 'Username can only contain letters, numbers, and underscores' }
                })}
                className={errors.accountName ? 'error' : ''}
                placeholder="Choose a username"
              />
              {errors.accountName && (
                <span className="field-error">{errors.accountName.message}</span>
              )}
            </div>

            <div className="form-group">
              <label htmlFor="email">Email</label>
              <input
                id="email"
                type="email"
                {...register('email', {
                  required: 'Email is required',
                  pattern: { value: /^\S+@\S+\.\S+$/, message: 'Please enter a valid email address' }
                })}
                className={errors.email ? 'error' : ''}
                placeholder="Enter your email"
              />
              {errors.email && (
                <span className="field-error">{errors.email.message}</span>
              )}
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="phoneNumber">Phone Number</label>
              <input
                id="phoneNumber"
                type="tel"
                {...register('phoneNumber', {
                  required: 'Phone number is required',
                  pattern: { value: /^\+?[\d\s-()]+$/, message: 'Please enter a valid phone number' }
                })}
                className={errors.phoneNumber ? 'error' : ''}
                placeholder="Enter your phone number"
              />
              {errors.phoneNumber && (
                <span className="field-error">{errors.phoneNumber.message}</span>
              )}
            </div>

            <div className="form-group">
              <label htmlFor="citizenId">Citizen ID</label>
              <input
                id="citizenId"
                type="text"
                {...register('citizenId', {
                  required: 'Citizen ID is required',
                  minLength: { value: 9, message: 'Citizen ID must be at least 9 characters' }
                })}
                className={errors.citizenId ? 'error' : ''}
                placeholder="Enter your citizen ID"
              />
              {errors.citizenId && (
                <span className="field-error">{errors.citizenId.message}</span>
              )}
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="password">Password</label>
              <input
                id="password"
                type="password"
                {...register('password', {
                  required: 'Password is required',
                  minLength: { value: 6, message: 'Password must be at least 6 characters' },
                  pattern: { 
                    value: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/,
                    message: 'Password must contain at least one uppercase letter, one lowercase letter, and one number'
                  }
                })}
                className={errors.password ? 'error' : ''}
                placeholder="Create a password"
              />
              {errors.password && (
                <span className="field-error">{errors.password.message}</span>
              )}
            </div>

            <div className="form-group">
              <label htmlFor="confirmPassword">Confirm Password</label>
              <input
                id="confirmPassword"
                type="password"
                {...register('confirmPassword', {
                  required: 'Please confirm your password',
                  validate: value => value === password || 'Passwords do not match'
                })}
                className={errors.confirmPassword ? 'error' : ''}
                placeholder="Confirm your password"
              />
              {errors.confirmPassword && (
                <span className="field-error">{errors.confirmPassword.message}</span>
              )}
            </div>
          </div>

          <button 
            type="submit" 
            className="btn btn-primary btn-full"
            disabled={loading}
          >
            {loading ? 'Creating Account...' : 'Create Account'}
          </button>
        </form>

        <div className="auth-footer">
          <p>Already have an account? <Link to="/login">Sign in here</Link></p>
        </div>
      </div>
    </div>
  );
};

export default RegisterForm;
