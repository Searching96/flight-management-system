import React, { useState, useEffect } from 'react';
import { addFlight, addFlightDetails } from '../services/FlightService';
import {
    getMaxMediumAirport,
    getMinFlightDuration,
    getMaxFlightDuration,
    getMaxStopDuration,
} from '../services/ParameterService'; // Import the services
import { useForm } from 'react-hook-form';
import { Button, Card, Form } from 'react-bootstrap';
import { listAirports } from '../services/FlightService-dev';
import Airport from '../models/Airport';
import { useNavigate } from 'react-router-dom';
import '../styles/FlightForm.css';

const FlightForm = ({ onSubmit, resetTrigger, getLatestId }) => {
    const [departureAirportList, setDepartureAirportList] = useState([]);
    const [arrivalAirportList, setArrivalAirportList] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [successMessage, setSuccessMessage] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [infoMessage, setInfoMessage] = useState('');
    const [selectedFlight, setSelectedFlight] = useState(null);
    const [resetFormTrigger, setResetFormTrigger] = useState(0);


    useEffect(() => {
        const fetchData = async () => {
            try {
                setIsLoading(true);
                setInfoMessage('Đang tải dữ liệu...');

                // Declare airportList before using it
                const [airportApiResponse] = await Promise.all([ // Destructure the result directly
                    listAirports()
                ]);

                // It seems departureAirportListResponse and arrivalAirportListResponse were intended to be the same
                // as airportApiResponse. If they can be different sources in the future, keep them separate.
                // For now, we can just use airportApiResponse.

                console.log("Raw API Response - listAirports:", airportApiResponse);

                // Make sure airportApiResponse is an array before mapping
                if (!Array.isArray(airportApiResponse)) {
                    console.error("API Response - listAirports is not an array:", airportApiResponse);
                    setDepartureAirportList([]);
                    setArrivalAirportList([]);
                    // Optionally set an error message for the user
                    setErrorMessage("Dữ liệu sân bay không đúng định dạng.");
                    // No need to throw an error here if you're handling it by setting empty lists
                    // and an error message. If you want to stop execution, you can throw.
                } else {
                    // Create Airport instances for both lists
                    // Adjust 'a.id' and 'a.name' to whatever properties your API returns for airport ID and name
                    const airports = airportApiResponse.map(a => new Airport(a.id, a.name));

                    setDepartureAirportList(airports);
                    setArrivalAirportList(airports); // Using the same list for both dropdowns

                    console.log("Processed Airports:", airports);
                }

            } catch (error) {
                console.error("Error loading data:", error);
                setErrorMessage("Lỗi tải dữ liệu sân bay: " + (error.message || "Lỗi không xác định"));
            } finally {
                setIsLoading(false);
                setInfoMessage('');
            }
        };

        fetchData();
    }, []);

    // Function to get the latest DaiLy ID
    // const fetchLatestDaiLyId = async () => {
    //     try {
    //         setInfoMessage('Đang lấy mã đại lý mới...');
    //         const idResponse = await getLatestMaDaiLy();
    //         setInfoMessage('');
    //         return idResponse?.maDaiLy || idResponse?.madaily;
    //     } catch (error) {
    //         console.error("Error fetching latest ID:", error);
    //         setErrorMessage("Không thể lấy mã đại lý mới: " + error.message);
    //         setInfoMessage('');
    //         return null;
    //     }
    // };

    useEffect(() => {
        console.log("State Updated - departureAirportList:", departureAirportList);
        console.log("State Updated - arrivalAirportList:", arrivalAirportList);
    }, [departureAirportList, arrivalAirportList]);

    // const handleEditRow = async (row) => {
    //     console.log("Edit row (full object):", JSON.stringify(row));

    //     try {
    //         setInfoMessage('Đang tải thông tin đại lý...');
    //         // Handle both ID formats
    //         const idToUse = row.madaily || row.maDaiLy;
    //         console.log("maDaiLy value:", idToUse || "NOT FOUND");

    //         if (!idToUse) {
    //             throw new Error("Could not find mã đại lý in the row data");
    //         }

    //         const daily = await getDaily(idToUse);
    //         console.log("Fetched daily for edit (full):", JSON.stringify(daily));
    //         setSelectedDaily(daily);
    //         setInfoMessage('');
    //     } catch (error) {
    //         console.error("Error fetching daily for edit:", error);
    //         setErrorMessage("Không thể tải thông tin đại lý: " + error.message);
    //         setInfoMessage('');
    //     }
    // };

    // const handleDeleteRow = async (row) => {
    //     console.log("Delete row (full object):", JSON.stringify(row));

    //     const idToUse = row.maDaiLy || row.madaily;
    //     if (!idToUse) {
    //         setErrorMessage("Không tìm thấy mã đại lý để xóa");
    //         return;
    //     }

    //     const isConfirmed = window.confirm(`Bạn có chắc chắn muốn xóa đại lý ${idToUse}?`);

    //     if (isConfirmed) {
    //         try {
    //             setInfoMessage('Đang xóa đại lý...');

    //             await deleteDaily(idToUse);

    //             setSuccessMessage(`Đại lý ${idToUse} đã được xóa thành công`);

    //             setInfoMessage('Đang cập nhật danh sách đại lý...');
    //             const updatedDaily = await getAllDaily();
    //             setDSDaiLy(updatedDaily || []);

    //             setInfoMessage('');

    //             if (selectedDaily && (selectedDaily.maDaiLy === idToUse || selectedDaily.madaily === idToUse)) {
    //                 setSelectedDaily(null);
    //             }
    //         } catch (error) {
    //             console.error("Error deleting daily:", error);
    //             setErrorMessage(`Không thể xóa đại lý: ${error.message}`);
    //             setInfoMessage('');
    //         }
    //     }
    // };

    const handleFormSubmit = async (formData, callback) => {
        if (formData.preventDefault) {
            formData.preventDefault();
        }

        console.log("Dữ liệu đã nhập: ", formData);

        setSuccessMessage('');
        setErrorMessage('');
        let operationSuccess = false;

        try {
            let result;

            if (selectedFlight) {
                setInfoMessage('Đang cập nhật chuyến bay...');
                // Ensure we have the ID in the correct format
                const idToUse = formData.id;
                result = await updateFlight(idToUse, formData);
                setSuccessMessage('Chuyến bay được cập nhật thành công: ' + idToUse);
                setSelectedFlight(null); // Only clear selection on success
                operationSuccess = true;
            } else {
                setInfoMessage('Đang tạo chuyến bay mới...');
                result = await createFlight(formData);
                const newId = result.id;
                setSuccessMessage('Chuyến bay được tạo thành công: ' + newId);
                operationSuccess = true;
            }

            setInfoMessage('Đang cập nhật danh sách chuyến bay...');
            setInfoMessage('');

            // Trigger form reset after successful submission
            setResetFormTrigger(prev => prev + 1);

            // Only clear selectedDaily after successful operation
            if (!selectedFlight) {
                // For new creation, we need to clear the form by setting a fresh selected state
                // that will be null but trigger the form to reset
                setSelectedFlight(null);
            }
        } catch (err) {
            console.error("Có lỗi xảy ra:", err);
            setErrorMessage(err.message || 'Có lỗi xảy ra khi xử lý yêu cầu.');
            setInfoMessage('');
            operationSuccess = false;
        }

        // Call the callback with the operation result if provided
        if (callback && typeof callback === 'function') {
            callback(operationSuccess);
        }
    };

    const { register, handleSubmit, setValue, reset, formState: { errors } } = useForm();
    const [editId, setEditId] = useState(null);
    const [newId, setNewId] = useState(null);
    const [cachedNextId, setCachedNextId] = useState(null);

    // Load cached ID from localStorage on component mount
    useEffect(() => {
        const storedId = localStorage.getItem('cachedFlightId');
        if (storedId) {
            setCachedNextId(storedId);
            console.log("Loaded cached ID from localStorage:", storedId);
        }
    }, []);

    // Save cachedNextId to localStorage whenever it changes
    useEffect(() => {
        if (cachedNextId) {
            localStorage.setItem('cachedFlightId', cachedNextId);
            console.log("Saved ID to localStorage:", cachedNextId);
        }
    }, [cachedNextId]);

    useEffect(() => {
        if (selectedFlight) {
            // Set form values for editing
            setValue("id", selectedFlight.id);
            setValue("departureAirportId", selectedFlight.departureAirportId);
            setValue("arrivalAirportId", selectedFlight.arrivalAirportId);
            setValue("flightDate", selectedFlight.flightDate);
            setValue("flightTime", selectedFlight.flightTime);
            setValue("duration", selectedFlight.duration);
            setValue("seatList", selectedFlight.seatList);
            setValue("mediumAirportList", selectedFlight.mediumAirportList);

            // Format the date properly for the date input
            let formattedDate;
            if (selectedFlight.flightDate) {
                // Handle different date formats
                const date = new Date(selectedFlight.flightDate);
                if (!isNaN(date.getTime())) {
                    formattedDate = date.toISOString().split('T')[0]; // Format as YYYY-MM-DD
                }
            }

            setValue("flightDate", formattedDate || new Date().toISOString().split("T")[0]);

            // Make sure to store the ID - handle both madaily and maDaiLy formats
            const flightId = selectedFlight.id;
            setEditId(flightId);
            setNewId(null);
        }
        else {
            // Don't auto-fetch ID anymore, wait for button click
            setEditId(null);

            // Reset other fields
            setValue("departureAirportId", "");
            setValue("arrivalAirportId", "");
            setValue("flightDate", "", new Date().toISOString().split("T")[0]); // Set today's date explicitly
            setValue("flightTime", "");
            setValue("duration", "");
            setValue("seatList", "");
            setValue("mediumAirportList");
        }
    }, [selectedFlight, setValue]);

    const resetForm = () => {
        reset();
        setEditId(null);
        setNewId(null); // Reset ID, don't auto-fetch

        // Reset other fields
        setValue("departureAirportId", "");
        setValue("arrivalAirportId", "");
        setValue("flightDate", "", new Date().toISOString().split("T")[0]); // Set today's date explicitly
        setValue("flightTime", "");
        setValue("duration", "");
        setValue("seatList", "");
        setValue("mediumAirportList");
    };

    // Listen for reset trigger from parent
    useEffect(() => {
        if (resetTrigger > 0) {
            resetForm();
        }
    }, [resetTrigger, resetForm]);

    // Modified getnewId function to use cached ID without fetching a new one
    const getnewId = async () => {
        try {
            // Reset form first to clear any existing data
            resetForm();

            if (cachedNextId) {
                // Use the cached ID if available
                setNewId(cachedNextId);
                setValue("id", cachedNextId);
                // Don't fetch a new ID here
            } else {
                // If no cached ID (first time), fetch it now
                const nextId = await getLatestId();
                if (nextId) {
                    setNewId(nextId);
                    setValue("id", nextId);
                    setCachedNextId(nextId);
                }
            }
        } catch (error) {
            console.error("Error using cached ID:", error);
        }
    };

    const submitHandler = (data) => {
        // Find the objects for display purposes
        const selectedDepartureAirport = departureAirportList.find(d => d.id === data.id);
        const selectedArrivalAirport = arrivalAirportList.find(a => a.id === data.id);

        // Create payload to send to API
        const payload = {
            id: editId || newId, // Use editId for updates, newId for new entries
            departureAirportId: data.departureAirportId,
            arrivalAirportId: data.arrivalAirportId,
            flightDate: data.flightDate || new Date().toISOString().split("T")[0],
            flightTime: data.flightTime,
            duration: data.duration,
            seatList: data.seatList,
            mediumAirportList: data.mediumAirportList, // Default to today if not provided
            // Extra info for display
            departureAirportName: selectedDepartureAirport?.name,
            arrivalAirportName: selectedArrivalAirport?.name
        };

        // Pass a callback function to get notified of successful operations
        onSubmit(payload, async (success) => {
            // Only fetch a new ID if the operation was successful
            if (success) {
                try {
                    const nextId = await getLatestId();
                    if (nextId) {
                        setCachedNextId(nextId);
                        console.log("Updated cached ID after successful operation:", nextId);
                        // localStorage is updated automatically via the useEffect
                    }
                } catch (error) {
                    console.error("Error fetching next ID after successful operation:", error);
                }
            }
        });
    };

    const isFormEnabled = Boolean(editId || newId || true);

    return (
        <>
            <Card>
                <Card.Header>{editId ? "Cập nhật lịch chuyến bay" : "Tiếp nhận lịch chuyến bay"}</Card.Header>
                <Card.Body>
                    <Form onSubmit={handleSubmit(submitHandler)}>
                        <section className="form-layout">
                            <div className="form-row">
                                <Form.Group className="mb-3 form-column">
                                    <Form.Label>Mã chuyến bay</Form.Label>
                                    <Form.Control
                                        type="text"
                                        placeholder="Mã chuyến bay"
                                        value={editId || newId || ""}
                                        readOnly />
                                    {errors.id && <span className="text-danger">{errors.id.message}</span>}
                                </Form.Group>
                                <Form.Group className="mb-3 form-column">
                                    <Form.Label>Sân bay đến</Form.Label>
                                    <Form.Select
                                        disabled={!isFormEnabled}
                                        {...register("masanbayden", { required: "Sân bay đến là bắt buộc" })}>
                                        <option value="">-- Chọn Sân bay đến --</option>
                                        {arrivalAirportList.map((airport) => (
                                            <option key={airport.id} value={airport.id}>
                                                {airport.name}
                                            </option>
                                        ))}
                                    </Form.Select>
                                    {errors.masanbayden && <span className="text-danger">{errors.masanbayden.message}</span>}
                                </Form.Group>
                                <Form.Group className="mb-3 form-column">
                                    <Form.Label>Sân bay đi</Form.Label>
                                    <Form.Select
                                        disabled={!isFormEnabled}
                                        {...register("masanbaydi", { required: "Sân bay đi là bắt buộc" })}>
                                        <option value="">-- Chọn Sân bay đi --</option>
                                        {departureAirportList.map((airport) => (
                                            <option key={airport.id} value={airport.id}>
                                                {airport.name}
                                            </option>
                                        ))}
                                    </Form.Select>
                                    {errors.masanbaydi && <span className="text-danger">{errors.masanbaydi.message}</span>}
                                </Form.Group>
                            </div>
                            <div className="form-row">
                                <Form.Group className="mb-3 form-column">
                                    <Form.Label>Ngày bay</Form.Label>
                                    <Form.Control
                                        type="date"
                                        disabled={!isFormEnabled}
                                        {...register("ngaybay", { required: "Ngày bay là bắt buộc" })} />
                                    {errors.ngaybay && <span className="text-danger">{errors.ngaybay.message}</span>}
                                </Form.Group>
                                <Form.Group className="mb-3 form-column">
                                    <Form.Label>Giờ bay</Form.Label>
                                    <Form.Control
                                        type="time"
                                        disabled={!isFormEnabled}
                                        {...register("giobay", { required: "Giờ bay là bắt buộc" })} />
                                    {errors.giobay && <span className="text-danger">{errors.giobay.message}</span>}
                                </Form.Group>
                                <Form.Group className="mb-3 form-column">
                                    <Form.Label>Thời gian bay</Form.Label>
                                    <Form.Control
                                        type="number"
                                        placeholder="Thời gian bay (phút)"
                                        disabled={!isFormEnabled}
                                        {...register("duration", {
                                            required: "Thời gian bay là bắt buộc",
                                            pattern: {
                                                value: /^-?(0|[1-9]\d*)$/,
                                                message: "Vui lòng nhập một số nguyên hợp lệ"
                                            }
                                        })} />
                                    {errors.ngaybay && <span className="text-danger">{errors.ngaybay.message}</span>}
                                </Form.Group>
                            </div>
                        </section>
                        <div className="d-flex justify-content-between form-row">
                            <div>
                                <Button
                                    type="submit"
                                    variant="primary"
                                    className="mt-3"
                                    disabled={!isFormEnabled}
                                    onClick={handleSubmit(handleFormSubmit)}>
                                    {editId ? "Cập nhật lịch chuyến bay" : "Tiếp nhận lịch chuyến bay"}
                                </Button>
                                <Button type="button" variant="secondary" className="mt-3 ms-2" onClick={getnewId}>
                                    Tiếp nhận lịch chuyến bay mới
                                </Button>
                            </div>

                            <Button
                                type="button"
                                variant="secondary"
                                className="mt-3"
                                onClick={resetForm}
                                disabled={!isFormEnabled}>
                                Hủy
                            </Button>
                        </div>
                    </Form>
                </Card.Body>
            </Card>
        </>
    );
}

export default FlightForm;