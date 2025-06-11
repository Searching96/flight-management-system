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
      <div className="statistics-container" style={{ padding: '20px' }}>
         {/* Navigation Buttons */}
         <div style={{ marginBottom: '30px', display: 'flex', gap: '10px' }}>
            <button
               onClick={() => setActiveView('yearly')}
               style={{
                  padding: '10px 20px',
                  backgroundColor: activeView === 'yearly' ? '#007bff' : '#6c757d',
                  color: 'white',
                  border: 'none',
                  borderRadius: '5px',
                  cursor: 'pointer'
               }}
            >
               Yearly Statistics
            </button>
            <button
               onClick={() => setActiveView('monthly')}
               style={{
                  padding: '10px 20px',
                  backgroundColor: activeView === 'monthly' ? '#007bff' : '#6c757d',
                  color: 'white',
                  border: 'none',
                  borderRadius: '5px',
                  cursor: 'pointer'
               }}
            >
               Monthly Statistics
            </button>
         </div>

         {/* Yearly Statistics Section */}
         {activeView === 'yearly' && (
            <div className="yearly-stats-section">
               <h3>Yearly Statistics</h3>

               {loading ? (
                  <div style={{ textAlign: 'center', padding: '40px' }}>Loading yearly statistics...</div>
               ) : yearlyStats ? (
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '40px', marginTop: '20px' }}>
                     {/* Total Flights Line Chart */}
                     <div>
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
                     <div>
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
                     <div>
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
                     <div style={{ display: 'flex', alignItems: 'flex-start', gap: '20px' }}>
                        <div style={{ flex: '0 0 auto' }}>
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
                        <div style={{ flex: '1', paddingTop: '60px' }}>
                           <div style={{ textAlign: 'left', marginBottom: '10px', fontSize: '14px', fontWeight: 'bold', color: '#333' }}>
                              Years
                           </div>
                           <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                              {yearlyStats.map((entry: any, index: number) => (
                                 <div key={entry.year} style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                    <div style={{ 
                                       width: '12px', 
                                       height: '12px', 
                                       backgroundColor: COLORS[index % COLORS.length],
                                       borderRadius: '2px'
                                    }}></div>
                                    <span style={{ fontSize: '12px' }}>{entry.year}</span>
                                 </div>
                              ))}
                           </div>
                           <div style={{ textAlign: 'left', marginTop: '15px', fontSize: '12px', color: '#666' }}>
                              <em>Note: Each color represents a different year</em>
                           </div>
                        </div>
                     </div>
                  </div>
               ) : (
                  <div style={{ textAlign: 'center', padding: '40px' }}>No yearly statistics available</div>
               )}

               {/* Yearly Statistics Table */}
               {yearlyStats && (
                  <div style={{ marginTop: '40px' }}>
                     <h4 style={{ marginBottom: '20px', color: '#333' }}>Yearly Statistics Summary</h4>
                     <div style={{ 
                        overflowX: 'auto', 
                        boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)', 
                        borderRadius: '8px',
                        backgroundColor: '#fff'
                     }}>
                        <table style={{ 
                           width: '100%', 
                           borderCollapse: 'collapse',
                           fontSize: '14px'
                        }}>
                           <thead>
                              <tr style={{ backgroundColor: '#f8f9fa' }}>
                                 <th style={{ 
                                    padding: '15px', 
                                    textAlign: 'center', 
                                    borderBottom: '2px solid #dee2e6',
                                    fontWeight: '600',
                                    color: '#495057'
                                 }}>Year</th>
                                 <th style={{ 
                                    padding: '15px', 
                                    textAlign: 'center', 
                                    borderBottom: '2px solid #dee2e6',
                                    fontWeight: '600',
                                    color: '#495057'
                                 }}>Total Flights</th>
                                 <th style={{ 
                                    padding: '15px', 
                                    textAlign: 'center', 
                                    borderBottom: '2px solid #dee2e6',
                                    fontWeight: '600',
                                    color: '#495057'
                                 }}>Total Passengers</th>
                                 <th style={{ 
                                    padding: '15px', 
                                    textAlign: 'center', 
                                    borderBottom: '2px solid #dee2e6',
                                    fontWeight: '600',
                                    color: '#495057'
                                 }}>Total Revenue</th>
                                 <th style={{ 
                                    padding: '15px', 
                                    textAlign: 'center', 
                                    borderBottom: '2px solid #dee2e6',
                                    fontWeight: '600',
                                    color: '#495057'
                                 }}>Revenue Rate (%)</th>
                              </tr>
                           </thead>
                           <tbody>
                              {yearlyStats.map((entry: any, index: number) => {
                                 const totalRevenue = yearlyStats.reduce((sum: number, item: any) => sum + item.totalRevenue, 0);
                                 const revenueRate = ((entry.totalRevenue / totalRevenue) * 100).toFixed(2);
                                 
                                 return (
                                    <tr key={entry.year} style={{ 
                                       backgroundColor: index % 2 === 0 ? '#fff' : '#f8f9fa',
                                       transition: 'background-color 0.2s ease'
                                    }}
                                    onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#e3f2fd'}
                                    onMouseLeave={(e) => e.currentTarget.style.backgroundColor = index % 2 === 0 ? '#fff' : '#f8f9fa'}
                                    >
                                       <td style={{ 
                                          padding: '12px 15px', 
                                          borderBottom: '1px solid #dee2e6',
                                          fontWeight: '500',
                                          color: '#333',
                                          textAlign: 'center'
                                       }}>{entry.year}</td>
                                       <td style={{ 
                                          padding: '12px 15px', 
                                          textAlign: 'center', 
                                          borderBottom: '1px solid #dee2e6',
                                          color: '#495057'
                                       }}>{entry.totalFlights.toLocaleString()}</td>
                                       <td style={{ 
                                          padding: '12px 15px', 
                                          textAlign: 'center', 
                                          borderBottom: '1px solid #dee2e6',
                                          color: '#495057'
                                       }}>{entry.totalPassengers.toLocaleString()}</td>
                                       <td style={{ 
                                          padding: '12px 15px', 
                                          textAlign: 'center', 
                                          borderBottom: '1px solid #dee2e6',
                                          color: '#28a745',
                                          fontWeight: '500'
                                       }}>${entry.totalRevenue.toLocaleString()}</td>
                                       <td style={{ 
                                          padding: '12px 15px', 
                                          textAlign: 'center', 
                                          borderBottom: '1px solid #dee2e6',
                                          color: '#007bff',
                                          fontWeight: '500'
                                       }}>{revenueRate}%</td>
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
               <h3>Monthly Statistics</h3>

               <div className="input-group" style={{ marginBottom: '20px' }}>
                  <label htmlFor="year-picker" style={{ 
                     marginRight: '15px', 
                     fontSize: '16px', 
                     fontWeight: '500',
                     color: '#333'
                  }}>
                     Select Year:
                  </label>
                  <select
                     id="year-picker"
                     value={selectedYear}
                     onChange={(e) => setSelectedYear(parseInt(e.target.value))}
                     className="year-input"
                     style={{ 
                        padding: '8px 12px',
                        fontSize: '14px',
                        border: '2px solid #ddd',
                        borderRadius: '8px',
                        backgroundColor: '#fff',
                        color: '#333',
                        cursor: 'pointer',
                        outline: 'none',
                        transition: 'all 0.3s ease',
                        boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
                        minWidth: '120px'
                     }}
                     onFocus={(e) => e.target.style.borderColor = '#007bff'}
                     onBlur={(e) => e.target.style.borderColor = '#ddd'}
                  >
                     {availableYears.map(year => (
                        <option key={year} value={year} style={{ padding: '8px' }}>{year}</option>
                     ))}
                  </select>
               </div>

               {monthlyLoading ? (
                  <div style={{ textAlign: 'center', padding: '40px' }}>Loading monthly statistics for {selectedYear}...</div>
               ) : monthlyStats ? (
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '40px', marginTop: '20px' }}>
                     {/* Monthly Total Flights Line Chart */}
                     <div>
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
                     <div>
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
                     <div>
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
                     <div style={{ display: 'flex', alignItems: 'flex-start', gap: '20px' }}>
                        <div style={{ flex: '0 0 auto' }}>
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
                        <div style={{ flex: '1', paddingTop: '60px' }}>
                           <div style={{ textAlign: 'left', marginBottom: '10px', fontSize: '14px', fontWeight: 'bold', color: '#333' }}>
                              Months
                           </div>
                           <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '8px' }}>
                              {Array.from({ length: 12 }, (_, i) => i + 1).map((month, index) => (
                                 <div key={month} style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                                    <div style={{ 
                                       width: '10px', 
                                       height: '10px', 
                                       backgroundColor: COLORS[index % COLORS.length],
                                       borderRadius: '2px'
                                    }}></div>
                                    <span style={{ fontSize: '11px' }}>{month}</span>
                                 </div>
                              ))}
                           </div>
                           <div style={{ textAlign: 'left', marginTop: '15px', fontSize: '12px', color: '#666' }}>
                              <em>Note: Each color represents a different month</em>
                           </div>
                        </div>
                     </div>
                  </div>
               ) : (
                  <div style={{ textAlign: 'center', padding: '40px' }}>No monthly statistics available for {selectedYear}</div>
               )}

               {/* Monthly Statistics Table */}
               {monthlyStats && (
                  <div style={{ marginTop: '40px' }}>
                     <h4 style={{ marginBottom: '20px', color: '#333' }}>Monthly Statistics Summary ({selectedYear})</h4>
                     <div style={{ 
                        overflowX: 'auto', 
                        boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)', 
                        borderRadius: '8px',
                        backgroundColor: '#fff'
                     }}>
                        <table style={{ 
                           width: '100%', 
                           borderCollapse: 'collapse',
                           fontSize: '14px'
                        }}>
                           <thead>
                              <tr style={{ backgroundColor: '#f8f9fa' }}>
                                 <th style={{ 
                                    padding: '15px', 
                                    textAlign: 'center', 
                                    borderBottom: '2px solid #dee2e6',
                                    fontWeight: '600',
                                    color: '#495057'
                                 }}>Month</th>
                                 <th style={{ 
                                    padding: '15px', 
                                    textAlign: 'center', 
                                    borderBottom: '2px solid #dee2e6',
                                    fontWeight: '600',
                                    color: '#495057'
                                 }}>Total Flights</th>
                                 <th style={{ 
                                    padding: '15px', 
                                    textAlign: 'center', 
                                    borderBottom: '2px solid #dee2e6',
                                    fontWeight: '600',
                                    color: '#495057'
                                 }}>Total Passengers</th>
                                 <th style={{ 
                                    padding: '15px', 
                                    textAlign: 'center', 
                                    borderBottom: '2px solid #dee2e6',
                                    fontWeight: '600',
                                    color: '#495057'
                                 }}>Total Revenue</th>
                                 <th style={{ 
                                    padding: '15px', 
                                    textAlign: 'center', 
                                    borderBottom: '2px solid #dee2e6',
                                    fontWeight: '600',
                                    color: '#495057'
                                 }}>Revenue Rate (%)</th>
                              </tr>
                           </thead>
                           <tbody>
                              {monthlyStats.map((entry: any, index: number) => {
                                 const totalRevenue = monthlyStats.reduce((sum: number, item: any) => sum + item.totalRevenue, 0);
                                 const revenueRate = ((entry.totalRevenue / totalRevenue) * 100).toFixed(2);
                                 
                                 return (
                                    <tr key={entry.month} style={{ 
                                       backgroundColor: index % 2 === 0 ? '#fff' : '#f8f9fa',
                                       transition: 'background-color 0.2s ease'
                                    }}
                                    onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#e3f2fd'}
                                    onMouseLeave={(e) => e.currentTarget.style.backgroundColor = index % 2 === 0 ? '#fff' : '#f8f9fa'}
                                    >
                                       <td style={{ 
                                          padding: '12px 15px', 
                                          borderBottom: '1px solid #dee2e6',
                                          fontWeight: '500',
                                          color: '#333',
                                          textAlign: 'center'
                                       }}>{entry.month}</td>
                                       <td style={{ 
                                          padding: '12px 15px', 
                                          textAlign: 'center', 
                                          borderBottom: '1px solid #dee2e6',
                                          color: '#495057'
                                       }}>{entry.totalFlights.toLocaleString()}</td>
                                       <td style={{ 
                                          padding: '12px 15px', 
                                          textAlign: 'center', 
                                          borderBottom: '1px solid #dee2e6',
                                          color: '#495057'
                                       }}>{entry.totalPassengers.toLocaleString()}</td>
                                       <td style={{ 
                                          padding: '12px 15px', 
                                          textAlign: 'center', 
                                          borderBottom: '1px solid #dee2e6',
                                          color: '#28a745',
                                          fontWeight: '500'
                                       }}>${entry.totalRevenue.toLocaleString()}</td>
                                       <td style={{ 
                                          padding: '12px 15px', 
                                          textAlign: 'center', 
                                          borderBottom: '1px solid #dee2e6',
                                          color: '#007bff',
                                          fontWeight: '500'
                                       }}>{revenueRate}%</td>
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
