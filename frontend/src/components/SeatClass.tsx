import React, { useEffect } from "react";
import { useForm, SubmitHandler } from "react-hook-form";
import { addSeatClass, getSeatClass, updateSeatClass } from "../services/SeatClassService";
import { useNavigate, useParams } from "react-router-dom";
import { Card, Button, Form, Container, Row, Col, Spinner } from "react-bootstrap";

type SeatClassFormInputs = {
  seatName: string;
};

const SeatClass: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    reset,
  } = useForm<SeatClassFormInputs>({
    defaultValues: { seatName: "" }
  });

  // Load existing seat class for edit
  useEffect(() => {
    if (id) {
      getSeatClass(Number(id))
        .then((response) => {
          reset({ seatName: response.seatName });
        })
        .catch((error) => {
          console.error("Error fetching seat class:", error);
        });
    }
  }, [id, reset]);

  const onSubmit: SubmitHandler<SeatClassFormInputs> = async (data) => {
    try {
      if (id) {
        await updateSeatClass(Number(id), data);
      } else {
        await addSeatClass(data);
      }
      navigate("/seat-classes");
    } catch (error) {
      console.error("Error saving seat class:", error);
    }
  };

  return (
    <Container className="py-5">
      <Row className="justify-content-center">
        <Col md={6}>
          <Card>
            <Card.Header className="text-center">
              <h2>{id ? "Update Seat Class" : "Add Seat Class"}</h2>
            </Card.Header>
            <Card.Body>
              <Form onSubmit={handleSubmit(onSubmit)} noValidate>
                <Form.Group controlId="seatName" className="mb-3">
                  <Form.Label>Seat Class Name:</Form.Label>
                  <Form.Control
                    type="text"
                    placeholder="Enter seat class name"
                    isInvalid={!!errors.seatName}
                    {...register("seatName", { required: "Seat class name is required" })}
                    autoFocus
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.seatName?.message}
                  </Form.Control.Feedback>
                </Form.Group>
                <div className="d-flex justify-content-center">
                  <Button
                    variant="success"
                    type="submit"
                    disabled={isSubmitting}
                  >
                    {isSubmitting ? (
                      <>
                        <Spinner
                          as="span"
                          animation="border"
                          size="sm"
                          className="me-2"
                        />
                        {id ? "Updating..." : "Submitting..."}
                      </>
                    ) : id ? "Update" : "Submit"}
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

export default SeatClass;