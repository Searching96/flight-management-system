import React, { useState, useEffect } from "react";
import {
  Container,
  Row,
  Col,
  Card,
  Form,
  Button,
  Alert,
} from "react-bootstrap";
import { useAuth } from "../../hooks/useAuth";
import { useNavigate } from "react-router-dom";
import {
  accountService,
  customerService,
  passengerService,
} from "../../services";

const EditProfile: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [fetchingData, setFetchingData] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    citizenId: "",
    phoneNumber: "",
    score: 0,
    accountType: 0,
  });

  useEffect(() => {
    const fetchAccountInfo = async () => {
      if (!user?.id) return;

      setFetchingData(true);
      try {
        const account = await accountService.getAccountById(user.id);
        console.log("Fetched account info:", account);

        // Fetch customer score if user is a customer
        let customerScore = 0;
        if (user.accountTypeName === "Customer") {
          try {
            const customerData = await customerService.getCustomerById(user.id);
            customerScore = customerData.data.score || 0; // Default to 0 if score is not set
            console.log("Fetched customer score:", customerScore);
          } catch (scoreErr) {
            console.warn("Could not fetch customer score:", scoreErr);
          }
        }

        const accountInfo = {
          accountName: user.accountName || "",
          password: "", // Don't populate password for security
          email: user.email || "",
          citizenId: account.data.citizenId || "",
          phoneNumber: account.data.phoneNumber || "",
          score: customerScore,
          accountType: account.data.accountType,
        };

        //Split accountName into firstName and lastName
        const nameParts = accountInfo.accountName
          .split(" ")
          .filter((part) => part.trim() !== "");
        const lastName =
          nameParts.length > 1 ? nameParts[nameParts.length - 1] : "";
        const firstName =
          nameParts.length > 1
            ? nameParts.slice(0, -1).join(" ")
            : nameParts[0] || "";

        setFormData({
          firstName,
          lastName,
          email: accountInfo.email,
          citizenId: accountInfo.citizenId,
          phoneNumber: accountInfo.phoneNumber,
          score: accountInfo.score,
          accountType: accountInfo.accountType,
        });
      } catch (err: any) {
        setError("Failed to load account information");
        console.error("Error fetching account info:", err);
      } finally {
        setFetchingData(false);
      }
    };

    fetchAccountInfo();
  }, [user?.id, user?.accountName, user?.email]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setSuccess("");

    // Validate lastName contains only one word
    if (
      formData.lastName
        .trim()
        .split(" ")
        .filter((part) => part.trim() !== "").length > 1
    ) {
      setError("Họ chỉ được chứa một từ");
      setLoading(false);
      return;
    }

    try {
      const updateData = {
        ...formData,
        accountName: `${formData.firstName} ${formData.lastName}`.trim(),
      };

      console.log("Updating profile for user:", user?.id, updateData);

      // Update account information
      try {
        await accountService.updateAccount(user!.id, {
          accountId: user!.id,
          accountName: updateData.accountName,
          email: updateData.email,
          phoneNumber: updateData.phoneNumber,
          citizenId: updateData.citizenId,
          accountType: updateData.accountType,
        });
      } catch (accountErr) {
        console.error("Failed to update account:", accountErr);
        throw new Error("Failed to update account information");
      }

      // Check if passenger exists and update accordingly
      try {
        const existingPassenger = await passengerService.findExistingPassenger(
          updateData.citizenId
        );
        if (existingPassenger) {
          // Update existing passenger
          await passengerService.updatePassenger(
            existingPassenger.data.passengerId!,
            {
              passengerName: updateData.accountName,
              email: updateData.email,
              phoneNumber: updateData.phoneNumber,
            }
          );
          console.log(
            "Updated existing passenger:",
            existingPassenger.data.passengerId
          );
        } else {
          console.log("No existing passenger found with this Citizen ID");
        }
      } catch (passengerErr) {
        console.warn("Error handling passenger data:", passengerErr);
      }

      setSuccess("Cập nhật hồ sơ thành công!");
    } catch (err: any) {
      setError(err.message || "Không thể cập nhật hồ sơ");
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    navigate(-1);
  };

  if (fetchingData) {
    return (
      <Container className="mt-4">
        <Row className="justify-content-center">
          <Col md={8} lg={6}>
            <Card className="shadow">
              <Card.Body className="text-center p-5">
                <div className="spinner-border text-primary mb-3" />
                <p>Đang tải thông tin tài khoản...</p>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    );
  }

  return (
    <Container className="mt-4">
      <Row className="justify-content-center">
        <Col md={8} lg={6}>
          <Card className="shadow">
            <Card.Header className="bg-primary text-white d-flex justify-content-between align-items-center">
              <h4 className="mb-0">
                <i className="bi bi-person-gear me-2"></i>
                Chỉnh sửa hồ sơ
              </h4>
              <Button
                onClick={() => navigate("/profile/reset-password")}
                variant="outline-light"
                size="sm"
              >
                <i className="bi bi-shield-lock me-1"></i>
                Đổi mật khẩu
              </Button>
            </Card.Header>
            <Card.Body className="p-4">
              {error && (
                <Alert
                  variant="danger"
                  dismissible
                  onClose={() => setError("")}
                >
                  {error}
                </Alert>
              )}
              {success && (
                <Alert
                  variant="success"
                  dismissible
                  onClose={() => setSuccess("")}
                >
                  {success}
                </Alert>
              )}

              <Form onSubmit={handleSubmit}>
                <Row>
                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label>Tên</Form.Label>
                      <Form.Control
                        type="text"
                        name="firstName"
                        value={formData.firstName}
                        onChange={handleInputChange}
                        required
                      />
                    </Form.Group>
                  </Col>
                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label>Họ</Form.Label>
                      <Form.Control
                        type="text"
                        name="lastName"
                        value={formData.lastName}
                        onChange={handleInputChange}
                        required
                      />
                    </Form.Group>
                  </Col>
                </Row>

                <Form.Group className="mb-3">
                  <Form.Label>Email</Form.Label>
                  <Form.Control
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    required
                  />
                </Form.Group>

                {user?.accountTypeName === "Customer" && (
                  <Form.Group className="mb-3">
                    <Form.Label>Điểm khách hàng</Form.Label>
                    <Form.Control
                      type="number"
                      name="score"
                      value={formData.score}
                      readOnly
                      className="bg-light"
                    />
                    <Form.Text className="text-muted">
                      Điểm dịch vụ khách hàng của bạn (chỉ đọc)
                    </Form.Text>
                  </Form.Group>
                )}

                <Form.Group className="mb-3">
                  <Form.Label>Căn cước công dân</Form.Label>
                  <Form.Control
                    type="text"
                    name="citizenId"
                    value={formData.citizenId}
                    readOnly
                    className="bg-light"
                  />
                  <Form.Text className="text-muted">
                    Căn cước công dân không thể thay đổi
                  </Form.Text>
                </Form.Group>

                <Form.Group className="mb-4">
                  <Form.Label>Số điện thoại</Form.Label>
                  <Form.Control
                    type="tel"
                    name="phoneNumber"
                    value={formData.phoneNumber}
                    onChange={handleInputChange}
                    required
                  />
                </Form.Group>

                <div className="d-flex gap-2 justify-content-end">
                  <Button
                    variant="secondary"
                    onClick={handleCancel}
                    disabled={loading}
                  >
                    Hủy
                  </Button>
                  <Button type="submit" variant="primary" disabled={loading}>
                    {loading ? (
                      <>
                        <span className="spinner-border spinner-border-sm me-2" />
                        Đang cập nhật...
                      </>
                    ) : (
                      <>
                        <i className="bi bi-check-lg me-2"></i>
                        Cập nhật hồ sơ
                      </>
                    )}
                  </Button>
                </div>
              </Form>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default EditProfile;
