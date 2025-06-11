import React, { useState, useEffect } from 'react';
import { statisticsService } from '../../services';
import {
   LineChart,
   Line,
   XAxis,
   YAxis,
   CartesianGrid,
   Tooltip,
   Legend,
   PieChart,
   Pie,
   Cell,
   ResponsiveContainer
} from 'recharts';
import './Statistics.css';

const Statistics: React.FC = () => {
   const [selectedYear, setSelectedYear] = useState<number>(new Date().getFullYear());
   const [yearlyStats, setYearlyStats] = useState<any>(null);
   const [monthlyStats, setMonthlyStats] = useState<any>(null);
   const [loading, setLoading] = useState<boolean>(false);
   const [monthlyLoading, setMonthlyLoading] = useState<boolean>(false);
   const [activeView, setActiveView] = useState<'yearly' | 'monthly'>('yearly');

   // Colors for charts
   const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8', '#82CA9D', '#FFC658', '#FF7C7C', '#8DD1E1', '#D084D0', '#FFB347', '#87CEEB'];

   const fetchYearlyStats = async () => {
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

   const fetchMonthlyStats = async () => {
      setMonthlyLoading(true);
      try {
         const stats = await statisticsService.getMonthlyStatistics(selectedYear);
         console.log('Monthly Statistics:', stats);
         setMonthlyStats(stats);
      } catch (error) {
         console.error('Error fetching monthly statistics:', error);
      } finally {
         setMonthlyLoading(false);
      }
   };

   // Auto-fetch yearly stats on component mount
   useEffect(() => {
      fetchYearlyStats();
   }, []);

   // Auto-fetch monthly stats when year changes or when switching to monthly view
   useEffect(() => {
      if (activeView === 'monthly') {
         fetchMonthlyStats();
      }
   }, [selectedYear, activeView]);

   // Generate years for combobox (current year and 4 previous years)
   const availableYears = Array.from({ length: 5 }, (_, i) => new Date().getFullYear() - i);

   return (
      <div className="statistics-container">
         {/* Navigation Buttons */}
         <div className="navigation-buttons">
            <button
               onClick={() => setActiveView('yearly')}
               className={`nav-button ${activeView === 'yearly' ? 'active' : 'inactive'}`}
            >
               Yearly Statistics
            </button>
            <button
               onClick={() => setActiveView('monthly')}
               className={`nav-button ${activeView === 'monthly' ? 'active' : 'inactive'}`}
            >
               Monthly Statistics
            </button>
         </div>

         {/* Yearly Statistics Section */}
         {activeView === 'yearly' && (
            <div className="yearly-stats-section">
               <h3 className="section-title">Yearly Statistics</h3>

               {loading ? (
                  <div className="loading-message">Loading yearly statistics...</div>
               ) : yearlyStats ? (
                  <div className="charts-grid">
                     {/* Total Flights Line Chart */}
                     <div className="chart-container">
                        <h4>Total Flights by Year</h4>
                        <ResponsiveContainer width="100%" height={300}>
                           <LineChart data={yearlyStats} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                              <CartesianGrid strokeDasharray="3 3" />
                              <XAxis dataKey="year" />
                              <YAxis />
                              <Tooltip formatter={(value, name) => [value, name === 'totalFlights' ? 'Total Flights' : name]} />
                              <Legend formatter={(value) => value === 'totalFlights' ? 'Total Flights' : value} />
                              <Line type="monotone" dataKey="totalFlights" stroke="#8884d8" strokeWidth={2} />
                           </LineChart>
                        </ResponsiveContainer>
                     </div>

                     {/* Total Passengers Line Chart */}
                     <div className="chart-container">
                        <h4>Total Passengers by Year</h4>
                        <ResponsiveContainer width="100%" height={300}>
                           <LineChart data={yearlyStats} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                              <CartesianGrid strokeDasharray="3 3" />
                              <XAxis dataKey="year" />
                              <YAxis />
                              <Tooltip formatter={(value, name) => [value, name === 'totalPassengers' ? 'Total Passengers' : name]} />
                              <Legend formatter={(value) => value === 'totalPassengers' ? 'Total Passengers' : value} />
                              <Line type="monotone" dataKey="totalPassengers" stroke="#82ca9d" strokeWidth={2} />
                           </LineChart>
                        </ResponsiveContainer>
                     </div>

                     {/* Total Revenue Line Chart */}
                     <div className="chart-container">
                        <h4>Total Revenue by Year</h4>
                        <ResponsiveContainer width="100%" height={300}>
                           <LineChart data={yearlyStats} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                              <CartesianGrid strokeDasharray="3 3" />
                              <XAxis dataKey="year" />
                              <YAxis />
                              <Tooltip formatter={(value) => [`$${value.toLocaleString()}`, 'Revenue']} />
                              <Legend formatter={(value) => value === 'totalRevenue' ? 'Total Revenue' : value} />
                              <Line type="monotone" dataKey="totalRevenue" stroke="#ffc658" strokeWidth={2} />
                           </LineChart>
                        </ResponsiveContainer>
                     </div>

                     {/* Revenue Distribution Pie Chart */}
                     <div className="pie-chart-container">
                        <div className="pie-chart-wrapper">
                           <h4>Revenue Distribution by Year</h4>
                           <ResponsiveContainer width={350} height={350}>
                              <PieChart margin={{ top: 5, right: 10, left: 20, bottom: 10 }}>
                                 <Pie
                                    data={yearlyStats}
                                    cx="50%"
                                    cy="50%"
                                    labelLine={false}
                                    outerRadius={100}
                                    fill="#8884d8"
                                    dataKey="totalRevenue"
                                 >
                                    {yearlyStats.map((entry: any, index: number) => (
                                       <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                                    ))}
                                 </Pie>
                                 <Tooltip formatter={(value) => [`$${value.toLocaleString()}`, 'Revenue']} />
                              </PieChart>
                           </ResponsiveContainer>
                        </div>
                        <div className="pie-legend-container">
                           <div className="legend-title">Years</div>
                           <div className="legend-items">
                              {yearlyStats.map((entry: any, index: number) => (
                                 <div key={entry.year} className="legend-item">
                                    <div 
                                       className="legend-color-box"
                                       style={{ backgroundColor: COLORS[index % COLORS.length] }}
                                    ></div>
                                    <span className="legend-text">{entry.year}</span>
                                 </div>
                              ))}
                           </div>
                           <div className="legend-note">
                              <em>Note: Each color represents a different year</em>
                           </div>
                        </div>
                     </div>
                  </div>
               ) : (
                  <div className="no-data-message">No yearly statistics available</div>
               )}

               {/* Yearly Statistics Table */}
               {yearlyStats && (
                  <div className="table-container">
                     <h4 className="table-title">Yearly Statistics Summary</h4>
                     <div className="table-wrapper">
                        <table className="statistics-table">
                           <thead className="table-header">
                              <tr>
                                 <th className="table-header-cell">Year</th>
                                 <th className="table-header-cell">Total Flights</th>
                                 <th className="table-header-cell">Total Passengers</th>
                                 <th className="table-header-cell">Total Revenue</th>
                                 <th className="table-header-cell">Revenue Rate (%)</th>
                              </tr>
                           </thead>
                           <tbody>
                              {yearlyStats.map((entry: any, index: number) => {
                                 const totalRevenue = yearlyStats.reduce((sum: number, item: any) => sum + item.totalRevenue, 0);
                                 const revenueRate = ((entry.totalRevenue / totalRevenue) * 100).toFixed(2);
                                 
                                 return (
                                    <tr key={entry.year} className={`table-row ${index % 2 === 0 ? 'even' : 'odd'}`}>
                                       <td className="table-cell year-month">{entry.year}</td>
                                       <td className="table-cell">{entry.totalFlights.toLocaleString()}</td>
                                       <td className="table-cell">{entry.totalPassengers.toLocaleString()}</td>
                                       <td className="table-cell revenue">${entry.totalRevenue.toLocaleString()}</td>
                                       <td className="table-cell rate">{revenueRate}%</td>
                                    </tr>
                                 );
                              })}
                           </tbody>
                        </table>
                     </div>
                  </div>
               )}
            </div>
         )}

         {/* Monthly Statistics Section */}
         {activeView === 'monthly' && (
            <div className="monthly-stats-section">
               <h3 className="section-title">Monthly Statistics</h3>

               <div className="year-selector-container">
                  <label htmlFor="year-picker" className="year-selector-label">
                     Select Year:
                  </label>
                  <select
                     id="year-picker"
                     value={selectedYear}
                     onChange={(e) => setSelectedYear(parseInt(e.target.value))}
                     className="year-selector"
                  >
                     {availableYears.map(year => (
                        <option key={year} value={year}>{year}</option>
                     ))}
                  </select>
               </div>

               {monthlyLoading ? (
                  <div className="loading-message">Loading monthly statistics for {selectedYear}...</div>
               ) : monthlyStats ? (
                  <div className="charts-grid">
                     {/* Monthly Total Flights Line Chart */}
                     <div className="chart-container">
                        <h4>Total Flights by Month ({selectedYear})</h4>
                        <ResponsiveContainer width="100%" height={300}>
                           <LineChart data={monthlyStats} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                              <CartesianGrid strokeDasharray="3 3" />
                              <XAxis dataKey="month" />
                              <YAxis />
                              <Tooltip formatter={(value, name) => [value, name === 'totalFlights' ? 'Total Flights' : name]} />
                              <Legend formatter={(value) => value === 'totalFlights' ? 'Total Flights' : value} />
                              <Line type="monotone" dataKey="totalFlights" stroke="#8884d8" strokeWidth={2} />
                           </LineChart>
                        </ResponsiveContainer>
                     </div>

                     {/* Monthly Total Passengers Line Chart */}
                     <div className="chart-container">
                        <h4>Total Passengers by Month ({selectedYear})</h4>
                        <ResponsiveContainer width="100%" height={300}>
                           <LineChart data={monthlyStats} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                              <CartesianGrid strokeDasharray="3 3" />
                              <XAxis dataKey="month" />
                              <YAxis />
                              <Tooltip formatter={(value, name) => [value, name === 'totalPassengers' ? 'Total Passengers' : name]} />
                              <Legend formatter={(value) => value === 'totalPassengers' ? 'Total Passengers' : value} />
                              <Line type="monotone" dataKey="totalPassengers" stroke="#82ca9d" strokeWidth={2} />
                           </LineChart>
                        </ResponsiveContainer>
                     </div>

                     {/* Monthly Total Revenue Line Chart */}
                     <div className="chart-container">
                        <h4>Total Revenue by Month ({selectedYear})</h4>
                        <ResponsiveContainer width="100%" height={300}>
                           <LineChart data={monthlyStats} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                              <CartesianGrid strokeDasharray="3 3" />
                              <XAxis dataKey="month" />
                              <YAxis />
                              <Tooltip formatter={(value) => [`$${value.toLocaleString()}`, 'Revenue']} />
                              <Legend formatter={(value) => value === 'totalRevenue' ? 'Total Revenue' : value} />
                              <Line type="monotone" dataKey="totalRevenue" stroke="#ffc658" strokeWidth={2} />
                           </LineChart>
                        </ResponsiveContainer>
                     </div>

                     {/* Monthly Revenue Distribution Pie Chart */}
                     <div className="pie-chart-container">
                        <div className="pie-chart-wrapper">
                           <h4>Revenue Distribution by Month ({selectedYear})</h4>
                           <ResponsiveContainer width={350} height={350}>
                              <PieChart margin={{ top: 5, right: 10, left: 20, bottom: 10 }}>
                                 <Pie
                                    data={monthlyStats}
                                    cx="50%"
                                    cy="50%"
                                    labelLine={false}
                                    outerRadius={100}
                                    fill="#8884d8"
                                    dataKey="totalRevenue"
                                 >
                                    {monthlyStats.map((entry: any, index: number) => (
                                       <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                                    ))}
                                 </Pie>
                                 <Tooltip formatter={(value) => [`$${value.toLocaleString()}`, 'Revenue']} />
                              </PieChart>
                           </ResponsiveContainer>
                        </div>
                        <div className="pie-legend-container">
                           <div className="legend-title">Months</div>
                           <div className="monthly-legend-items">
                              {Array.from({ length: 12 }, (_, i) => i + 1).map((month, index) => (
                                 <div key={month} className="monthly-legend-item">
                                    <div 
                                       className="monthly-legend-color-box"
                                       style={{ backgroundColor: COLORS[index % COLORS.length] }}
                                    ></div>
                                    <span className="monthly-legend-text">{month}</span>
                                 </div>
                              ))}
                           </div>
                           <div className="legend-note">
                              <em>Note: Each color represents a different month</em>
                           </div>
                        </div>
                     </div>
                  </div>
               ) : (
                  <div className="no-data-message">No monthly statistics available for {selectedYear}</div>
               )}

               {/* Monthly Statistics Table */}
               {monthlyStats && (
                  <div className="table-container">
                     <h4 className="table-title">Monthly Statistics Summary ({selectedYear})</h4>
                     <div className="table-wrapper">
                        <table className="statistics-table">
                           <thead className="table-header">
                              <tr>
                                 <th className="table-header-cell">Month</th>
                                 <th className="table-header-cell">Total Flights</th>
                                 <th className="table-header-cell">Total Passengers</th>
                                 <th className="table-header-cell">Total Revenue</th>
                                 <th className="table-header-cell">Revenue Rate (%)</th>
                              </tr>
                           </thead>
                           <tbody>
                              {monthlyStats.map((entry: any, index: number) => {
                                 const totalRevenue = monthlyStats.reduce((sum: number, item: any) => sum + item.totalRevenue, 0);
                                 const revenueRate = ((entry.totalRevenue / totalRevenue) * 100).toFixed(2);
                                 
                                 return (
                                    <tr key={entry.month} className={`table-row ${index % 2 === 0 ? 'even' : 'odd'}`}>
                                       <td className="table-cell year-month">{entry.month}</td>
                                       <td className="table-cell">{entry.totalFlights.toLocaleString()}</td>
                                       <td className="table-cell">{entry.totalPassengers.toLocaleString()}</td>
                                       <td className="table-cell revenue">${entry.totalRevenue.toLocaleString()}</td>
                                       <td className="table-cell rate">{revenueRate}%</td>
                                    </tr>
                                 );
                              })}
                           </tbody>
                        </table>
                     </div>
                  </div>
               )}
            </div>
         )}
      </div>
   );
};

export default Statistics;
