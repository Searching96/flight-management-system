import React, { useState, useEffect } from 'react';
import {
   updateParameter,
   getParameter,
} from '../services/ParameterService';
import { useForm, SubmitHandler } from "react-hook-form";
import { useNavigate, useParams } from "react-router-dom";
import { ParameterDto } from "../models/Parameter";
import { Form, Button, Container, Card, Col, Spinner } from "react-bootstrap";

const ParameterForm: React.FC = () => {
   const { id } = useParams<{ id: string }>();
   const navigate = useNavigate();
   const [parameter, setParameter] = useState<ParameterDto | null>(null);
   const [loading, setLoading] = useState<boolean>(true);

   const {
      register,
      handleSubmit,
      getValues,
      reset,
      formState: { errors, isSubmitting },
   } = useForm<ParameterDto>({
      defaultValues: {
         maxMediumAirport: 0,
         minFlightDuration: 0,
         maxFlightDuration: 0,
         maxStopDuration: 0,
      },
   });

   useEffect(() => {
      getParameter().then(data => {
         setParameter(data);
         reset(data);
         setLoading(false);
      });
   }, []);

   const onSubmit: SubmitHandler<ParameterDto> = async (data) => {
      await updateParameter(data);
      navigate("/parameters");
   };

   return (
      <Card className="mx-auto mt-4" style={{ width: '36rem' }}>
         <Card.Header className="mb-3">Edit Parameter</Card.Header>
         <Card.Body>
            <Form onSubmit={handleSubmit(onSubmit)}>
               <Form.Group className="mb-3" controlId="maxMediumAirport">
                  <Form.Label>Max medium airport</Form.Label>
                  <Form.Control
                     type="number"
                     min={1}
                     {...register("maxMediumAirport", {
                        required: "Max medium airport is required",
                        valueAsNumber: true,
                     })}
                     isInvalid={!!errors.maxMediumAirport}
                  />
                  <Form.Control.Feedback type="invalid">
                     {errors.maxMediumAirport?.message}
                  </Form.Control.Feedback>
               </Form.Group>
               <Form.Group className="mb-3" controlId="minFlightDuration">
                  <Form.Label>Min flight duration (minutes)</Form.Label>
                  <Form.Control
                     type="number"
                     min={1}
                     {...register("minFlightDuration", {
                        required: "Min flight duration is required",
                        valueAsNumber: true,
                        min: { value: 1, message: "Min flight duration must be at least 1 minute" },
                     })}
                     isInvalid={!!errors.minFlightDuration}
                  />
                  <Form.Control.Feedback type="invalid">
                     {errors.minFlightDuration?.message}
                  </Form.Control.Feedback>
               </Form.Group>
               <Form.Group className="mb-3" controlId="maxFlightDuration">
                  <Form.Label>Duration (minutes)</Form.Label>
                  <Form.Control
                     type="number"
                     min={1}
                     {...register("maxFlightDuration", {
                        required: "Max flight duration is required",
                        valueAsNumber: true,
                        min: { value: 1, message: "Max flight duration must be at least 1 minute" },
                        validate: value => {
                           const minFlightDuration = getValues("minFlightDuration");
                           if (value < minFlightDuration) {
                              return "Max flight duration must be greater than or equal to min flight duration";
                           }
                           return true;
                        }
                     })}
                     isInvalid={!!errors.maxFlightDuration}
                  />
                  <Form.Control.Feedback type="invalid">
                     {errors.maxFlightDuration?.message}
                  </Form.Control.Feedback>
               </Form.Group>
               <Form.Group className="mb-3" controlId="maxStopDuration">
                  <Form.Label>Max stop duration (minutes)</Form.Label>
                  <Form.Control
                     type="number"
                     min={1}
                     {...register("maxStopDuration", {
                        required: "Max stop duration is required",
                        valueAsNumber: true,
                        min: { value: 1, message: "Max stop duration must be at least 1 minute" },
                     })}
                     isInvalid={!!errors.maxStopDuration}
                  />
                  <Form.Control.Feedback type="invalid">
                     {errors.maxStopDuration?.message}
                  </Form.Control.Feedback>
               </Form.Group>
            </Form>
            <Col className="d-flex justify-content-between">
               <Button variant="success" type="submit" disabled={isSubmitting}>
                  Update
               </Button>
               <Button variant="secondary" type="button" disabled={isSubmitting} onClick={() => navigate('/parameters')}>
                  Cancel
               </Button>
            </Col>
         </Card.Body>
      </Card>
   );
};

export default ParameterForm;