import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Container, Row, Col, Card, Button, Form, Alert } from 'react-bootstrap';
import { chatService } from '../../services/index.ts';
import { CreateTestChatboxRequest } from '../../models';

export const TestForm: React.FC = () => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const {
        register,
        handleSubmit,
        reset,
        formState: { errors }
    } = useForm<CreateTestChatboxRequest>();

    const onSubmit = async (data: CreateTestChatboxRequest) => {
        try {
            setError('');
            setLoading(true);
            
            // Simulate API call
            await chatService.createChatboxTest(data);

            console.log('Form submitted:', data);
            setSuccess('Form submitted successfully!');
            reset();
        } catch (err: any) {
            setError(err.message || 'Failed to submit form');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="py-4">
            <Row className="justify-content-center">
                <Col md={8}>
                    <Card>
                        <Card.Header>
                            <Card.Title className="mb-0">📝 Test Form</Card.Title>
                        </Card.Header>
                        <Card.Body>
                            {error && (
                                <Alert variant="danger" className="mb-3">
                                    {error}
                                </Alert>
                            )}

                            {success && (
                                <Alert variant="success" className="mb-3">
                                    {success}
                                </Alert>
                            )}

                            <Form onSubmit={handleSubmit(onSubmit)}>
                                <Row className="mb-3">
                                    <Col md={6}>
                                        <Form.Group>
                                            <Form.Label>ID khách hàng</Form.Label>
                                            <Form.Control
                                                type="text"
                                                {...register('customerId', {
                                                    required: 'ID khách hàng là bắt buộc',
                                                    minLength: {
                                                        value: 1,
                                                        message: 'ID khách hàng phải có ít nhất 1 ký tự'
                                                    }
                                                })}
                                                isInvalid={!!errors.customerId}
                                                placeholder="Nhập ID khách hàng"
                                            />
                                            <Form.Control.Feedback type="invalid">
                                                {errors.customerId?.message}
                                            </Form.Control.Feedback>
                                        </Form.Group>
                                    </Col>

                                    <Col md={6}>
                                        <Form.Group>
                                            <Form.Label>ID nhân viên</Form.Label>
                                            <Form.Control
                                                type="text"
                                                {...register('employeeId', {
                                                    required: 'ID nhân viên là bắt buộc',
                                                    minLength: {
                                                        value: 1,
                                                        message: 'ID nhân viên phải có ít nhất 1 ký tự'
                                                    }
                                                })}
                                                isInvalid={!!errors.employeeId}
                                                placeholder="Nhập ID nhân viên"
                                            />
                                            <Form.Control.Feedback type="invalid">
                                                {errors.employeeId?.message}
                                            </Form.Control.Feedback>
                                        </Form.Group>
                                    </Col>
                                </Row>

                                <Row>
                                    <Col className="text-end">
                                        <Button
                                            type="button"
                                            variant="secondary"
                                            className="me-2"
                                            onClick={() => reset()}
                                            disabled={loading}
                                        >
                                            Reset
                                        </Button>
                                        <Button
                                            type="submit"
                                            variant="primary"
                                            disabled={loading}
                                        >
                                            {loading ? 'Đang xử lý...' : 'Gửi'}
                                        </Button>
                                    </Col>
                                </Row>
                            </Form>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default TestForm;