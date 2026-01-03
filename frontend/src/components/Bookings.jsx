import React, { useState } from 'react';
import { Calendar, Clock, CheckCircle } from 'lucide-react';
import axios from 'axios';
import './Bookings.css';

const Bookings = ({ user }) => {
    const [bookingData, setBookingData] = useState({
        resourceId: '', // Mentor ID
        startAt: '',
        endAt: ''
    });
    const [status, setStatus] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setStatus('Processing...');
        try {
            const token = localStorage.getItem('token');
            // Format dates to OffsetDateTime string (ISO-8601)
            // e.g. 2023-10-27T10:00:00Z
            const payload = {
                resourceId: parseInt(bookingData.resourceId),
                customerName: user.username, // Using username as customer identifier
                startAt: new Date(bookingData.startAt).toISOString(),
                endAt: new Date(bookingData.endAt).toISOString()
            };

            const response = await axios.post('/api/bookings', payload, {
                headers: { Authorization: `Bearer ${token}` }
            });

            setStatus(`Booking Confirmation: ID ${response.data.id} - ${response.data.status}`);
        } catch (err) {
            console.error(err);
            setStatus('Booking Failed: ' + (err.response?.data?.message || err.message));
        }
    };

    return (
        <div className="bookings-container card glass">
            <h2><Calendar className="icon" /> Book a Session</h2>
            <form onSubmit={handleSubmit} className="booking-form">
                <div className="form-group">
                    <label>Mentor ID (Resource)</label>
                    <input
                        type="number"
                        placeholder="Enter Mentor ID (e.g. 1)"
                        value={bookingData.resourceId}
                        onChange={(e) => setBookingData({ ...bookingData, resourceId: e.target.value })}
                        required
                    />
                </div>
                <div className="form-row">
                    <div className="form-group">
                        <label>Start Time</label>
                        <input
                            type="datetime-local"
                            value={bookingData.startAt}
                            onChange={(e) => setBookingData({ ...bookingData, startAt: e.target.value })}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label>End Time</label>
                        <input
                            type="datetime-local"
                            value={bookingData.endAt}
                            onChange={(e) => setBookingData({ ...bookingData, endAt: e.target.value })}
                            required
                        />
                    </div>
                </div>
                <button type="submit" className="btn btn-primary">Confirm Booking</button>
            </form>

            {status && (
                <div className="booking-status">
                    <CheckCircle size={20} />
                    <p>{status}</p>
                </div>
            )}
        </div>
    );
};

export default Bookings;
