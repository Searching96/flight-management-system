import React, { useState } from 'react';
import { statisticsService } from '../../services';

const Statistics: React.FC = () => {
  const [selectedYear, setSelectedYear] = useState<number>(new Date().getFullYear());
  const [yearlyStats, setYearlyStats] = useState<any>(null);
  const [loading, setLoading] = useState<boolean>(false);

  const handleFetchYearlyStats = async () => {
    setLoading(true);
    try {
      const stats = await statisticsService.getYearlyStatistics();
      console.log('Yearly Statistics:', stats);
      setYearlyStats(stats);
    } catch (error) {
      console.error('Error fetching yearly statistics:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleFetchMonthlyStats = async () => {
    try {
      const stats = await statisticsService.getMonthlyStatistics(selectedYear);
      console.log('Monthly Statistics:', stats);
    } catch (error) {
      console.error('Error fetching monthly statistics:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="statistics-container">
      <h2>Flight Statistics</h2>
      
      <div className="yearly-stats-section">
        <h3>Yearly Statistics</h3>
        <button 
          onClick={handleFetchYearlyStats}
          disabled={loading}
          className="fetch-btn"
        >
          {loading ? 'Loading...' : 'Fetch Yearly Statistics'}
        </button>
      </div>

      <div className="monthly-stats-section">
        <h3>Monthly Statistics</h3>
        <div className="input-group">
          <label htmlFor="year-picker">Year:</label>
          <input
            id="year-picker"
            type="number"
            value={selectedYear}
            onChange={(e) => setSelectedYear(parseInt(e.target.value))}
            min="2000"
            max="2030"
            className="year-input"
          />
        </div>
        
        <button 
          onClick={handleFetchMonthlyStats}
          className="fetch-btn"
        >
          {loading ? 'Loading...' : 'Fetch Monthly Statistics'}
        </button>
      </div>
    </div>
  );
};

export default Statistics;
