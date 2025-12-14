import React, { useState, useEffect } from "react";
import { statisticsService } from "../../services";
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
  ResponsiveContainer,
} from "recharts";
import "./Statistics.css";

const Statistics: React.FC = () => {
  const [selectedYear, setSelectedYear] = useState<number>(
    new Date().getFullYear()
  );
  const [yearlyStats, setYearlyStats] = useState<any>(null);
  const [monthlyStats, setMonthlyStats] = useState<any>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [monthlyLoading, setMonthlyLoading] = useState<boolean>(false);
  const [activeView, setActiveView] = useState<"yearly" | "monthly">("yearly");

  // Colors for charts
  const COLORS = [
    "#0088FE",
    "#00C49F",
    "#FFBB28",
    "#FF8042",
    "#8884D8",
    "#82CA9D",
    "#FFC658",
    "#FF7C7C",
    "#8DD1E1",
    "#D084D0",
    "#FFB347",
    "#87CEEB",
  ];

  const fetchYearlyStats = async () => {
    setLoading(true);
    try {
      const stats = await statisticsService.getYearlyStatistics();
      console.log("Yearly Statistics:", stats);
      setYearlyStats(stats.data);
    } catch (error) {
      console.error("Error fetching yearly statistics:", error);
    } finally {
      setLoading(false);
    }
  };

  const fetchMonthlyStats = async () => {
    setMonthlyLoading(true);
    try {
      const stats = await statisticsService.getMonthlyStatistics(selectedYear);
      console.log("Monthly Statistics:", stats);
      setMonthlyStats(stats.data);
    } catch (error) {
      console.error("Error fetching monthly statistics:", error);
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
    if (activeView === "monthly") {
      fetchMonthlyStats();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedYear, activeView]);

  // Generate years for combobox (current year and 4 previous years)
  const availableYears = Array.from(
    { length: 5 },
    (_, i) => new Date().getFullYear() - i
  );

  return (
    <div className="statistics-container">
      {/* Navigation Buttons */}
      <div className="navigation-buttons">
        <button
          onClick={() => setActiveView("yearly")}
          className={`nav-button ${
            activeView === "yearly" ? "active" : "inactive"
          }`}
        >
          Thống kê theo năm
        </button>
        <button
          onClick={() => setActiveView("monthly")}
          className={`nav-button ${
            activeView === "monthly" ? "active" : "inactive"
          }`}
        >
          Thống kê theo tháng
        </button>
      </div>

      {/* Yearly Statistics Section */}
      {activeView === "yearly" && (
        <div className="yearly-stats-section">
          <h3 className="section-title">Thống kê theo năm</h3>

          {loading ? (
            <div className="loading-message">Đang tải thống kê theo năm...</div>
          ) : yearlyStats ? (
            <div className="charts-grid">
              {/* Total Flights Line Chart */}
              <div className="chart-container">
                <h4>Tổng số chuyến bay theo năm</h4>
                <ResponsiveContainer width="100%" height={300}>
                  <LineChart
                    data={yearlyStats}
                    margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
                  >
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="year" />
                    <YAxis />
                    <Tooltip
                      formatter={(value, name) => [
                        value,
                        name === "totalFlights" ? "Tổng chuyến bay" : name,
                      ]}
                    />
                    <Legend
                      formatter={(value) =>
                        value === "totalFlights" ? "Tổng chuyến bay" : value
                      }
                    />
                    <Line
                      type="monotone"
                      dataKey="totalFlights"
                      stroke="#8884d8"
                      strokeWidth={2}
                    />
                  </LineChart>
                </ResponsiveContainer>
              </div>

              {/* Total Passengers Line Chart */}
              <div className="chart-container">
                <h4>Tổng số hành khách theo năm</h4>
                <ResponsiveContainer width="100%" height={300}>
                  <LineChart
                    data={yearlyStats}
                    margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
                  >
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="year" />
                    <YAxis />
                    <Tooltip
                      formatter={(value, name) => [
                        value,
                        name === "totalPassengers" ? "Tổng hành khách" : name,
                      ]}
                    />
                    <Legend
                      formatter={(value) =>
                        value === "totalPassengers" ? "Tổng hành khách" : value
                      }
                    />
                    <Line
                      type="monotone"
                      dataKey="totalPassengers"
                      stroke="#82ca9d"
                      strokeWidth={2}
                    />
                  </LineChart>
                </ResponsiveContainer>
              </div>

              {/* Total Revenue Line Chart */}
              <div className="chart-container">
                <h4>Tổng doanh thu theo năm</h4>
                <ResponsiveContainer width="100%" height={300}>
                  <LineChart
                    data={yearlyStats}
                    margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
                  >
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="year" />
                    <YAxis />
                    <Tooltip
                      formatter={(value) => [
                        `${value.toLocaleString("vi-VN")} VND`,
                        "Doanh thu",
                      ]}
                    />
                    <Legend
                      formatter={(value) =>
                        value === "totalRevenue" ? "Tổng doanh thu" : value
                      }
                    />
                    <Line
                      type="monotone"
                      dataKey="totalRevenue"
                      stroke="#ffc658"
                      strokeWidth={2}
                    />
                  </LineChart>
                </ResponsiveContainer>
              </div>

              {/* Revenue Distribution Pie Chart */}
              <div className="pie-chart-container">
                <div className="pie-chart-wrapper">
                  <h4>Phân bổ doanh thu theo năm</h4>
                  <ResponsiveContainer width={350} height={350}>
                    <PieChart
                      margin={{ top: 5, right: 10, left: 20, bottom: 10 }}
                    >
                      <Pie
                        data={yearlyStats}
                        cx="50%"
                        cy="50%"
                        labelLine={false}
                        outerRadius={100}
                        fill="#8884d8"
                        dataKey="totalRevenue"
                      >
                        {yearlyStats.map((_entry: any, index: number) => (
                          <Cell
                            key={`cell-${index}`}
                            fill={COLORS[index % COLORS.length]}
                          />
                        ))}
                      </Pie>
                      <Tooltip
                        formatter={(value) => [
                          `${value.toLocaleString("vi-VN")} VND`,
                          "Doanh thu",
                        ]}
                      />
                    </PieChart>
                  </ResponsiveContainer>
                </div>
                <div className="pie-legend-container">
                  <div className="legend-title">Các năm</div>
                  <div className="legend-items">
                    {yearlyStats.map((entry: any, index: number) => (
                      <div key={entry.year} className="legend-item">
                        <div
                          className="legend-color-box"
                          style={{
                            backgroundColor: COLORS[index % COLORS.length],
                          }}
                        ></div>
                        <span className="legend-text">{entry.year}</span>
                      </div>
                    ))}
                  </div>
                  <div className="legend-note">
                    <em>Lưu ý: Mỗi màu đại diện cho một năm khác nhau</em>
                  </div>
                </div>
              </div>
            </div>
          ) : (
            <div className="no-data-message">
              Không có dữ liệu thống kê theo năm
            </div>
          )}

          {/* Yearly Statistics Table */}
          {yearlyStats && (
            <div className="table-container">
              <h4 className="table-title">Tóm tắt thống kê theo năm</h4>
              <div className="table-wrapper">
                <table className="statistics-table">
                  <thead className="table-header">
                    <tr>
                      <th className="table-header-cell">Năm</th>
                      <th className="table-header-cell">Tổng chuyến bay</th>
                      <th className="table-header-cell">Tổng hành khách</th>
                      <th className="table-header-cell">Tổng doanh thu</th>
                      <th className="table-header-cell">Tỷ lệ doanh thu (%)</th>
                    </tr>
                  </thead>
                  <tbody>
                    {yearlyStats.map((entry: any, index: number) => {
                      const totalRevenue = yearlyStats.reduce(
                        (sum: number, item: any) => sum + item.totalRevenue,
                        0
                      );
                      const revenueRate = (
                        (entry.totalRevenue / totalRevenue) *
                        100
                      ).toFixed(2);

                      return (
                        <tr
                          key={entry.year}
                          className={`table-row ${
                            index % 2 === 0 ? "even" : "odd"
                          }`}
                        >
                          <td className="table-cell year-month">
                            {entry.year}
                          </td>
                          <td className="table-cell">
                            {entry.totalFlights.toLocaleString("vi-VN")}
                          </td>
                          <td className="table-cell">
                            {entry.totalPassengers.toLocaleString("vi-VN")}
                          </td>
                          <td className="table-cell revenue">
                            {entry.totalRevenue.toLocaleString("vi-VN")} VND
                          </td>
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
      {activeView === "monthly" && (
        <div className="monthly-stats-section">
          <h3 className="section-title">Thống kê theo tháng</h3>

          <div className="year-selector-container">
            <label htmlFor="year-picker" className="year-selector-label">
              Chọn năm:
            </label>
            <select
              id="year-picker"
              value={selectedYear}
              onChange={(e) => setSelectedYear(parseInt(e.target.value))}
              className="year-selector"
            >
              {availableYears.map((year) => (
                <option key={year} value={year}>
                  {year}
                </option>
              ))}
            </select>
          </div>

          {monthlyLoading ? (
            <div className="loading-message">
              Đang tải thống kê theo tháng cho năm {selectedYear}...
            </div>
          ) : monthlyStats ? (
            <div className="charts-grid">
              {/* Monthly Total Flights Line Chart */}
              <div className="chart-container">
                <h4>Tổng số chuyến bay theo tháng ({selectedYear})</h4>
                <ResponsiveContainer width="100%" height={300}>
                  <LineChart
                    data={monthlyStats}
                    margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
                  >
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="month" />
                    <YAxis />
                    <Tooltip
                      formatter={(value, name) => [
                        value,
                        name === "totalFlights" ? "Tổng chuyến bay" : name,
                      ]}
                    />
                    <Legend
                      formatter={(value) =>
                        value === "totalFlights" ? "Tổng chuyến bay" : value
                      }
                    />
                    <Line
                      type="monotone"
                      dataKey="totalFlights"
                      stroke="#8884d8"
                      strokeWidth={2}
                    />
                  </LineChart>
                </ResponsiveContainer>
              </div>

              {/* Monthly Total Passengers Line Chart */}
              <div className="chart-container">
                <h4>Tổng số hành khách theo tháng ({selectedYear})</h4>
                <ResponsiveContainer width="100%" height={300}>
                  <LineChart
                    data={monthlyStats}
                    margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
                  >
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="month" />
                    <YAxis />
                    <Tooltip
                      formatter={(value, name) => [
                        value,
                        name === "totalPassengers" ? "Tổng hành khách" : name,
                      ]}
                    />
                    <Legend
                      formatter={(value) =>
                        value === "totalPassengers" ? "Tổng hành khách" : value
                      }
                    />
                    <Line
                      type="monotone"
                      dataKey="totalPassengers"
                      stroke="#82ca9d"
                      strokeWidth={2}
                    />
                  </LineChart>
                </ResponsiveContainer>
              </div>

              {/* Monthly Total Revenue Line Chart */}
              <div className="chart-container">
                <h4>Tổng doanh thu theo tháng ({selectedYear})</h4>
                <ResponsiveContainer width="100%" height={300}>
                  <LineChart
                    data={monthlyStats}
                    margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
                  >
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="month" />
                    <YAxis />
                    <Tooltip
                      formatter={(value) => [
                        `${value.toLocaleString("vi-VN")} VND`,
                        "Doanh thu",
                      ]}
                    />
                    <Legend
                      formatter={(value) =>
                        value === "totalRevenue" ? "Tổng doanh thu" : value
                      }
                    />
                    <Line
                      type="monotone"
                      dataKey="totalRevenue"
                      stroke="#ffc658"
                      strokeWidth={2}
                    />
                  </LineChart>
                </ResponsiveContainer>
              </div>

              {/* Monthly Revenue Distribution Pie Chart */}
              <div className="pie-chart-container">
                <div className="pie-chart-wrapper">
                  <h4>Phân bổ doanh thu theo tháng ({selectedYear})</h4>
                  <ResponsiveContainer width={350} height={350}>
                    <PieChart
                      margin={{ top: 5, right: 10, left: 20, bottom: 10 }}
                    >
                      <Pie
                        data={monthlyStats}
                        cx="50%"
                        cy="50%"
                        labelLine={false}
                        outerRadius={100}
                        fill="#8884d8"
                        dataKey="totalRevenue"
                      >
                        {monthlyStats.map((_entry: any, index: number) => (
                          <Cell
                            key={`cell-${index}`}
                            fill={COLORS[index % COLORS.length]}
                          />
                        ))}
                      </Pie>
                      <Tooltip
                        formatter={(value) => [
                          `${value.toLocaleString("vi-VN")} VND`,
                          "Doanh thu",
                        ]}
                      />
                    </PieChart>
                  </ResponsiveContainer>
                </div>
                <div className="pie-legend-container">
                  <div className="legend-title">Các tháng</div>
                  <div className="monthly-legend-items">
                    {Array.from({ length: 12 }, (_, i) => i + 1).map(
                      (month, index) => (
                        <div key={month} className="monthly-legend-item">
                          <div
                            className="monthly-legend-color-box"
                            style={{
                              backgroundColor: COLORS[index % COLORS.length],
                            }}
                          ></div>
                          <span className="monthly-legend-text">
                            Tháng {month}
                          </span>
                        </div>
                      )
                    )}
                  </div>
                  <div className="legend-note">
                    <em>Lưu ý: Mỗi màu đại diện cho một tháng khác nhau</em>
                  </div>
                </div>
              </div>
            </div>
          ) : (
            <div className="no-data-message">
              Không có dữ liệu thống kê theo tháng cho năm {selectedYear}
            </div>
          )}

          {/* Monthly Statistics Table */}
          {monthlyStats && (
            <div className="table-container">
              <h4 className="table-title">
                Tóm tắt thống kê theo tháng ({selectedYear})
              </h4>
              <div className="table-wrapper">
                <table className="statistics-table">
                  <thead className="table-header">
                    <tr>
                      <th className="table-header-cell">Tháng</th>
                      <th className="table-header-cell">Tổng chuyến bay</th>
                      <th className="table-header-cell">Tổng hành khách</th>
                      <th className="table-header-cell">Tổng doanh thu</th>
                      <th className="table-header-cell">Tỷ lệ doanh thu (%)</th>
                    </tr>
                  </thead>
                  <tbody>
                    {monthlyStats.map((entry: any, index: number) => {
                      const totalRevenue = monthlyStats.reduce(
                        (sum: number, item: any) => sum + item.totalRevenue,
                        0
                      );
                      const revenueRate = (
                        (entry.totalRevenue / totalRevenue) *
                        100
                      ).toFixed(2);

                      return (
                        <tr
                          key={entry.month}
                          className={`table-row ${
                            index % 2 === 0 ? "even" : "odd"
                          }`}
                        >
                          <td className="table-cell year-month">
                            Tháng {entry.month}
                          </td>
                          <td className="table-cell">
                            {entry.totalFlights.toLocaleString("vi-VN")}
                          </td>
                          <td className="table-cell">
                            {entry.totalPassengers.toLocaleString("vi-VN")}
                          </td>
                          <td className="table-cell revenue">
                            {entry.totalRevenue.toLocaleString("vi-VN")} VND
                          </td>
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
