import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { parameterService } from '../../services';
import { Parameter } from '../../models';
import './ParameterSettings.css';

interface ParameterFormData {
    maxMediumAirport: number;
    minFlightDuration: number;
    maxLayoverDuration: number;
    minLayoverDuration: number;
    minBookingInAdvanceDuration: number;
    maxBookingHoldDuration: number;
}

const ParameterSettings: React.FC = () => {
    const [parameters, setParameters] = useState<Parameter | null>(null);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const {
        register,
        handleSubmit,
        reset,
        formState: { errors }
    } = useForm<ParameterFormData>();

    useEffect(() => {
        loadParameters();
    }, []);

    const loadParameters = async () => {
        try {
            setLoading(true);
            const data = await parameterService.getParameters();
            setParameters(data);
            reset(data);
        } catch (err: any) {
            setError('Failed to load parameters');
        } finally {
            setLoading(false);
        }
    };

    const onSubmit = async (data: ParameterFormData) => {
        try {
            setSaving(true);
            setError('');
            setSuccess('');

            await parameterService.updateParameters(data);
            setSuccess('Parameters updated successfully');
            loadParameters();
        } catch (err: any) {
            setError(err.message || 'Failed to update parameters');
        } finally {
            setSaving(false);
        }
    };

    const handleInitializeDefaults = async () => {
        if (!window.confirm('Are you sure you want to initialize default parameters? This will reset all current values.')) {
            return;
        }

        try {
            setSaving(true);
            await parameterService.initializeDefaultParameters();
            setSuccess('Default parameters initialized successfully');
            loadParameters();
        } catch (err: any) {
            setError(err.message || 'Failed to initialize default parameters');
        } finally {
            setSaving(false);
        }
    };

    if (loading) {
        return <div className="loading">Loading system parameters...</div>;
    }

    return (
        <div className="parameter-settings">
            <div className="settings-header">
                <h2>System Parameters</h2>
                <p>Configure flight management system constraints and rules</p>
            </div>

            {error && <div className="error-message">{error}</div>}
            {success && <div className="success-message">{success}</div>}

            <form onSubmit={handleSubmit(onSubmit)} className="settings-form">
                <div className="settings-section">
                    <h3>Flight Constraints</h3>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Maximum Medium Airports</label>
                            <input
                                type="number"
                                min="0"
                                max="10"
                                {...register('maxMediumAirport', {
                                    required: 'Maximum medium airports is required',
                                    min: { value: 0, message: 'Value must be at least 0' },
                                    max: { value: 10, message: 'Value must be at most 10' },
                                    valueAsNumber: true
                                })}
                                className={errors.maxMediumAirport ? 'error' : ''}
                            />
                            {errors.maxMediumAirport && (
                                <span className="field-error">{errors.maxMediumAirport.message}</span>
                            )}
                            <small className="field-help">Maximum number of intermediate stops allowed per flight</small>
                        </div>

                        <div className="form-group">
                            <label>Minimum Flight Duration (minutes)</label>
                            <input
                                type="number"
                                min="30"
                                max="1440"
                                {...register('minFlightDuration', {
                                    required: 'Minimum flight duration is required',
                                    min: { value: 30, message: 'Minimum duration is 30 minutes' },
                                    max: { value: 1440, message: 'Maximum duration is 24 hours' },
                                    valueAsNumber: true
                                })}
                                className={errors.minFlightDuration ? 'error' : ''}
                            />
                            {errors.minFlightDuration && (
                                <span className="field-error">{errors.minFlightDuration.message}</span>
                            )}
                            <small className="field-help">Minimum allowed flight duration</small>
                        </div>
                    </div>
                </div>

                <div className="settings-section">
                    <h3>Layover Settings</h3>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Minimum Layover Duration (minutes)</label>
                            <input
                                type="number"
                                min="30"
                                max="720"
                                {...register('minLayoverDuration', {
                                    required: 'Minimum layover duration is required',
                                    min: { value: 30, message: 'Minimum layover is 30 minutes' },
                                    max: { value: 720, message: 'Maximum layover is 12 hours' },
                                    valueAsNumber: true
                                })}
                                className={errors.minLayoverDuration ? 'error' : ''}
                            />
                            {errors.minLayoverDuration && (
                                <span className="field-error">{errors.minLayoverDuration.message}</span>
                            )}
                            <small className="field-help">Minimum time required between connecting flights</small>
                        </div>

                        <div className="form-group">
                            <label>Maximum Layover Duration (minutes)</label>
                            <input
                                type="number"
                                min="60"
                                max="1440"
                                {...register('maxLayoverDuration', {
                                    required: 'Maximum layover duration is required',
                                    min: { value: 60, message: 'Minimum duration is 60 minutes' },
                                    max: { value: 1440, message: 'Maximum duration is 24 hours' },
                                    valueAsNumber: true
                                })}
                                className={errors.maxLayoverDuration ? 'error' : ''}
                            />
                            {errors.maxLayoverDuration && (
                                <span className="field-error">{errors.maxLayoverDuration.message}</span>
                            )}
                            <small className="field-help">Maximum allowed layover time</small>
                        </div>
                    </div>
                </div>

                <div className="settings-section">
                    <h3>Booking Rules</h3>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Minimum Booking Advance (minutes)</label>
                            <input
                                type="number"
                                min="60"
                                max="10080"
                                {...register('minBookingInAdvanceDuration', {
                                    required: 'Minimum booking advance is required',
                                    min: { value: 60, message: 'Minimum advance is 1 hour' },
                                    max: { value: 10080, message: 'Maximum advance is 1 week' },
                                    valueAsNumber: true
                                })}
                                className={errors.minBookingInAdvanceDuration ? 'error' : ''}
                            />
                            {errors.minBookingInAdvanceDuration && (
                                <span className="field-error">{errors.minBookingInAdvanceDuration.message}</span>
                            )}
                            <small className="field-help">Minimum time before departure to allow booking</small>
                        </div>

                        <div className="form-group">
                            <label>Maximum Booking Hold (minutes)</label>
                            <input
                                type="number"
                                min="15"
                                max="1440"
                                {...register('maxBookingHoldDuration', {
                                    required: 'Maximum booking hold is required',
                                    min: { value: 15, message: 'Minimum hold is 15 minutes' },
                                    max: { value: 1440, message: 'Maximum hold is 24 hours' },
                                    valueAsNumber: true
                                })}
                                className={errors.maxBookingHoldDuration ? 'error' : ''}
                            />
                            {errors.maxBookingHoldDuration && (
                                <span className="field-error">{errors.maxBookingHoldDuration.message}</span>
                            )}
                            <small className="field-help">Maximum time to hold a booking before payment</small>
                        </div>
                    </div>
                </div>

                <div className="form-actions">
                    <button
                        type="button"
                        className="btn btn-secondary"
                        onClick={handleInitializeDefaults}
                        disabled={saving}
                    >
                        Reset to Defaults
                    </button>
                    <button
                        type="submit"
                        className="btn btn-primary"
                        disabled={saving}
                    >
                        {saving ? 'Saving...' : 'Save Parameters'}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default ParameterSettings;
